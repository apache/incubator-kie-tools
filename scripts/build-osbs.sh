#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -e

DEBUG=
GIT_USER=${GIT_USER:-"Your Name"}
GIT_EMAIL=${GIT_EMAIL:-"yourname@email.com"}
WORK_DIR=$(pwd)/build-temp

function help()
{
    echo "usage: build-osbs.sh [options]"
    echo
    echo "Run a cekit osbs build of an rhpam ba operator image with bundle"
    echo
    echo "For each of the options below, the names of the arguments are environment variables that may be set"
    echo "instead of using the particular option on the invocation"
    echo ""
    echo "Required:"
    echo "  -v PROD_VERSION           Version being built. Passed to the build-overrides.sh -v option"
    echo "  -t OSBS_BUILD_TARGET      Build target for osbs, for example rhba-7.3-openshift-containers-candidate"
    echo ""
    echo "Optional:"
    echo "  -h                        Print this help message"
    echo "  -p KERBEROS_PRINCIPAL     Kerberos principal to use with to access build systems. If not specified,"
    echo "                            the script assumes there is a valid kerberos ticket in force. If it is specified"
    echo "                            then one of KERBEROS_KEYTAB or KERBEROS_PASSWORD is required."
    echo "  -k KERBEROS_KEYTAB        Path to a keytab file for KERBEROS_PRINCIPAL if no KERBEROS_PASSWORD is specified."
    echo "  -s KERBEROS_PASSWORD      Password for KERBEROS_PRINCIPAL (a keytab file may be used instead via KERBEROS_KEYTAB)"
    echo "  -i OSBS_BUILD_USER        Maps to the build-osbs-user option for cekit (ie the user for rhpkg commands)"
    echo "                            The default will be KERBEROS_PRINCIPAL if this is not set"
    echo "  -b BUILD_DATE             The date of the nightly build to access. Passed to the build-overrides.sh -b option if set"
    echo "  -w WORK_DIR               The working directory used for generating overrides, cekit cache, etc. Default is ./build-temp."
    echo "  -u GIT_USER               User config for git commits to internal repositories. Default is 'Your Name'"
    echo "  -e GIT_EMAIL              Email config for git commits to internal repositories. Default is 'yourname@email.com'"
    echo "  -o CEKIT_BUILD_OPTIONS    Additional options to pass through to the cekit build command, should be quoted"
    echo "  -l CEKIT_CACHE_LOCAL      Comma-separated list of urls to download and add to the local cekit cache"
    echo "  -c PROD_COMPONENT         Prod Component or image name that will be built."
    echo "  -g                        Debug setting, currently sets verbose flag on cekit commands"
}


function get_short_version() {
  local version_array
  local short_version=$1
  IFS='.' read -r -a version_array <<< "$1"
  if [ ${#version_array[@]} -gt 1 ]; then
      short_version="${version_array[0]}.${version_array[1]}"
  fi
  echo $short_version
}

function check_for_required_envs()
{
    if [ -z "$GIT_EMAIL" ]; then
        echo "No git email specified with GIT_EMAIL"
        exit -1
    fi
    if [ -z "$GIT_USER" ]; then
        echo "No git user specified with GIT_USER"
        exit -1
    fi
    if [ -z "$PROD_VERSION" ]; then
        echo "No version specified with PROD_VERSION"
        exit -1
    fi
    if [ -z "$OSBS_BUILD_TARGET" ]; then
        echo "No build target specified with OSBS_BUILD_TARGET"
        exit -1
    fi
    if [ -z "PROD_COMPONENT" ]; then
        echo "No prod component specified with PROD_COMPONENT"
        exit -1
    fi
}

function get_kerb_ticket() {
    set +e
    retries=10
    delay=5
    if [ -n "$KERBEROS_PASSWORD" ]; then
        echo "$KERBEROS_PASSWORD" | kinit "$KERBEROS_PRINCIPAL"
        _klist
        if [ "$?" -ne 0 ]; then
            echo "Failed to get kerberos token for $KERBEROS_PRINCIPAL with password"
            exit -1
        fi
    elif [ -n "$KERBEROS_KEYTAB" ]; then
        for i in `seq 1 $retries`; do
            kinit -k -t "$KERBEROS_KEYTAB" "$KERBEROS_PRINCIPAL"
            [ $? -eq 0 ] && break
            echo "Failed to acquire Kerberos ticket, retrying (try $i of $retries)..."
            _klist
            sleep $delay
        done
        if [ "$?" -ne 0 ]; then
            echo "Failed to get kerberos token for $KERBEROS_PRINCIPAL with $KERBEROS_KEYTAB"
            exit -1
        fi
    else
        echo "No kerberos password or keytab specified with KERBEROS_PASSWORD or KERBEROS_KEYTAB"
        exit -1
    fi
    set -e
}

# _klist will help to indentify if the kerberos ticket, prints when debug is enabled
function _klist() {
    if [ -n "$DEBUG" ]; then
        klist
    fi
}


function set_git_config() {
    git config --global user.email "$GIT_EMAIL"
    git config --global user.name  "$GIT_USER"
    git config --global core.pager ""
}


while getopts gu:e:v:c:t:o:r:n:d:p:k:s:b:l:i:w:h option; do
    case $option in
        g)
            DEBUG=true
            ;;
        u)
            GIT_USER=$OPTARG
            ;;
        e)
            GIT_EMAIL=$OPTARG
            ;;
        v)
            PROD_VERSION=$OPTARG
            ;;
        c)
            PROD_COMPONENT=$OPTARG
            ;;
        t)
            OSBS_BUILD_TARGET=$OPTARG
            ;;
        o)
            CEKIT_BUILD_OPTIONS=$OPTARG
            ;;
        p)
            KERBEROS_PRINCIPAL=$OPTARG
            ;;
        k)
            KERBEROS_KEYTAB=$OPTARG
            ;;
        s)
            KERBEROS_PASSWORD=$OPTARG
            ;;
        b)
            BUILD_DATE=$OPTARG
            ;;
        l)
            CEKIT_CACHE_LOCAL=$OPTARG
            ;;
        i)
            OSBS_BUILD_USER=$OPTARG
            ;;
        w)
            WORK_DIR=$OPTARG
            ;;
        h)
            help
            exit 0
            ;;
        *)
            ;;
    esac
done
shift $((OPTIND-1))

mkdir -p $WORK_DIR
bo_options=" --no-color"

check_for_required_envs
set_git_config

if [ -n "$KERBEROS_PRINCIPAL" ]; then
    get_kerb_ticket
    # overrides the OSBS_BUILD_USER if it is not set and KERBEROS principal is in use
    if [ ! -n "$OSBS_BUILD_USER" ]; then
      echo "setting OSBS_BUILD_USER to KERBEROS_PRINCIPAL"
      # need to catch only the first part of the principal, before the / otherwise 'rhpkg' will fail
      OSBS_BUILD_USER=$(echo ${KERBEROS_PRINCIPAL} | awk -F"/" '{print $1}')
    fi
else
    echo No kerberos principal specified, assuming there is a current kerberos ticket
fi

debug=
if [ -n "$DEBUG" ]; then
    debug="--verbose"
fi

builduser=
if [ -n "$OSBS_BUILD_USER" ]; then
    builduser="$OSBS_BUILD_USER"
fi

cd ../
set -x
PROD_C="${PROD_COMPONENT}-image"
# hack to replace the branch overrides as cekit detects two osbs sections and don't know which one to override.
sed -i 's/rhba-7-rhel-8/rhba-7-rhel-8-nightly/g' ${PROD_C}.yaml
make container-build-osbs prod_component=${PROD_C}
set +x
