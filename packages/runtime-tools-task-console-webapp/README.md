# runtime-tools-task-console-webapp

## Working with Task Console features

The task console shows a list of user tasks which are available for a process. Each column contains detailed information about the user task which are - _Name_, _Process_, _Priority_, _Status_, _Started_ and _Last update_. The columns are sortable.

![Task Console](./docs/taskconsole.png?raw=true "TaskConsole")

The task console consist of filters, which can be used to narrow down the search on a user task. There are two filters available

- A filter based on the status(dropdown)
- A filter based on the task name(text search)

![Filters](./docs/filters.png?raw=true "Filters")

The _Status_ filter can be dropped down to view the and select the states available

![States](./docs/states.png?raw=true "States")

A _refresh_ button is available to refresh the list of user tasks

A _Reset to default_ button is available to reset the filters to its initial state.

The user task list also supports pagination.

Clicking on the name of the user task will navigate to another screen, which consist of the auto generated forms.

### Task details

The task details page consist of an auto generated forms and buttons to perform corresponding action on the user tasks.

![Forms](./docs/forms.png?raw=true "Forms")

The task details page also contains a _View details_ button, to view more details about the task.

![Details](./docs/details.png?raw=true "Details")

## Enabling Keycloak security

### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v ./dev/config/kogito-realm.json:/tmp/kogito-realm.json -p 8280:8080 quay.io/keycloak/keycloak:legacy
```

You should be able to access your Keycloak Server at [localhost:8280/auth](http://localhost:8280)
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console.
Username should be admin and password admin.

The following are the users available in keycloak

| Login | Password | Roles               |
| ----- | -------- | ------------------- |
| admin | admin    | _admin_, _managers_ |
| alice | alice    | _user_              |
| jdoe  | jdoe     | _managers_          |

To change any of this client configuration access to http://localhost:8280/auth/admin/master/console/#/realms/kogito.

### Changing configs

Enable `PROD` env mode by running the app with:

`RUNTIME_TOOLS_TASK_CONSOLE_WEBAPP__kogitoEnvMode=PROD pnpm dev`
