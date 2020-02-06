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
    [[ "${output}" =~ "--godog.tags=\"hello\"" ]]
}

@test "invoke run-smoke with tags multiple values" {
    run ./run-smoke.sh --tags "hello and bonjour" --dry-run 
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"hello and bonjour\"" ]]
}

@test "invoke run-smoke with tags missing value" {
    run ./run-smoke.sh --tags --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"\"" ]]
}

@test "invoke run-smoke with tags empty value" {
    run ./run-smoke.sh --tags "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"\"" ]]
}

@test "invoke run-smoke with concurrent" {
    run ./run-smoke.sh --concurrent 3 --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.concurrency=3" ]]
}

@test "invoke run-smoke with concurrent missing value" {
    run ./run-smoke.sh --concurrent --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--godog.concurrency"* ]]
}

@test "invoke run-smoke with concurrent empty value" {
    run ./run-smoke.sh --concurrent "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--godog.concurrency"* ]]
}

@test "invoke run-smoke with feature" {
    run ./run-smoke.sh --feature feature --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ " feature" ]]
}

@test "invoke run-smoke with feature missing value" {
    run ./run-smoke.sh --feature --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *" feature"* ]]
}

@test "invoke run-smoke with feature empty value" {
    run ./run-smoke.sh --feature "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *" feature"* ]]
}

@test "invoke run-smoke with timeout" {
    run ./run-smoke.sh --timeout 120 --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"120m\"" ]]
}

@test "invoke run-smoke with timeout missing value" {
    run ./run-smoke.sh --timeout --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"240m\"" ]]
}

@test "invoke run-smoke with timeout empty value" {
    run ./run-smoke.sh --timeout "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"240m\"" ]]
}

@test "invoke run-smoke with local true" {
    run ./run-smoke.sh --local true --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.local" ]]
}

@test "invoke run-smoke with local false" {
    run ./run-smoke.sh --local false --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.local"* ]]
}

@test "invoke run-smoke with local missing value" {
    run ./run-smoke.sh --local --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.local"* ]]
}

@test "invoke run-smoke with local empty value" {
    run ./run-smoke.sh --local "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.local"* ]]
}

@test "invoke run-smoke with debug true" {
    run ./run-smoke.sh --debug true --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" = "DEBUG=true"* ]]
}

@test "invoke run-smoke with debug false" {
    run ./run-smoke.sh --debug false --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" = "DEBUG=false"* ]]
}

@test "invoke run-smoke with debug missing value" {
    run ./run-smoke.sh --debug --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" = "DEBUG=false"* ]]
}

@test "invoke run-smoke with debug empty value" {
    run ./run-smoke.sh --debug "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" = "DEBUG=false"* ]]
}

@test "invoke run-smoke with operator_image" {
    run ./run-smoke.sh --operator_image image --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.operator-image-name=image" ]]
}

@test "invoke run-smoke with operator_image missing value" {
    run ./run-smoke.sh --operator_image --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-image-name"* ]]
}

@test "invoke run-smoke with operator_image empty value" {
    run ./run-smoke.sh --operator_image "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-image-name"* ]]
}

@test "invoke run-smoke with operator_tag" {
    run ./run-smoke.sh --operator_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.operator-image-tag=tag" ]]
}

@test "invoke run-smoke with operator_tag missing value" {
    run ./run-smoke.sh --operator_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-image-tag"* ]]
}

@test "invoke run-smoke with operator_tag empty value" {
    run ./run-smoke.sh --operator_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-image-tag"* ]]
}

@test "invoke run-smoke with cli_path" {
    run ./run-smoke.sh --cli_path cli --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.cli-path=cli" ]]
}

@test "invoke run-smoke with cli_path missing value" {
    run ./run-smoke.sh --cli_path --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.cli-path"* ]]
}

@test "invoke run-smoke with cli_path empty value" {
    run ./run-smoke.sh --cli_path "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.cli-path"* ]]
}

@test "invoke run-smoke with deploy_uri" {
    run ./run-smoke.sh --deploy_uri folder --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.operator-deploy-uri=folder" ]]
}

@test "invoke run-smoke with deploy_uri missing value" {
    run ./run-smoke.sh --deploy_uri --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-deploy-uri"* ]]
}

@test "invoke run-smoke with deploy_uri empty value" {
    run ./run-smoke.sh --deploy_uri "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.operator-deploy-uri"* ]]
}

@test "invoke run-smoke with maven_mirror" {
    run ./run-smoke.sh --maven_mirror maven --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.maven-mirror-url=maven" ]]
}

@test "invoke run-smoke with maven_mirror missing value" {
    run ./run-smoke.sh --maven_mirror --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.maven-mirror-url"* ]]
}

@test "invoke run-smoke with maven_mirror empty value" {
    run ./run-smoke.sh --maven_mirror "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.maven-mirror-url"* ]]
}

@test "invoke run-smoke with build_image_version" {
    run ./run-smoke.sh --build_image_version version --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.build-image-version=version" ]]
}

@test "invoke run-smoke with build_image_version missing value" {
    run ./run-smoke.sh --build_image_version --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-image-version"* ]]
}

@test "invoke run-smoke with build_image_version empty value" {
    run ./run-smoke.sh --build_image_version "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-image-version"* ]]
}

@test "invoke run-smoke with build_s2i_image_tag" {
    run ./run-smoke.sh --build_s2i_image_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.build-s2i-image-tag=tag" ]]
}

@test "invoke run-smoke with build_s2i_image_tag missing value" {
    run ./run-smoke.sh --build_s2i_image_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-s2i-image-tag"* ]]
}

@test "invoke run-smoke with build_s2i_image_tag empty value" {
    run ./run-smoke.sh --build_s2i_image_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-s2i-image-tag"* ]]
}

@test "invoke run-smoke with build_runtime_image_tag" {
    run ./run-smoke.sh --build_runtime_image_tag tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.build-runtime-image-tag=tag" ]]
}

@test "invoke run-smoke with build_runtime_image_tag missing value" {
    run ./run-smoke.sh --build_runtime_image_tag --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-runtime-image-tag"* ]]
}

@test "invoke run-smoke with build_runtime_image_tag empty value" {
    run ./run-smoke.sh --build_runtime_image_tag "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.build-runtime-image-tag"* ]]
}

@test "invoke run-smoke with examples_uri" {
    run ./run-smoke.sh --examples_uri uri --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.examples-uri=uri" ]]
}

@test "invoke run-smoke with examples_uri missing value" {
    run ./run-smoke.sh --examples_uri --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.examples-uri"* ]]
}

@test "invoke run-smoke with examples_uri empty value" {
    run ./run-smoke.sh --examples_uri "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.examples-uri"* ]]
}

@test "invoke run-smoke with examples_ref" {
    run ./run-smoke.sh --examples_ref ref --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--smoke.examples-ref=ref" ]]
}

@test "invoke run-smoke with examples_ref missing value" {
    run ./run-smoke.sh --examples_ref --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.examples-ref"* ]]
}

@test "invoke run-smoke with examples_ref empty value" {
    run ./run-smoke.sh --examples_ref "" --dry-run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--smoke.examples-ref"* ]]
}