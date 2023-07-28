# Serverless Logic Web Tools SWF Deployment Webapp2

This is the web application for Serverless Logic Web Tools SWF Deployment to be used in Kogito-runtime.

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

4.  Edit the `pom.xml` file and add the following plugin configuration:

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.2.0</version>
            <executions>
                <execution>
                    <id>unpack-webjar</id>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>unpack</goal>
                    </goals>
                    <configuration>
                        <artifactItems>
                            <artifactItem>
                                <groupId>org.serverless-logic-web-tools</groupId>
                                <artifactId>serverless-logic-web-tools-swf-deployment-webapp2</artifactId>
                                <version>0.1.0</version>
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
