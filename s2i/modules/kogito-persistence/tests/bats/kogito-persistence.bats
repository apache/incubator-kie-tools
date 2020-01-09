#!/usr/bin/env bats

export KOGITO_HOME=$BATS_TMPDIR/kogito_home
export MOCK_RESPONSE=""
mkdir -p $KOGITO_HOME/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh $KOGITO_HOME/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/kogito-persistence.sh

unset -f list_or_get_k8s_resource
unset -f patch_json_k8s_resource
unset -f is_running_on_kubernetes

function is_running_on_kubernetes() {
    # yes, we are :)
    log_info "Yes, we are in kubernetes"
    return 0
}

function patch_json_k8s_resource() {
    local api="${1}"
    local resource="${2}"
    local body="${3}"

    log_info "Calling k8s api '${api}', resource '${resource}'"
    echo "${body}200"
}

function list_or_get_k8s_resource() {
    local response=$(cat $BATS_TEST_DIRNAME/mocks/$MOCK_RESPONSE)
    response="${response}200"
    echo "${response}"
}

setup() {
    export HOME=$KOGITO_HOME
    mkdir -p ${KOGITO_HOME}
    mkdir -p $KOGITO_HOME/bin
    mkdir -p $KOGITO_HOME/data/protobufs/
    mkdir -p $KOGITO_HOME/podinfo
    echo "exampleapp-cm" > $KOGITO_HOME/podinfo/protobufcm
}

teardown() {
    rm -rf ${KOGITO_HOME}
    rm -rf /tmp/src
    rm -rf $KOGITO_HOME/bin/*
}

@test "There's some proto files in the target directory" {
    mkdir -p /tmp/src/target/classes/persistence
    touch /tmp/src/target/classes/persistence/{file1.proto,kogito-application.proto}

    run copy_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Copying persistence files..." ]
    [ "${lines[1]}" = "'/tmp/src/target/classes/persistence/file1.proto' -> '${KOGITO_HOME}/bin/file1.proto'" ]
    [ "${lines[2]}" = "'/tmp/src/target/classes/persistence/kogito-application.proto' -> '${KOGITO_HOME}/bin/kogito-application.proto'" ]
    [ "${lines[3]}" = "removed '${KOGITO_HOME}/bin/kogito-application.proto'" ]
    [ "${lines[4]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[5]}" = "'${KOGITO_HOME}/bin/file1.proto' -> '${KOGITO_HOME}/data/protobufs/file1.proto'" ]
    [ "${lines[6]}" = "INFO ---> [persistence] generating md5 for persistence files" ]
    
}

@test "There are no persistence files" {
    KOGITO_HOME=/tmp/kogito

    run copy_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Copying persistence files..." ]
    [ "${lines[1]}" = "INFO ---> [persistence] Skip copying files, persistence directory does not exist..." ]
}

@test "There's some proto files in the bin directory" {
    touch $KOGITO_HOME/bin/file1.proto

    run move_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[1]}" = "'${KOGITO_HOME}/bin/file1.proto' -> '${KOGITO_HOME}/data/protobufs/file1.proto'" ]
}

@test "There's no proto files in the bin directory" {
    run move_persistence_files

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Moving persistence files to final directory" ]
    [ "${lines[1]}" = "INFO ---> [persistence] Skip copying files, ${KOGITO_HOME}/bin directory does not have proto files!" ]
}

@test "MD5 correctly generated for file1.proto" {
    touch $KOGITO_HOME/data/protobufs/file1.proto

    run generate_md5_persistence_files
    
    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ -e $KOGITO_HOME/data/protobufs/file1-md5.txt ]
    # if md5 isn't generated, grep will fail to find the given string
    grep -q "d41d8cd98f00b204e9800998ecf8427e" $KOGITO_HOME/data/protobufs/file1-md5.txt
}

@test "MD5 not generated for file1.proto1" {
    touch $KOGITO_HOME/data/protobufs/file1.proto1

    run generate_md5_persistence_files
    
    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ ! -e $KOGITO_HOME/data/protobufs/file1-md5.txt ]
}

@test "Patch a configMap when we have empty annotations and data" {
    MOCK_RESPONSE="config_map_no_annotations.json"
    cp -v $BATS_TEST_DIRNAME/mocks/travels.proto $KOGITO_HOME/data/protobufs/
    cp -v $BATS_TEST_DIRNAME/mocks/visaApplications.proto $KOGITO_HOME/data/protobufs/
    generate_md5_persistence_files

    local expected=$(cat $BATS_TEST_DIRNAME/expected/patch_cm_travel_agency.json)

    run update_configmap
    
    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[1]}" = "INFO ---> [persistence] About to patch configMap exampleapp-cm" ]
    [ "${lines[2]}" = "Body: ${expected}" ]
}

@test "Patch a configMap with empty data" {
    MOCK_RESPONSE="config_map.json" # we have annotations, but no files in the file system
    
    local expected=$(cat $BATS_TEST_DIRNAME/expected/patch_empty_data.json)

    run update_configmap
    
    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[1]}" = "INFO ---> [persistence] About to patch configMap exampleapp-cm" ]
    [ "${lines[2]}" = "Body: ${expected}" ]
}

@test "Patch with an empty annotations configmap with files in disk" {
    MOCK_RESPONSE="config_map_empty_annotations.json" # we have empty annotations and files in disk
    
    cp -v $BATS_TEST_DIRNAME/mocks/travels.proto $KOGITO_HOME/data/protobufs/
    cp -v $BATS_TEST_DIRNAME/mocks/visaApplications.proto $KOGITO_HOME/data/protobufs/
    generate_md5_persistence_files

    local expected=$(cat $BATS_TEST_DIRNAME/expected/patch_cm_travel_agency.json)

    run update_configmap
    
    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[1]}" = "INFO ---> [persistence] About to patch configMap exampleapp-cm" ]
    [ "${lines[2]}" = "Body: ${expected}" ]
}