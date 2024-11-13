# DEPRECATED
This repository is no longer in use. 


# Airbyte E2E testing tool

This stand-alone testing configurable application will help us execute different end-to-end tests and improve the Airbyte final product quality.
You can find the development state and future roadmap in this [epic issue](https://github.com/airbytehq/airbyte/issues/15152).

#### Ways to use it:
- Automatic run before publishing a new connector version to the could - prevent versions without backward compatibility or braking change for one of the possible integrations between source and destination.
- Run scenario for a dev version in the PR.
- Run scenario locally during development.

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

## How to run in the GitHub

You can put your comment with one of the testing tool commands to any PR in [the repository](https://github.com/airbytehq/airbyte-e2e-testing-tool) to trigger the GitHub action. The action will run the testing tool using your comment message as input parameters for the main method.
The GA publish the execution result in your original comment.

### Example

1. Open some PR
2. Put the comment `/run-scenario name="Simple sync scenario" airbyte_1=tt_airbyte_dev2 source_1=tt_postgres_source_aws_1 destination_1=tt_postgres_destination_aws_1`
3. Wait till GA updates your comment with the execution results.
![Screenshot from 2022-09-29 13-23-46](https://user-images.githubusercontent.com/30464745/193007238-1c628c56-7a95-4cc8-8327-316b662972d7.png)

## How to run locally

The tool provides the possibility to run all scenarios locally. If your command requires credentials, you should provide them locally or request them from the secret service.
You have two ways how to provide the required credentials to the tool. The main flow is to get credentials from the secret service, but also you can use
your local files as a source for credentials. (See more about credentials below)

### Example: Run scenario with credentials from Secret service

1. Clone the repository
2. Build the project
3. Put the secret service credential file in the `secrets/service_account_credentials.json` folder in the project root (You can find the file structure in
   the ServiceAccountConfig.yaml)
4. Run the scenario by passing the required arguments to the method `TestingTool::main` with the command `/run-scenario`
![Screenshot from 2022-08-30 12-54-42](https://user-images.githubusercontent.com/30464745/187407611-1eeefdff-2417-41a7-8b8b-4467dc4f885a.png)

The result you can find in the execution log.
![Screenshot from 2022-09-29 13-41-04](https://user-images.githubusercontent.com/30464745/193010808-318603ad-066d-420e-b964-44222d27b3c1.png)
_Note! The help text is formatted for GitHub. You can put it there to get a more readable view._
![Screenshot from 2022-09-29 13-41-25](https://user-images.githubusercontent.com/30464745/193010811-a0737686-6fe4-4b63-ad71-d3488496efc3.png)

## How to get the list of scenarios

The tool has help command `/list-scenarios`. The command provides the full list of available scenarios and hints on how to get more details about a specific scenario.

_Note! The command also runs logical validation for all scenarios. If some scenario is unfinished or invalid, you will see :x: sing after its name._
![Screenshot from 2022-09-29 13-46-12](https://user-images.githubusercontent.com/30464745/193011727-5e8259c4-a51f-42ac-83ba-25464b67a897.png)

## How to prepare a scenario command line

Each scenario has its list of required credentials and their number. In addition, some scenarios might require parameter values in the command line.
To make this step easier, the tool has command `/help name="<scenario name>"`(How to get name see `How to get the list of scenarios). This command returns a prepared command line and a list of all credentials you should put into this line.
In the result, you can find such parts:
- Call examples - different command lines which can be useful for you. Note that you need to replace texts like `<put_credential_name>` in this line before running.

![Screenshot from 2022-09-29 13-53-41](https://user-images.githubusercontent.com/30464745/193013332-b7bc7049-415c-4b1d-85e5-ee3754b1d3ea.png)
- Instances in the scenario - List required instance credentials with their types. Use it to put the proper credential name into the command line.

![Screenshot from 2022-09-29 13-53-47](https://user-images.githubusercontent.com/30464745/193013336-f7a416ca-f1b1-4182-b908-2575af822021.png)
- Parameters in the scenario - parameter is a simple text value required inside a scenario. The name and type of the parameter should be enough to understand which value is expected from you.

![Screenshot from 2022-09-29 13-53-52](https://user-images.githubusercontent.com/30464745/193013338-2ec3acd7-3b36-427b-8a72-0cc041c6dd99.png)

In the end, you should have a command line ready for execution.
For example, in this case we can get a line like this : `/run-scenario name="Update destination version scenario" airbyte_1=tt_airbyte_docker source_1=tt_postgres_source_aws_1 destination_1=tt_postgres_destination_aws_1 old_version="0.3.22" new_version="0.3.23"`

## How to choose a credential

TBD

## CHANGELOG

| Version | Description                                                                            |
|---------|----------------------------------------------------------------------------------------|
| 0.4.4   | Add common local instances for destinations + Postgres impl                            |
| 0.4.3   | Make normalization on by default                                                       |
| 0.4.2   | Add autonomous instances                                                               |
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
