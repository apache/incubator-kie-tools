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


# runs all smoke tests for the operator
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
  printf "Run smoke tests."
  printf "\n"
  printf "\n${SCRIPT_NAME} [options]*"
  printf "\n"
  printf "\nOptions:"
  printf "\n"
  printf "\n-h | --help\n\tPrint the usage of this script."
  printf "\n--tags {TAGS}\n\tFilter scenarios by tags."
    printf "\n\tExpressions can be:"
      printf "\n\t\t- '@wip': run all scenarios with wip tag"
      printf "\n\t\t- '~@wip': exclude all scenarios with wip tag"
      printf "\n\t\t- '@wip && ~@new': run wip scenarios, but exclude new"
      printf "\n\t\t- '@wip,@undone': run wip or undone scenarios"
    printf "\n\t Scenarios with '@disabled' tag are always ignored."
  printf "\n--concurrent {NUMBER}\n\tSet the number of concurrent tests. Default is 1."
  printf "\n--feature {FEATURE_PATH}\n\tRun a specific feature file."
  printf "\n--timeout {TIMEOUT_IN_MINUTES}\n\tSet a timeout overall run in minutes. Default is 240."
  printf "\n--local {BOOLEAN}\n\tSpecify whether you run test in local."
  printf "\n--operator_image {NAME}\n\tOperator image name. Default is 'quay.io/kiegroup' one."
  printf "\n--operator_tag {TAG}\n\tOperator image tag. Default is operator version."
  printf "\n--cli_path {PATH}\n\tPath to built CLI to test. Default is local built one."
  printf "\n--deploy_uri {URI}\n\tUrl or Path to operator 'deploy' folder. Default is local 'deploy/' folder."
  printf "\n--maven_mirror {URI}\n\tMaven mirror url to be used when building app in the tests."
  printf "\n--build_image_version\n\tSet the image version. Default to current operator version"
  printf "\n--build_image_tag\n\tSet the build image full tag."
  printf "\n--build_s2i_image_tag \n\tSet the S2I build image full tag."
  printf "\n--build_runtime_image_tag \n\tSet the Runtime build image full tag."
  printf "\n--examples_uri ${URI}\n\tSet the URI for the kogito-examples repository. Default is https://github.com/kiegroup/kogito-examples."
  printf "\n--examples_ref ${REF}\n\tSet the branch for the kogito-examples repository. Default is none."
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
    echo "Apply crds file ${crd_file}"
    oc apply -f ${crd_file}
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
DRY_RUN=

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
  --local)
    shift
    if isValueNotOption ${1}; then
      if isValueNotEmpty ${1}; then
        if [[ "${1}" = "true" ]]; then
          addParam "--smoke.local"
        fi
      fi
      shift
    fi
  ;;
  --operator_image)
    shift
    if addParamKeyValueIfAccepted "--smoke.operator-image-name" ${1}; then shift; fi
  ;;
  --operator_tag)
    shift
    if addParamKeyValueIfAccepted "--smoke.operator-image-tag" ${1}; then shift; fi
  ;;
  --cli_path)
    shift
    if addParamKeyValueIfAccepted "--smoke.cli-path" ${1}; then shift; fi
  ;;
  --deploy_uri)
    shift
    if addParamKeyValueIfAccepted "--smoke.operator-deploy-uri" ${1}; then shift; fi
  ;;
  --maven_mirror)
    shift
    if addParamKeyValueIfAccepted "--smoke.maven-mirror-url" ${1}; then shift; fi
  ;;
  --build_image_version)
    shift
    if addParamKeyValueIfAccepted "--smoke.build-image-version" ${1}; then shift; fi
  ;;
  --build_s2i_image_tag)
    shift
    if addParamKeyValueIfAccepted "--smoke.build-s2i-image-tag" ${1}; then shift; fi
  ;;
  --build_runtime_image_tag)
    shift
    if addParamKeyValueIfAccepted "--smoke.build-runtime-image-tag" ${1}; then shift; fi
  ;;
  --examples_uri)
    shift
    if addParamKeyValueIfAccepted "--smoke.examples-uri" ${1}; then shift; fi
  ;;
  --examples_ref)
    shift
    if addParamKeyValueIfAccepted "--smoke.examples-ref" ${1}; then shift; fi
  ;;
  --dry-run)
    DRY_RUN=true
    shift
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

echo "DEBUG=${DEBUG} go test ./test/smoke -v -timeout \"${TIMEOUT}m\" --godog.tags=\"${TAGS}\" ${PARAMS} ${FEATURE}"

if [[ ${DRY_RUN} ]]; then
  exit 0
fi

echo "-------- Setup CRD files"

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

echo "-------- Running smoke tests"

DEBUG=${DEBUG} go test ./test/smoke -v -timeout "${TIMEOUT}m" --godog.tags="${TAGS}" ${PARAMS} ${FEATURE}
exit_code=$?
echo "Tests finished with code ${exit_code}"

echo "-------- Set back master CRD files"

deploy_folder=`mktemp -d`
download_remote_crds ${deploy_folder} ${MASTER_RAW_URL}
apply_crds ${deploy_folder}

exit ${exit_code}
