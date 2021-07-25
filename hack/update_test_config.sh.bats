#!/usr/bin/env bats

source ./hack/env.sh

VERSION_MAJOR_MINOR=$(echo "$(getOperatorVersion)" | awk -F. '{print $1"."$2}')

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

@test "check update_test_config default" {
    function git() { echo '* anything'; }
    export -f git

    function make() { echo "make $@"; }
    export -f make

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    hack/bump-version.sh "${VERSION_MAJOR_MINOR}.0-snapshot"
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    [[ "${testConfigFile}" =~ "tests.build-image-version=latest" ]]
    [[ "${testConfigFile}" =~ "tests.services-image-version=latest" ]]
    [[ "${testConfigFile}" =~ "tests.runtime-application-image-version=latest" ]]
    [[ "${testConfigFile}" =~ "tests.examples-ref=nightly-main" ]]
}

@test "check update_test_config on release branch" {
    function git() { echo "* ${VERSION_MAJOR_MINOR}.x"; }
    export -f git
    export VERSION_MAJOR_MINOR=${VERSION_MAJOR_MINOR}

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    [[ "${testConfigFile}" =~ "tests.build-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.services-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.runtime-application-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.examples-ref=nightly-${VERSION_MAJOR_MINOR}.x" ]]
}

@test "check update_test_config on release branch with non snapshot version" {
    function git() { echo "* ${VERSION_MAJOR_MINOR}.x"; }
    export -f git
    export VERSION_MAJOR_MINOR=${VERSION_MAJOR_MINOR}

    function make() { echo "make $@"; }
    export -f make

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    hack/bump-version.sh "${VERSION_MAJOR_MINOR}.0"
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    [[ "${testConfigFile}" =~ "tests.build-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.services-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.runtime-application-image-version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.examples-ref=${VERSION_MAJOR_MINOR}.x" ]]
}