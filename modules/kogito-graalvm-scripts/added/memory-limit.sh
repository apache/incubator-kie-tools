#!/usr/bin/env bash
#
# Limit memory usage for Graal VM native builds
# for more info access https://issues.jboss.org/browse/KOGITO-306
#
# For now we only limit the memory usage based on the LIMIT_MEMORY env which is set by the operator in bytes.
# the default value is JVM builds 1Gi and for 4Gi for native builds
#
# TODO: rely on cgroups to detect the container limits to define the memory usage based on the total memory available on the container.

# configure the GraalVM build memory usage limit based on the LIMIT_MEMORY env
# its value must be in binary bytes.

#import
source ${KOGITO_HOME}/launch/logging.sh

function configure() {
    # does not accepts
    if [[  "${LIMIT_MEMORY}" =~ ^[-+]?[0-9]+{9}$ ]]; then
        # native builds requires at least 1024m (1073741824=1024MB - 1 MB = 2^20 B in base 2)
        local limit=1073741824
        # only 80% of the actual limit will be used for the JVM
        local jvm_limit_memory=$(($LIMIT_MEMORY*80/100))
        log_info "Limit memory for this container is set to ${LIMIT_MEMORY}. Allocated memory for JVM will be set to ${jvm_limit_memory}."
        if [ "${jvm_limit_memory}" -lt "${limit}" ]; then
            limit=$(echo "scale=1; ${limit} / (80/100)" | bc -l)
            printf "Provided memory (${LIMIT_MEMORY}) limit is too small (should be greater then %.0f bytes), native build will use all available memory.\n" $limit
        else
            export KOGITO_OPTS="${KOGITO_OPTS} -Dnative-image.xmx=${jvm_limit_memory}"
        fi
    else
        log_warning "Provided memory (${LIMIT_MEMORY}) limit is not valid, native build will use all available memory"
    fi
}

