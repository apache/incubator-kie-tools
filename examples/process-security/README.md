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

# Process Security Example

This example shows the security features that come with Apache KIE. It shows how to get an access token and how to use it to interact with the Apache KIE business services. How to configure your application, and at least, it exemplifies how to use multiple IDPs in Management Console.

## Enabling OpenID Connect (OIDC)

To enable OIDC we must configure our project by adding properties to the `src/main/resources/application.properties` file.

<!-- TODO: explain properties -->

```
# Enabling OIDC
quarkus.oidc.enabled=true
quarkus.oidc.auth-server-url=http://127.0.0.1:8180/realms/kie
quarkus.oidc.discovery-enabled=true
quarkus.oidc.tenant-enabled=true
quarkus.oidc.client-id=kie-app
quarkus.oidc.credentials.secret=secret
quarkus.oidc.application-type=service
quarkus.http.auth.permission.authenticated.paths=/*
quarkus.http.auth.permission.authenticated.policy=authenticated
quarkus.http.auth.permission.public.paths=/q/*,/docs/*
quarkus.http.auth.permission.public.policy=permit
```

## Infrastructure requirements

To help bootstrapping the Infrastructure Services, the example provides a `docker-compose.yml` file. This example will start two Keycloak services, one in port `8180` that is used by `kie-service-1` and another in `8280` used by `kie-service-2`, and two ways of running the example application are provided. In development ("development") mode, the user can start the Keycloak services using `docker-compose` and must run the Apache KIE business services manually. In "container" mode the `docker-compose` file will start the Keycloak services, a PostgreSQL service, a pgAdmin instance, and the Apache KIE business services, requiring the project to be compiled first to generate the services container images. To use `docker-compose` we must first create a `.env` file in the example root, and it should have the following variables:

```
PROJECT_VERSION=0.0.0
MANAGEMENT_CONSOLE_IMAGE=docker.io/apache/incubator-kie-kogito-management-console:main
COMPOSE_PROFILES=container
HOST=127.0.0.1
```

- `PROJECT_VERSION`: Should be set with the current version being used: `PROJECT_VERSION=0.0.0`
- `MANAGEMENT_CONSOLE_IMAGE`: Should be set with the Kogito Management Console image `docker.io/apache/incubator-kie-kogito-management-console:main`
- `COMPOSE_PROFILES`: filters which services will run.
- `HOST`: The host used to communicate between the Management Console and the business service. For Windows Subsystem for Linux (WSL) users, use `localhost`.

### Development mode

For development mode, the `.env` must have the `COMPOSE_PROFILES=development`:

```
PROJECT_VERSION=0.0.0
MANAGEMENT_CONSOLE_IMAGE=docker.io/apache/incubator-kie-kogito-management-console:main
COMPOSE_PROFILES=development
HOST=127.0.0.1
```

### JVM mode

For JVM mode, the `.env` must have the `COMPOSE_PROFILES=jvm`:

```
PROJECT_VERSION=0.0.0
MANAGEMENT_CONSOLE_IMAGE=docker.io/apache/incubator-kie-kogito-management-console:main
COMPOSE_PROFILES=jvm
HOST=127.0.0.1
```

### Container mode

For container mode, the `.env` must have the `COMPOSE_PROFILES=container`:

```
PROJECT_VERSION=0.0.0
MANAGEMENT_CONSOLE_IMAGE=docker.io/apache/incubator-kie-kogito-management-console:main
COMPOSE_PROFILES=container
HOST=127.0.0.1
```

> NOTE: Integrating the Apache KIE business service with the Keycloak instances requires running the docker compose with the [Host network](https://docs.docker.com/engine/network/drivers/host/). This is only necessary for this example, where everything is running on the same host. In production environments each application should have their own domain/host.

### Handling services

To start the services use the command above:

```bash
docker compose up
```

To stop the services you can hit `CTRL/CMD + C` in your terminal, and to clean up perform the command above:

```bash
docker compose down
```

## Running

### Prerequisites

- Java 17 installed
- Environment variable `JAVA_HOME` set accordingly
- Maven 3.9.6 installed
- Docker and Docker Compose to run the required example infrastructure.

### Compile and Run in local development mode

First, start the Keycloak services (["Infrastructure requirements/Development mode"](#development-mode)), and then start the Apache KIE business services in development mode. To do so, open three new terminals, access the service folders by using the `cd <project_path>` (kie-service-1/kie-service-2/kie-service-3) and run the command below on each terminal:

```sh
mvn clean package quarkus:dev
```

The **kie-service-1** will run in the port `8081`, **kie-service-2** on `8082` and **kie-service-3** on `8083`.

NOTE: With the dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables, and Java code. No need to redeploy or restart your running application.

NOTE: Adding the `development` profile is optional, which enables the jBPM Dev UI. To do so add the `-Pdevelopment` at the end of the command.

### Compile and Run in local JVM mode

Start the Keycloak services (["Infrastructure requirements/JVM mode"](#jvm-mode)), and then open three new terminals to start the Apache KIE business services with the following commands on each terminal:

```sh
# Starts the kie-service-1
java -jar kie-service-1/target/quarkus-app/quarkus-run.jar

# Starts the kie-service-2
java -jar kie-service-2/target/quarkus-app/quarkus-run.jar

# Starts the kie-service-3
java -jar kie-service-3/target/quarkus-app/quarkus-run.jar
```

or on Windows:

```sh
# Starts the kie-service-1
java -jar kie-service-1\target\quarkus-app\quarkus-run.jar

# Starts the kie-service-2
java -jar kie-service-2\target\quarkus-app\quarkus-run.jar

# Starts the kie-service-3
java -jar kie-service-3\target\quarkus-app\quarkus-run.jar
```

### Compile and Run using Docker compose

To run all services using Docker compose build the example using the "container" profile:

```sh
mvn clean package -Pcontainer
```

After that, start all services (["Infrastructure requirements/Container mode"](#container-mode)) by running:

```sh
docker compose up
```

### Important Note on Resource Usage

This example relies on multiple services running via Docker Compose, including Keycloak, PostgreSQL, pgAdmin, and other kie services . Running all these services simultaneously _requires a significant amount of system memory_. If you experience frequent container restarts or failures without logs, it may indicate that system is running out of memory.

**Recommendations**:

- Increase Docker's allocated memory for your Docker VM based on system configuration.
- Close unnecessary applications to free up system resources before running the example.

## Using

### Access Token

Once all services are up and running you can authenticate the `kie-service-1` using the Keycloak available on port 8180. First, we get the access token and set the `access_token_1` variable using `jq` to shorten our next requests, but this isn't required. For this request, we use the `jdoe` user:

```sh
export access_token_1=$(\
    curl -X POST http://127.0.0.1:8180/realms/kie/protocol/openid-connect/token \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d "client_id=kie-app" \
    -d "client_secret=secret" \
    -d "grant_type=password" \
    -d "username=jdoe" \
    -d "password=jdoe" \
    -d "scope=openid" | jq --raw-output '.access_token' \
 )
```

On this request, we have some important things to notice. The request is made to the `127.0.0.1` IP (`localhost`) as all our services are running on the local machine. Now, we can't use `localhost` directly because the Apache KIE business service expects a token that was obtained from the exact same URL in the `quarkus.oidc.auth-server-url` property from `application.properties`. In this case, we choose to use the IP instead of `localhost` to the request be compatible with both environments ("development" and "container"), as `localhost` can't be used in the `docker compose` as each container has its own network.

The request uses the Keycloak `kie` realm to get the token, passing the `client-id` (kie-app) and `secret` (secret). All these configurations are available in the `docker-compose/keycloak-realm-1/kie.json`

To get the token for the `kie-service-2` we must make a request to Keycloak on port 8280. For this, we will set the `access_token_2` variable, and we will use `jane` user.

```sh
export access_token_2=$(\
    curl -X POST http://127.0.0.1:8280/realms/kie/protocol/openid-connect/token \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d "client_id=kie-app" \
    -d "client_secret=secret" \
    -d "grant_type=password" \
    -d "username=jane" \
    -d "password=jane" \
    -d "scope=openid" | jq --raw-output '.access_token' \
 )
```

It's possible to get the user information by making a request to Keycloak, in this case we're sending to the one on port 8180:

```sh
curl -X GET "http://127.0.0.1:8180/realms/kie/protocol/openid-connect/userinfo" \
-H "Authorization: Bearer $access_token_1"
```

NOTE: The access token is configured to expire in 5 minutes.

### Refresh Token

The refresh token is available in the same request of the [Access Token](#access-token), but as we filtered using `jq` we didn't see:

```sh
export refresh_token_1=$(\
    curl -X POST http://127.0.0.1:8180/realms/kie/protocol/openid-connect/token \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d "client_id=kie-app" \
    -d "client_secret=secret" \
    -d "grant_type=password" \
    -d "username=jdoe" \
    -d "password=jdoe" \
    -d "scope=openid" | jq --raw-output '.refresh_token' \
 )
```

NOTE: The refresh token is configured to expire in 30 minutes.

With the refresh token in hands, we can get a new access token with the request above:

```sh
curl -X POST "http://127.0.0.1:8180/realms/kie/protocol/openid-connect/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=refresh_token" \
     -d "client_id=kie-app" \
     -d "client_secret=secret" \
     -d "refresh_token=$refresh_token_1"
```

NOTE: All requests can be changed to port 8280 and user `jane` with password `jane`.

### Requests to the Apache KIE business service

With the access token in hands, you have access to the entire Apache KIE business service API, you just need to pass the `Authorization` header. To get the processes:

```sh
curl -X GET -H "Authorization: Bearer $access_token_1" http://localhost:8081/hiring
```

Or to create a new process:

```sh
curl -X POST "http://localhost:8081/hiring" \
     -H "Content-Type:application/json" \
     -H "accept: application/json" \
     -H "Authorization: Bearer $access_token_1" \
     -d '{"candidateData": { "name": "Jon", "lastName": "Snow", "email": "jon@snow.org", "experience": 5, "skills": ["Java", "Fencing"]}}'
```

NOTE: For debbuging purposes, you can add the `--dump-header - ` to the `curl` command: `curl --dump-header - -X GET ...`

### Connecting to the Apache KIE business services in the Management Console

Management Console is only available in container mode. So if you would like to acess Management Console, make sure you are running this example in [container mode](#compile-and-run-using-docker-compose). After that, open the Management Console in http://localhost:8380 and click on the `+ Connect to a runtime…` button and fill in the required information on the
modal:

- **Alias**: The name to give your connected runtime instance (can be anything that helps you identify it).
- **URL**: The runtime root URL (E.g., http://localhost:8080)

#### kie-service-1

- Alias: `kie-service-1`
- URL: `http://localhost:8081`

Login with user `jdoe` and password `jdoe`.

### kie-service-2

- Alias: `kie-service-2`
- URL: `http://localhost:8082`

Login with user `jane` and password `jane`.

### kie-service-3

- Alias: `kie-service-3`
- URL: `http://localhost:8083`

No login required.

If your runtime uses OpenID Connect authentication, you should be redirected to the Identity Provider
(IdP) login page or, if you’re already logged in, redirected back to the Management Console. If your
runtime is unsecured, it should connect directly.

Once logged in, the management pages will be displayed in the side menu, listing Process Instances,
Jobs, and Tasks.

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
