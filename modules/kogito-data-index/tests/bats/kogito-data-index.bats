#!/usr/bin/env bats

export KOGITO_HOME=/tmp/kogito
export HOME=$KOGITO_HOME
mkdir -p ${KOGITO_HOME}/launch
cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh ${KOGITO_HOME}/launch/

# imports
load $BATS_TEST_DIRNAME/../../added/launch/kogito-data-index.sh

teardown() {
    rm -rf ${KOGITO_HOME}
}

@test "http port configuration default value" {
    configure_data_index_http_port
    expected=" -Dquarkus.http.port=8080"
    echo "Result is ${KOGITO_DATA_INDEX_PROPS} and expected is ${expected}"
    [ "${KOGITO_DATA_INDEX_PROPS}" = "${expected}" ]
}

@test "http port configuration custom value" {
    export HTTP_PORT="9090"
    configure_data_index_http_port
    expected=" -Dquarkus.http.port=9090"
    echo "Result is ${KOGITO_DATA_INDEX_PROPS} and expected is ${expected}"
    [ "${KOGITO_DATA_INDEX_PROPS}" = "${expected}" ]
}

@test "test if the default data index persistence service is correctly set" {
    configure_data_index_persistence
    expected="data-index-service-infinispan.jar"
    echo "result: ${KOGITO_DATA_INDEX_SERVICE_JAR} \n expected: ${expected}"
    [ "${KOGITO_DATA_INDEX_SERVICE_JAR}" = "${expected}" ]
}

@test "test if the default data index persistence service is correctly set (messages output only)" {
    run configure_data_index_persistence
    echo "output: ${output}"
    [[ "${output}" = *"INFO Data index persistence is set to INFINISPAN"* ]]
}

@test "test if data index persistence service default value s correctly set if a nonsense type is set" {
    DATA_INDEX_PERSISTENCE="nonsense"
    configure_data_index_persistence
    expected="data-index-service-infinispan.jar"
    echo "result: ${KOGITO_DATA_INDEX_SERVICE_JAR} \n expected: ${expected}"
    [ "${KOGITO_DATA_INDEX_SERVICE_JAR}" = "${expected}" ]
}

@test "test if data index persistence service default value s correctly set if a nonsense type is set (messages output only)" {
    DATA_INDEX_PERSISTENCE="nonsense"
    run configure_data_index_persistence
    echo "lines: ${lines[@]}"
    [[ "${lines[0]}" = *"WARN Data index persistence type nonsense is not allowed, the allowed types are [INFINISPAN MONGODB]. Defaulting to INFINISPAN."* ]]
    [[ "${lines[1]}" = *"INFO Data index persistence is set to INFINISPAN"* ]]
}

@test "test if data index persistence service default value s correctly set if set to mongodb" {
    DATA_INDEX_PERSISTENCE="mongodb"
    configure_data_index_persistence
    expected="data-index-service-mongodb.jar"
    echo "result: ${KOGITO_DATA_INDEX_SERVICE_JAR} \n expected: ${expected}"
    [ "${KOGITO_DATA_INDEX_SERVICE_JAR}" = "${expected}" ]
}

@test "test if data index persistence service default value s correctly set if set to mongodb (messages output only)" {
    DATA_INDEX_PERSISTENCE="mongodb"
    run configure_data_index_persistence
    echo "output: ${output}"
    [[ "${output}" = *"INFO Data index persistence is set to MONGODB"* ]]
}