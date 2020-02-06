#!/usr/bin/env bats

@test "invoke run-smoke" {
    run ./run-smoke.sh --dry-run
    [ "$status" -eq 0 ]
}

@test "invoke run-smoke unknown option" {
    run ./run-smoke.sh something
    [ "$status" -eq 1 ]
    [ "${lines[0]}" = "Unknown arguments: something" ]
}

@test "invoke run-smoke with tags" {
    run ./run-smoke.sh --tags hello --dry-run 
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=hello && ~@disabled " ]]
}

@test "invoke run-smoke with tags missing value" {
    run ./run-smoke.sh --tags --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with tags empty value" {
    run ./run-smoke.sh --tags "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with concurrent" {
    run ./run-smoke.sh --concurrent 3 --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 3 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with concurrent missing value" {
    run ./run-smoke.sh --concurrent --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with concurrent empty value" {
    run ./run-smoke.sh --concurrent "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with feature" {
    run ./run-smoke.sh --feature feature --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled feature" ]]
}

@test "invoke run-smoke with feature missing value" {
    run ./run-smoke.sh --feature --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with feature empty value" {
    run ./run-smoke.sh --feature "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${lines[0]}" = "godog -c 1 --random -f progress --tags=~@disabled " ]]
}

@test "invoke run-smoke with local true" {
    run ./run-smoke.sh --local true --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "LOCAL_TESTS=true" ]]
}

@test "invoke run-smoke with local false" {
    run ./run-smoke.sh --local false --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"LOCAL_TESTS="* ]]
}

@test "invoke run-smoke with local missing value" {
    run ./run-smoke.sh --local --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"LOCAL_TESTS="* ]]
}

@test "invoke run-smoke with local empty value" {
    run ./run-smoke.sh --local "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"LOCAL_TESTS="* ]]
}

@test "invoke run-smoke with operator_image" {
    run ./run-smoke.sh --operator_image image --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "OPERATOR_IMAGE_NAME=image" ]]
}

@test "invoke run-smoke with operator_image missing value" {
    run ./run-smoke.sh --operator_image --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_IMAGE_NAME="* ]]
}

@test "invoke run-smoke with operator_image empty value" {
    run ./run-smoke.sh --operator_image "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_IMAGE_NAME="* ]]
}

@test "invoke run-smoke with operator_tag" {
    run ./run-smoke.sh --operator_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "OPERATOR_IMAGE_TAG=tag" ]]
}

@test "invoke run-smoke with operator_tag missing value" {
    run ./run-smoke.sh --operator_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_IMAGE_TAG="* ]]
}

@test "invoke run-smoke with operator_tag empty value" {
    run ./run-smoke.sh --operator_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_IMAGE_TAG="* ]]
}

@test "invoke run-smoke with cli_path" {
    run ./run-smoke.sh --cli_path cli --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "OPERATOR_CLI_PATH=cli" ]]
}

@test "invoke run-smoke with cli_path missing value" {
    run ./run-smoke.sh --cli_path --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_CLI_PATH="* ]]
}

@test "invoke run-smoke with cli_path empty value" {
    run ./run-smoke.sh --cli_path "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_CLI_PATH="* ]]
}

@test "invoke run-smoke with deploy_uri" {
    run ./run-smoke.sh --deploy_uri folder --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "OPERATOR_DEPLOY_FOLDER=folder" ]]
}

@test "invoke run-smoke with deploy_uri missing value" {
    run ./run-smoke.sh --deploy_uri --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_DEPLOY_FOLDER="* ]]
}

@test "invoke run-smoke with deploy_uri empty value" {
    run ./run-smoke.sh --deploy_uri "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"OPERATOR_DEPLOY_FOLDER="* ]]
}

@test "invoke run-smoke with maven_mirror" {
    run ./run-smoke.sh --maven_mirror maven --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "MAVEN_MIRROR_URL=maven" ]]
}

@test "invoke run-smoke with maven_mirror missing value" {
    run ./run-smoke.sh --maven_mirror --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"MAVEN_MIRROR_URL="* ]]
}

@test "invoke run-smoke with maven_mirror empty value" {
    run ./run-smoke.sh --maven_mirror "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"MAVEN_MIRROR_URL="* ]]
}

@test "invoke run-smoke with build_image_version" {
    run ./run-smoke.sh --build_image_version version --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_BUILD_IMAGE_VERSION=version" ]]
}

@test "invoke run-smoke with build_image_version missing value" {
    run ./run-smoke.sh --build_image_version --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_IMAGE_VERSION="* ]]
}

@test "invoke run-smoke with build_image_version empty value" {
    run ./run-smoke.sh --build_image_version "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_IMAGE_VERSION="* ]]
}

@test "invoke run-smoke with build_image_tag" {
    run ./run-smoke.sh --build_image_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_BUILD_IMAGE_STREAM_TAG=tag" ]]
}

@test "invoke run-smoke with build_image_tag missing value" {
    run ./run-smoke.sh --build_image_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with build_image_tag empty value" {
    run ./run-smoke.sh --build_image_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with build_s2i_image_tag" {
    run ./run-smoke.sh --build_s2i_image_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_BUILD_S2I_IMAGE_STREAM_TAG=tag" ]]
}

@test "invoke run-smoke with build_s2i_image_tag missing value" {
    run ./run-smoke.sh --build_s2i_image_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_S2I_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with build_s2i_image_tag empty value" {
    run ./run-smoke.sh --build_s2i_image_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_S2I_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with build_runtime_image_tag" {
    run ./run-smoke.sh --build_runtime_image_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_BUILD_RUNTIME_IMAGE_STREAM_TAG=tag" ]]
}

@test "invoke run-smoke with build_runtime_image_tag missing value" {
    run ./run-smoke.sh --build_runtime_image_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_RUNTIME_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with build_runtime_image_tag empty value" {
    run ./run-smoke.sh --build_runtime_image_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_BUILD_RUNTIME_IMAGE_STREAM_TAG="* ]]
}

@test "invoke run-smoke with examples_uri" {
    run ./run-smoke.sh --examples_uri uri --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_EXAMPLES_REPOSITORY_URI=uri" ]]
}

@test "invoke run-smoke with examples_uri missing value" {
    run ./run-smoke.sh --examples_uri --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_EXAMPLES_REPOSITORY_URI="* ]]
}

@test "invoke run-smoke with examples_uri empty value" {
    run ./run-smoke.sh --examples_uri "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_EXAMPLES_REPOSITORY_URI="* ]]
}

@test "invoke run-smoke with examples_ref" {
    run ./run-smoke.sh --examples_ref ref --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" =~ "KOGITO_EXAMPLES_REPOSITORY_REF=ref" ]]
}

@test "invoke run-smoke with examples_ref missing value" {
    run ./run-smoke.sh --examples_ref --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_EXAMPLES_REPOSITORY_REF="* ]]
}

@test "invoke run-smoke with examples_ref empty value" {
    run ./run-smoke.sh --examples_ref "" --dry-run
    [ "$status" -eq 0 ]
    [[ "$output" != *"KOGITO_EXAMPLES_REPOSITORY_REF="* ]]
}