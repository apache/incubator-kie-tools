#!/usr/bin/env bats

export KOGITO_HOME=$BATS_TMPDIR/kogito_home
mkdir -p $KOGITO_HOME/launch

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh $KOGITO_HOME/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/kogito-kubernetes-client.sh

@test "list_or_get_k8s_resource sanity check" {
    run list_or_get_k8s_resource "" "configmaps/my-config-map"

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO --> [k8s-client] Trying to fetch Kubernetes API  for resource configmaps/my-config-map" ]
    [ "${lines[1]}" = "INFO --> [k8s-client] Not running on Kubernetes, skipping..." ]
}

@test "patch_json_k8s_resource sanity check" {
    run patch_json_k8s_resource "" "configmaps/my-config-map" "{ spec: {data: []}}"

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "INFO --> [k8s-client] Trying to patch resource configmaps/my-config-map in Kubernetes API " ]
    [ "${lines[1]}" = "INFO --> [k8s-client] Not running on Kubernetes, skipping..." ]
}