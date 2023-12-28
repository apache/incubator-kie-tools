#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME="${KOGITO_HOME}"
export JBOSS_CONTAINER_JAVA_JVM_MODULE=/tmp/container/java/jvm
mkdir -p "${KOGITO_HOME}"/launch
mkdir -p "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
cp $BATS_TEST_DIRNAME/../../../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/
cp -r $BATS_TEST_DIRNAME/../../../../../kogito-dynamic-resources/added/* "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"/
chmod -R +x "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
cp $BATS_TEST_DIRNAME/../../added/jvm-settings.sh "${KOGITO_HOME}"/launch/

teardown() {
    rm -rf "${KOGITO_HOME}"
    rm -rf "${JBOSS_CONTAINER_JAVA_JVM_MODULE}"
}

@test "run jvm-settings with no custom conf" {
    expected_status_code=0
    mkdir -p $KOGITO_HOME/my-app

    source ${KOGITO_HOME}/launch/jvm-settings.sh

    echo "MAVEN_OPTS is: ${MAVEN_OPTS}"
    [[ "${MAVEN_OPTS}" == *"-XX:+UseParallelGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:+ExitOnOutOfMemoryError"* ]]
}

@test "run jvm-settings with custom conf" {
    expected_status_code=0
    mkdir -p $KOGITO_HOME/my-app/.mvn
    cd $KOGITO_HOME/my-app
    echo "-Xmx1024m -Xms512m -Xotherthing" > $KOGITO_HOME/my-app/.mvn/jvm.config

    source ${KOGITO_HOME}/launch/jvm-settings.sh

    echo "MAVEN_OPTS is: ${MAVEN_OPTS}"
    [[ "${MAVEN_OPTS}" == *"-Xmx1024m -Xms512m -Xotherthing -XX:+UseParallelGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:+ExitOnOutOfMemoryError"* ]]
}
