#!/usr/bin/env bash
# Holds common maven configuration for CI;
# Usage: . setup-maven.sh

MAVEN_VERSION="3.8.x"

MVN_MODULE="$(dirname "${BASH_SOURCE[0]}")/../modules/kogito-maven/${MAVEN_VERSION}"
MAVEN_OPTIONS="-DskipTests"

if [ "${CI}" ]; then
    # setup maven env
    export JBOSS_MAVEN_REPO_URL="https://repository.jboss.org/nexus/content/groups/public/"
    # export MAVEN_REPO_URL=
    cp "${MVN_MODULE}"/maven/settings.xml "${HOME}"/.m2/settings.xml
    source "${MVN_MODULE}"/added/configure-maven.sh
    configure

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
        sed -i -E "s|(<!-- ### extra maven repositories ### -->)|\1\n${npm_profile}|" "${HOME}"/.m2/settings.xml
        sed -i -E "s|(<!-- ### extra maven profile ### -->)|\1\n<activeProfile>internal-npm-registry</activeProfile>|" "${HOME}"/.m2/settings.xml
    fi

    cat "${HOME}"/.m2/settings.xml
fi

if [ "${MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE}" = "true" ]; then
    MAVEN_OPTIONS="${MAVEN_OPTIONS} -Denforcer.skip"
fi

