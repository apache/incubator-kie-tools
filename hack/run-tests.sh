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
CRD_FILES_TO_IMPORT=(
  "app.kiegroup.org_kogitoapps_crd.yaml"
  "app.kiegroup.org_kogitodataindices_crd.yaml"
  "app.kiegroup.org_kogitoinfras_crd.yaml"
  "app.kiegroup.org_kogitojobsservices_crd.yaml"
  )

function usage(){
  printf "Run BDD tests."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  printf "\n--feature {FEATURE_PATH}\n\tRun a specific feature file."
  printf "\n--tags {TAGS}\n\tFilter scenarios by tags."
    printf "\n\tExpressions can be:"
      printf "\n\t\t- '@wip': run all scenarios with wip tag"
      printf "\n\t\t- '~@wip': exclude all scenarios with wip tag"
      printf "\n\t\t- '@wip && ~@new': run wip scenarios, but exclude new"
      printf "\n\t\t- '@wip,@undone': run wip or undone scenarios"
    printf "\n\t Scenarios with '@disabled' tag are always ignored."
  printf "\n--concurrent {NUMBER}\n\tSet the number of concurrent tests. Default is 1."
  printf "\n--timeout {TIMEOUT_IN_MINUTES}\n\tSet a timeout overall run in minutes. Default is 240."
  printf "\n--debug {BOOLEAN}\n\tRun in debug mode."
  printf "\n--local {BOOLEAN}\n\tSpecify whether you run test in local."
  printf "\n--ci {CI_NAME}\n\tSpecify whether you run test with ci, give also the name of the CI."
  printf "\n--smoke {BOOLEAN}\n\tFilter to run only the tests tagged with '@smoke'."
  printf "\n--operator_image {NAME}\n\tOperator image name. Default is 'quay.io/kiegroup' one."
  printf "\n--operator_tag {TAG}\n\tOperator image tag. Default is operator version."
  printf "\n--cli_path {PATH}\n\tPath to built CLI to test. Default is local built one."
  printf "\n--deploy_uri {URI}\n\tUrl or Path to operator 'deploy' folder. Default is local 'deploy/' folder."
  printf "\n--services_image_version\n\tSet the services image version. Default to current operator version"
  printf "\n--maven_mirror {URI}\n\tMaven mirror url to be used when building app in the tests."
  printf "\n--build_image_version\n\tSet the build image version. Default to current operator version"
  printf "\n--build_image_tag\n\tSet the build image full tag."
  printf "\n--build_s2i_image_tag \n\tSet the S2I build image full tag."
  printf "\n--build_runtime_image_tag \n\tSet the Runtime build image full tag."
  printf "\n--examples_uri ${URI}\n\tSet the URI for the kogito-examples repository. Default is https://github.com/kiegroup/kogito-examples."
  printf "\n--examples_ref ${REF}\n\tSet the branch for the kogito-examples repository. Default is none."

  # Dev options
  printf "\n--show_scenarios\n\tDisplay scenarios which will be executed."
  printf "\n--disabled_crds_update\n\tDisabled the update of CRDs."
  printf "\n--dry_run ${REF}\n\tExecute a dry run of the tests, disabled crds updates and display the scenarios which would be executed."
  printf "\n--keep_namespace\n\tDo not delete namespace(s) after scenario run (WARNING: can be resources consuming ...)."
  printf "\n"
}

function download_remote_crds(){
  deploy_folder=${1}
  url=${2}
  echo "Download crd files into '${deploy_folder}' from ${url}"

  mkdir -p "${deploy_folder}/crds"
  for file in ${CRD_FILES_TO_IMPORT[*]}
  do
    curl -k -o "${deploy_folder}/crds/${file}" "${url}/crds/${file}"
  done
}

function apply_crds(){
  deploy_folder=${1}
  for file in ${CRD_FILES_TO_IMPORT[*]}
  do
    crd_file="${deploy_folder}/crds/${file}"
    echo "Replace crds file ${crd_file}"
    oc replace -f ${crd_file}
    if [ "$?" != 0 ]; then
      echo "crd from file '${file}' may not exist yet in cluster, try to simply apply it"
      oc apply -f ${crd_file}
    fi
  done
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

while (( $# ))
do
case $1 in
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
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        if [[ "${1}" = "true" ]]; then
          DEBUG=true
        fi
      fi
      shift
    fi
  ;;
  --local)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        if [[ "${1}" = "true" ]]; then
          addParam "--tests.local"
        fi
      fi
      shift
    fi
  ;;
  --ci)
    shift
    if addParamKeyValueIfAccepted "--tests.ci" ${1}; then shift; fi
  ;;
  --smoke)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        if [[ "${1}" = "true" ]]; then
          addParam "--tests.smoke"
        fi
      fi
      shift
    fi
  ;;
  --operator_image)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-name" ${1}; then shift; fi
  ;;
  --operator_tag)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-image-tag" ${1}; then shift; fi
  ;;
  --cli_path)
    shift
    if addParamKeyValueIfAccepted "--tests.cli-path" ${1}; then shift; fi
  ;;
  --deploy_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.operator-deploy-uri" ${1}; then shift; fi
  ;;
  --services_image_version)
    shift
    if addParamKeyValueIfAccepted "--tests.services-image-version" ${1}; then shift; fi
  ;;
  --maven_mirror)
    shift
    if addParamKeyValueIfAccepted "--tests.maven-mirror-url" ${1}; then shift; fi
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
  --examples_uri)
    shift
    if addParamKeyValueIfAccepted "--tests.examples-uri" ${1}; then shift; fi
  ;;
  --examples_ref)
    shift
    if addParamKeyValueIfAccepted "--tests.examples-ref" ${1}; then shift; fi
  ;;
  --show_scenarios)
    shift
    addParam "--tests.show-scenarios"
  ;;
  --disabled_crds_update)
    CRDS_UPDATE=false
    shift
  ;;
  --dry_run)
    CRDS_UPDATE=false
    addParam "--tests.show-scenarios"
    addParam "--tests.dry-run"
    shift
  ;;
  --keep_namespace)
    shift
    addParam "--tests.keep-namespace"
  ;;
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

if ${CRDS_UPDATE}; then
  echo "-------- Apply CRD files"

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

echo "DEBUG=${DEBUG} go test ./test -v -timeout \"${TIMEOUT}m\" --godog.tags=\"${TAGS}\" ${PARAMS} ${FEATURE}"
DEBUG=${DEBUG} go test ./test -v -timeout "${TIMEOUT}m" --godog.tags="${TAGS}" ${PARAMS} ${FEATURE}
exit_code=$?
echo "Tests finished with code ${exit_code}"

if ${CRDS_UPDATE}; then
  echo "-------- Set back master CRD files"

  deploy_folder=`mktemp -d`
  download_remote_crds ${deploy_folder} ${MASTER_RAW_URL}
  apply_crds ${deploy_folder}
fi

exit ${exit_code}
