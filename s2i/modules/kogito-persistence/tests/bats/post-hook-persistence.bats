#!/usr/bin/env bats

export KOGITO_HOME=$BATS_TMPDIR/kogito_home
mkdir -p $KOGITO_HOME/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../added/kogito-persistence.sh $KOGITO_HOME/launch/

setup() {
    export HOME=$KOGITO_HOME
    mkdir -p ${KOGITO_HOME}
}

teardown() {
    rm -rf ${KOGITO_HOME}
}

@test "Call post hook script (sanity check)" {
    run $BATS_TEST_DIRNAME/../../added/post-hook-persistence.sh

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO ---> [persistence] Not running on kubernetes cluster, skipping config map update" ]
}