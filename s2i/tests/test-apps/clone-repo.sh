#!/bin/bash
#
# Clone the kogito-examples and edit the drools-quarkus-example
# by adding the application.properties file telling quarkus to start on
# port 10000, the purpose of this tests is make sure that the images
# will ensure the use of the port 8080.

CURRENT_DIR=`pwd`
cd /tmp
rm -rf kogito-examples/
git clone https://github.com/kiegroup/kogito-examples.git
cd kogito-examples/drools-quarkus-example
git fetch origin --tags
git checkout 0.4.0
cp ${CURRENT_DIR}/application.properties src/main/resources/META-INF/
git add --all
git commit -am "test"

