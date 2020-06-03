#!/usr/bin/env bash

function prepareEnv() {
    unset HTTP_PROXY_HOST
    unset HTTP_PROXY_PORT
    unset HTTP_PROXY_PASSWORD
    unset HTTP_PROXY_USERNAME
    unset HTTP_PROXY_NONPROXYHOSTS
    unset HTTPS_PROXY
    unset MAVEN_MIRROR_URL
    unset MAVEN_DOWNLOAD_OUTPUT
    unset MAVEN_REPO_ID
    unset MAVEN_REPO_LAYOUT
    unset MAVEN_REPO_RELEASES_ENABLED
    unset MAVEN_REPO_RELEASES_UPDATE_POLICY
    unset MAVEN_REPO_RELEASES_CHECKSUM_POLICY
    unset MAVEN_REPO_SNAPSHOTS_ENABLED
    unset MAVEN_REPO_SNAPSHOTS_UPDATE_POLICY
    unset MAVEN_REPO_SNAPSHOTS_CHECKSUM_POLICY
    unset MAVEN_REPO_URL
    unset MAVEN_REPOS
}

function configure() {
    configure_proxy
    configure_mirrors
    configure_maven_download_output
    add_maven_repo
}

# insert settings for HTTP proxy into maven settings.xml if supplied
function configure_proxy() {
    # prefer old http_proxy_ format for username/password, but
    # also allow proxy_ format.
    HTTP_PROXY_USERNAME=${HTTP_PROXY_USERNAME:-$PROXY_USERNAME}
    HTTP_PROXY_PASSWORD=${HTTP_PROXY_PASSWORD:-$PROXY_PASSWORD}

    proxy=${HTTPS_PROXY:-${https_proxy:-${HTTP_PROXY:-$http_proxy}}}
    # if http_proxy_host/port is set, prefer that (oldest mechanism)
    # before looking at HTTP(S)_PROXY
    proxyhost=${HTTP_PROXY_HOST:-$(echo $proxy | cut -d : -f 1,2)}
    proxyport=${HTTP_PROXY_PORT:-$(echo $proxy | cut -d : -f 3)}

    if [ -n "$proxyhost" ]; then
        if [[ `echo $proxyhost | grep -i https://` ]]; then
          proxyport=${proxyport:-443}
          proxyprotocol="https"
        else
          proxyport=${proxyport:-80}
          proxyprotocol="http"
        fi

        xml="<proxy>\
         <id>genproxy</id>\
         <active>true</active>\
         <protocol>$proxyprotocol</protocol>\
         <host>$proxyhost</host>\
         <port>$proxyport</port>"

        if [ -n "$HTTP_PROXY_USERNAME" -a -n "$HTTP_PROXY_PASSWORD" ]; then
            xml="$xml\
         <username>$HTTP_PROXY_USERNAME</username>\
         <password>$HTTP_PROXY_PASSWORD</password>"
        fi
        if [ -n "$HTTP_PROXY_NONPROXYHOSTS" ]; then
            nonproxyhosts=$(echo ${HTTP_PROXY_NONPROXYHOSTS} | sed -e 's/|/\\|/')
            xml="$xml\
         <nonProxyHosts>$nonproxyhosts</nonProxyHosts>"
        fi
        xml="$xml\
       </proxy>"
        sed -i "s|<!-- ### configured http proxy ### -->|${xml}|" $HOME/.m2/settings.xml
    fi
}

# insert settings for mirrors/repository managers into settings.xml if supplied
function configure_mirrors() {
    if [ -n "$MAVEN_MIRROR_URL" ]; then
        xml="    <mirror>\
      <id>mirror.default</id>\
      <url>$MAVEN_MIRROR_URL</url>\
      <mirrorOf>external:*</mirrorOf>\
    </mirror>"
        sed -i "s|<!-- ### configured mirrors ### -->|$xml|" $HOME/.m2/settings.xml
    fi
}

function configure_maven_download_output() {
    if [ "${MAVEN_DOWNLOAD_OUTPUT}" != "true" ]; then
        export MAVEN_ARGS_APPEND="${MAVEN_ARGS_APPEND} --no-transfer-progress"
    fi
}

function add_maven_repo() {
    # single remote repository scenario: respect fully qualified url if specified, otherwise find and use service
    local single_repo_url="${MAVEN_REPO_URL}"
    if [ -n "$single_repo_url" ]; then
        local single_repo_id=$(_maven_find_env "MAVEN_REPO_ID" "repo-$(_generate_random_id)")
        _add_maven_repo "$single_repo_url" "$single_repo_id" ""
    fi

    # multiple remote repositories scenario: respect fully qualified url(s) if specified, otherwise find and use service(s); can be used together with "single repo scenario" above
    local multi_repo_counter=1
    IFS=',' read -a multi_repo_prefixes <<< ${MAVEN_REPOS}
    for multi_repo_prefix in ${multi_repo_prefixes[@]}; do
        local multi_repo_url=$(_maven_find_prefixed_env "${multi_repo_prefix}" "MAVEN_REPO_URL")
        local multi_repo_id=$(_maven_find_prefixed_env "${multi_repo_prefix}" "MAVEN_REPO_ID" "repo${multi_repo_counter}-$(_generate_random_id)")
        _add_maven_repo "$multi_repo_url" "$multi_repo_id" "$multi_repo_prefix"
        multi_repo_counter=$((multi_repo_counter+1))
    done
}
# add maven repositories
# Parameters:
#   $1 - repo url
#   $2 - repo id
#   $3 - repo prefix
function _add_maven_repo() {
    local repo_url=$1
    local repo_id=$2
    local prefix=$3

    local repo_name=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_NAME" "${repo_id}")
    local repo_layout=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_LAYOUT" "default")
    local releases_enabled=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_RELEASES_ENABLED" "true")
    local releases_update_policy=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_RELEASES_UPDATE_POLICY" "always")
    local releases_checksum_policy=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_RELEASES_CHECKSUM_POLICY" "warn")
    local snapshots_enabled=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_SNAPSHOTS_ENABLED" "true")
    local snapshots_update_policy=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_SNAPSHOTS_UPDATE_POLICY" "always")
    local snapshots_checksum_policy=$(_maven_find_prefixed_env "${prefix}" "MAVEN_REPO_SNAPSHOTS_CHECKSUM_POLICY" "warn")

    local repo="\n\
                <repository>\n\
                    <id>${repo_id}</id>\n\
                    <name>${repo_name}</name>\n\
                    <url>${repo_url}</url>\n\
                    <layout>${repo_layout}</layout>\n\
                    <releases>\n\
                        <enabled>${releases_enabled}</enabled>\n\
                        <updatePolicy>${releases_update_policy}</updatePolicy>\n\
                        <checksumPolicy>${releases_checksum_policy}</checksumPolicy>\n\
                    </releases>\n\
                    <snapshots>\n\
                        <enabled>${snapshots_enabled}</enabled>\n\
                        <updatePolicy>${snapshots_update_policy}</updatePolicy>\n\
                        <checksumPolicy>${snapshots_checksum_policy}</checksumPolicy>\n\
                    </snapshots>\n\
                </repository>\n\
                <!-- ### configured repositories ### -->"
    sed -i "s|<!-- ### configured repositories ### -->|${repo}|" $HOME/.m2/settings.xml


    local pluginRepo="\n\
                <pluginRepository>\n\
                    <id>${repo_id}</id>\n\
                    <name>${repo_name}</name>\n\
                    <url>${repo_url}</url>\n\
                    <layout>${repo_layout}</layout>\n\
                    <releases>\n\
                        <enabled>${releases_enabled}</enabled>\n\
                        <updatePolicy>${releases_update_policy}</updatePolicy>\n\
                        <checksumPolicy>${releases_checksum_policy}</checksumPolicy>\n\
                    </releases>\n\
                    <snapshots>\n\
                        <enabled>${snapshots_enabled}</enabled>\n\
                        <updatePolicy>${snapshots_update_policy}</updatePolicy>\n\
                        <checksumPolicy>${snapshots_checksum_policy}</checksumPolicy>\n\
                    </snapshots>\n\
                </pluginRepository>\n\
                <!-- ### configured plugin repositories ### -->"

    sed -i "s|<!-- ### configured plugin repositories ### -->|${pluginRepo}|" $HOME/.m2/settings.xml
}

# Finds the environment variable  and returns its value if found.
# Otherwise returns the default value if provided.
#
# Arguments:
# $1 env variable name to check
# $2 default value if environment variable was not set
function _maven_find_env() {
    local var=${!1}
    echo "${var:-$2}"
}

# Finds the environment variable with the given prefix. If not found
# the default value will be returned. If no prefix is provided will
# rely on _maven_find_env
#
# Arguments
#  - $1 prefix. Transformed to uppercase and replace - by _
#  - $2 variable name. Prepended by "prefix_"
#  - $3 default value if the variable is not defined
function _maven_find_prefixed_env() {
    local prefix=$1

    if [[ -z $prefix ]]; then
        _maven_find_env $2 $3
    else
        prefix=${prefix^^} # uppercase
        prefix=${prefix//-/_} #replace - by _

        local var_name=$prefix"_"$2
        echo ${!var_name:-$3}
      fi
}

# private
function _generate_random_id() {
    cat /dev/urandom | env LC_CTYPE=C tr -dc 'a-zA-Z0-9' | fold -w 16 | head -n 1
}

