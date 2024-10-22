# kogito-db-migrator

This is a java quarkus based postgres database migrator application for Sonataflow Data Index and Jobs Service applications for use by SonataFlow Operator.

_NOTE_: This postgres database migrator application and its corresponding images are only envisaged to be made use of by SonataFlow Operator, Data Index and Jobs Service internally. Conversely this application is of no use outside the usecases involved with SonataFlow Operator, Data Index and Jobs Service.

## Running the application in dev mode

Though you can run the application locally in dev mode but it is advisable to run this application as a container image.
The primary reason not to run as standalone application in dev mode, is that by default there are no DDL migration files included in the source. The script `get-kogito-ddl-scripts.sh` can be used to download the needed postgres DDL files into the application.

You can run your application in dev mode that enables live coding using:

```shell script
./get-kogito-ddl-scripts.sh
./mvnw compile quarkus:dev
```

## Build jar file

You can build the jar file with the script `build-db-migrator-jar.sh` which places the jar into /tmp/ kogito-db-migrator-tool directory for use by the corresponding image builder in package kogito-db-migrator-tool-image later, which can be found [here](../kogito-db-migrator-tool-image/README.md).

```shell
./build-db-migrator-jar.sh
```
