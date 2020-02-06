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

function delete_and_apply_crds(){
  deploy_folder=${1}

  OLD_CLUSTER_CRDS=`oc get crds | grep kogito | awk -F' ' '{print $1}'`
  for crd in ${OLD_CLUSTER_CRDS};
  do
    echo "Delete crds ${crd}"
    oc delete crds ${crd}
  done
  for file in ${CRD_FILES_TO_IMPORT[*]}
  do
    crd_file="${deploy_folder}/crds/${file}"
    echo "Apply crds file ${crd_file}"
    oc apply -f ${crd_file}
  done
}

FEATURE=""
CONCURRENT=1
TAGS=""
DRY_RUN=

while (( $# ))
do
case $1 in
  --tags)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        TAGS="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --concurrent)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        CONCURRENT="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --feature)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        FEATURE="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --local)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        if [ "${1}" = "true" ]; then 
          export LOCAL_TESTS=true; 
        fi
      fi; 
      shift; 
    fi
  ;;
  --operator_image)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export OPERATOR_IMAGE_NAME="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --operator_tag)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export OPERATOR_IMAGE_TAG="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --cli_path)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export OPERATOR_CLI_PATH="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --deploy_uri)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export OPERATOR_DEPLOY_FOLDER="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --maven_mirror)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export MAVEN_MIRROR_URL="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --build_image_version)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_BUILD_IMAGE_VERSION="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --build_image_tag)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_BUILD_IMAGE_STREAM_TAG="${1}"
      fi; 
      shift; 
    fi
  ;;
  --build_s2i_image_tag)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_BUILD_S2I_IMAGE_STREAM_TAG="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --build_runtime_image_tag)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_BUILD_RUNTIME_IMAGE_STREAM_TAG="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --examples_uri)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_EXAMPLES_REPOSITORY_URI="${1}"; 
      fi; 
      shift; 
    fi
  ;;
  --examples_ref)
    shift
    if [[ ! ${1} =~ ^-.* ]]; then 
      if [[ ! -z "${1}" ]]; then 
        export KOGITO_EXAMPLES_REPOSITORY_REF="${1}"; 
      fi; 
      shift; 
    fi
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

# Always adding @disabled tags
if [[ ! -z ${TAGS} ]]; then
  TAGS="${TAGS} && ~@disabled"
else
  TAGS="~@disabled"
fi
TAGS="--tags=${TAGS}"

if [[ ${DRY_RUN} ]]; then
  echo "godog -c ${CONCURRENT} --random -f progress "${TAGS}" ${FEATURE}"
  env
  exit 0
fi

echo "-------- Retrieve godog"

go get github.com/DATA-DOG/godog/cmd/godog


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
delete_and_apply_crds ${deploy_folder}

echo "-------- Running smoke tests"

cd ${SCRIPT_DIR}/../test/smoke/ && godog -c ${CONCURRENT} --random -f progress "${TAGS}" ${FEATURE}
exit_code=$?
echo "Tests finished with code ${exit_code}"

echo "-------- Set back master CRD files"

deploy_folder=`mktemp -d`
download_remote_crds ${deploy_folder} ${MASTER_RAW_URL}
delete_and_apply_crds ${deploy_folder}

exit ${exit_code}
