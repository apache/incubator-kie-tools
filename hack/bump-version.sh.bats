#!/usr/bin/env bats

source ./hack/env.sh

OLD_VERSION=998.0.0
NEW_VERSION=999.0.0
NEW_VERSION_MAJOR_MINOR=$(echo "${NEW_VERSION}" | awk -F. '{print $1"."$2}')
CURRENT_VERSION=$(getOperatorVersion)

function make() { 
    echo "make $@" 
}

setup() {
    # copy structure in a temp folder for the test
    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    rm -rf ${dir}
    mkdir -p ${dir}
    cp -r ${BATS_TEST_DIRNAME}/.. ${dir}/

    mkdir -p ${dir}/bundle/
}

teardown() {
    rm -rf "$BATS_TMPDIR/$BATS_TEST_NAME"
}

@test "check bump-version error with no version" {
    run ${BATS_TEST_DIRNAME}/bump-version.sh
    [ "$status" -eq 1 ]
    [[ "${output}" =~ "Please inform the new version. Use X.X.X" ]]
}

@test "check csv file is set correctly" {
    export -f make

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    run hack/bump-version.sh ${NEW_VERSION}
    [ "$status" -eq 0 ]

    # Check csv file
    [[ "${output}" =~ "Version bumped from ${CURRENT_VERSION} to ${NEW_VERSION}" ]]
    # fine tune results
    csv_file=$(cat $(getCsvFile))
    [[ "${csv_file}" =~ "replaces: kogito-operator.v${getLatestOlmReleaseVersion}" ]]
    [[ "${csv_file}" =~ "version: ${NEW_VERSION}" ]]
    [[ "${csv_file}" =~ "operated-by: kogito-operator.${NEW_VERSION}" ]]
    [[ "${csv_file}" =~ "quay.io/kiegroup/kogito-operator:${NEW_VERSION}" ]]
}