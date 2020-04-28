#!/usr/bin/env bats

@test "invoke run-tests with dry_run" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.dry-run" ]]
}

@test "invoke run-tests unknown option" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh something
    [ "$status" -eq 1 ]
    [ "${lines[0]}" = "Unknown arguments: something" ]
}

# tests configuration

@test "invoke run-tests with feature" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --feature ${BATS_TEST_DIRNAME}/../test/features --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ " ${BATS_TEST_DIRNAME}/../test/features" ]]
}

@test "invoke run-tests with feature missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --feature --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *" features"* ]]
}

@test "invoke run-tests with feature empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --feature "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *" features"* ]]
}

@test "invoke run-tests with tags" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --tags hello --dry_run 
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"hello\"" ]]
}

@test "invoke run-tests with tags multiple values" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --tags "hello and bonjour" --dry_run 
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"hello and bonjour\"" ]]
}

@test "invoke run-tests with tags missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --tags --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"\"" ]]
}

@test "invoke run-tests with tags empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --tags "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.tags=\"\"" ]]
}

@test "invoke run-tests with concurrent" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --concurrent 3 --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--godog.concurrency=3" ]]
}

@test "invoke run-tests with concurrent missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --concurrent --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--godog.concurrency"* ]]
}

@test "invoke run-tests with concurrent empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --concurrent "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--godog.concurrency"* ]]
}

@test "invoke run-tests with timeout" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --timeout 120 --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"120m\"" ]]
}

@test "invoke run-tests with timeout missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --timeout --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"240m\"" ]]
}

@test "invoke run-tests with timeout empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --timeout "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "-timeout \"240m\"" ]]
}

@test "invoke run-tests with debug" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --debug --dry_run
    [ "$status" -eq 0 ]
    [[ "${lines[1]}" = "DEBUG=true"* ]]
}

@test "invoke run-tests without debug" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${lines[1]}" = "DEBUG=false"* ]]
}

@test "invoke run-tests with smoke" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --smoke --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.smoke" ]]
}

@test "invoke run-tests without smoke" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.smoke"* ]]
}

@test "invoke run-tests with performance" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --performance --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.performance" ]]
}

@test "invoke run-tests without performance" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.performance"* ]]
}

@test "invoke run-tests with load_factor" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --load_factor 3 --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.load-factor=3" ]]
}

@test "invoke run-tests with load_factor missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --load_factor --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.load-factor"* ]]
}

@test "invoke run-tests with load_factor empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --load_factor "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.load-factor"* ]]
}

@test "invoke run-tests with local" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --local --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.local" ]]
}

@test "invoke run-tests without local" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.local"* ]]
}

@test "invoke run-tests with ci" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --ci jenkins --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.ci=jenkins" ]]
}

@test "invoke run-tests with ci missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --ci --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.ci"* ]]
}

@test "invoke run-tests with ci empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --ci "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.ci"* ]]
}

@test "invoke run-tests with cr_deployment_only" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --cr_deployment_only --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.cr-deployment-only" ]]
}

@test "invoke run-tests without cr_deployment_only" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.cr-deployment-only"* ]]
}

@test "invoke run-tests with load_default_config" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --load_default_config --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "Load default test config" ]]
}

@test "invoke run-tests without load_default_config" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"Load default test config"* ]]
}

# operator information

@test "invoke run-tests with operator_image" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_image image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-image-name=image" ]]
}

@test "invoke run-tests with operator_image missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-image-name"* ]]
}

@test "invoke run-tests with operator_image empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_image "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-image-name"* ]]
}

@test "invoke run-tests with operator_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-image-tag=tag" ]]
}

@test "invoke run-tests with operator_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-image-tag"* ]]
}

@test "invoke run-tests with operator_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-image-tag"* ]]
}

# files/binaries

@test "invoke run-tests with deploy_uri" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --deploy_uri folder --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-deploy-uri=folder" ]]
}

@test "invoke run-tests with deploy_uri missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --deploy_uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-deploy-uri"* ]]
}

@test "invoke run-tests with deploy_uri empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --deploy_uri "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-deploy-uri"* ]]
}

@test "invoke run-tests with cli_path" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --cli_path cli --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.cli-path=cli" ]]
}

@test "invoke run-tests with cli_path missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --cli_path --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.cli-path"* ]]
}

@test "invoke run-tests with cli_path empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --cli_path "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.cli-path"* ]]
}

# runtime

@test "invoke run-tests with services_image_version" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_version version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.services-image-version=version" ]]
}

@test "invoke run-tests with services_image_version missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-version"* ]]
}

@test "invoke run-tests with services_image_version empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_version "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-version"* ]]
}

@test "invoke run-tests with services_image_namespace" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_namespace namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.services-image-namespace=namespace" ]]
}

@test "invoke run-tests with services_image_namespace missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-namespace"* ]]
}

@test "invoke run-tests with services_image_namespace empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_namespace "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-namespace"* ]]
}

@test "invoke run-tests with services_image_registry" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_registry registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.services-image-registry=registry" ]]
}

@test "invoke run-tests with services_image_registry missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-registry"* ]]
}

@test "invoke run-tests with services_image_registry empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_registry "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-registry"* ]]
}

@test "invoke run-tests with data_index_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --data_index_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.data-index-image-tag=tag" ]]
}

@test "invoke run-tests with data_index_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --data_index_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.data-index-image-tag"* ]]
}

@test "invoke run-tests with data_index_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --data_index_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.data-index-image-tag"* ]]
}

@test "invoke run-tests with jobs_service_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --jobs_service_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.jobs-service-image-tag=tag" ]]
}

@test "invoke run-tests with jobs_service_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --jobs_service_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.jobs-service-image-tag"* ]]
}

@test "invoke run-tests with jobs_service_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --jobs_service_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.jobs-service-image-tag"* ]]
}

@test "invoke run-tests with management_console_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --management_console_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.management-console-image-tag=tag" ]]
}

@test "invoke run-tests with management_console_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --management_console_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.management-console-image-tag"* ]]
}

@test "invoke run-tests with management_console_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --management_console_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.management-console-image-tag"* ]]
}

# build

@test "invoke run-tests with maven_mirror" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --maven_mirror maven --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.maven-mirror-url=maven" ]]
}

@test "invoke run-tests with maven_mirror missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --maven_mirror --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.maven-mirror-url"* ]]
}

@test "invoke run-tests with maven_mirror empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --maven_mirror "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.maven-mirror-url"* ]]
}

@test "invoke run-tests with build_image_version" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_version version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-image-version=version" ]]
}

@test "invoke run-tests with build_image_version missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-version"* ]]
}

@test "invoke run-tests with build_image_version empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_version "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-version"* ]]
}

@test "invoke run-tests with build_image_namespace" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_namespace namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-image-namespace=namespace" ]]
}

@test "invoke run-tests with build_image_namespace missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-namespace"* ]]
}

@test "invoke run-tests with build_image_namespace empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_namespace "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-namespace"* ]]
}

@test "invoke run-tests with build_image_registry" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_registry registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-image-registry=registry" ]]
}

@test "invoke run-tests with build_image_registry missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-registry"* ]]
}

@test "invoke run-tests with build_image_registry empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_registry "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-registry"* ]]
}

@test "invoke run-tests with build_s2i_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_s2i_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-s2i-image-tag=tag" ]]
}

@test "invoke run-tests with build_s2i_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_s2i_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-s2i-image-tag"* ]]
}

@test "invoke run-tests with build_s2i_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_s2i_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-s2i-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-runtime-image-tag=tag" ]]
}

@test "invoke run-tests with build_runtime_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-image-tag"* ]]
}

# examples repository

@test "invoke run-tests with examples_uri" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_uri uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.examples-uri=uri" ]]
}

@test "invoke run-tests with examples_uri missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.examples-uri"* ]]
}

@test "invoke run-tests with examples_uri empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_uri "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.examples-uri"* ]]
}

@test "invoke run-tests with examples_ref" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_ref ref --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.examples-ref=ref" ]]
}

@test "invoke run-tests with examples_ref missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_ref --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.examples-ref"* ]]
}

@test "invoke run-tests with examples_ref empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --examples_ref "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.examples-ref"* ]]
}

# dev options

@test "invoke run-tests with show_scenarios" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --show_scenarios --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.show-scenarios" ]]
}

@test "invoke run-tests with keep_namespace" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --keep_namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.keep-namespace" ]]
}

@test "invoke run-tests without keep_namespace" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.keep-namespace"* ]]
}

@test "invoke run-tests with namespace_name" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --namespace_name test-namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.dev.namespace-name=test-namespace" ]]
}

@test "invoke run-tests with namespace_name missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --namespace_name --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.dev.namespace-name"* ]]
}