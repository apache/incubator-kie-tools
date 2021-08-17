#!/bin/bash
# Copyright 2019 Red Hat, Inc. and/or its affiliates
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# runs all BDD tests for the operator
SCRIPT_NAME=`basename $0`
SCRIPT_DIR=`dirname $0`

function usage(){
  printf "Run BDD tests."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  
  # tests configuration
  printf "\n--test_main_dir {TEST_MAIN_DIR}\n\tWhere to find the `main_test.go` file. Default to `{kogito_operator}/test/`."
  printf "\n--feature {FEATURE_PATH}\n\tRun a specific feature file."
  printf "\n--tags {TAGS}\n\tFilter scenarios by tags."
    printf "\n\tExpressions can be:"
      printf "\n\t\t- '@wip': run all scenarios with wip tag"
      printf "\n\t\t- '~@wip': exclude all scenarios with wip tag"
      printf "\n\t\t- '@wip && ~@new': run wip scenarios, but exclude new"
      printf "\n\t\t- '@wip,@undone': run wip or undone scenarios"
    printf "\n\t Scenarios with '@disabled' tag are always ignored."
  printf "\n--concurrent {NUMBER}\n\tSet the number of concurrent tests. Default is 1."
  printf "\n--timeout {TIMEOUT_IN_MINUTES}\n\tSet a timeout overall tests run in minutes. Default is 240."
  printf "\n--debug\n\tRun in debug mode."
  printf "\n--smoke\n\tFilter to run only the tests tagged with '@smoke'."
  printf "\n--performance\n\tFilter to run only the tests tagged with '@performance'. If not provided and the tag itself is not specified, these tests will be ignored."
  printf "\n--load_factor {INT_VALUE}\n\tSet the tests load factor. Useful for the tests to take into account that the cluster can be overloaded, for example for the calculation of timeouts. Default value is 1."
  printf "\n--local\n\tSpecify whether you run test in local using either a local or remote cluster."
  printf "\n--ci {CI_NAME}\n\tSpecify whether you run test with ci, give also the name of the CI."
  printf "\n--cr_deployment_only\n\tUse this option if you have no CLI to test against. It will use only direct CR deployments."
  printf "\n--load_default_config\n\tTo be used if you want to directly use the default test config contained into ${SCRIPT_DIR}/../test/.default_config"
  printf "\n--container_engine\n\tTo be used if you want to specify engine to interact with images and containers. Default is docker."
  printf "\n--domain_suffix\n\tTo be used if you want to set a domain suffix for exposed services. Ignored when running tests on Openshift."
  printf "\n--image_cache_mode\n\tUse this option to specify whether you want to use image cache for runtime images. Available options are 'always', 'never' or 'if-available'(default)."
  printf "\n--http_retry_nb {INT_VALUE}\n\tSet the retry number for all HTTP calls in case it fails (and response code != 500). Default value is 3."
  printf "\n--olm_namespace \n\tSet the namespace which is used for cluster scope operators. Default is 'openshift-operators'."

  # operator information
  printf "\n--operator_image {NAME}\n\tOperator image name. Default is 'quay.io/kiegroup/kogito-operator' one."
  printf "\n--operator_tag {TAG}\n\tOperator image tag. Default is operator version."
  printf "\n--operator_namespaced {TAG}\n\tSet to true to deploy Kogito operator into namespace used for scenario execution, false for cluster wide deployment. Default is false."
  printf "\n--operator_installation_source {TAG}\n\tDefines installation source for the Kogito operator. Options are 'olm' and 'yaml'. Default is yaml."
  printf "\n--operator_catalog_image {TAG}\n\tDefines image containing operator catalog. Needs to be specified only when operator_installation_source is 'olm'."

  # operator profiling
  printf "\n--operator_profiling\n\tEnable the profiling of the operator. If enabled, operator will be automatically deployed with yaml files."
  printf "\n--operator_profiling_data_access_yaml_uri\n\tUrl or Path to kogito-operator-profiling-data-access.yaml file."
  printf "\n--operator_profiling_output_file_uri\n\tUrl or Path where to store the profiling outputs."

  # files/binaries
  printf "\n--operator_yaml_uri {URI}\n\tUrl or Path to kogito-operator.yaml file."
  printf "\n--cli_path {PATH}\n\tPath to built CLI to test. Default is local built one."

  # runtime

  printf "\n--services_image_registry {REGISTRY}\n\tSet the services image registry."
  printf "\n--services_image_namespace {NAMESPACE}\n\tSet the services image namespace."
  printf "\n--services_image_name_suffix {NAMESPACE}\n\tSet the build image name suffix to append to usual image names."
  printf "\n--services_image_version {VERSION}\n\tSet the services image version."
  printf "\n--data_index_image_tag {IMAGE_TAG}\n\tSet the Kogito Data Index image tag ('services_image_version' is ignored)"
  printf "\n--explainability_image_tag {IMAGE_TAG}\n\tSet the Kogito Explainability image tag ('services-image-version' is ignored)"
  printf "\n--jobs_service_image_tag {IMAGE_TAG}\n\tSet the Kogito Jobs Service image tag ('services_image_version' is ignored)"
  printf "\n--management_console_image_tag {IMAGE_TAG}\n\tSet the Kogito Management Console image tag ('services_image_version' is ignored)"
  printf "\n--task_console_image_tag {IMAGE_TAG}\n\tSet the Kogito Task Console image tag ('services-image-version' is ignored)"
  printf "\n--trusty_image_tag {IMAGE_TAG}\n\tSet the Kogito Trusty image tag ('services_image_version' is ignored)"
  printf "\n--trusty_ui_image_tag {IMAGE_TAG}\n\tSet the Kogito Trusty UI image tag ('services_image_version' is ignored)"
  printf "\n--runtime_application_image_registry {REGISTRY}\n\tSet the registry for built runtime applications."
  printf "\n--runtime_application_image_namespace {NAMESPACE}\n\tSet the namespace for built runtime applications."
  printf "\n--runtime_application_image_name_prefix {NAME_PREFIX}\n\tSet the image name suffix to prepend to usual image names for built runtime applications."
  printf "\n--runtime_application_image_name_suffix {NAME_SUFFIX}\n\tSet the image name suffix to append to usual image names for built runtime applications."
  printf "\n--runtime_application_image_version {VERSION}\n\tSet the version for built runtime applications."

  # build
  printf "\n--custom_maven_repo {URI}\n\tSet a custom Maven repository url for S2I builds, in case your artifacts are in a specific repository. See https://github.com/kiegroup/kogito-images/README.md for more information."
  printf "\n--custom_maven_repo_replace_default\n\tIf you specified the option 'custom_maven_repo' and you want that one to replace the main JBoss repository (useful with snapshots)."
  printf "\n--maven_mirror {URI}\n\tMaven mirror url to be used when building app in the tests."
  printf "\n--maven_ignore_self_signed_certificate\n\tSet to true if maven build need to ignore self-signed certificate. This could happen when using internal maven mirror url."
  printf "\n--build_image_registry {REGISTRY}\n\tSet the build image registry."
  printf "\n--build_image_namespace {NAMESPACE}\n\tSet the build image namespace."
  printf "\n--build_image_name_suffix {NAMESPACE}\n\tSet the build image name suffix to append to usual image names."
  printf "\n--build_image_version {VERSION}\n\tSet the build image version."
  printf "\n--build_image_tag {TAG}\n\tSet the build image full tag."
  printf "\n--build_s2i_image_tag {TAG}\n\tSet the S2I build image full tag."
  printf "\n--build_runtime_image_tag {NAME}\n\tSet the Runtime build image full tag."
  printf "\n--disable_maven_native_build_container\n\tBy default, Maven native builds are done in container (via container engine). Possibility to disable it."

  # examples repository
  printf "\n--examples_uri {URI}\n\tSet the URI for the kogito-examples repository. Default is https://github.com/kiegroup/kogito-examples."
  printf "\n--examples_ref {REF}\n\tSet the branch for the kogito-examples repository. Default is none."
  printf "\n--examples_ignore_ssl\n\tTell Git to ignore SSL check when checking out examples repository."

  # Infinispan
  printf "\n--infinispan_installation_source {TAG}\n\tDefines installation source for the Infinispan operator. Options are 'olm' and 'yaml'. Default is olm."

  # Hyperfoil
  printf "\n--hyperfoil_output_directory {PATH}\n\tDefines output directory to store Hyperfoil run statistics. Default is test folder."

  # dev options
  printf "\n--show_scenarios\n\tDisplay scenarios which will be executed."
  printf "\n--show_steps\n\tDisplay scenarios and their steps which will be executed."
  printf "\n--dry_run\n\tExecute a dry run of the tests, disable crds updates and display the scenarios which would be executed."
  printf "\n--keep_namespace\n\tDo not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)."
  printf "\n--namespace_name\n\tSpecify name of the namespace which will be used for scenario execution (intended for development purposes)."
  printf "\n--local_cluster\n\tSpecify whether you run test using a local cluster."
  printf "\n"
}

function addParam(){
  PARAMS="${PARAMS} ${1}"
}

function addParamKeyValueIfAccepted(){
  key=${1}
  value=${2}
  if isValueNotOption ${value}; then 
    if isValueNotEmpty ${value}; then
      addParam "${key}=${value}"
    fi
    return 0
  fi
  return 1
}

function isValueNotOption(){
  if [[ ! ${1} =~ ^-.* ]]; then 
    return 0
  fi
  return 1
}

function isValueNotEmpty(){
  if [[ ! -z "${1}" ]]; then 
    return 0
  fi; 
  return 1
}

PARAMS=""
TAGS="" # tags are parsed independently as there could be whitespace to be handled correctly
FEATURE=""
TIMEOUT=240
DEBUG=false
KEEP_NAMESPACE=false
LOAD_DEFAULT_CONFIG=false
TEST_MAIN_DIR=${SCRIPT_DIR}/../test

while (( $# ))
do
case $1 in

  # tests configuration
  --test_main_dir)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        TEST_MAIN_DIR=${1}
      fi
      shift
    fi
  ;;
  --feature)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        FEATURE=${1}
      fi
      shift
    fi
  ;;
  --tags)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        TAGS="${1}"
      fi
      shift
    fi
  ;;
  --concurrent)
    shift
    if addParamKeyValueIfAccepted "--godog.concurrency" ${1}; then shift; fi
  ;;
  --timeout)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        TIMEOUT=${1}
      fi
      shift
    fi
  ;;
  --debug)
    DEBUG=true
    shift
  ;;
  --smoke)
    addParam "--tests.smoke"
    shift
  ;;
  --performance)
    addParam "--tests.performance"
    shift
  ;;
  --load_factor)
    shift
    if addParamKeyValueIfAccepted "--tests.load-factor" ${1}; then shift; fi
  ;;
  --local)
    addParam "--tests.local"
    shift
  ;;
  --ci)
    shift
    if addParamKeyValueIfAccepted "--tests.ci" ${1}; then shift; fi
  ;;
  --cr_deployment_only)
    addParam "--tests.cr-deployment-only"
    shift
  ;;
  --load_default_config)
    LOAD_DEFAULT_CONFIG=true
    shift
  ;;
  --container_engine)
    shift
    if addParamKeyValueIfAccepted "--tests.container-engine" ${1}; then shift; fi
  ;;
  --domain_suffix)
    shift
    if addParamKeyValueIfAccepted "--tests.domain-suffix" ${1}; then shift; fi
  ;;
  --image_cache_mode)
    shift
    if addParamKeyValueIfAccepted "--tests.image-cache-mode" ${1}; then shift; fi
  ;;
  --http_retry_nb)
    shift
    if addParamKeyValueIfAccepted "--tests.http-retry-nb" ${1}; then shift; fi
  ;;
  --olm_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.olm-namespace" ${1}; then shift; fi
  ;;

  # operator information
  --operator_image)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-name" ${1}; then shift; fi
  ;;
  --operator_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-tag" ${1}; then shift; fi
  ;;
  --operator_namespaced)
    addParam "--tests.operator-namespaced"
    shift
  ;;
  --operator_installation_source)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-installation-source" ${1}; then shift; fi
  ;;
  --operator_catalog_image)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-catalog-image" ${1}; then shift; fi
  ;;

  # operator profiling
  --operator_profiling)
    addParam "--tests.operator-profiling"
    shift
  ;;
  --operator_profiling_data_access_yaml_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-profiling-data-access-yaml-uri" ${1}; then shift; fi
  ;;
  --operator_profiling_output_file_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-profiling-output-file-uri" ${1}; then shift; fi
  ;;

  # files/binaries
  --operator_yaml_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-yaml-uri" ${1}; then shift; fi
  ;;
  --cli_path)
    shift
    if addParamKeyValueIfAccepted "--tests.cli-path" ${1}; then shift; fi
  ;;

  # runtime
  --services_image_registry)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-registry" ${1}; then shift; fi
  ;;
  --services_image_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-namespace" ${1}; then shift; fi
  ;;
  --services_image_name_suffix)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-name-suffix" ${1}; then shift; fi
  ;;
  --services_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-version" ${1}; then shift; fi
  ;;
  --data_index_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.data-index-image-tag" ${1}; then shift; fi
  ;;  
  --explainability_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.explainability-image-tag" ${1}; then shift; fi
  ;;
  --jobs_service_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.jobs-service-image-tag" ${1}; then shift; fi
  ;;
  --management_console_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.management-console-image-tag" ${1}; then shift; fi
  ;;
  --task_console_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.task-console-image-tag" ${1}; then shift; fi
  ;;
  --trusty_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.trusty-image-tag" ${1}; then shift; fi
  ;;
  --trusty_ui_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.trusty-ui-image-tag" ${1}; then shift; fi
  ;;
  --runtime_application_image_registry)
    shift
    if addParamKeyValueIfAccepted "--tests.runtime-application-image-registry" ${1}; then shift; fi
  ;;
  --runtime_application_image_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.runtime-application-image-namespace" ${1}; then shift; fi
  ;;
  --runtime_application_image_name_prefix)
    shift
    if addParamKeyValueIfAccepted "--tests.runtime-application-image-name-prefix" ${1}; then shift; fi
  ;;
  --runtime_application_image_name_suffix)
    shift
    if addParamKeyValueIfAccepted "--tests.runtime-application-image-name-suffix" ${1}; then shift; fi
  ;;
  --runtime_application_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.runtime-application-image-version" ${1}; then shift; fi
  ;;

  # build
  --custom_maven_repo)
    shift
    if addParamKeyValueIfAccepted "--tests.custom-maven-repo-url" ${1}; then shift; fi
  ;;
  --custom_maven_repo_replace_default)
    addParam "--tests.custom-maven-repo-replace-default"
    shift
  ;;
  --maven_mirror)
    shift
    if addParamKeyValueIfAccepted "--tests.maven-mirror-url" ${1}; then shift; fi
  ;;
  --maven_ignore_self_signed_certificate)
    addParam "--tests.maven-ignore-self-signed-certificate"
    shift
  ;;
  --build_image_registry)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-registry" ${1}; then shift; fi
  ;;
  --build_image_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-namespace" ${1}; then shift; fi
  ;;
  --build_image_name_suffix)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-name-suffix" ${1}; then shift; fi
  ;;
  --build_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-version" ${1}; then shift; fi
  ;;
  --build_s2i_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.build-s2i-image-tag" ${1}; then shift; fi
  ;;
  --build_runtime_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.build-runtime-image-tag" ${1}; then shift; fi
  ;;
  --disable_maven_native_build_container)
    addParam "--tests.disable-maven-native-build-container"
    shift
  ;;

  # examples repository
  --examples_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.examples-uri" ${1}; then shift; fi
  ;;
  --examples_ref)
    shift
    if addParamKeyValueIfAccepted "--tests.examples-ref" ${1}; then shift; fi
  ;;
  --examples_ignore_ssl)
    addParam "--tests.examples-ignore-ssl"
    shift
  ;;

  # Infinispan
  --infinispan_installation_source)
    shift
    if addParamKeyValueIfAccepted "--tests.infinispan-installation-source" ${1}; then shift; fi
  ;;

  # Hyperfoil
  --hyperfoil_output_directory)
    shift
    if addParamKeyValueIfAccepted "--tests.hyperfoil-output-directory" ${1}; then shift; fi
  ;;

  # dev options
  --show_scenarios)
    addParam "--tests.show-scenarios"
    shift
  ;;
  --show_steps)
    addParam "--tests.show-steps"
    shift
  ;;
  --dry_run)
    addParam "--tests.show-scenarios"
    addParam "--tests.dry-run"
    shift
  ;;
  --keep_namespace)
    KEEP_NAMESPACE=true
    addParam "--tests.keep-namespace"
    shift
  ;;
  --namespace_name)
    shift
    if addParamKeyValueIfAccepted "--tests.dev.namespace-name" ${1}; then shift; fi
  ;;
  --local_cluster)
    addParam "--tests.dev.local-cluster"
    shift
  ;;

  # others
  -h|--help)
    usage
    exit 0
  ;;
  *)
    echo "Unknown arguments: ${1}"
    usage
    exit 1
  ;;
esac
done

# load test default config options if not set already
if [ "${LOAD_DEFAULT_CONFIG}" = "true" ]; then
  echo "Load default test config"
  while IFS="=" read -r key value
  do
    if [[ $PARAMS != *"${key}"* ]]; then
      addParam "--${key}=${value}"
    fi
  done < "${SCRIPT_DIR}/../test/.default_config"
fi

echo "-------- Running BDD tests"
echo "DEBUG=${DEBUG} go test ${TEST_MAIN_DIR} -v -timeout \"${TIMEOUT}m\" --godog.tags=\"${TAGS}\" ${PARAMS} ${FEATURE}"
DEBUG=${DEBUG} go test ${TEST_MAIN_DIR} -v -timeout "${TIMEOUT}m" --godog.tags="${TAGS}" ${PARAMS} ${FEATURE}
exit_code=$?
echo "Tests finished with code ${exit_code}"

if [ "${KEEP_NAMESPACE}" = "false" ]; then
  echo "-------- Pruning namespaces"
  cd ${SCRIPT_DIR}/../test
  go run scripts/prune_namespaces.go
  echo "Pruning namespaces done."
  cd -
fi

echo "-------- Delete stucked namespaces"
${SCRIPT_DIR}/clean-stuck-namespaces.sh

exit ${exit_code}
