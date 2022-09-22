# Airbyte E2E testing tool

This stand-alone testing configurable application will help us execute different end-to-end tests and improve the Airbyte final product quality.
You can find the development state and future roadmap in this [epic issue](https://github.com/airbytehq/airbyte/issues/15152).

### Main flow

The central concept of the tool is a universal testing platform that you can easily configure by providing scenario config and required credentials.
The solution helps us run different test scenarios from the CI referring to already defined config files and cover all possible e2e test cases with
minimal effort.

![image](https://user-images.githubusercontent.com/30464745/185084724-1fa9ce8e-52d5-4b17-92a6-3ce06e8715f1.png)

#### Credentials

All credentials are independent of the selected scenario and can be used in different runs. In addition, we store credentials in the secrets storage
as we do for the integration tests.

As the credential file is input data, you can find structure in
the [CredentialConfig.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/CredentialConfig.yaml)
file.

**Source credential file example:**

```json
{
  "credentialName": "test_source_creds",
  "credentialType": "source_creds",
  "instanceType": "Postgres",
  "credentialJson": {
    "database": "test_source_database",
    "host": "put.your.host.here",
    "password": "yourPassword",
    "port": 5432,
    "replication_method": {
      "method": "Standard"
    },
    "schemas": [
      "public"
    ],
    "ssl": false,
    "ssl_mode": {
      "mode": "disable"
    },
    "tunnel_method": {
      "tunnel_method": "NO_TUNNEL"
    },
    "username": "sourceuser"
  }
}
```

**Airbyte credential file example:**

```json
{
  "credentialName": "airbyte_local_creds",
  "credentialType": "airbyte_creds",
  "credentialJson": {
    "apiHost": "your.airbyte.host",
    "apiPort": "80",
    "apiScheme": "https"
  }
}
```

#### Scenarios

The scenario config describes a list of actions, their sequence, and used instances. It doesn't contain any credentials for used instances. So, we can
reuse the same scenario for different Airbyte instances, sources, and destinations. As there is no sensitive data, we can store it as part of the
application.

You can find scenario config file structure in
the [ScenarioConfig.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfig.yaml)
file. (Sub
files: [ScenarioConfigAction.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfigAction.yaml)
, [ScenarioConfigInstance.yaml](https://github.com/airbytehq/airbyte-e2e-testing-tool/blob/master/src/main/resources/configmodels/ScenarioConfigInstance.yaml))

_Note! The instance name is a way to use the same instance in different actions_

**Scenario config example:**

```json
{
  "scenarioName": "Simple sync scenario",
  "usedInstances": [
    {
      "instanceName": "airbyte_1",
      "instanceType": "AIRBYTE"
    },
    {
      "instanceName": "source_1",
      "instanceType": "SOURCE"
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
  "preparationActions": [
    {
      "action": "CONNECT_AIRBYTE_API",
      "resultInstance": "airbyte_1"
    },
    {
      "action": "CREATE_SOURCE",
      "requiredInstances": [
        "airbyte_1"
      ],
      "resultInstance": "source_1"
    },
    {
      "action": "CREATE_DESTINATION",
      "requiredInstances": [
        "airbyte_1"
      ],
      "resultInstance": "destination_1"
    },
    {
      "action": "CREATE_CONNECTION",
      "requiredInstances": [
        "airbyte_1",
        "source_1",
        "destination_1"
      ],
      "resultInstance": "connection_1"
    }
  ],
  "scenarioActions": [
    {
      "action": "SYNC_CONNECTION",
      "requiredInstances": [
        "airbyte_1",
        "connection_1"
      ]
    }
  ]
}
```

## How to run

You have two ways how to provide required credentials to the tool. The main flow is to get credentials from the secret service, but also you can use
your local files a source for credentials.

### Get list of existing scenarios

1. Clone the repository
2. Build the project
3. Create the `Run/Debug` configuration with argument line `/list-scenarios` to get list of available scenarios
4. Run the configuration
5. Check log

The result you can find in the execution log.
![Screenshot from 2022-08-30 12-58-25](https://user-images.githubusercontent.com/30464745/187408450-da041b4d-7390-4965-820c-897c048cae27.png)

_Note! The help text is formatted for GitHub. You can put it there to get a more readable view._
![Screenshot from 2022-08-30 12-58-37](https://user-images.githubusercontent.com/30464745/187408455-314c9538-b016-423a-a341-eb53f7dcc57f.png)

### Get list of required parameters for the scenario

1. Clone the repository
2. Build the project
3. Create the `Run/Debug` configuration with argument line `/help name="Simple sync scenario"` (For getting extended help use `/help-full` instead
   of `/help`)

   ![image](https://user-images.githubusercontent.com/30464745/186178683-55c29578-44c4-47fb-b4d2-5e2b4da99149.png)

4. Run the configuration

The result you can find in the execution log.
![Screenshot from 2022-08-23 17-04-44](https://user-images.githubusercontent.com/30464745/186179197-68b8c932-c483-4da1-9e0f-91169c335a8d.png)

_Note! The help text is formatted for GitHub. You can put it there to get a more readable view._
![Screenshot from 2022-08-23 17-05-03](https://user-images.githubusercontent.com/30464745/186179206-a8193142-5278-434b-8ddd-d7bc666725b3.png)

### Run scenario with credentials from Secret service

1. Clone the repository
2. Build the project
3. Put secret service credential file in the `secrets/service_account_credentials.json` folder in the project root (You can find file structure in
   the ServiceAccountConfig.yaml)
4. Run the scenario by passing the required arguments to the method `TestingTool::main` with the command `/run-scenario`

#### Example:

![Screenshot from 2022-08-30 12-54-42](https://user-images.githubusercontent.com/30464745/187407611-1eeefdff-2417-41a7-8b8b-4467dc4f885a.png)

_Note! Credential parameters should have secret name like `tt_airbyte_dev2`_
![Screenshot from 2022-08-30 12-53-34](https://user-images.githubusercontent.com/30464745/187407350-1ea6f14f-a55f-47e7-aad9-ec1369d653ba.png)

### Run scenario with local credentials

1. Clone the repository
2. Build the project
3. Put your credential files in the `secrets` folder in the project root
4. Run the scenario by passing the required arguments to the method `TestingTool::main` with the command `/run-scenario-local`

#### Example:

![Screenshot from 2022-08-30 12-56-21](https://user-images.githubusercontent.com/30464745/187407955-7b0be9af-de38-427d-b037-c077fdf2673d.png)

_Note! Credential parameters should have file name with extension like `some_cred_file.json`_

## CHANGELOG

| Version | Description                                                                            |
|---------|----------------------------------------------------------------------------------------|
| 0.4.1   | Action structure improvement + add delete actions for conn/src/dest instances          |
| 0.4.0   | Phase 4 and 5 complete                                                                 |
| 0.3.0   | Add possibility to provide params by actions + new action ReadSourceVersion            |
| 0.2.3   | Scenario validation result is now available in help and list commands                  |
| 0.2.2   | Scenario description                                                                   |
| 0.2.1   | Add additional settings to CredentialConfig                                            |
| 0.2.0   | Run scenario using secret service. !No default test run anymore!                       |
| 0.1.3   | Run scenario using local files                                                         |
| 0.1.2   | Action parameters.                                                                     |
| 0.1.1   | Implement HelpService. Now you can get short description with examples for a scenario. |
| 0.1.0   | Stage 1. POC                                                                           |
