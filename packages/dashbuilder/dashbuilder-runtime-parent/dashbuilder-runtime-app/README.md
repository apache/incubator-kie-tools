## Dashbuilder Runtime App

This app can run dashboards exported from Dashbuilder Design, Business Central or from Dashbuilder DSL.

### Building

First make sure that `dashbuilder-runtime-client` is built with flag `-Dfull` for a production build or `-Dsources` for client debugging.

Then run `mvn clean install` and the distribution JAR will be in `target` with name `dashbuidler-runtime-app-x-runner.jar`, where x is the version.

### Running

To run use Java 11:

```
java -jar dashbuidler-runtime-app-x-runner.jar
```

You can configure the application using the system properties from class `RuntimeOptions` and SQL connections can be configured using the following system properties:

```
-Ddashbuilder.datasources=sample \
-Ddashbuilder.datasource.sample.jdbcUrl=JDBCCOnnectionURL \
-Ddashbuilder.datasource.sample.providerClassName=driverAccordingToTheDatabase \
-Ddashbuilder.datasource.sample.maxSize=10 \
-Ddashbuilder.datasource.sample.principal=user \
-Ddashbuilder.datasource.sample.credential=password
```

The name `sample` must match either the dataset UUID, name or datasource.
