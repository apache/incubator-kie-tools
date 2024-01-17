#!/usr/bin/env bats
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


# imports
source $BATS_TEST_DIRNAME/../../common/added/configure-maven.sh


setup() {
    export HOME=$BATS_TMPDIR/maven
    mkdir -p ${HOME}/.m2/
    cp $BATS_TEST_DIRNAME/../../common/maven/settings.xml ${HOME}/.m2/
    export MAVEN_SETTINGS_PATH="${HOME}/.m2/settings.xml"
}

teardown() {
    rm -rf ${HOME}
}

# override this function, cat /dec/urandon makes the test hangs on GH actions
function _generate_random_id() {
    echo "testing" | env LC_CTYPE=C tr -dc 'a-zA-Z0-9' | fold -w 16 | head -n 1
}


@test "test maven mirror configuration" {
    prepareEnv
    MAVEN_MIRROR_URL="http://localhost:8081/nexus/custom/repo/public"
    run configure_mirrors
    expected="<mirror>      <id>mirror.default</id>      <url>http://localhost:8081/nexus/custom/repo/public</url>      <mirrorOf>external:*</mirrorOf>    </mirror>"
    result=$(xmllint --xpath "//*[local-name()='mirrors']//*[local-name()='mirror']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "expected=${result}"
     [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTPS_PROXY env custom port https" {
    prepareEnv
    HTTPS_PROXY="https://10.10.10.10:8443"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>https</protocol>         <host>https://10.10.10.10</host>         <port>8443</port>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTPS_PROXY env default port https" {
    prepareEnv
    HTTPS_PROXY="https://10.10.10.10:"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>https</protocol>         <host>https://10.10.10.10</host>         <port>443</port>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTPS_PROXY env custom port https with username and password" {
    prepareEnv
    HTTPS_PROXY="https://10.10.10.10:8443"
    PROXY_USERNAME="hello"
    PROXY_PASSWORD="impossible2guess"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>https</protocol>         <host>https://10.10.10.10</host>         <port>8443</port>         <username>hello</username>         <password>impossible2guess</password>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTP_PROXY env custom port http" {
    prepareEnv
    HTTP_PROXY="http://10.10.10.20:8003"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>http</protocol>         <host>http://10.10.10.20</host>         <port>8003</port>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTP_PROXY env default port http with password" {
    prepareEnv
    HTTP_PROXY="http://10.10.10.20"
    PROXY_USERNAME="hello"
    PROXY_PASSWORD="impossible2guess"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>http</protocol>         <host>http://10.10.10.20</host>         <port>80</port>         <username>hello</username>         <password>impossible2guess</password>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with HTTP_PROXY env custom port http with username" {
    prepareEnv
    HTTP_PROXY="http://10.10.10.20:8003"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>http</protocol>         <host>http://10.10.10.20</host>         <port>8003</port>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with PROXY_ envs http" {
    prepareEnv
    HTTP_PROXY_HOST="10.10.10.20"
    HTTP_PROXY_PORT="8080"
    HTTP_PROXY_PASSWORD="impossible2guess"
    HTTP_PROXY_USERNAME="beleza_pura"
    HTTP_PROXY_NONPROXYHOSTS="127.0.0.1|10.1.1.1"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>http</protocol>         <host>10.10.10.20</host>         <port>8080</port>         <username>beleza_pura</username>         <password>impossible2guess</password>         <nonProxyHosts>127.0.0.1|10.1.1.1</nonProxyHosts>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with PROXY_ envs default port http no username" {
    prepareEnv
    HTTP_PROXY_HOST="10.10.10.20"
    HTTP_PROXY_NONPROXYHOSTS="127.0.0.1|10.1.1.1"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>http</protocol>         <host>10.10.10.20</host>         <port>80</port>         <nonProxyHosts>127.0.0.1|10.1.1.1</nonProxyHosts>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven proxy configuration with PROXY_ envs https" {
    prepareEnv
    HTTP_PROXY_HOST="https://10.10.10.20"
    HTTP_PROXY_PORT="8443"
    HTTP_PROXY_PASSWORD="impossible2guess"
    HTTP_PROXY_USERNAME="beleza_pura"
    HTTP_PROXY_NONPROXYHOSTS="127.0.0.1|10.1.1.1"
    run configure_proxy
    expected="<proxy>         <id>genproxy</id>         <active>true</active>         <protocol>https</protocol>         <host>https://10.10.10.20</host>         <port>8443</port>         <username>beleza_pura</username>         <password>impossible2guess</password>         <nonProxyHosts>127.0.0.1|10.1.1.1</nonProxyHosts>       </proxy>"
    result=$(xmllint --xpath "//*[local-name()='proxies']//*[local-name()='proxy']" ${HOME}/.m2/settings.xml)
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven download output logs when MAVEN_DOWNLOAD_OUTPUT is not true" {
    prepareEnv
    configure_maven_download_output
    expected=" --no-transfer-progress"
    result="${MAVEN_ARGS_APPEND}"
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven download output logs when MAVEN_DOWNLOAD_OUTPUT is true" {
    prepareEnv
    MAVEN_DOWNLOAD_OUTPUT="true"
    configure_maven_download_output
    expected=""
    result="${MAVEN_ARGS_APPEND}"
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven args when IGNORE_SELF_SIGNED_CERTIFICATE is true" {
    prepareEnv
    MAVEN_IGNORE_SELF_SIGNED_CERTIFICATE="true"
    ignore_maven_self_signed_certificates
    expected=" -Denforcer.skip -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true"
    result="${MAVEN_ARGS_APPEND}"
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven args when IGNORE_SELF_SIGNED_CERTIFICATE is false" {
    prepareEnv
    ignore_maven_self_signed_certificates
    expected=""
    result="${MAVEN_ARGS_APPEND}"
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}

@test "test maven custom repo with ID and all supported configurations" {
    prepareEnv
    MAVEN_REPO_URL="http://my.cool.mvn.repo.severinolabs.com/group/public"
    MAVEN_REPO_ID="my_cool_id"
    MAVEN_REPO_LAYOUT="test"
    MAVEN_REPO_RELEASES_ENABLED="true"
    MAVEN_REPO_RELEASES_UPDATE_POLICY="never"
    MAVEN_REPO_RELEASES_CHECKSUM_POLICY="test"
    MAVEN_REPO_SNAPSHOTS_ENABLED="false"
    MAVEN_REPO_SNAPSHOTS_UPDATE_POLICY="test"
    MAVEN_REPO_SNAPSHOTS_CHECKSUM_POLICY="test"

    run add_maven_repo

    repository_expected="<repository>
                    <id>my_cool_id</id>
                    <name>my_cool_id</name>
                    <url>http://my.cool.mvn.repo.severinolabs.com/group/public</url>
                    <layout>test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>test</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </snapshots>
                </repository>"
    repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "repository_expected=${repository_expected}"
    echo "repository_result  =${repository_result}"
    [ "${repository_expected}" = "${repository_result}" ]

    plugin_repository_expected="<pluginRepository>
                    <id>my_cool_id</id>
                    <name>my_cool_id</name>
                    <url>http://my.cool.mvn.repo.severinolabs.com/group/public</url>
                    <layout>test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>test</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </snapshots>
                </pluginRepository>"
    plugin_repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "plugin_repository_expected=${plugin_repository_expected}"
    echo "plugin_repository_result  =${plugin_repository_result}"
    [ "${plugin_repository_expected}" = "${plugin_repository_result}" ]
}


@test "test maven custom repo with no ID and other configuration, test default values" {
    prepareEnv
    MAVEN_REPO_URL="http://my.cool.mvn.repo.severinolabs.com/group/public"
    run add_maven_repo

    repository_url_expected="<url>http://my.cool.mvn.repo.severinolabs.com/group/public</url>"
    repository_url_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository']//*[local-name()='url'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "repository_url_expected=${repository_url_expected}"
    echo "repository_url_result  =${repository_url_result}"
    [ "${repository_url_expected}" = "${repository_url_result}" ]

    repository_releases_expected="<releases>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                        <checksumPolicy>warn</checksumPolicy>
                    </releases>"
    repository_releases_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository']//*[local-name()='releases'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "repository_releases_expected=${repository_releases_expected}"
    echo "repository_releases_result  =${repository_releases_result}"
    [ "${repository_releases_expected}" = "${repository_releases_result}" ]

    repository_snapshots_expected="<snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                        <checksumPolicy>warn</checksumPolicy>
                    </snapshots>"
    repository_snapshots_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository']//*[local-name()='snapshots'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "repository_snapshots_expected=${repository_snapshots_expected}"
    echo "repository_snapshots_result  =${repository_snapshots_result}"
    [ "${repository_snapshots_expected}" = "${repository_snapshots_result}" ]


    plugin_repository_url_expected="<url>http://my.cool.mvn.repo.severinolabs.com/group/public</url>"
    plugin_repository_url_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository']//*[local-name()='url'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "plugin_repository_url_expected=${plugin_repository_url_expected}"
    echo "plugin_repository_url_result  =${plugin_repository_url_result}"
    [ "${plugin_repository_url_expected}" = "${plugin_repository_url_result}" ]

    plugin_repository_releases_expected="<releases>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                        <checksumPolicy>warn</checksumPolicy>
                    </releases>"
    plugin_repository_releases_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository']//*[local-name()='releases'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "plugin_repository_releases_expected=${plugin_repository_releases_expected}"
    echo "plugin_repository_releases_result  =${plugin_repository_releases_result}"
    [ "${plugin_repository_releases_expected}" = "${plugin_repository_releases_result}" ]

    plugin_repository_snapshots_expected="<snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                        <checksumPolicy>warn</checksumPolicy>
                    </snapshots>"
    plugin_repository_snapshots_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository']//*[local-name()='snapshots'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "plugin_repository_snapshots_expected=${plugin_repository_snapshots_expected}"
    echo "plugin_repository_snapshots_result  =${plugin_repository_snapshots_result}"
    [ "${plugin_repository_snapshots_expected}" = "${plugin_repository_snapshots_result}" ]
}

@test "test maven multiple custom repos with ID and all supported configuration" {
    prepareEnv
    MAVEN_REPOS="CENTRAL,COMPANY"
    CENTRAL_MAVEN_REPO_URL="http://central.severinolabs.com/group/public"
    CENTRAL_MAVEN_REPO_ID="my_cool_id_central"
    CENTRAL_MAVEN_REPO_LAYOUT="test"
    CENTRAL_MAVEN_REPO_RELEASES_ENABLED="true"
    CENTRAL_MAVEN_REPO_RELEASES_UPDATE_POLICY="never"
    CENTRAL_MAVEN_REPO_RELEASES_CHECKSUM_POLICY="test"
    CENTRAL_MAVEN_REPO_SNAPSHOTS_ENABLED="false"
    CENTRAL_MAVEN_REPO_SNAPSHOTS_UPDATE_POLICY="test"
    CENTRAL_MAVEN_REPO_SNAPSHOTS_CHECKSUM_POLICY="test"

    COMPANY_MAVEN_REPO_URL="http://company.severinolabs.com/group/public"
    COMPANY_MAVEN_REPO_ID="my_cool_id_company"
    COMPANY_MAVEN_REPO_LAYOUT="another-test"
    COMPANY_MAVEN_REPO_RELEASES_ENABLED="true"
    COMPANY_MAVEN_REPO_RELEASES_UPDATE_POLICY="never"
    COMPANY_MAVEN_REPO_RELEASES_CHECKSUM_POLICY="another-test"
    COMPANY_MAVEN_REPO_SNAPSHOTS_ENABLED="false"
    COMPANY_MAVEN_REPO_SNAPSHOTS_UPDATE_POLICY="another-test"
    COMPANY_MAVEN_REPO_SNAPSHOTS_CHECKSUM_POLICY="another-test"

    run add_maven_repo

    central_repository_expected="<repository>
                    <id>my_cool_id_central</id>
                    <name>my_cool_id_central</name>
                    <url>http://central.severinolabs.com/group/public</url>
                    <layout>test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>test</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </snapshots>
                </repository>"
    central_repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository'])[last()-1]"  ${HOME}/.m2/settings.xml)
    echo "central_repository_expected=${central_repository_expected}"
    echo "central_repository_result  =${central_repository_result}"
    [ "${central_repository_expected}" = "${central_repository_result}" ]

    company_repository_expected="<repository>
                    <id>my_cool_id_company</id>
                    <name>my_cool_id_company</name>
                    <url>http://company.severinolabs.com/group/public</url>
                    <layout>another-test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>another-test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>another-test</updatePolicy>
                        <checksumPolicy>another-test</checksumPolicy>
                    </snapshots>
                </repository>"
    company_repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='repositories']//*[local-name()='repository'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "company_repository_expected=${company_repository_expected}"
    echo "company_repository_result  =${company_repository_result}"
    [ "${company_repository_expected}" = "${company_repository_result}" ]


    central_plugin_repository_expected="<pluginRepository>
                    <id>my_cool_id_central</id>
                    <name>my_cool_id_central</name>
                    <url>http://central.severinolabs.com/group/public</url>
                    <layout>test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>test</updatePolicy>
                        <checksumPolicy>test</checksumPolicy>
                    </snapshots>
                </pluginRepository>"
    central_plugin_repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository'])[last()-1]"  ${HOME}/.m2/settings.xml)
    echo "central_plugin_repository_expected=${central_plugin_repository_expected}"
    echo "central_plugin_repository_result  =${central_plugin_repository_result}"
    [ "${central_plugin_repository_expected}" = "${central_plugin_repository_result}" ]

    company_plugin_repository_expected="<pluginRepository>
                    <id>my_cool_id_company</id>
                    <name>my_cool_id_company</name>
                    <url>http://company.severinolabs.com/group/public</url>
                    <layout>another-test</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                        <checksumPolicy>another-test</checksumPolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                        <updatePolicy>another-test</updatePolicy>
                        <checksumPolicy>another-test</checksumPolicy>
                    </snapshots>
                </pluginRepository>"
    company_plugin_repository_result=$(xmllint --xpath "(//*[local-name()='profiles']//*[local-name()='profile']//*[local-name()='pluginRepositories']//*[local-name()='pluginRepository'])[last()]"  ${HOME}/.m2/settings.xml)
    echo "company_plugin_repository_expected=${company_plugin_repository_expected}"
    echo "company_plugin_repository_result  =${company_plugin_repository_result}"
    [ "${company_plugin_repository_expected}" = "${company_plugin_repository_result}" ]
}

@test "test maven args if it contains the user.home pointing to /home/kogito" {
    # it is expected that KOGITO_HOME is already set.
    export KOGITO_HOME=/home/kogito
    prepareEnv
    configureMavenHome
    expected=" -Duser.home=/home/kogito"
    result="${MAVEN_ARGS_APPEND}"
    echo "expected=${expected}"
    echo "result=${result}"
    [ "${expected}" = "${result}" ]
}