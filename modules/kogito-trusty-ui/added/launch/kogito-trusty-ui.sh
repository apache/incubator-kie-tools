#!/usr/bin/env bash

#import
source ${KOGITO_HOME}/launch/logging.sh

function prepareEnv() {
    # keep it on alphabetical order
    unset HTTP_PORT
    unset KOGITO_TRUSTY_ENDPOINT
}

function configure() {
    configure_trusty_url
    configure_trusty_ui_http_port
}

# Exit codes:
#   10 - invalid url
function configure_trusty_url {
    url_simple_regex='(https?)://'
    local trustyURL=${KOGITO_TRUSTY_ENDPOINT}
    if [ "${trustyURL}x" != "x" ]; then
        if [[ ! "${trustyURL}x" =~ $url_simple_regex ]]; then
            log_error "URL must start with http or https."
            exit 10
        fi
    else
        log_info "Trusty url not set, default will be used: http://localhost:8180"
        trustyURL="http://localhost:8180"
    fi
    KOGITO_TRUSTY_UI_PROPS="${KOGITO_TRUSTY_PROPS} -Dkogito.trusty.http.url=${trustyURL}"
}

function configure_trusty_ui_http_port {
    local httpPort=${HTTP_PORT:-8080}
    KOGITO_TRUSTY_UI_PROPS="${KOGITO_TRUSTY_UI_PROPS} -Dquarkus.http.port=${httpPort}"
}
