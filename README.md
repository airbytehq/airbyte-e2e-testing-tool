# Airbyte E2E testing tool
This stand-alone testing configurable application will help us execute different end-to-end tests and improve the Airbyte final product quality.
You can find the development state and future roadmap in this [epic issue](https://github.com/airbytehq/airbyte/issues/15152).

### Main flow
The central concept of the tool is a universal testing platform that you can easily configure by providing scenario config and required credentials. The solution helps us run different test scenarios from the CI referring to already defined config files and cover all possible e2e test cases with minimal effort.

![image](https://user-images.githubusercontent.com/30464745/185084724-1fa9ce8e-52d5-4b17-92a6-3ce06e8715f1.png)

#### Credentials
All credentials are independent of the selected scenario and can be used in different runs. In addition, we store credentials in the secrets storage as we do for the integration tests.

As the credential file is input data, you can find structure in the [CredentialConfig.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/CredentialConfig.yaml) file.

**Source credential file example:**
```json
{
  "credentialName" : "test_source_creds",
  "credentialType" : "source_creds",
  "instanceType" : "Postgres",
  "credentialJson" : {
    "database" : "test_source_database",
    "host" : "put.your.host.here",
    "password" : "yourPassword",
    "port" : 5432,
    "replication_method" : {
      "method" : "Standard"
    },
    "schemas" : ["public"],
    "ssl" : false,
    "ssl_mode" : {
      "mode" : "disable"
    },
    "tunnel_method" : {
      "tunnel_method" : "NO_TUNNEL"},
    "username" : "sourceuser"
  }
}
```
**Airbyte credential file example:**
```json
{
  "credentialName" : "airbyte_local_creds",
  "credentialType" : "airbyte_creds",
  "credentialJson" : {
    "apiHost" : "your.airbyte.host",
    "apiPort" : "80",
    "apiScheme" : "https"
  }
}
```

#### Scenarios
The scenario config describes a list of actions, their sequence, and used instances. It doesn't contain any credentials for used instances. So, we can reuse the same scenario for different Airbyte instances, sources, and destinations. As there is no sensitive data, we can store it as part of the application.

You can find scenario config file structure in the [ScenarioConfig.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfig.yaml) file. (Sub files: [ScenarioConfigAction.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfigAction.yaml), [ScenarioConfigInstance.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfigInstance.yaml))

_Note! The instance name is a way to use the same instance in different actions_

**Scenario config example:**
```json
{
  "scenarioName" : "Simple sync scenario",
  "usedInstances" : [
    {
      "instanceName" : "airbyte_1",
      "instanceType" : "AIRBYTE"
    },
    {
      "instanceName" : "source_1",
      "instanceType" : "SOURCE"
    },
    {
      "instanceName": "destination_1",
      "instanceType": "DESTINATION"
    },
    {
      "instanceName": "connection_1",
      "instanceType": "CONNECTION"
    }
  ],
  "preparationActions" : [
    {
      "action" : "CONNECT_AIRBYTE_API",
      "resultInstance" : "airbyte_1"
    },
    {
      "action" : "CREATE_SOURCE",
      "requiredInstances" : ["airbyte_1"],
      "resultInstance" : "source_1"
    },
    {
      "action": "CREATE_DESTINATION",
      "requiredInstances" : ["airbyte_1"],
      "resultInstance": "destination_1"
    },
    {
      "action" : "CREATE_CONNECTION",
      "requiredInstances" : ["airbyte_1", "source_1", "destination_1"],
      "resultInstance" : "connection_1"
    }
  ],
  "scenarioActions" : [
    {
      "action" : "SYNC_CONNECTION",
      "requiredInstances" : ["airbyte_1", "connection_1"]
    }
  ]
}
```

## How to run
The tool is on the very early state and can be executed with local hardcoded credential files.

1. Clone the repository
2. Build the project
3. Create three files with credential configs (`airbyte_creds.json`, `destination_creds.json`, `source_creds.json`) in the folder `secrets`.

     ![image](https://user-images.githubusercontent.com/1310940/185700802-b6a75916-efdf-4b22-bafb-19fc23f0c9d7.png)

4. Run `TestingTool::main`

_Note! By default, you run it using the hardcoded configuration!_

## How to run /help for a scenario
You can get generated doc for a scenario by passing specific arguments to the main method `TestingTool::main`.

1. Clone the repository
2. Build the project
3. Create the `Run/Debug` configuration with argument line `/help name="Simple sync scenario"` (For getting extended help use `/help-full` instead of `/help`)

     ![image](https://user-images.githubusercontent.com/30464745/186178683-55c29578-44c4-47fb-b4d2-5e2b4da99149.png)

4. Run the configuration

The result you can find in the execution log.
![Screenshot from 2022-08-23 17-04-44](https://user-images.githubusercontent.com/30464745/186179197-68b8c932-c483-4da1-9e0f-91169c335a8d.png)

_Note! The help text is formatted for GitHub. You can put it there to get a more readable view._
![Screenshot from 2022-08-23 17-05-03](https://user-images.githubusercontent.com/30464745/186179206-a8193142-5278-434b-8ddd-d7bc666725b3.png)

## CHANGELOG

| Version | Description                                                                            |
|---------|----------------------------------------------------------------------------------------|
| 0.1.1   | Implement HelpService. Now you can get short description with examples for a scenario. |
| 0.1.0   | Stage 1. POC                                                                           |
