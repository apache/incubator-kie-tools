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

# jBPM Spring Boot Dev Console

This package provides a Spring Boot counterpart of the [jBPM Quarkus Dev UI extension](../jbpm-quarkus-devui). It embeds the
same process Dev UI webapp ([`@kie-tools/runtime-tools-process-dev-ui-webapp`](../runtime-tools-process-dev-ui-webapp)) into a
plain jar with a Spring Boot auto-configuration, so jBPM applications running on Spring Boot get the same development console
that Quarkus users get via the Quarkus Dev UI: Process Instances, Jobs, Tasks and Forms.

Unlike Quarkus, Spring Boot has no Dev UI framework to plug into, so the console is served by the application itself on a
dedicated path (default: `/jbpm-dev-console`) and must be explicitly enabled.

## Usage

Add the dependency to a jBPM Spring Boot application (an application using `org.jbpm:jbpm-spring-boot-starter`):

```xml
<dependency>
  <groupId>org.jbpm</groupId>
  <artifactId>jbpm-addons-springboot-dev-console</artifactId>
</dependency>
```

Enable it in `application.properties`:

```properties
jbpm.dev-console.enabled=true
```

Then open `http://localhost:8080/jbpm-dev-console/`.

The console is a **development tool**: it has no authentication of its own and the Forms editor writes form updates back to the
project sources. Do not enable it in production. A good practice is to only set `jbpm.dev-console.enabled=true` in a `dev` Spring
profile.

For the console to be fully functional the application needs:

- A Data Index with its GraphQL endpoint, e.g. the embedded `org.kie:kogito-addons-springboot-data-index-jpa` (serves `/graphql`
  in-process) or an external Data Index service configured via `kogito.data-index.url`.
- `org.kie:kie-addons-springboot-process-management` for process instance management operations (abort, retry, node instances...).
- `org.jbpm:jbpm-addons-springboot-task-management` for task operations.
- Optionally `org.kie:kie-addons-springboot-process-svg` for the process diagram panel and
  `org.kie:kie-addons-springboot-source-files` for the source panel.
- Optionally `org.kie:kogito-addons-springboot-embedded-jobs` (plus `kogito-addons-springboot-embedded-jobs-jpa` for storage)
  for an in-process Jobs Service.

See the [dev application](./dev) for a minimal setup, or the
[process-compact-architecture-springboot](../../examples/process-compact-architecture-springboot) example for a complete
Compact Architecture application that enables this console in its `development` profile.

## Configuration

| Property                             | Default                          | Description                                                                                                                   |
| ------------------------------------ | -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `jbpm.dev-console.enabled`           | `false`                          | Enables the console. It is disabled by default for safety.                                                                    |
| `jbpm.dev-console.path`              | `/jbpm-dev-console`              | Base path where the console is served.                                                                                        |
| `jbpm.dev-console.users.<id>.groups` | -                                | Users (and their groups) available in the console's user switcher, e.g. `jbpm.dev-console.users.jdoe.groups=admin,HR`.        |
| `jbpm.dev-console.forms.folder`      | auto-detected                    | Filesystem folder where form updates are persisted. Defaults to `src/main/resources/custom-forms-dev` of the running project. |
| `kogito.service.url`                 | `http://localhost:<server.port>` | Origin of the running application, used by the webapp to call the management REST endpoints.                                  |
| `kogito.data-index.url`              | `kogito.service.url`             | Base URL of the Data Index. The console calls `<url>/graphql`.                                                                |

## Custom forms

Just like the Quarkus Dev UI, custom forms live in `src/main/resources/custom-forms-dev`. A form is a pair of files named after
the process (`<processId>`) or task (`<processId>_<taskName>`):

- `<name>.html` or `<name>.tsx` — the form source.
- `<name>.config` — a JSON file with the form schema and external script/style resources.

The Forms page of the console lists them, previews them, and lets you edit them; edits are written back to the project sources.

## Build

```bash
pnpm -F @kie-tools/jbpm-addons-springboot-dev-console... build:dev
```

## Run the example

```bash
pnpm -F @kie-tools/jbpm-addons-springboot-dev-console start
```

Then open `http://localhost:8080/jbpm-dev-console/`. Start a process instance with:

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"candidate": "Jon Snow", "skills": "Java, Kogito", "experience": 5}' \
  http://localhost:8080/hiring
```

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator.

Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process have
stabilized in a manner consistent with other successful ASF projects.

While incubation status is not necessarily a reflection of the completeness
or stability of the code, it does indicate that the project has yet to be
fully endorsed by the ASF.

Some of the incubating project's releases may not be fully compliant with ASF
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
