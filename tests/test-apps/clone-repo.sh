#!/bin/bash
#
# Clone the kogito-examples and edit the rules-quarkus-helloworld and dmn-quarkus-example for testing purposes

TEST_DIR=`pwd`
cd /tmp
rm -rf kogito-examples/
git clone https://github.com/kiegroup/kogito-examples.git
cd kogito-examples/
git fetch origin --tags
git checkout master

# generating the app binaries to test the binary build
mvn -f rules-quarkus-helloworld clean package -DskipTests
mvn -f process-springboot-example clean package -DskipTests

# preparing directory to run kogito maven archetypes tests
cp /tmp/kogito-examples/dmn-quarkus-example/src/main/resources/* /tmp/kogito-examples/dmn-quarkus-example/
rm -rf /tmp/kogito-examples/dmn-quarkus-example/src
rm -rf /tmp/kogito-examples/dmn-quarkus-example/pom.xml

# by adding the application.properties file telling quarkus to start on
# port 10000, the purpose of this tests is make sure that the images
# will ensure the use of the port 8080.
cp ${TEST_DIR}/application.properties /tmp/kogito-examples/rules-quarkus-helloworld/src/main/resources/META-INF/

cd rules-quarkus-helloworld
git add --all  :/
git commit -am "test"
