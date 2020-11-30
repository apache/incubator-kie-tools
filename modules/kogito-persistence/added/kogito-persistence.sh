#!/usr/bin/env bash

# imports
source "${KOGITO_HOME}"/launch/kogito-kubernetes-client.sh
source "${KOGITO_HOME}"/launch/logging.sh

# copies the generated persistence files to
# "${KOGITO_HOME}"/bin, that's the directory used to exchange files between builds
# TODO: copy those files directly to the final dir only when bin is not used to exchange data anymore
function copy_persistence_files() {
    local persistenceDir="target"
    if [ -n "${ARTIFACT_DIR}" ]; then
        persistenceDir="${ARTIFACT_DIR}"
    fi

    log_info "---> [persistence] Copying persistence files..."
    if [ -d /tmp/src/"${persistenceDir}"/classes/persistence ]; then
        cp -v /tmp/src/"${persistenceDir}"/classes/persistence/* "${KOGITO_HOME}"/bin/
        # we don't need this file to be indexed
        rm -rfv "${KOGITO_HOME}"/bin/kogito-application.proto
        move_persistence_files
    else
        log_info "---> [persistence] Skip copying files, persistence directory does not exist..."
    fi
}

# move_persistence_files moves persistence files from "${KOGITO_HOME}"/bin to the final directory
# where those files will be handled by the runtime image.
# TODO: remove this function when s2i build move the KOGITO_HOME instead bin directory between images in chained builds
function move_persistence_files() {
    log_info "---> [persistence] Moving persistence files to final directory"
    if ls "${KOGITO_HOME}"/bin/*.proto &>/dev/null; then
        # copy to the final dir, so we keep bin clean
        cp -v "${KOGITO_HOME}"/bin/*.proto "${KOGITO_HOME}"/data/protobufs/
    else
        log_info "---> [persistence] Skip copying files, ${KOGITO_HOME}/bin directory does not have proto files!"
    fi
}
