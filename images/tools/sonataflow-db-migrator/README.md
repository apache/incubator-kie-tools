# sonataflow-db-migrator

This is a quarkus postgres database migrator application for Sonataflow Data Index and Jobs Service applications.

## Running the application in dev mode
Though you can run the application locally in dev mode but it is advisable to run this application as a container image as described in the next section. 
The primary reason not to run as standalone application in dev mode, is that by default there are no DDL migration files included in the source. 
However the DDL files are dynamically included from its respective sources when a container image is created.

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

## Build and Run container image locally
You can build the cekit container image by using the provided image builder shell script
```shell
./build-container-image.sh
```
Ensure the script completes without errors.
If you may have a cekit specific Python virtual environment, be sure to activate it, so the script can find the cekit command.
```shell
virtualenv ~/cekit
source ~/cekit/bin/activate;
```

Assuming you have a Postgres database running locally, e.g., a `di` database for data index and a `js` database for jobs service, you can run the image with the following command. Substitute appropriate values:
```shell
podman run \ 
--env MIGRATE_DB_DATAINDEX=true \
--env QUARKUS_DATASOURCE_DATAINDEX_JDBC_URL=<data-index-db-url e.g. jdbc:postgresql://host.docker.internal:5432/di> \
--env QUARKUS_DATASOURCE_DATAINDEX_USERNAME=<data-index-db-user> \ 
--env QUARKUS_DATASOURCE_DATAINDEX_PASSWORD=<data-index-db-password> \
--env QUARKUS_FLYWAY_DATAINDEX_SCHEMAS=dataindex \
--env MIGRATE_DB_JOBSSERVICE=true \
--env QUARKUS_DATASOURCE_JOBSSERVICE_JDBC_URL=<jobs-service-db-url e.g. jdbc:postgresql://host.docker.internal:5432/js> \
--env QUARKUS_DATASOURCE_JOBSSERVICE_USERNAME=<jobs-service-db-user> \
--env QUARKUS_DATASOURCE_JOBSSERVICE_PASSWORD=<jobs-service-db-password> \
--env QUARKUS_FLYWAY_JOBSSERVICE_SCHEMAS=jobsservice \
docker.io/apache/incubator-kie-kogito-service-db-migration-postgresql:999-SNAPSHOT
```

### Environment variables
| NAME  | DESCRIPTION  | DEFAULT |
|---|---|---|
| MIGRATE_DB_DATAINDEX  |  Set to true if you want to migrate data index database, set to false otherwise | false |
| QUARKUS_DATASOURCE_DATAINDEX_JDBC_URL  | Data index database url  e.g. jdbc:postgresql://host.docker.internal:5432/di|  jdbc:postgresql://localhost:5432/postgres |
| QUARKUS_DATASOURCE_DATAINDEX_USERNAME  | Data index database username|  postgres |
| QUARKUS_DATASOURCE_DATAINDEX_PASSWORD  | Data index database password|  postgres |
| QUARKUS_FLYWAY_DATAINDEX_SCHEMAS  | Data index database schema|  dataindex |
| MIGRATE_DB_JOBSSERVICE  |  Set to true if you want to migrate jobs service database, set to false otherwise | false |
| QUARKUS_DATASOURCE_JOBSSERVICE_JDBC_URL  | Jobs service database url  e.g. jdbc:postgresql://host.docker.internal:5432/js|  jdbc:postgresql://localhost:5432/postgres |
| QUARKUS_DATASOURCE_JOBSSERVICE_USERNAME  | Jobs service database username|  postgres |
| QUARKUS_DATASOURCE_JOBSSERVICE_PASSWORD  | Jobs service database password|  postgres |
| QUARKUS_FLYWAY_JOBSSERVICE_SCHEMAS  | Jobs service database schema|  jobsservice |