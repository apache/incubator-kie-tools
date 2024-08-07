<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

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

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
