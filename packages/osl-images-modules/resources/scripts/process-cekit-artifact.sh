#!/usr/bin/env bash
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

OS=""
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

detect_os() {
  OS=$(uname -s)
  case "$OS" in
    Darwin)
      OS="mac"
      ;;
    Linux)
      OS="linux"
      ;;
    *)
      echo "Unsupported OS: $OS"
      exit 1
      ;;
  esac
}

detect_os

if [ $# -lt 1 ]; then
  echo "Usage: $0 <file_url>"
  exit 1
fi

IFS=',' read -r -a FILE_URLS <<< "$1"  # Split FILE_URL by commas
MODULE_PATH="$2"

if [ -z "$1" ]; then
  echo "No artifact url or path, skipping Cekit Cache updates"
  exit 0
fi

YAML_FILE="${SCRIPT_DIR}/../${MODULE_PATH}" 

if [ ! -f "${YAML_FILE}" ]; then
  echo "Module YAML file ${YAML_FILE} does not exist. Make sure the path is correct"
  exit 1
fi

for FILE_URL in "${FILE_URLS[@]}"; do
  if [[ ! "$FILE_URL" =~ ^(https?://|/) ]]; then
    echo "Skipping invalid file URL: $FILE_URL. Must start with http, https, or /"
    continue
  fi
  
  FILE_NAME=$(basename "$FILE_URL")

  if [ "$OS" = "mac" ]; then
    FILE_MD5=$(echo "$FILE_URL" | md5 -r | awk '{ print $1 }')
  else
    FILE_MD5=$(echo "$FILE_URL" | md5sum | awk '{ print $1 }')
  fi

  CACHE_INFO=$(cekit-cache ls | grep -B 2 "${FILE_NAME%.*}")
  MD5_CHECKSUM=""

  if echo "$CACHE_INFO" | grep -q "${FILE_NAME%.*}"; then
    echo "File $FILE_NAME is already in cache. Skipping download and cache addition."

    MD5_CHECKSUM=$(echo "${CACHE_INFO}" | awk '/md5:/ {print $2}')
  else
    TEMP_FILE="/tmp/${FILE_NAME%.*}.${FILE_NAME##*.}"

    if [[ "$FILE_URL" =~ ^https?:// ]]; then
      rm -rf $TEMP_FILE

      echo "Downloading $FILE_URL to $TEMP_FILE..."
      curl -L -o "$TEMP_FILE" "$FILE_URL"

      if [ $? -ne 0 ]; then
        echo "Download failed!"
        exit 1
      fi
    else
      TEMP_FILE="$FILE_URL"
    fi

    if [ "$OS" == "mac" ]; then
      MD5_CHECKSUM=$(md5 -r "$TEMP_FILE" | awk '{ print $1 }')
    else
      MD5_CHECKSUM=$(md5sum "$TEMP_FILE" | awk '{ print $1 }')
    fi

    echo "File $TEMP_FILE to be added to cache with MD5 checksum: $MD5_CHECKSUM"
    
    cekit-cache add "$TEMP_FILE" --md5 "$MD5_CHECKSUM"
  fi

  echo "Updating $YAML_FILE with the new checksum $MD5_CHECKSUM and file name..."

  python3 "${SCRIPT_DIR}/update_artifact.py" "$YAML_FILE" "${FILE_NAME%.*}" "$MD5_CHECKSUM"

  if [ $? -ne 0 ]; then
    echo "Updating YAML file failed, check output!"
    exit 1
  fi
done