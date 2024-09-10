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

SOURCES_DIR=/tmp/artifacts
SCRIPT_DIR=$(dirname "${0}")
MGMT_CONSOLE_HOME="${KOGITO_HOME}/management-console"

# Configure the default httpd conf
echo "Mutex posixsem" >> "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i -e "/#ServerName www.example.com:80/aHeader set Content-Security-Policy \"frame-ancestors 'self';\"" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i -e 's/Options Indexes FollowSymLinks/Options -Indexes +FollowSymLinks/' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i "s/Listen 80/Listen ${KOGITO_MANAGEMENT_CONSOLE_PORT}/g" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i "s/#ServerName www.example.com:80/ServerName 127.0.0.1:${KOGITO_MANAGEMENT_CONSOLE_PORT}/g" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i '$ a ServerTokens Prod' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i '$ a ServerSignature Off' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i -e "/<Directory '${HTTPD_DATA_PATH}/html'>/a    RewriteEngine on\n    RewriteCond %{REQUEST_FILENAME} -f [OR]\n    RewriteCond %{REQUEST_FILENAME} -d\n    RewriteRule ^ - [L]\n    RewriteRule ^ index.html [L]" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"

# Set the required paths
mkdir -p "${MGMT_CONSOLE_HOME}"

# Copy the entrypoint and other init scripts
cp -v "${SCRIPT_DIR}"/added/* "${MGMT_CONSOLE_HOME}"/launch

# Copy the application
cp -vr "${SOURCES_DIR}/management-console/"* "${MGMT_CONSOLE_HOME}/"

# Fixing permissions
chmod +x "${MGMT_CONSOLE_HOME}/launch/entrypoint.sh" "${MGMT_CONSOLE_HOME}/image-env-to-json-standalone"
chown -R "${KOGITO_USER}" "${MGMT_CONSOLE_HOME}"

 if [ -f "${MGMT_CONSOLE_HOME}/app/env.json" ]; then chmod a+w "${MGMT_CONSOLE_HOME}/app/env.json"; fi
