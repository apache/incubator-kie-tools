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

# SonataFlow Deployment Webapp

This is the web application for SonataFlow Deployments.

## Run local web server

To run the local web server and the mock server, use the following command:

    pnpm dev

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
