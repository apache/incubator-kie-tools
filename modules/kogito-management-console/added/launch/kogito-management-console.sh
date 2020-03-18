#!/usr/bin/env bash


function prepareEnv() {
    # keep it on alphabetical order
    unset KOGITO_DATAINDEX_HTTP_URL
}

function configure() {
    configure_data_index_url
}

# Exit codes:
#   10 - invalid url
function configure_data_index_url {
    url_simple_regex='(https?)://'
    local dataIndexURL=${KOGITO_DATAINDEX_HTTP_URL}
    if [ "${dataIndexURL}x" != "x" ]; then
        if [[ ! "${dataIndexURL}x" =~ $url_simple_regex ]]; then
            echo "URL must start with http or https."
            exit 10
        fi
    else
        echo "Data index url not set, default will be used: http://localhost:8180"
        dataIndexURL="http://localhost:8180"
    fi
    KOGITO_MANAGEMENT_CONSOLE_PROPS="${KOGITO_DATA_INDEX_PROPS} -Dkogito.dataindex.http.url=${dataIndexURL}"
}

