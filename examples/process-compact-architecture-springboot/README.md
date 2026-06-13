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

# Process Compact Architecture: Hiring (Spring Boot)

This example is the **Spring Boot** counterpart of the [process-compact-architecture](../process-compact-architecture) Quarkus
example. It showcases the same **Hiring** process that drives a _Candidate_ through different interviews until they get hired,
featuring User Task orchestration, a DMN decision to generate the candidate's offer, and timers to skip interviews.

Like its Quarkus sibling, this example uses the **Compact Architecture**: Data-Index, Jobs Service, Data-Audit, and the process
Runtime are all collocated inside a single Spring Boot application, removing the need of events (Kafka/HTTP) between them. This
is achieved with the following Spring Boot addons:

- `org.kie:kogito-addons-springboot-data-index-jpa`: enables the Runtime persisting directly into the Data-Index database and
  serving the Data-Index GraphQL endpoint (`/graphql`) from the same application.
- `org.kie:kogito-addons-springboot-embedded-jobs-jpa`: enables collocating the Jobs Service inside the Runtime.
- `org.kie:kogito-addons-springboot-data-audit-jpa`: enables collocating the Data-Audit service inside the Runtime.

For the full documentation of the process, the DMN decision, and the Java models — which are identical in both examples — please
refer to the [Quarkus example README](../process-compact-architecture/README.md).

## The jBPM Dev Console

In development mode this example also includes the **jBPM Dev Console**
([`jbpm-addons-springboot-dev-console`](../../packages/jbpm-addons-springboot-dev-console)), the Spring Boot counterpart of the
jBPM Quarkus Dev UI. It serves the process development console (Process Instances, Jobs, Tasks and Forms) at:

```
http://localhost:8080/jbpm-dev-console/
```

The console is only added by the `development` Maven profile and only enabled by the `dev` Spring profile
(`jbpm.dev-console.enabled=true` in [application-dev.properties](src/main/resources/application-dev.properties)), so it never
ships in a production build.

## Running

### Prerequisites

- Java 17+ installed
- Environment variable `JAVA_HOME` set accordingly
- Maven 3.9.6+ installed
- Docker and Docker Compose (only for the PostgreSQL-backed mode)

### Development mode (H2 in-memory, with Dev Console)

```bash
mvn clean spring-boot:run -DskipTests -Pdevelopment
```

or, in the context of the kie-tools monorepo:

```bash
pnpm -F @kie-tools-examples/process-compact-architecture-springboot start
```

Then open the Dev Console at http://localhost:8080/jbpm-dev-console/.

### Example mode (PostgreSQL via Docker Compose)

Starts a PostgreSQL instance (and pgAdmin at http://localhost:8055) and runs the application against it:

```bash
docker compose --profile example up -d
mvn clean spring-boot:run -DskipTests
```

or:

```bash
pnpm -F @kie-tools-examples/process-compact-architecture-springboot startExample
```

To stop the containers:

```bash
docker compose --profile example down
```

## Using

Start a new Hiring process instance:

```bash
curl -X POST 'http://localhost:8080/hiring' \
  -H 'Content-Type: application/json' \
  -d '{
        "candidateData": {
          "name": "Jon",
          "lastName": "Snow",
          "email": "jsnow@example.com",
          "experience": 5,
          "skills": ["Java", "Kogito", "Fencing"]
        }
      }'
```

The new instance — and its **HR Interview** and **IT Interview** user tasks — can then be inspected and worked on in the
Dev Console (development mode), or queried through the Data-Index GraphQL endpoint:

```bash
curl -X POST 'http://localhost:8080/graphql' \
  -H 'Content-Type: application/json' \
  -d '{"query": "{ ProcessInstances { id processName state } }"}'
```

Other useful endpoints:

- Swagger UI: http://localhost:8080/swagger-ui.html
- GraphiQL UI: http://localhost:8080/graphiql

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
