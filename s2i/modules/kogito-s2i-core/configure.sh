#!/usr/bin/env bash
# Configure module
set -e

SCRIPT_DIR=$(dirname $0)
ADDED_DIR="${SCRIPT_DIR}/added"

mkdir "${S2I_MODULE_LOCATION}"

cp -v "${ADDED_DIR}/s2i-core" "${S2I_MODULE_LOCATION}"
cp -rv ${SCRIPT_DIR}/s2i/bin/* "${S2I_MODULE_LOCATION}"
chmod 755 ${S2I_MODULE_LOCATION}/*