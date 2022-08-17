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
  "scenarioName" : "Poc Scenario",
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
3. Create three files with credential configs (`airbyte_local_creds.json`, `postgres_test_dest_creds.json`, `postgres_test_source_creds.json`) in the folder `secrets`.

     ![image](https://user-images.githubusercontent.com/30464745/185108683-8aeb3adb-5a5d-4560-8ed4-8ea6bbf2fb28.png)

4. Run `TestingTool.main`


## CHANGELOG

| Version | Description   |
|---------|---------------|
| 0.1.0   | Stage 1. POC  |
