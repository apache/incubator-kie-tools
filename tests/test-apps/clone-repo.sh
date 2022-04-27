#!/usr/bin/env bash
#
# Clone the kogito-examples and edit the rules-quarkus-helloworld and dmn-quarkus-example for testing purposes

set -e
base_dir=`dirname $(realpath -s $0)`

. ${base_dir}/../../scripts/setup-maven.sh

MAVEN_OPTIONS="-U ${MAVEN_OPTIONS}"

CONTAINER_ENGINE="docker"
MAVEN_QUARKUS_NATIVE_CONTAINER_BUILD_ARGS="-Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=${CONTAINER_ENGINE}"

NATIVE_BUILD=$1
if [ -z $NATIVE_BUILD ]; then
    NATIVE_BUILD=true
fi

# Clone examples
cd /tmp
rm -rf kogito-examples/
git clone https://github.com/kiegroup/kogito-examples.git
cd kogito-examples/
git fetch origin --tags
git checkout nightly-main

# make a new copy of rules-quarkus-helloworld for native tests
cp -rv  /tmp/kogito-examples/kogito-quarkus-examples/rules-quarkus-helloworld/ /tmp/kogito-examples/kogito-quarkus-examples/rules-quarkus-helloworld-native/

# generating the app binaries to test the binary build
mvn -f kogito-quarkus-examples/rules-quarkus-helloworld clean package ${MAVEN_OPTIONS}
mvn -f kogito-springboot-examples/process-springboot-example clean package ${MAVEN_OPTIONS}

if [ "$NATIVE_BUILD" = 'true' ]; then
    mvn -f kogito-quarkus-examples/rules-quarkus-helloworld-native -Pnative clean package ${MAVEN_OPTIONS} ${MAVEN_QUARKUS_NATIVE_CONTAINER_BUILD_ARGS}
    ls -lah /tmp/kogito-examples/kogito-quarkus-examples/rules-quarkus-helloworld-native/target/
fi

# preparing directory to run kogito maven archetypes tests
mkdir -pv /tmp/kogito-examples/dmn-example
cp /tmp/kogito-examples/kogito-quarkus-examples/dmn-quarkus-example/src/main/resources/* /tmp/kogito-examples/dmn-example/

# by adding the application.properties file telling app to start on
# port 10000, the purpose of this tests is make sure that the images
# will ensure the use of the port 8080.

cp ${base_dir}/application.properties /tmp/kogito-examples/kogito-quarkus-examples/rules-quarkus-helloworld/src/main/resources/META-INF/
(echo ""; echo "server.port=10000") >> /tmp/kogito-examples/kogito-springboot-examples/process-springboot-example/src/main/resources/application.properties

git add --all  :/
git commit -am "test"

if [ "${CI}" ]; then
    rm "${HOME}"/.m2/settings.xml
fi