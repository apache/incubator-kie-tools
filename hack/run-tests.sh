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

MASTER_RAW_URL=https://raw.githubusercontent.com/kiegroup/kogito-cloud-operator/master/deploy

function usage(){
  printf "Run BDD tests."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  
  # tests configuration
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
  printf "\n--local\n\tSpecify whether you run test in local."
  printf "\n--ci {CI_NAME}\n\tSpecify whether you run test with ci, give also the name of the CI."
  printf "\n--cr_deployment_only\n\tUse this option if you have no CLI to test against. It will use only direct CR deployments."
  printf "\n--load_default_config\n\tTo be used if you want to directly use the default test config contained into ${SCRIPT_DIR}/../test/.default_config"

  # operator information
  printf "\n--operator_image {NAME}\n\tOperator image name. Default is 'quay.io/kiegroup/kogito-cloud-operator' one."
  printf "\n--operator_tag {TAG}\n\tOperator image tag. Default is operator version."

  # files/binaries
  printf "\n--deploy_uri {URI}\n\tUrl or Path to operator 'deploy' folder. Default is local 'deploy/' folder."
  printf "\n--cli_path {PATH}\n\tPath to built CLI to test. Default is local built one."

  # runtime
  printf "\n--services_image_version {VERSION}\n\tSet the services image version."
  printf "\n--services_image_namespace {NAMESPACE}\n\tSet the services image namespace."
  printf "\n--services_image_registry {REGISTRY}\n\tSet the services image registry."
  printf "\n--data_index_image_tag {IMAGE_TAG}\n\tSet the Kogito Data Index image tag ('services_image_version' is ignored)"
  printf "\n--jobs_service_image_tag {IMAGE_TAG}\n\tSet the Kogito Jobs Service image tag ('services_image_version' is ignored)"
  printf "\n--management_console_image_tag {IMAGE_TAG}\n\tSet the Kogito Management Console image tag ('services_image_version' is ignored)"

  # build
  printf "\n--maven_mirror {URI}\n\tMaven mirror url to be used when building app in the tests."
  printf "\n--build_image_version {VERSION}\n\tSet the build image version."
  printf "\n--build_image_namespace {NAMESPACE}\n\tSet the build image namespace."
  printf "\n--build_image_registry {REGISTRY}\n\tSet the build image registry."
  printf "\n--build_image_tag {TAG}\n\tSet the build image full tag."
  printf "\n--build_s2i_image_tag {TAG}\n\tSet the S2I build image full tag."
  printf "\n--build_runtime_image_tag {NAME}\n\tSet the Runtime build image full tag."

  # examples repository
  printf "\n--examples_uri {URI}\n\tSet the URI for the kogito-examples repository. Default is https://github.com/kiegroup/kogito-examples."
  printf "\n--examples_ref {REF}\n\tSet the branch for the kogito-examples repository. Default is none."

  # dev options
  printf "\n--show_scenarios\n\tDisplay scenarios which will be executed."
  printf "\n--show_steps\n\tDisplay scenarios and their steps which will be executed."
  printf "\n--dry_run\n\tExecute a dry run of the tests, disable crds updates and display the scenarios which would be executed."
  printf "\n--keep_namespace\n\tDo not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)."
  printf "\n--disabled_crds_update\n\tDisable the update of CRDs."
  printf "\n--namespace_name\n\tSpecify name of the namespace which will be used for scenario execution (intended for development purposes)."
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
CRDS_UPDATE=true
LOAD_DEFAULT_CONFIG=false

while (( $# ))
do
case $1 in

  # tests configuration
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

  # operator information
  --operator_image)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-name" ${1}; then shift; fi
  ;;
  --operator_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-tag" ${1}; then shift; fi
  ;;

  # files/binaries
  --deploy_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-deploy-uri" ${1}; then shift; fi
  ;;
  --cli_path)
    shift
    if addParamKeyValueIfAccepted "--tests.cli-path" ${1}; then shift; fi
  ;;

  # runtime
  --services_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-version" ${1}; then shift; fi
  ;;
  --services_image_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-namespace" ${1}; then shift; fi
  ;;
  --services_image_registry)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-registry" ${1}; then shift; fi
  ;;
  --data_index_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.data-index-image-tag" ${1}; then shift; fi
  ;;
  --jobs_service_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.jobs-service-image-tag" ${1}; then shift; fi
  ;;
  --management_console_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.management-console-image-tag" ${1}; then shift; fi
  ;;

  # build
  --maven_mirror)
    shift
    if addParamKeyValueIfAccepted "--tests.maven-mirror-url" ${1}; then shift; fi
  ;;
  --build_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-version" ${1}; then shift; fi
  ;;
  --build_image_namespace)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-namespace" ${1}; then shift; fi
  ;;
  --build_image_registry)
    shift
    if addParamKeyValueIfAccepted "--tests.build-image-registry" ${1}; then shift; fi
  ;;
  --build_s2i_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.build-s2i-image-tag" ${1}; then shift; fi
  ;;
  --build_runtime_image_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.build-runtime-image-tag" ${1}; then shift; fi
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
    CRDS_UPDATE=false
    addParam "--tests.show-scenarios"
    addParam "--tests.dry-run"
    shift
  ;;
  --keep_namespace)
    addParam "--tests.keep-namespace"
    shift
  ;;
  --disabled_crds_update)
    CRDS_UPDATE=false
    shift
  ;;
  --namespace_name)
    shift
    if addParamKeyValueIfAccepted "--tests.dev.namespace-name" ${1}; then shift; fi
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

if ${CRDS_UPDATE}; then
  echo "-------- Apply CRD files"
  source $SCRIPT_DIR/crds-utils.sh

  deploy_folder="${SCRIPT_DIR}/../deploy"
  if [[ ! -z "${OPERATOR_DEPLOY_FOLDER}" ]]; then 
    # Get crds files from URI if 
    if [[ ${OPERATOR_DEPLOY_FOLDER} == http://* ]] || [[ ${OPERATOR_DEPLOY_FOLDER} == https://* ]]; then
      url=${OPERATOR_DEPLOY_FOLDER}
      deploy_folder=`mktemp -d`
      download_remote_crds ${deploy_folder} ${url}
    else
      deploy_folder="${OPERATOR_DEPLOY_FOLDER}"
    fi
  fi
  apply_crds ${deploy_folder}
fi

echo "-------- Running BDD tests"
echo "DEBUG=${DEBUG} go test ${SCRIPT_DIR}/../test -v -timeout \"${TIMEOUT}m\" --godog.tags=\"${TAGS}\" ${PARAMS} ${FEATURE}"
DEBUG=${DEBUG} go test ${SCRIPT_DIR}/../test -v -timeout "${TIMEOUT}m" --godog.tags="${TAGS}" ${PARAMS} ${FEATURE}
exit_code=$?
echo "Tests finished with code ${exit_code}"

if ${CRDS_UPDATE}; then
  echo "-------- Set back master CRD files"

  deploy_folder=`mktemp -d`
  download_remote_crds ${deploy_folder} ${MASTER_RAW_URL}
  apply_crds ${deploy_folder}
fi

exit ${exit_code}
