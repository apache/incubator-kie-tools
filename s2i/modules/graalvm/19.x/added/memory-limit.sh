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

function configure() {
    # native builds requires at least 1024m (1073741824=1024MB - 1 MB = 2^20 B in base 2)
    # does not accepts
    if [[  "${LIMIT_MEMORY}" =~ ^[-+]?[0-9]+{9}$ ]]; then
        if [ "${LIMIT_MEMORY}" -lt 1073741824 ]; then
            echo "Provided memory (${LIMIT_MEMORY}) limit is too small, native build will use all available memory"
        else
            export KOGITO_OPTS="${KOGITO_OPTS} -Dnative-image.xmx=${LIMIT_MEMORY}"
        fi
    else
        echo "Provided memory (${LIMIT_MEMORY}) limit is not valid, native build will use all available memory"
    fi
}

