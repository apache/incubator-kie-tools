#!/usr/bin/env bash
# Holds common maven configuration for CI;
# Usage: . setup-maven.sh

MAVEN_VERSION="3.8.x"

MVN_MODULE="$(dirname "${BASH_SOURCE[0]}")/../modules/kogito-maven/${MAVEN_VERSION}"
MAVEN_OPTIONS="-DskipTests"
# Do not remove below, this can be updated by the python scripts
MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE=true

maven_settings_path=$1
if [ -z "${maven_settings_path}" ]; then
    maven_settings_path="${HOME}"/.m2/settings.xml
fi

if [ "${CI}" ]; then
    echo "Updating settings file ${maven_settings_path}"

    # setup maven env
    # Do not remove below, this can be updated by the python scripts
    export JBOSS_MAVEN_REPO_URL="https://repository.jboss.org/nexus/content/groups/public/"
    # export MAVEN_REPO_URL=
    cp "${MVN_MODULE}"/maven/settings.xml "${maven_settings_path}"
    export MAVEN_SETTINGS_PATH="${maven_settings_path}"
    source "${MVN_MODULE}"/added/configure-maven.sh
    configure

    export MAVEN_OPTIONS="${MAVEN_OPTIONS} -s ${maven_settings_path}"

    # Add NPM registry if needed
    if [ ! -z "${NPM_REGISTRY_URL}" ]; then
        echo "enabling npm repository: ${NPM_REGISTRY_URL}"
        npm_profile="\
<profile>\
  <id>internal-npm-registry</id>\
  <properties>\
  <npmRegistryURL>${NPM_REGISTRY_URL}</npmRegistryURL>\
  <yarnDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/yarn/</yarnDownloadRoot>\
  <nodeDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/node/</nodeDownloadRoot>\
  <npmDownloadRoot>http://download.devel.redhat.com/rcm-guest/staging/rhba/dist/npm/</npmDownloadRoot>\
  </properties>\
</profile>\
"   
        sed -i -E "s|(<!-- ### extra maven repositories ### -->)|\1\n${npm_profile}|" "${MAVEN_SETTINGS_PATH}"
        sed -i -E "s|(<!-- ### extra maven profile ### -->)|\1\n<activeProfile>internal-npm-registry</activeProfile>|" "${MAVEN_SETTINGS_PATH}"
    fi

    cat "${maven_settings_path}"
fi

if [ "${MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE}" = "true" ]; then
    export MAVEN_OPTIONS="${MAVEN_OPTIONS} -Denforcer.skip"
fi

