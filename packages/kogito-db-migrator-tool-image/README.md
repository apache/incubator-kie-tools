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

# Kogito DB Migrator Tool Image

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for Kogito DB Migrator Tool, which currently supports PostgreSQL database. Future versions are expected to support more database variants. Details about the Kogito DB Migrator Tool can be found [here](../kogito-db-migrator-tool/README.md)

## Additional requirements

- docker

## Run

- Start up a clean container with:

  ```bash
    docker run docker.io/apache/kie-kogito-db-migrator-tool:main
  ```

## Customization

1. Run a container with custom environment variables:

| NAME                                    | DESCRIPTION                                                                      | DEFAULT                                   |
| --------------------------------------- | -------------------------------------------------------------------------------- | ----------------------------------------- |
| MIGRATE_DB_DATAINDEX                    | Set to true if you want to migrate data index database, set to false otherwise   | false                                     |
| QUARKUS_DATASOURCE_DATAINDEX_JDBC_URL   | Data index database url e.g. jdbc:postgresql://host.docker.internal:5432/di      | jdbc:postgresql://localhost:5432/postgres |
| QUARKUS_DATASOURCE_DATAINDEX_USERNAME   | Data index database username                                                     | postgres                                  |
| QUARKUS_DATASOURCE_DATAINDEX_PASSWORD   | Data index database password                                                     | postgres                                  |
| QUARKUS_FLYWAY_DATAINDEX_SCHEMAS        | Data index database schema                                                       | data-index-service                        |
| MIGRATE_DB_JOBSSERVICE                  | Set to true if you want to migrate jobs service database, set to false otherwise | false                                     |
| QUARKUS_DATASOURCE_JOBSSERVICE_JDBC_URL | Jobs service database url e.g. jdbc:postgresql://host.docker.internal:5432/js    | jdbc:postgresql://localhost:5432/postgres |
| QUARKUS_DATASOURCE_JOBSSERVICE_USERNAME | Jobs service database username                                                   | postgres                                  |
| QUARKUS_DATASOURCE_JOBSSERVICE_PASSWORD | Jobs service database password                                                   | postgres                                  |
| QUARKUS_FLYWAY_JOBSSERVICE_SCHEMAS      | Jobs service database schema                                                     | jobs-service                              |

### Example

An example to use diverse environment variables

```bash
   docker run \
   --env MIGRATE_DB_DATAINDEX=true \
   --env QUARKUS_DATASOURCE_DATAINDEX_JDBC_URL=<data-index-db-url e.g. jdbc:postgresql://host.docker.internal:5432/di> \
   --env QUARKUS_DATASOURCE_DATAINDEX_USERNAME=<data-index-db-user> \
   --env QUARKUS_DATASOURCE_DATAINDEX_PASSWORD=<data-index-db-password> \
   --env QUARKUS_FLYWAY_DATAINDEX_SCHEMAS=data-index-service \
   --env MIGRATE_DB_JOBSSERVICE=true \
   --env QUARKUS_DATASOURCE_JOBSSERVICE_JDBC_URL=<jobs-service-db-url e.g. jdbc:postgresql://host.docker.internal:5432/js> \
   --env QUARKUS_DATASOURCE_JOBSSERVICE_USERNAME=<jobs-service-db-user> \
   --env QUARKUS_DATASOURCE_JOBSSERVICE_PASSWORD=<jobs-service-db-password> \
   --env QUARKUS_FLYWAY_JOBSSERVICE_SCHEMAS=jobs-service \
   docker.io/apache/kie-kogito-db-migrator-tool:main
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
