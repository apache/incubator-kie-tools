# tests configuration
test_main_dir=
feature=
tags=
concurrent=1
timeout=240
debug=false
smoke=false
performance=false
load_factor=1
local=false
ci=
cr_deployment_only=false
load_default_config=false
container_engine=
domain_suffix=
image_cache_mode=
http_retry_nb=
olm_namespace=
# operator information
operator_image=
operator_tag=
operator_namespaced=false
operator_installation_source=
operator_catalog_image=
# operator profiling
operator_profiling=false
operator_profiling_data_access_yaml_uri=
operator_profiling_output_file_uri=
# files/binaries
operator_yaml_uri=
cli_path=
# runtime
services_image_registry=
services_image_name_suffix=
services_image_version=
data_index_image_tag=
explainability_image_tag=
jobs_service_image_tag=
management_console_image_tag=
task_console_image_tag=
trusty_image_tag=
trusty_ui_image_tag=
runtime_application_image_registry=
runtime_application_image_name_prefix=
runtime_application_image_name_suffix=
runtime_application_image_version=
# build
custom_maven_repo=
custom_maven_repo_replace_default=false
maven_mirror=
maven_ignore_self_signed_certificate=false
build_image_registry=
build_image_name_suffix=
build_image_version=
build_builder_image_tag=
build_runtime_jvm_image_tag=
build_runtime_native_image_tag=
disable_maven_native_build_container=false
native_builder_image=
# examples repository
examples_uri=
examples_ref=
examples_ignore_ssl=
# Infinispan
infinispan_installation_source=
# Hyperfoil
hyperfoil_output_directory=
# dev options
show_scenarios=false
show_steps=false
dry_run=false
keep_namespace=false
namespace_name=
local_cluster=false

.PHONY: run-tests
run-tests:
	declare -a opts \
	&& if [ "${debug}" = "true" ]; then opts+=("--debug"); fi \
	&& if [ "${smoke}" = "true" ]; then opts+=("--smoke"); fi \
	&& if [ "${performance}" = "true" ]; then opts+=("--performance"); fi \
	&& if [ "${local}" = "true" ]; then opts+=("--local"); fi \
	&& if [ "${local_cluster}" = "true" ]; then opts+=("--local_cluster"); fi \
	&& if [ "${cr_deployment_only}" = "true" ]; then opts+=("--cr_deployment_only"); fi \
	&& if [ "${show_scenarios}" = "true" ]; then opts+=("--show_scenarios"); fi \
	&& if [ "${show_steps}" = "true" ]; then opts+=("--show_steps"); fi \
	&& if [ "${dry_run}" = "true" ]; then opts+=("--dry_run"); fi \
	&& if [ "${keep_namespace}" = "true" ]; then opts+=("--keep_namespace"); fi \
	&& if [ "${load_default_config}" = "true" ]; then opts+=("--load_default_config"); fi \
	&& if [ "${maven_ignore_self_signed_certificate}" = "true" ]; then opts+=("--maven_ignore_self_signed_certificate"); fi \
	&& if [ "${disable_maven_native_build_container}" = "true" ]; then opts+=("--disable_maven_native_build_container"); fi \
	&& if [ "${custom_maven_repo_replace_default}" = "true" ]; then opts+=("--custom_maven_repo_replace_default"); fi \
	&& if [ "${examples_ignore_ssl}" = "true" ]; then opts+=("--examples_ignore_ssl"); fi \
	&& if [ "${operator_namespaced}" = "true" ]; then opts+=("--operator_namespaced"); fi \
	&& if [ "${operator_profiling}" = "true" ]; then opts+=("--operator_profiling"); fi \
	&& opts_str=$$(IFS=' ' ; echo "$${opts[*]}") \
	&& ../hack/run-tests.sh \
		--test_main_dir ${test_main_dir} \
		--feature ${feature} \
		--tags "${tags}" \
		--concurrent ${concurrent} \
		--timeout ${timeout} \
		--ci ${ci} \
		--operator_image $(operator_image) \
		--operator_tag $(operator_tag) \
		--operator_yaml_uri ${operator_yaml_uri} \
		--operator_profiling_data_access_yaml_uri ${operator_profiling_data_access_yaml_uri} \
		--operator_profiling_output_file_uri ${operator_profiling_output_file_uri} \
		--cli_path ${cli_path} \
		--services_image_registry ${services_image_registry} \
		--services_image_name_suffix ${services_image_name_suffix} \
		--services_image_version ${services_image_version} \
		--data_index_image_tag ${data_index_image_tag} \
		--explainability_image_tag ${explainability_image_tag} \
		--jobs_service_image_tag ${jobs_service_image_tag} \
		--management_console_image_tag ${management_console_image_tag} \
		--task_console_image_tag ${task_console_image_tag} \
		--trusty_image_tag ${trusty_image_tag} \
		--trusty_ui_image_tag ${trusty_ui_image_tag} \
		--runtime_application_image_registry ${runtime_application_image_registry} \
		--runtime_application_image_name_prefix ${runtime_application_image_name_prefix} \
		--runtime_application_image_name_suffix ${runtime_application_image_name_suffix} \
		--runtime_application_image_version ${runtime_application_image_version} \
		--custom_maven_repo $(custom_maven_repo) \
		--maven_mirror $(maven_mirror) \
		--build_image_registry ${build_image_registry} \
		--build_image_name_suffix ${build_image_name_suffix} \
		--build_image_version ${build_image_version} \
		--build_builder_image_tag ${build_builder_image_tag} \
		--build_runtime_jvm_image_tag ${build_runtime_jvm_image_tag} \
		--build_runtime_native_image_tag ${build_runtime_native_image_tag} \
		--native_builder_image ${native_builder_image} \
		--examples_uri ${examples_uri} \
		--examples_ref ${examples_ref} \
		--namespace_name ${namespace_name} \
		--load_factor ${load_factor} \
		--container_engine ${container_engine} \
		--domain_suffix ${domain_suffix} \
		--image_cache_mode ${image_cache_mode} \
		--http_retry_nb ${http_retry_nb} \
		--olm_namespace ${olm_namespace} \
		--operator_installation_source ${operator_installation_source} \
		--operator_catalog_image ${operator_catalog_image} \
		--infinispan_installation_source ${infinispan_installation_source} \
		$${opts_str}

.PHONY: build-examples-images
build-examples-images:
	make run-tests test_main_dir=./examples feature=../features cr_deployment_only=true

.PHONY: run-smoke-tests
run-smoke-tests:
	make run-tests smoke=true

.PHONY: build-smoke-examples-images
build-smoke-examples-images:
	make build-examples-images smoke=true

.PHONY: run-performance-tests
run-performance-tests:
	make run-tests performance=true

.PHONY: build-performance-examples-images
build-performance-examples-images:
	make build-examples-images performance=true