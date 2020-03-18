#!/usr/bin/env bash

# imports
source ${KOGITO_HOME}/launch/kogito-kubernetes-client.sh
source ${KOGITO_HOME}/launch/logging.sh

# copies the generated persistence files to
# $KOGITO_HOME/bin, that's the directory used to exchange files between builds
# TODO: copy those files directly to the final dir only when bin is not used to exchange data anymore
function copy_persistence_files() {
    local persistenceDir="target"
    if [ ! -z "${ARTIFACT_DIR}" ]; then
        persistenceDir="${ARTIFACT_DIR}"
    fi

    log_info "---> [persistence] Copying persistence files..."
    if [ -d /tmp/src/${persistenceDir}/classes/persistence ]; then
        cp -v /tmp/src/${persistenceDir}/classes/persistence/* $KOGITO_HOME/bin/
        # we don't need this file to be indexed
        rm -rfv $KOGITO_HOME/bin/kogito-application.proto
        move_persistence_files
    else
        log_info "---> [persistence] Skip copying files, persistence directory does not exist..."
    fi
}

# move_persistence_files moves persistence files from $KOGITO_HOME/bin to the final directory
# where those files will be handled by the runtime image.
# TODO: remove this function when s2i build move the KOGITO_HOME instead bin directory between images in chained builds
function move_persistence_files() {
    log_info "---> [persistence] Moving persistence files to final directory"
    if ls $KOGITO_HOME/bin/*.proto &>/dev/null; then
        # copy to the final dir, so we keep bin clean
        cp -v $KOGITO_HOME/bin/*.proto $KOGITO_HOME/data/protobufs/
        generate_md5_persistence_files
    else
        log_info "---> [persistence] Skip copying files, $KOGITO_HOME/bin directory does not have proto files!"
    fi
}

# generate_md5_persistence_files generates md5 files for each *.proto file found in $KOGITO_HOME/data/protobufs/
function generate_md5_persistence_files() {
    if ls $KOGITO_HOME/data/protobufs/*.proto &>/dev/null; then
        log_info "---> [persistence] generating md5 for persistence files"
        for entry in "$KOGITO_HOME/data/protobufs"/*.proto; do
            md5sum ${entry} | awk '{ print $1 }' >${entry%.*}-md5.txt
        done
    fi
}

# Updates the configMap for this Kogito Runtime instance 
# with the generated proto files mounted in the file system.
# Can be called multiple times or outside of a k8s cluster.
# If outside the cluster, just skips the update
function update_configmap() {
    if ! is_running_on_kubernetes; then
        log_info "---> [persistence] Not running on kubernetes cluster, skipping config map update"
        return 0
    fi

    local config_map=$(cat $KOGITO_HOME/podinfo/protobufcm)
    local file_contents=""
    local file_name=""
    local md5=""
    local annotation=""
    local data=""
    local metadata=""
    local body=""

    if ls $KOGITO_HOME/data/protobufs/*.proto &>/dev/null; then
        for entry in "$KOGITO_HOME/data/protobufs"/*.proto; do
            # sanitize input
            file_contents=$(jq -aRs . <<<$(cat $entry))
            file_name=$(basename $entry)
            md5=$(cat ${entry%.*}-md5.txt)
            annotation="org.kie.kogito.protobuf.hash/${file_name%.*}"
            metadata="${metadata} \"${annotation}\": \"${md5}\","
            # doesn't need quotes since jq already added
            data="${data} \"${file_name}\": ${file_contents},"
        done
    fi

    if [ "${metadata}" != "" ]; then
        metadata="${metadata%,}" # cut last comma
    fi

    if [ "${data}" != "" ]; then
        data="${data%,}"
    fi

    body="[ { \"op\": \"replace\", \"path\": \"/metadata/annotations\", \"value\": { ${metadata} } }, { \"op\": \"replace\", \"path\": \"/data\", \"value\": { ${data} } } ]"
    log_info "---> [persistence] About to patch configMap ${config_map}"
    # prints the raw data
    echo "Body: ${body}"
    printf "%s" "${body}" >$KOGITO_HOME/data/protobufs/configmap_patched.json
    response=$(patch_json_k8s_resource "api" "configmaps/${config_map}" $KOGITO_HOME/data/protobufs/configmap_patched.json)
    if [ "${response: -3}" != "200" ]; then
        log_warning "---> [persistence] Fail to patch configMap ${config_map}, the Service Account might not have the necessary privileges"
        if [ ! -z "${response}" ]; then
            log_warning "---> [persistence] Response message: ${response::-3} - HTTP Status code: ${response: -3}"
        fi
        return 1
    fi

    return 0
}
