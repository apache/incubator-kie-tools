#!/bin/bash
#
# Clone the kogito-examples and edit the drools-quarkus-example and dmn-quarkus-example for testing purposes

TEST_DIR=`pwd`
cd /tmp
rm -rf kogito-examples/
git clone https://github.com/kiegroup/kogito-examples.git
cd kogito-examples/drools-quarkus-example
git fetch origin --tags
git checkout -b 0.5.0 0.5.0

# by adding the application.properties file telling quarkus to start on
# port 10000, the purpose of this tests is make sure that the images
# will ensure the use of the port 8080.
cp ${TEST_DIR}/application.properties src/main/resources/META-INF/

# preparing directory to run kogito maven archetypes tests
cp /tmp/kogito-examples/dmn-quarkus-example/src/main/resources/* /tmp/kogito-examples/dmn-quarkus-example/
rm -rf /tmp/kogito-examples/dmn-quarkus-example/src
rm -rf /tmp/kogito-examples/dmn-quarkus-example/pom.xml

git add --all
git commit -am "test"
