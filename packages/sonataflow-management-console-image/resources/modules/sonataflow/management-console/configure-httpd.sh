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
sed -i "s/Listen 80/Listen ${SONATAFLOW_MANAGEMENT_CONSOLE_PORT}/g" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i "s/#ServerName www.example.com:80/ServerName 127.0.0.1:${SONATAFLOW_MANAGEMENT_CONSOLE_PORT}/g" "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i '$ a ServerTokens Prod' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i '$ a ServerSignature Off' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"
sed -i '$ a SSLProxyEngine on' "${HTTPD_MAIN_CONF_PATH}/httpd.conf"

# Add RewriteRules
sed -i -e '/<Directory "\/var\/www\/html">/a \
    RewriteEngine on \
    # Data Index rewrite rules \
    RewriteCond %{REQUEST_URI} ^\/graphql([\/\?$]|$) \
    RewriteCond %{ENV:SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT} ^$ \
    RewriteRule ^.*$ - [R=503,L] \
    RewriteCond %{REQUEST_URI} ^\/graphql([\/\?$]|$) \
    RewriteCond %{ENV:SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT} ^(.*)$ \
    RewriteRule ^.*$ %1 [P,L]\n \
    RewriteCond %{REQUEST_FILENAME} -f [OR] \
    RewriteCond %{REQUEST_FILENAME} -d \
    RewriteRule ^ - [L] \
    RewriteRule ^ index.html [L]\n' \
    "${HTTPD_MAIN_CONF_PATH}/httpd.conf"

# Set the required paths
mkdir -p "${MGMT_CONSOLE_HOME}/launch"

# Copy the entrypoint and other init scripts
cp -v "${SCRIPT_DIR}"/added/* "${MGMT_CONSOLE_HOME}"/launch

# Fixing permissions
chmod +x "${MGMT_CONSOLE_HOME}/launch/entrypoint.sh" "${MGMT_CONSOLE_HOME}/image-env-to-json-linux-amd64"
chown -R "${USER_ID}" "${MGMT_CONSOLE_HOME}"

# Fixing /var/www permissions
chgrp -R 0 ${HTTPD_LOG_PATH} ${HTTPD_VAR_RUN} ${HTTPD_DATA_PATH}/html 
chmod -R g=u ${HTTPD_LOG_PATH} ${HTTPD_VAR_RUN} ${HTTPD_DATA_PATH}/html 

 if [ -f "${MGMT_CONSOLE_HOME}/app/env.json" ]; then chmod a+w "${MGMT_CONSOLE_HOME}/app/env.json"; fi
