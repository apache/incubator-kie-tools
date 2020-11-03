#!/usr/bin/env bats

export KOGITO_HOME=$BATS_TMPDIR/kogito_home
export MOCK_RESPONSE=""
mkdir -p $KOGITO_HOME/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh $KOGITO_HOME/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/kogito-persistence.sh

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
