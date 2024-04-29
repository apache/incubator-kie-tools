# SonataFlow Deployment Webapp

This is the web application for SonataFlow Deployments.

## Run local web server

To run the local web server, use the following command:

    pnpm start

## Build the Webjar

To build the webjar, use the following command:

    pnpm build:prod

## Install locally

To install the project locally, use the following command:

    pnpm build:dev
    mvn clean install

## Consumer app for local tests

To create a consumer app for local tests following the Quarkus Getting Started App as an example, follow these steps:

1.  Choose a directory for your Quarkus Getting Started App outside of this repository.
2.  Create a new Quarkus Getting Started App using the Quarkus Maven Plugin:

        mvn io.quarkus.platform:quarkus-maven-plugin:3.2.2.Final:create \
            -DprojectGroupId=org.acme \
            -DprojectArtifactId=getting-started \
            -Dextensions='resteasy-reactive'

3.  Change to the newly created directory:

        cd getting-started

### Using a local webjar

1.  From this project run `mvn install`, to make this project available in the `.m2` repository for use in the consumer app.
2.  Edit the `pom.xml` file in your consumer app and add the following plugin configuration:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <id>unpack-webjar</id>
            <phase>generate-resources</phase>
            <goals>
                <goal>unpack</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>org.webjars.npm</groupId>
                        <artifactId>sonataflow-deployment-webapp-local</artifactId>
                        <version>0.0.1</version>
                        <type>jar</type>
                        <overWrite>true</overWrite>
                        <outputDirectory>${project.basedir}/src/main/resources/META-INF/resources</outputDirectory>
                        <includes>**/*</includes>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Using the deployed webjar

1.  Visit [webjars.org](https://www.webjars.org/) and search for the leatest available version of `sonataflow-deployment-webapp`
2.  Edit the `pom.xml` file and add the following plugin configuration:

Add the `sonataflow-deployment-webapp` version from webjars.org to the properties section of your pom.xml

```xml
<sonataFlowDeploymentWebapp.version>0.32.0</sonataFlowDeploymentWebapp.version>
```

Add the webjar as a dependency in the dependencies section

```xml
<dependencies>
    <dependency>
        <groupId>org.webjars.npm</groupId>
        <artifactId>sonataflow-deployment-webapp</artifactId>
        <version>${sonataFlowDeploymentWebapp.version}</version>
    </dependency>
  </dependencies>
```

Add a plugin to unpack and copy the Webjar in the plugins section

```xml
<build>
    <plugins>
      <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-dependency-plugin</artifactId>
          <executions>
              <execution>
                  <id>unpack-sonataflow-deployment-webapp</id>
                  <phase>generate-resources</phase>
                  <goals>
                      <goal>unpack</goal>
                  </goals>
                  <configuration>
                      <artifactItems>
                          <artifactItem>
                              <groupId>org.webjars.npm</groupId>
                              <artifactId>sonataflow-deployment-webapp</artifactId>
                              <version>${sonataFlowDeploymentWebapp.version}</version>
                              <outputDirectory>${project.build.directory}/sonataflow-deployment-webapp</outputDirectory>
                          </artifactItem>
                      </artifactItems>
                      <overWriteReleases>false</overWriteReleases>
                      <overWriteSnapshots>true</overWriteSnapshots>
                  </configuration>
              </execution>
          </executions>
      </plugin>
      <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <executions>
              <execution>
                  <id>copy-sonataflow-deployment-webapp-resources</id>
                  <phase>generate-resources</phase>
                  <goals>
                      <goal>copy-resources</goal>
                  </goals>
                  <configuration>
                      <outputDirectory>${project.basedir}/src/main/resources/META-INF/resources</outputDirectory>
                      <overwrite>true</overwrite>
                      <resources>
                          <resource>
                              <directory
                >${project.build.directory}/sonataflow-deployment-webapp/META-INF/resources/webjars/sonataflow-deployment-webapp/${sonataFlowDeploymentWebapp.version}/dist</directory>

                                <includes>**/*</includes>
                          </resource>
                      </resources>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

## Optional customizations

After the installation, you can optionally add a file at `/src/main/resources/META-INF/resources/sonataflow-deployment-webapp-data.json` with the following text to set some customizations:

```JSON
{
  "appName": "SonataFlow Deployment",
  "showDisclaimer": true,
  "dataIndexUrl": ""
}
```

Please replace:

- `appName` with the desired name for the app
- `showDisclaimer` show/hide the development disclamer
- `dataIndexUrl` optional url to your SonataFlow Data Index Service. If not set the app will try to use the embedded Data Index Service.
