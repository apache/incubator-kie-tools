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

# Process user task orchestration

## Description

A quickstart project shows very typical user task orchestration. It comes with two tasks assigned
to human actors via groups assignments - `managers`. So essentially anyone who is a member of that
group can act on the tasks. Though this example applies four eye principle which essentially means
that user who approved first task cannot approve second one. So there must be always at least two
distinct manager involved.

This example shows

- working with user tasks
- four eye principle with user tasks

<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

- Diagram Properties (top)
<p align="center"><img src="docs/images/diagramProperties.png"></p>

- Diagram Properties (bottom)
<p align="center"><img src="docs/images/diagramProperties3.png"></p>

- First Line Approval (top)
<p align="center"><img src="docs/images/firstLineApprovalUserTask.png"></p>

- First Line Approval (bottom)
<p align="center"><img src="docs/images/firstLineApprovalUserTask2.png"></p>

- First Line Approval (Assignments)
<p align="center"><img src="docs/images/firstLineApprovalUserTaskAssignments.png"></p>

- Second Line Approval
<p align="center"><img src="docs/images/secondLineApprovalUserTask.png"></p>

- Second Line Approval (Assignments)
<p align="center"><img src="docs/images/secondLineApprovalUserTaskAssignments.png"></p>

## Build and run

### Prerequisites

You will need:

- Java 17+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.6+ installed

When using native image compilation, you will also need:

- GraalVM 19.3+ installed
- Environment variable GRAALVM_HOME set accordingly
- GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```sh
mvn clean compile quarkus:dev
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

### Package and Run in JVM mode

```sh
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

or on windows

```sh
mvn clean package
java -jar target\quarkus-app\quarkus-run.jar
```

### Package and Run using Local Native Image

Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```sh
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```sh
./target/process-usertasks-quarkus-runner
```

### OpenAPI (Swagger) documentation

[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/openapi?format=json) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

When running in either Quarkus Development or Native mode, we also leverage the [Quarkus OpenAPI extension](https://quarkus.io/guides/openapi-swaggerui#use-swagger-ui-for-development) that exposes [Swagger UI](http://localhost:8080/swagger-ui/) that you can use to look at available REST endpoints and send test requests.

### Submit a request to start new approval

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/approvals` with following content

```json
{
  "traveller": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "jon.doe@example.com",
    "nationality": "American",
    "address": {
      "street": "main street",
      "city": "Boston",
      "zipCode": "10005",
      "country": "US"
    }
  }
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/approvals
```

### Show active approvals

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/approvals
```

### Show tasks

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=admin&group=managers'
```

where `{uuid}` is the id of the given approval instance

### Complete first line approval task

```sh
curl -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/firstLineApproval/{tuuid}?user=admin&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance

### Show tasks

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=admin&group=managers'
```

where `{uuid}` is the id of the given approval instance

This should return empty response as the admin user was the first approver and by that can't be assigned to another one.

Repeating the request with another user will return task

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=john&group=managers'
```

### Complete second line approval task

```sh
curl -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/secondLineApproval/{tuuid}?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance

This completes the approval and returns approvals model where both approvals of first and second line can be found,
plus the approver who made the first one.

```json
{
  "approver": "admin",
  "firstLineApproval": true,
  "id": "2eeafa82-d631-4554-8d8e-46614cbe3bdf",
  "secondLineApproval": true,
  "traveller": {
    "address": {
      "city": "Boston",
      "country": "US",
      "street": "main street",
      "zipCode": "10005"
    },
    "email": "jon.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "nationality": "American"
  }
}
```

You should see a similar message after performing the second line approval after the curl command

```json
{
  "id": "f498de73-e02d-4829-905e-2f768479a4f1",
  "approver": "admin",
  "firstLineApproval": true,
  "secondLineApproval": true,
  "traveller": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "jon.doe@example.com",
    "nationality": "American",
    "address": { "street": "main street", "city": "Boston", "zipCode": "10005", "country": "US" }
  }
}
```

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
