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
    sed -i "s|Version = .*|Version = \"${VERSION_MAJOR_MINOR}.0-snapshot\"|g" version/app/version.go
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    
    [[ "${testConfigFile}" =~ "tests.build_builder_image_tag=quay.io/kiegroup/kogito-builder-nightly:latest" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_jvm_image_tag=quay.io/kiegroup/kogito-runtime-jvm-nightly:latest" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_native_image_tag=quay.io/kiegroup/kogito-runtime-native-nightly:latest" ]]
    [[ "${testConfigFile}" =~ "tests.services_image_version=latest" ]]
    [[ "${testConfigFile}" =~ "tests.runtime_application_image_version=latest" ]]
    [[ "${testConfigFile}" =~ "tests.examples_ref=nightly-main" ]]
}

@test "check update_test_config on release branch with snapshot version" {
    function git() { echo "* ${VERSION_MAJOR_MINOR}.x"; }
    export -f git
    export VERSION_MAJOR_MINOR=${VERSION_MAJOR_MINOR}

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    sed -i "s|Version = .*|Version = \"${VERSION_MAJOR_MINOR}.0-snapshot\"|g" version/app/version.go
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    [[ "${testConfigFile}" =~ "tests.build_builder_image_tag=quay.io/kiegroup/kogito-builder-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_jvm_image_tag=quay.io/kiegroup/kogito-runtime-jvm-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_native_image_tag=quay.io/kiegroup/kogito-runtime-native-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.services_image_version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.runtime_application_image_version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.examples_ref=nightly-${VERSION_MAJOR_MINOR}.x" ]]
}

@test "check update_test_config on release branch with non snapshot version" {
    function git() { echo "* ${VERSION_MAJOR_MINOR}.x"; }
    export -f git
    export VERSION_MAJOR_MINOR=${VERSION_MAJOR_MINOR}

    function make() { echo "make $@"; }
    export -f make

    dir="${BATS_TMPDIR}/${BATS_TEST_NAME}"
    cd ${dir}
    sed -i "s|Version = .*|Version = \"${VERSION_MAJOR_MINOR}.0-Final\"|g" version/app/version.go
    hack/bump-version.sh "${VERSION_MAJOR_MINOR}.0"
    run hack/update_test_config.sh
    [ "$status" -eq 0 ]

    testConfigFile=$(cat ${TEST_CONFIG_FILE})
    [[ "${testConfigFile}" =~ "tests.build_builder_image_tag=quay.io/kiegroup/kogito-builder-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_jvm_image_tag=quay.io/kiegroup/kogito-runtime-jvm-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.build_runtime_native_image_tag=quay.io/kiegroup/kogito-runtime-native-nightly:${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.services_image_version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.runtime_application_image_version=${VERSION_MAJOR_MINOR}" ]]
    [[ "${testConfigFile}" =~ "tests.examples_ref=${VERSION_MAJOR_MINOR}.x" ]]
}