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

# Apache KIE™ Management Console

A web application to manage multiple Runtimes.

## Configuring your Kogito Runtimes application to support the Management Console

### Unsecured

Add this to the `application.properties` to disable authentication:

```properties
# Kogito security
kogito.security.auth.enabled=false

# Quarkus OIDC
quarkus.oidc.enabled=false
```

### Secured (OIDC authentication/authorization)

Add the `quarkus-oidc-proxy` extension to your `pom.xml` to proxy the Identity Provider:

```xml
<!-- OIDC Proxy -->
<dependency>
  <groupId>io.quarkiverse.oidc-proxy</groupId>
  <artifactId>quarkus-oidc-proxy</artifactId>
  <version>0.1.2</version>
</dependency>
```

Add this to the `application.properties` to enable authentication through an Identity Provider:

```properties
# Kogito security
kogito.security.auth.enabled=true

# Quarkus OIDC
quarkus.oidc.enabled=true
quarkus.oidc.auth-server-url=<IDENTITY_PROVIDER_URL>
quarkus.oidc.discovery-enabled=true
quarkus.oidc.tenant-enabled=true
quarkus.oidc.application-type=service
quarkus.oidc.client-id=<CLIENT_ID> # Usually a client specific to your application
quarkus.oidc.credentials.secret=<CLIENT_SECRET> # The secret configured in your Identity Provider for the client used

# Authenticated and public paths
quarkus.http.auth.permission.authenticated.paths=/*
quarkus.http.auth.permission.authenticated.policy=authenticated
quarkus.http.auth.permission.public.paths=/q/*,/docs/*
quarkus.http.auth.permission.public.policy=permit
```

---

> In both cases, if using CORS, remember to allow the Management Console origin:
>
> ```properties
> quarkus.http.cors=true
> quarkus.http.cors.origins=<MANAGEMENT_CONSOLE_ORIGIN> # Using `*` will allow all origins.
> ```

## Working with Management Console features

### Connecting to a Kogito Runtimes instance

To do so, click on the `+ Connect to a runtime…` button and fill in the required information on the
modal:

- **Alias**: The name to give your connected runtime instance (can be anything that helps you identify it).
- **URL**: The runtime root URL (E.g., http://localhost:8080).
- **Force login prompt**: Check this if you are already logged in your Identity Provider but would like to log in again (maybe with a different user).

More settings are available in the **Advanced OpenID Connect settings** section:

- **Client ID**: Overrides the Client ID used for this connection. Defaults to the value of the `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID` environment variable.
- **Scope**: Overrides the scopes requested to the Identity Provider. Useful from some Identity Providers that will only grant a Refresh Token if the `offline_access` scope is included. Defaults to `openid email profile`.
- **Audience**: This is the `audience` parameter in the Authorization request. Used to identify the service that the token is intended for. Empty by default.

If your runtime uses OpenID Connect authentication, you should be redirected to the Identity Provider
(IdP) login page or, if you’re already logged in, redirected back to the Management Console. If your
runtime is unsecured, it should connect directly.

Once logged in, the management pages will be displayed in the side menu, listing Process Instances,
Jobs, and Tasks.

#### Connecting to the local dev apps

- `secured-runtime`:

  - **Alias**: Secured Runtime
  - **URL**: http://localhost:8080/my-subpath

- `unsecured-runtime`:
  - **Alias**: Unsecured Runtime
  - **URL**: http://localhost:8081

### Process instances

The process instances page shows the list of process instances available. The list is an expandable list , which shows all the child process instance of the particular process instance when expanded. Each row in the list contains the information about the process instances. The info includes details like _name_, _status_, _endpoint_ etc... The list also provides many ways to [filter](#filters) out the process instances and also to perform [process-management](#process-management) operations to the processes. The details of them are mentioned below.

#### Filters

![Filters](./docs/filters.png?raw=true "Filters")

It has two ways of filtering the list:

- filter by status (a checkbox based filter)
- search by Business key (a textbox based filter)
  These two filters can be used in combination to filter the list.

##### a) Filter by status :

![Status](./docs/status.png?raw=true "Status")

There are five status in total :

- Active
- Completed
- Aborted
- Suspended
- Error

Initially the process instance list loads all the active instances available.To search using different filters, click on the dropdown with the **Status** tag and select the necessary status from the drowdown. Click on **Apply filter** to load the new list.Also there is a **chip array** below the filters to show the current filters applied. The filters for status can be removed by either by deselecting the options from the dropdown and clicking on **Apply filter** or by clicking on the '**X**' in the chip.

##### b) Filter by business key :

The business key is a business relevant identifier that might or might not be present in the process instance. The business key, if available would appear as a **blue coloured badge** near the process name in the list. We can enter the business key or parts of business key in the textbox and either click **Apply filter** or press **Enter** key to apply the filter. Similar to the Status filter, the chips with the typed keywords would appear below the textbox. The search for business key works on a _OR_ based condition. Also to remove a filter based on the keyword, click on the '**X**' in the chip to remove and the list will reload to show the applied filter. The search supports [Wild cards](https://en.wikipedia.org/wiki/Wildcard_character "Wild cards") in its keywords. For example, consider a process having a business key as _WIOO2E_. This business key can be searched by entering \*W\*_ or _\*OO\** or *WIOO2E\*.

#### Bulk Process Actions

The multi select is used to select multiple process instances while performing bulk [process-management](#process-management) actions.The multi select checkbox by default selects all the parent and child process instances(if loaded). It also has drop-down actions to select none (removes all selections), select only parent process instances, select all the process instances (both parent and child). The multi-select works in correlation with the set of buttons present as a part of the toolbar. The buttons present are - **Abort selected**, **Retry selected** and **Skip selected**.

- Clicking on the _Abort selected_ will open a box to show the instances being aborted and the instances being skipped(An instance which is already in _Completed_ or _Aborted_ cannot be Aborted again, hence the instances are skipped).
- Clicking on the _Retry selected_ or _Skip selected_ will open a box to show the instances being retriggered or skipped respectively. These actions can be performed on instances which are in _Error_ state only. Other instances(in different states), if selected will appear under the skipped list.

For all the bulk actions, if any of the instance goes throws an error while execution, they appear under the error list of the box.

In addition to these , there is a **reload** button(a sync-icon), to reload the list and a **Reset to default** button which resets all the filters to its original state(Active status and no business key filter).

> **Note :** all the filters are applied only to the parent process instances.

#### Process list

![Processlist](./docs/processlist.png?raw=true "Processlist")

The List shows details of the process instances. Initially it loads only the parent process instances. The following are the details shows in the list :

- Checkbox
- Process name
- State of the process
- when was the process created
- when was the process updated
- a kebab button to provide [process-management](#process-management) functions

The list has a toggle button on the left end, which when toggled would load all the child process instances of that parent process instance.
The child process instance also has a similar sturcture as mentioned above.

**Checkbox** - A checkbox to select the process instance to perform [process-management](#process-management) operations. Checkboxes which are disabled either do not have `kogito.service.url` missing or the `process management` capability is disabled.

**Process name** - It shows the process name of the instance along with the business key (as a blue badge) if available. If business key is absent, then it shows
the spliced process instance id(spliced to 5 chars). When hovered over the process name ,a tooltip with the full process instance id pops up.

**Status** - The process status along with their corresponding icons is shown. For instances in **Error**, a pop over shows up with the corresponding error message. Also there is a provision to either Skip or Retry the error instance.

**Process** **Created** - This shows the time elapsed from the process creation.

**Last** **Updated** - This shows the time elapsed from the last update.

**Kebab** **button** - The kebab button is enabled or disabled based on the process instance status and the contents of the kebab button varies based on the process-management capability provided to the particular instance. It consists of Abort, Skip and Retry.

#### Process management

There are currently three process management capabilities in the process instance list.

- Abort
- Skip
- Retry

##### Abort :

An instance which is either in _Active_, _Error_, _Suspended_ state can be aborted. Clicking on the abort would open up a box displaying the instance which was aborted.

#### Skip :

A node in _Error_ state can be skipped. Clicking on the skip would open up a box displaying the success or failure status of the skip operation.

#### Retry :

A node in _Error_ state can be Retriggered. Clicking on the Retry would open up a box displaying the success or failure status of the Retry operation.

### Process instance Details

![Processdetails](./docs/processdetails.png?raw=true "Processdetails")

The process details page consist of the following :

#### Heading

The heading shows the name of the process instance. The heading also shows the business key(if available) as a blue coloured badge.
In the absence of business key it shows the process instance id as a concatenated string.

#### Process management buttons

The process management buttons consist of the following

- _Abort_ - Abort button aborts the process instance. Clicking on abort button results in opening a checkbox to show the process instance which was aborted.

#### Details

The details consist of the following :

- _Name_ - shows the name of the travels
- _BusinessKey_ - shows the business key if available
- _State_ - shows the current state of the process instance
- _Id_ - shows the full unique id of the process instance
- _Endpoint_ - Shows the endpoint of the backend to which the client is connected
- _Start_ - shows the time elapased from the start of the process
- _End_ - shows the time elapased from the end of the process(if available)
- _Parent process_ - shows if the current process instance has a parent process. Allows navigation to the parent process when clicked on. Shows the unique id when hovered over.
- _Sub processes_ - shows if the current process instance has sub processes.Allows navigation to the respective child process when clicked on. Shows the unique id when hovered over

#### Timeline

The timeline shows the timeline of all the nodes of a process.It shows the state of the node(_Active_ or _Completed_ or _Error_), the name of
the node(_User Icon_ on the side for user tasks ) and a kebab button. The nodes in _error_ are shown(hovering over the error icon would sho the error message) can be _skipped_ or _retriggered_ by selection the required option from the kebab toggle.

#### Process variables

The process variables shows the domain data in _JSON_ format.

### Process Diagram

The process diagram panel contains the BPMN process diagram, which the users you to view the current progress of the process.

![Process Diagram](./docs/processdiagram.png?raw=true "ProcessDiagram")

### Jobs Panel

The Jobs panel shows the list of jobs(timer) and we can execute operations on the jobs using this panel. The available operations are - _View details_, _Reschedule_ and _Cancel_.

![Jobs Panel](./docs/jobspanel.png?raw=true "JobsPanel")

### Node Trigger Panel

The nodes of a process can be tirggered manually using this panel. It consist of a dropdown, which shows the list of triggerable nodes. Once the required node is selected, click on _Trigger_ button to trigger the node.

![Node Trigger](./docs/nodetrigger.png?raw=true "NodeTrigger")

### Milestones Panel

The milestones panel show the list of milestones present and their current states.

![Milestones](./docs/milestones.png?raw=true "Milestones")

## Tasks

The tasks panel shows a list of user tasks which are available for a process. Each column contains detailed information about the user task which are - _Name_, _Process_, _Priority_, _Status_, _Started_ and _Last update_. The columns are sortable.

![Tasks](./docs/taskconsole.png?raw=true "Tasks")

The task console consist of filters, which can be used to narrow down the search on a user task. There are two filters available

- A filter based on the status(dropdown)
- A filter based on the task name(text search)

![Filters](./docs/taskfilters.png?raw=true "Filters")

The _Status_ filter can be dropped down to view the and select the states available

![States](./docs/taskstates.png?raw=true "States")

A _refresh_ button is available to refresh the list of user tasks

A _Reset to default_ button is available to reset the filters to its initial state.

The user task list also supports pagination.

Clicking on the name of the user task will navigate to another screen, which consist of the auto generated forms.

### Task details

The task details page consist of an auto generated forms and buttons to perform corresponding action on the user tasks.

![Forms](./docs/taskforms.png?raw=true "Forms")

The task details page also contains a _View details_ button, to view more details about the task.

![Details](./docs/taskdetails.png?raw=true "Details")

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
