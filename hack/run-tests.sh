#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# runs all BDD tests for the operator
SCRIPT_NAME=`basename $0`
SCRIPT_DIR=`dirname "${BASH_SOURCE[0]}"`

# tests configuration
BOOLEAN_TEST_PARAMS=(smoke performance cr_deployment_only)
STRING_TEST_PARAMS=(load_factor ci container_engine domain_suffix image_cache_mode http_retry_nb olm_namespace)

# operator information
BOOLEAN_TEST_PARAMS+=(use_product_operator)
STRING_TEST_PARAMS+=(operator_image_tag operator_installation_source operator_catalog_image)

# operator profiling
BOOLEAN_TEST_PARAMS+=(operator_profiling_enabled)
STRING_TEST_PARAMS+=(operator_profiling_data_access_yaml_uri operator_profiling_output_file_uri)

# files/binaries
STRING_TEST_PARAMS+=(operator_yaml_uri cli_path rhpam_operator_yaml_uri)

# runtime
STRING_TEST_PARAMS+=(services_.+_image_tag services_image_registry services_image_name_suffix services_image_version runtime_application_image_registry runtime_application_image_name_prefix runtime_application_image_name_suffix runtime_application_image_version)

# build
BOOLEAN_TEST_PARAMS+=(custom_maven_repo_replace_default maven_ignore_self_signed_certificate disable_maven_native_build_container)
STRING_TEST_PARAMS+=(custom_maven_repo_url maven_mirror_url quarkus_platform_maven_mirror_url build_builder_image_tag build_runtime_jvm_image_tag build_runtime_native_image_tag native_builder_image)

# examples repository
BOOLEAN_TEST_PARAMS+=(examples_ignore_ssl)
STRING_TEST_PARAMS+=(examples_uri examples_ref)

# Infinispan
STRING_TEST_PARAMS+=(infinispan_installation_source infinispan_storage_class)

# Hyperfoil
STRING_TEST_PARAMS+=(hyperfoil_output_directory hyperfoil_controller_image_version)

# dev options
BOOLEAN_TEST_PARAMS+=(show_scenarios show_steps local_execution)
DEV_BOOLEAN_TEST_PARAMS=(local_cluster)
DEV_STRING_TEST_PARAMS=(namespace_name)

arrayMatchElement() {
  local value=$1
  local array=$2
  for pattern in ${array}
  do
    [[ "${value}" =~ ${pattern} ]] && return 0
  done
  return 1
}

function usage(){
  printf "Run BDD tests."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  
  # tests configuration
  printf "\n--test_main_dir {TEST_MAIN_DIR}\n\tWhere to find the 'main_test.go' file. Defaults to 'testbdd/'."
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
  printf "\n--ci {CI_NAME}\n\tSpecify whether you run test with ci, give also the name of the CI."
  printf "\n--cr_deployment_only\n\tUse this option if you have no CLI to test against. It will use only direct CR deployments."
  printf "\n--load_default_config\n\tTo be used if you want to directly use the default test config contained into ${SCRIPT_DIR}/../testbdd/.default_config"
  printf "\n--format\n\tFormat to use for Godog output, possible values are 'pretty' or 'junit' (default)"
  printf "\n--container_engine\n\tTo be used if you want to specify engine to interact with images and containers. Default is docker."
  printf "\n--domain_suffix\n\tTo be used if you want to set a domain suffix for exposed services. Ignored when running tests on Openshift."
  printf "\n--image_cache_mode\n\tUse this option to specify whether you want to use image cache for runtime images. Available options are 'always', 'never' or 'if-available'(default)."
  printf "\n--http_retry_nb {INT_VALUE}\n\tSet the retry number for all HTTP calls in case it fails (and response code != 500). Default value is 3."
  printf "\n--olm_namespace \n\tSet the namespace which is used for cluster scope operators. Default is 'openshift-operators'."

  # operator information
  printf "\n--operator_image_tag {IMAGE_TAG}\n\tOperator image name."
  printf "\n--operator_installation_source {TAG}\n\tDefines installation source for the Kogito operator. Options are 'olm' and 'yaml'. Default is yaml."
  printf "\n--operator_catalog_image {TAG}\n\tDefines image containing operator catalog. Needs to be specified only when operator_installation_source is 'olm'."
  printf "\n--use_product_operator {TAG}\n\tSet to true to deploy RHPAM Kogito operator, false for using Kogito operator. Default is false."

  # operator profiling
  printf "\n--operator_profiling_enabled\n\tEnable the profiling of the operator. If enabled, operator will be automatically deployed with yaml files."
  printf "\n--operator_profiling_data_access_yaml_uri {URI}\n\tUrl or Path to kogito-operator-profiling-data-access.yaml file."
  printf "\n--operator_profiling_output_file_uri {URI}\n\tUrl or Path where to store the profiling outputs."

  # files/binaries
  printf "\n--operator_yaml_uri {URI}\n\tUrl or Path to kogito-operator.yaml file."
  printf "\n--cli_path {PATH}\n\tPath to built CLI to test. Default is local built one."
  printf "\n--rhpam_operator_yaml_uri {URI}\n\tUrl or Path to kogito-operator.yaml file."

  # runtime
  printf "\n--services_{image_type}_{persistence_type}_image_tag {IMAGE_TAG}\n\tSet the services (jobs-service, data-index, ...) image tag.\n\t\timage_type => data-index, explainibility, jobs-service, mgmt-console, task-console, trusty, trusty-ui\n\t\tpersistence_type => ephemeral, infinispan, mongodb, postgresql, redis"
  printf "\n--services_image_registry {REGISTRY}\n\tSet the global services image registry."
  printf "\n--services_image_name_suffix {NAMESPACE}\n\tSet the global services image name suffix to append to usual image names."
  printf "\n--services_image_version {VERSION}\n\tSet the global services image version."
  printf "\n--runtime_application_image_registry {REGISTRY}\n\tSet the registry for built runtime applications."
  printf "\n--runtime_application_image_name_prefix {NAME_PREFIX}\n\tSet the image name suffix to prepend to usual image names for built runtime applications."
  printf "\n--runtime_application_image_name_suffix {NAME_SUFFIX}\n\tSet the image name suffix to append to usual image names for built runtime applications."
  printf "\n--runtime_application_image_version {VERSION}\n\tSet the version for built runtime applications."

  # build
  printf "\n--custom_maven_repo_url {URI}\n\tSet a custom Maven repository url for S2I builds, in case your artifacts are in a specific repository. See https://github.com/kiegroup/kogito-images/README.md for more information."
  printf "\n--custom_maven_repo_replace_default\n\tIf you specified the option 'custom_maven_repo' and you want that one to replace the main Apache repository (useful with snapshots)."
  printf "\n--maven_mirror_url {URI}\n\tMaven mirror url to be used when building app in the tests."
  printf "\n--quarkus_platform_maven_mirror_url {URI}\n\tMaven mirror url to be used when building app from source files with Quarkus, using the quarkus maven plugin."
  printf "\n--maven_ignore_self_signed_certificate\n\tSet to true if maven build need to ignore self-signed certificate. This could happen when using internal maven mirror url."
  printf "\n--build_builder_image_tag {IMAGE_TAG}\n\tSet the Builder image full tag."
  printf "\n--build_runtime_jvm_image_tag {IMAGE_TAG}\n\tSet the Runtime JVM image full tag."
  printf "\n--build_runtime_native_image_tag {IMAGE_TAG}\n\tSet the Runtime Native image full tag."
  printf "\n--disable_maven_native_build_container\n\tBy default, Maven native builds are done in container (via container engine). Possibility to disable it."

  # examples repository
  printf "\n--examples_uri {URI}\n\tSet the URI for the kogito-examples repository. Default is https://github.com/kiegroup/kogito-examples."
  printf "\n--examples_ref {REF}\n\tSet the branch for the kogito-examples repository. Default is none."
  printf "\n--examples_ignore_ssl\n\tTell Git to ignore SSL check when checking out examples repository."

  # Infinispan
  printf "\n--infinispan_installation_source {TAG}\n\tDefines installation source for the Infinispan operator. Options are 'olm' and 'yaml'. Default is olm."
  printf "\n--infinispan_storage_class {TAG}\n\tDefines storage class for Infinispan PVC to be used."

  # Hyperfoil
  printf "\n--hyperfoil_output_directory {PATH}\n\tDefines output directory to store Hyperfoil run statistics. Default is test folder."
  printf "\n--hyperfoil_controller_image_version {VERSION}\n\ttSet the Hyperfoil controller image version."

  # dev options
  printf "\n--show_scenarios\n\tDisplay scenarios which will be executed."
  printf "\n--show_steps\n\tDisplay scenarios and their steps which will be executed."
  printf "\n--dry_run\n\tExecute a dry run of the tests, disable crds updates and display the scenarios which would be executed."
  printf "\n--keep_namespace\n\tDo not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)."
  printf "\n--namespace_name\n\tSpecify name of the namespace which will be used for scenario execution (intended for development purposes)."
  printf "\n--local_cluster\n\tSpecify whether you run test using a local cluster."
  printf "\n--local_execution\n\tSpecify whether you run test in local using either a local or remote cluster."
  printf "\n--enable_clean_cluster\n\tSet to true to cleanup the cluster before/after the tests."
  printf "\n"
}

function addParam(){
  if [ ! -z $2 ]; then 
    if [ "$2" = "true" ]; then
      PARAMS="${PARAMS} ${1}"
      return 0
    elif [ "$2" = "false" ]; then
      return 0
    fi
  fi
  PARAMS="${PARAMS} ${1}"
  return 1
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

function clean_cluster() {
  echo "-------- Clean Cluster operators"
  ${SCRIPT_DIR}/clean-cluster-operators.sh

  echo "-------- Clean dependencies CRDs"
  ${SCRIPT_DIR}/clean-crds.sh
}

PARAMS=""
TAGS="" # tags are parsed independently as there could be whitespace to be handled correctly
FEATURE=""
TIMEOUT=240
DEBUG=false
KEEP_NAMESPACE=false
LOAD_DEFAULT_CONFIG=false
TEST_MAIN_DIR=${SCRIPT_DIR}/../testbdd
ENABLE_CLEAN_CLUSTER=false

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
    shift
    if [ ! -z $1 ]; then 
      if [ "$1" = "true" ]; then
        DEBUG=true
        shift
      elif [ "$1" = "false" ]; then
        shift
      fi
    else
      DEBUG=true
    fi
  ;;
  --load_default_config)
    shift
    if [ ! -z $1 ]; then 
      if [ "$1" = "true" ]; then
        LOAD_DEFAULT_CONFIG=true
        shift
      elif [ "$1" = "false" ]; then
        shift
      fi
    else
      LOAD_DEFAULT_CONFIG=true
    fi
  ;;
  --format)
      shift
      if addParamKeyValueIfAccepted "--godog.format" ${1}; then shift; fi
    ;;

  # dev options
  --dry_run)
    shift
    if [ ! -z $1 ]; then 
      if [ "$1" = "true" ]; then
        addParam "--tests.show_scenarios"
        addParam "--tests.dry_run"
        shift
      elif [ "$1" = "false" ]; then
        shift
      fi
    else
      addParam "--tests.show_scenarios"
      addParam "--tests.dry_run"
    fi
  ;;
  --keep_namespace)
    shift
    if [ ! -z $1 ]; then 
      if [ "$1" = "true" ]; then
        KEEP_NAMESPACE=true
        addParam "--tests.keep_namespace"
        shift
      elif [ "$1" = "false" ]; then
        shift
      fi
    else
      KEEP_NAMESPACE=true
      addParam "--tests.keep_namespace"
    fi
  ;;
  --enable_clean_cluster)
    shift
    if [ ! -z $1 ]; then 
      if [ "$1" = "true" ]; then
        ENABLE_CLEAN_CLUSTER=true
        shift
      elif [ "$1" = "false" ]; then
        shift
      fi
    else
      ENABLE_CLEAN_CLUSTER=true
    fi
  ;;

  # others
  -h|--help)
    usage
    exit 0
  ;;
  *)
    option=$1
    value=${option#--}
    shift
    if arrayMatchElement ${value} "${BOOLEAN_TEST_PARAMS[*]}"; then
      if addParam "--tests.${value}" ${1}; then shift; fi
    elif arrayMatchElement ${value} "${STRING_TEST_PARAMS[*]}"; then
      if addParamKeyValueIfAccepted "--tests.${value}" ${1}; then shift; fi
    elif arrayMatchElement ${value} "${DEV_BOOLEAN_TEST_PARAMS[*]}"; then
      if addParam "--tests.dev.${value}" ${1}; then shift; fi
    elif arrayMatchElement ${value} "${DEV_STRING_TEST_PARAMS[*]}"; then
      if addParamKeyValueIfAccepted "--tests.dev.${value}" ${1}; then shift; fi
    else
      echo "Unknown arguments: ${option}"
      usage
      exit 1
    fi
  ;;
esac
done

# load test default config options if not set already
if [ "${LOAD_DEFAULT_CONFIG}" = "true" ]; then
  echo "Load default test config"
  cat "${SCRIPT_DIR}/../testbdd/.default_config"
  while IFS="=" read -r key value
  do
    if [[ $PARAMS != *"${key}"* ]]; then
      addParam "--${key}=${value}"
    fi
  done < "${SCRIPT_DIR}/../testbdd/.default_config"
fi

## Clean cluster before executing the tests
if [ "${ENABLE_CLEAN_CLUSTER}" = "true" ]; then
  clean_cluster
fi

echo "-------- Running BDD tests"
echo "DEBUG=${DEBUG} go test ${TEST_MAIN_DIR} -v -timeout \"${TIMEOUT}m\" --godog.tags=\"${TAGS}\" ${PARAMS} ${FEATURE}"
DEBUG=${DEBUG} go test ${TEST_MAIN_DIR} -v -timeout "${TIMEOUT}m" --godog.tags="${TAGS}" ${PARAMS} ${FEATURE}
exit_code=$?

echo "${exit_code}" > /tmp/bdd-exit-code.txt

echo "Tests finished with code ${exit_code}"

if [ "${KEEP_NAMESPACE}" = "false" ]; then
  echo "-------- Pruning namespaces"
  cd ${SCRIPT_DIR}/../testbdd
  go run scripts/prune_namespaces.go
  echo "Pruning namespaces done."
  cd - >/dev/null
fi

echo "-------- Delete stuck namespaces"
${SCRIPT_DIR}/clean-stuck-namespaces.sh

if [ "${KEEP_NAMESPACE}" = "false" ] && [ "${ENABLE_CLEAN_CLUSTER}" = "true" ]; then
  clean_cluster
fi

exit ${exit_code}
