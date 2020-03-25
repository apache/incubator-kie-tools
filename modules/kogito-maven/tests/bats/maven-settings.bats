#!/usr/bin/env bats

# imports
source $BATS_TEST_DIRNAME/../../3.6.x/added/configure-maven.sh


setup() {
    export HOME=$BATS_TMPDIR/maven
    mkdir -p ${HOME}/.m2/
    cp $BATS_TEST_DIRNAME/../../3.6.x/maven/settings.xml ${HOME}/.m2/
}

teardown() {
    rm -rf ${HOME}
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