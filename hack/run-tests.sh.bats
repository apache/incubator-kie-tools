#!/usr/bin/env bats

function go() { 
    echo '' 
}

function oc() { 
    echo ''
}

export -f go
export -f oc

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

@test "invoke run-tests with test_main_dir" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --test_main_dir ${BATS_TEST_DIRNAME}/../test/scripts/examples --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ " ${BATS_TEST_DIRNAME}/../test/scripts/examples" ]]
}

@test "invoke run-tests with test_main_dir missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --test_main_dir --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ " ${BATS_TEST_DIRNAME}/../test" ]]
}

@test "invoke run-tests with test_main_dir empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --test_main_dir "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ " ${BATS_TEST_DIRNAME}/../test" ]]
}

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
    [[ "${output}" =~ "DEBUG=true go test"* ]]

}

@test "invoke run-tests without debug" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "DEBUG=false go test"* ]]
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

@test "invoke run-tests with container_engine" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --container_engine podman --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.container-engine=podman" ]]
}

@test "invoke run-tests with container_engine missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --container_engine --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.container-engine"* ]]
}

@test "invoke run-tests with container_engine empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --container_engine "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.container-engine"* ]]
}

@test "invoke run-tests with domain_suffix" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --domain_suffix suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.domain-suffix=suffix" ]]
}

@test "invoke run-tests with domain_suffix missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --domain_suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.domain-suffix"* ]]
}

@test "invoke run-tests with domain_suffix empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --domain_suffix "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.domain-suffix"* ]]
}

@test "invoke run-tests with image_cache_mode" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --image_cache_mode always --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.image-cache-mode=always" ]]
}

@test "invoke run-tests with image_cache_mode missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --image_cache_mode --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.image-cache-mode"* ]]
}

@test "invoke run-tests with image_cache_mode empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --image_cache_mode "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.image-cache-mode"* ]]
}

@test "invoke run-tests with http_retry_nb" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --http_retry_nb 3 --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.http-retry-nb=3" ]]
}

@test "invoke run-tests with http_retry_nb missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --http_retry_nb --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.http-retry-nb"* ]]
}

@test "invoke run-tests with http_retry_nb empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --http_retry_nb "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.http-retry-nb"* ]]
}

@test "invoke run-tests with olm_namespace" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --olm_namespace olm --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.olm-namespace=olm" ]]
}

@test "invoke run-tests with olm_namespace missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --olm_namespace --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.olm-namespace"* ]]
}

@test "invoke run-tests with olm_namespace empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --olm_namespace "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.olm-namespace"* ]]
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

@test "invoke run-tests with operator_namespaced" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_namespaced --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-namespaced" ]]
}

@test "invoke run-tests without operator_namespaced" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-namespaced"* ]]
}

@test "invoke run-tests with operator_installation_source" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_installation_source olm --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-installation-source=olm" ]]
}

@test "invoke run-tests with operator_installation_source missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_installation_source --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-installation-source"* ]]
}

@test "invoke run-tests with operator_installation_source empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_installation_source "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-installation-source"* ]]
}

@test "invoke run-tests with operator_catalog_image" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_catalog_image catalog-image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-catalog-image=catalog-image" ]]
}

@test "invoke run-tests with operator_catalog_image missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_catalog_image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-catalog-image"* ]]
}

@test "invoke run-tests with operator_catalog_image empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_catalog_image "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-catalog-image"* ]]
}

# operator profiling

@test "invoke run-tests with operator_profiling" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-profiling" ]]
}

@test "invoke run-tests without operator_profiling" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-profiling"* ]]
}

@test "invoke run-tests with operator_profiling_data_access_yaml_uri" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_data_access_yaml_uri uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-profiling-data-access-yaml-uri=uri" ]]
}

@test "invoke run-tests with operator_profiling_data_access_yaml_uri missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_data_access_yaml_uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-profiling-data-access-yaml-uri"* ]]
}

@test "invoke run-tests with operator_profiling_data_access_yaml_uri empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_data_access_yaml_uri "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-profiling-data-access-yaml-uri"* ]]
}

@test "invoke run-tests with operator_profiling_output_file_uri" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_output_file_uri uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-profiling-output-file-uri=uri" ]]
}

@test "invoke run-tests with operator_profiling_output_file_uri missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_output_file_uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-profiling-output-file-uri"* ]]
}

@test "invoke run-tests with operator_profiling_output_file_uri empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_profiling_output_file_uri "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-profiling-output-file-uri"* ]]
}

# files/binaries

@test "invoke run-tests with operator_yaml_uri" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_yaml_uri file.yaml --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.operator-yaml-uri=file.yaml" ]]
}

@test "invoke run-tests with operator_yaml_uri missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_yaml_uri --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-yaml-uri"* ]]
}

@test "invoke run-tests with operator_yaml_uri empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --operator_yaml_uri "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.operator-yaml-uri"* ]]
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

@test "invoke run-tests with services_image_name_suffix" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_name_suffix suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.services-image-name-suffix=suffix" ]]
}

@test "invoke run-tests with services_image_name_suffix missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_name_suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-name-suffix"* ]]
}

@test "invoke run-tests with services_image_name_suffix empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --services_image_name_suffix "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.services-image-name-suffix"* ]]
}

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
@test "invoke run-tests with explainability_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --explainability_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.explainability-image-tag=tag" ]]
}

@test "invoke run-tests with explainability_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --explainability_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.explainability-image-tag"* ]]
}

@test "invoke run-tests with explainability_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --explainability_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.explainability-image-tag"* ]]
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

@test "invoke run-tests with task_console_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --task_console_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.task-console-image-tag=tag" ]]
}

@test "invoke run-tests with task_console_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --task_console_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.task-console-image-tag"* ]]
}

@test "invoke run-tests with task_console_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --task_console_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.task-console-image-tag"* ]]
}

@test "invoke run-tests with trusty_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.trusty-image-tag=tag" ]]
}

@test "invoke run-tests with trusty_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.trusty-image-tag"* ]]
}

@test "invoke run-tests with trusty_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.trusty-image-tag"* ]]
}

@test "invoke run-tests with trusty_ui_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_ui_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.trusty-ui-image-tag=tag" ]]
}

@test "invoke run-tests with trusty_ui_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_ui_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.trusty-ui-image-tag"* ]]
}

@test "invoke run-tests with trusty_ui_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --trusty_ui_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.trusty-ui-image-tag"* ]]
}

@test "invoke run-tests with runtime_application_image_registry" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_registry registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.runtime-application-image-registry=registry" ]]
}

@test "invoke run-tests with runtime_application_image_registry missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_registry --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-registry"* ]]
}

@test "invoke run-tests with runtime_application_image_registry empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_registry "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-registry"* ]]
}

@test "invoke run-tests with runtime_application_image_name_prefix" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_prefix prefix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.runtime-application-image-name-prefix=prefix" ]]
}

@test "invoke run-tests with runtime_application_image_name_prefix missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_prefix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-name-prefix"* ]]
}

@test "invoke run-tests with runtime_application_image_name_prefix empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_prefix "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-name-prefix"* ]]
}

@test "invoke run-tests with runtime_application_image_name_suffix" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_suffix suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.runtime-application-image-name-suffix=suffix" ]]
}

@test "invoke run-tests with runtime_application_image_name_suffix missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-name-suffix"* ]]
}

@test "invoke run-tests with runtime_application_image_name_suffix empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_name_suffix "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-name-suffix"* ]]
}

@test "invoke run-tests with runtime_application_image_version" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_version latest --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.runtime-application-image-version=latest" ]]
}

@test "invoke run-tests with runtime_application_image_version missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-version"* ]]
}

@test "invoke run-tests with runtime_application_image_version empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --runtime_application_image_version "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.runtime-application-image-version"* ]]
}

# build

@test "invoke run-tests with custom_maven_repo" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --custom_maven_repo repourl --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.custom-maven-repo-url=repourl" ]]
}

@test "invoke run-tests with custom_maven_repo missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --custom_maven_repo --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.custom-maven-repo-url"* ]]
}

@test "invoke run-tests with custom_maven_repo empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --custom_maven_repo "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.custom-maven-repo-url"* ]]
}

@test "invoke run-tests with custom_maven_repo_replace_default" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --custom_maven_repo_replace_default --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.custom-maven-repo-replace-default" ]]
}

@test "invoke run-tests without custom_maven_repo_replace_default" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.custom-maven-repo-replace-default"* ]]
}

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

@test "invoke run-tests with maven_ignore_self_signed_certificate" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --maven_ignore_self_signed_certificate --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.maven-ignore-self-signed-certificate" ]]
}

@test "invoke run-tests without maven_ignore_self_signed_certificate" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.maven-ignore-self-signed-certificate"* ]]
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

@test "invoke run-tests with build_image_name_suffix" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_name_suffix suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-image-name-suffix=suffix" ]]
}

@test "invoke run-tests with build_image_name_suffix missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_name_suffix --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-name-suffix"* ]]
}

@test "invoke run-tests with build_image_name_suffix empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_image_name_suffix "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-image-name-suffix"* ]]
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

@test "invoke run-tests with build_builder_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_builder_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-builder-image-tag=tag" ]]
}

@test "invoke run-tests with build_builder_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_builder_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-builder-image-tag"* ]]
}

@test "invoke run-tests with build_builder_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_builder_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-builder-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_jvm_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_jvm_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-runtime-jvm-image-tag=tag" ]]
}

@test "invoke run-tests with build_runtime_jvm_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_jvm_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-jvm-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_jvm_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_jvm_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-jvm-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_native_image_tag" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_native_image_tag tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.build-runtime-native-image-tag=tag" ]]
}

@test "invoke run-tests with build_runtime_native_image_tag missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_native_image_tag --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-native-image-tag"* ]]
}

@test "invoke run-tests with build_runtime_native_image_tag empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --build_runtime_native_image_tag "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.build-runtime-native-image-tag"* ]]
}

@test "invoke run-tests with disable_maven_native_build_container" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --disable_maven_native_build_container --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.disable-maven-native-build-container" ]]
}

@test "invoke run-tests without disable_maven_native_build_container" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.disable-maven-native-build-container"* ]]
}

@test "invoke run-tests with native_builder_image" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --native_builder_image image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.native-builder-image=image" ]]
}

@test "invoke run-tests with native_builder_image missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --native_builder_image --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.native-builder-image"* ]]
}

@test "invoke run-tests with native_builder_image empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --native_builder_image "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.native-builder-image"* ]]
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

# Infinispan

@test "invoke run-tests with infinispan_installation_source" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_installation_source yaml --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.infinispan-installation-source=yaml" ]]
}

@test "invoke run-tests with infinispan_installation_source missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_installation_source --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.infinispan-installation-source"* ]]
}

@test "invoke run-tests with infinispan_installation_source empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_installation_source "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.infinispan-installation-source"* ]]
}

@test "invoke run-tests with infinispan_storage_class" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_storage_class local --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.infinispan-storage-class=local" ]]
}

@test "invoke run-tests with infinispan_storage_class missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_storage_class --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.infinispan-storage-class"* ]]
}

@test "invoke run-tests with infinispan_storage_class empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --infinispan_storage_class "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.infinispan-storage-class"* ]]
}

# Hyperfoil

@test "invoke run-tests with hyperfoil_output_directory" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_output_directory /some/folder --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.hyperfoil-output-directory=/some/folder" ]]
}

@test "invoke run-tests with hyperfoil_output_directory missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_output_directory --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.hyperfoil-output-directory"* ]]
}

@test "invoke run-tests with hyperfoil_output_directory empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_output_directory "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.hyperfoil-output-directory"* ]]
}

@test "invoke run-tests with hyperfoil_controller_image_version" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_controller_image_version 0.1.0 --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.hyperfoil-controller-image-version=0.1.0" ]]
}

@test "invoke run-tests with hyperfoil_controller_image_version missing value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_controller_image_version --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.hyperfoil-controller-image-version"* ]]
}

@test "invoke run-tests with hyperfoil_controller_image_version empty value" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --hyperfoil_controller_image_version "" --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.hyperfoil-controller-image-version"* ]]
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

@test "invoke run-tests with local_cluster" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --local_cluster --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" =~ "--tests.dev.local-cluster" ]]
}

@test "invoke run-tests without local_cluster" {
    run ${BATS_TEST_DIRNAME}/run-tests.sh --dry_run
    [ "$status" -eq 0 ]
    [[ "${output}" != *"--tests.dev.local-cluster"* ]]
}