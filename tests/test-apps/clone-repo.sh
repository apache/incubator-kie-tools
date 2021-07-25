#!/usr/bin/env bash
#
# Clone the kogito-examples and edit the rules-quarkus-helloworld and dmn-quarkus-example for testing purposes

SCRIPT_DIR=`pwd`
MVN_MODULE="${SCRIPT_DIR}/../../modules/kogito-maven/3.6.x"
CONTAINER_ENGINE="docker"
MAVEN_OPTIONS="-DskipTests -U"
MAVEN_QUARKUS_NATIVE_CONTAINER_BUILD_ARGS="-Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=${CONTAINER_ENGINE}"
# MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=true

NATIVE_BUILD=$1
if [ -z $NATIVE_BUILD ]; then
    NATIVE_BUILD=true
fi

# exit when any command fails
set -e
# Setup maven configuration only on CI
if [ "${CI}" ]; then
    # setup maven env
    export JBOSS_MAVEN_REPO_URL="https://repository.jboss.org/nexus/content/groups/public/"
    # export MAVEN_REPO_URL=
    cp "${MVN_MODULE}"/maven/settings.xml "${HOME}"/.m2/settings.xml
    source "${MVN_MODULE}"/added/configure-maven.sh
    configure

    cat "${HOME}"/.m2/settings.xml
fi

if [ "${MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE}" = "true" ]; then
    MAVEN_OPTIONS="${MAVEN_OPTIONS} -Denforcer.skip"
fi

# Clone examples
cd /tmp
rm -rf kogito-examples/
git clone https://github.com/kiegroup/kogito-examples.git
cd kogito-examples/
git fetch origin --tags
git checkout nightly-main

# make a new copy of rules-quarkus-helloworld for native tests
cp -rv  /tmp/kogito-examples/rules-quarkus-helloworld/ /tmp/kogito-examples/rules-quarkus-helloworld-native/

# generating the app binaries to test the binary build
mvn -f rules-quarkus-helloworld clean package ${MAVEN_OPTIONS}
mvn -f process-springboot-example clean package ${MAVEN_OPTIONS}

if [ "$NATIVE_BUILD" = 'true' ]; then
    mvn -f rules-quarkus-helloworld-native -Pnative clean package ${MAVEN_OPTIONS} ${MAVEN_QUARKUS_NATIVE_CONTAINER_BUILD_ARGS}
fi

# preparing directory to run kogito maven archetypes tests
mkdir -pv /tmp/kogito-examples/dmn-example
cp /tmp/kogito-examples/dmn-quarkus-example/src/main/resources/* /tmp/kogito-examples/dmn-example/

# by adding the application.properties file telling app to start on
# port 10000, the purpose of this tests is make sure that the images
# will ensure the use of the port 8080.
cp "${SCRIPT_DIR}"/application.properties /tmp/kogito-examples/rules-quarkus-helloworld/src/main/resources/META-INF/
(echo ""; echo "server.port=10000") >> /tmp/kogito-examples/process-springboot-example/src/main/resources/application.properties

git add --all  :/
git commit -am "test"

if [ "${CI}" ]; then
    rm "${HOME}"/.m2/settings.xml
fi