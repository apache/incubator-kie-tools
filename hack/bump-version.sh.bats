#!/usr/bin/env bats

source ./hack/export-version.sh

OLD_VERSION=998.0.0
NEW_VERSION=999.0.0
NEW_VERSION_MAJOR_MINOR=$(echo "${NEW_VERSION}" | awk -F. '{print $1"."$2}')
function make() { 
    echo "make $@" 
}

function operator-sdk() { 
    echo "operator-sdk $@" 
}

setup() {
    # copy structure in a temp folder for the test
    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    rm -rf ${dir}
    mkdir -p ${dir}
    cp -r ${BATS_TEST_DIRNAME}/.. ${dir}/

    mkdir -p ${dir}/bundle/

    export CURRENT_VERSION=$(grep -m 1 'Version =' ${dir}/pkg/version/version.go) && CURRENT_VERSION=$(echo ${CURRENT_VERSION#*=} | tr -d '"')
}

teardown() {
    rm -rf "$BATS_TMPDIR/$BATS_TEST_NAME"
}

@test "check bump-version error with no version" {
    run ${BATS_TEST_DIRNAME}/bump-version.sh
    [ "$status" -eq 1 ]
    [[ "${output}" =~ "Please inform the new version. Use X.X.X" ]]
}

@test "check bump-version set default config with version only" {
    export -f make
    export -f operator-sdk

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    run hack/bump-version.sh ${NEW_VERSION}
    echo ${output}
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "Latest released OLM version = ${LATEST_RELEASED_OLM_VERSION}" ]]
    [[ "${output}" =~ "make bundle" ]]
    [[ "${output}" =~ "make vet" ]]

    # No version should be set in test config
    result=$(cat test/.default_config)
    [[ "${result}" =~ "latest" ]]
    [[ "${result}" =~ "master" ]]
    [[ "${result}" != *${NEW_VERSION_MAJOR_MINOR}.x* ]]
    [[ "${result}" != *${NEW_VERSION_MAJOR_MINOR}* ]]
}


@test "check bump-version set default config with version and release true" {
    export -f make
    export -f operator-sdk

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    run hack/bump-version.sh ${NEW_VERSION} true
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "Latest released OLM version = ${LATEST_RELEASED_OLM_VERSION}" ]]
    [[ "${output}" =~ "make bundle" ]]
    [[ "${output}" =~ "make vet" ]]

    # Only image version should be set
    result=$(cat test/.default_config)
    [[ "${result}" =~ ${NEW_VERSION_MAJOR_MINOR} ]]
    [[ "${result}" =~ "master" ]]
    [[ "${result}" != *${NEW_VERSION_MAJOR_MINOR}.x* ]]
    [[ "${result}" != *latest* ]]
}

@test "check bump-version set default config with version, release true and on release branch" {
    export -f make
    export -f operator-sdk

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    run hack/bump-version.sh ${NEW_VERSION} true false
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "Latest released OLM version = ${LATEST_RELEASED_OLM_VERSION}" ]]
    [[ "${output}" =~ "make bundle" ]]
    [[ "${output}" =~ "make vet" ]]

    # Release branch and image version should be set
    result=$(cat test/.default_config)
    [[ "${result}" =~ "${NEW_VERSION_MAJOR_MINOR}" ]]
    [[ "${result}" =~ "${NEW_VERSION_MAJOR_MINOR}.x" ]]
    [[ "${result}" != *master* ]]
    [[ "${result}" != *latest* ]]
}

@test "check csv file is set correctly" {
    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    #current_version=$(cat )
    run hack/bump-version.sh ${NEW_VERSION}
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "Latest released OLM version = ${LATEST_RELEASED_OLM_VERSION}" ]]

    # Check csv file
    result=$(cat config/manifests/bases/kogito-operator.clusterserviceversion.yaml)
    [[ "${result}" =~ "${LATEST_RELEASED_OLM_VERSION}" ]]
    [[ "${result}" =~ "${NEW_VERSION}" ]]
    [[ "${result}" != *${CURRENT_VERSION}* ]]
    # fine tune results
    [[ "${result}" =~ "replaces: kogito-operator.v${LATEST_RELEASED_OLM_VERSION}" ]]
    [[ "${result}" =~ "version: ${NEW_VERSION}" ]]
    [[ "${result}" =~ "operated-by: kogito-operator.${NEW_VERSION}" ]]
    [[ "${result}" =~ "quay.io/kiegroup/kogito-cloud-operator:${NEW_VERSION}" ]]
}