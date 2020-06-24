#!/usr/bin/env bash

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
    unset KOGITO_DATAINDEX_HTTP_URL
}

function configure() {
    configure_data_index_url
    configure_mgmt_console_http_port
}

# Exit codes:
#   10 - invalid url
function configure_data_index_url {
    url_simple_regex='(https?)://'
    local dataIndexURL=${KOGITO_DATAINDEX_HTTP_URL}
    if [ "${dataIndexURL}x" != "x" ]; then
        if [[ ! "${dataIndexURL}x" =~ $url_simple_regex ]]; then
            log_error "URL must start with http or https."
            exit 10
        fi
    else
        log_info "Data index url not set, default will be used: http://localhost:8180"
        dataIndexURL="http://localhost:8180"
    fi
    KOGITO_MANAGEMENT_CONSOLE_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dkogito.dataindex.http.url=${dataIndexURL}"
}

function configure_mgmt_console_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_MANAGEMENT_CONSOLE_PROPS="${KOGITO_MANAGEMENT_CONSOLE_PROPS} -Dquarkus.http.port=${httpPort}"
}
