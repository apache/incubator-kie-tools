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


export KOGITO_HOME=$BATS_TMPDIR/kogito_home
mkdir -p "${KOGITO_HOME}"/{bin,launch}

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh "${KOGITO_HOME}"/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-persistence/added/kogito-persistence.sh "${KOGITO_HOME}"/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh "${KOGITO_HOME}"/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/s2i-core

setup() {
    export HOME="${KOGITO_HOME}"
    mkdir -p target
    function log_error() { echo "${1}"; }
    function log_info() { echo "${1}"; }
}

teardown() {
    rm -rf "${KOGITO_HOME}"/bin
    rm -rf /tmp/.s2i
    rm -rf /tmp/src
    rm -rf target
    rm -rf "${KOGITO_HOME}"/bin/*
}


@test "test manage_incremental_builds" {
    rm -rf /tmp/artifacts && mkdir /tmp/artifacts
    touch /tmp/artifacts/{file,file1,file2,file3}
    run manage_incremental_build

    IFS=$'\n' sorted=($(sort <<<"${lines[*]}")); unset IFS
    echo "result= ${sorted[@]}"

    [ "$status" -eq 0 ]
    if [ -z "${GITHUB_ACTIONS}" ]; then
        [ "$status" -eq 0 ]
        [ "${sorted[0]}" = "./" ]
        [ "${sorted[1]}" = "Expanding artifacts from incremental build..." ]
        [ "${sorted[2]}" = "./file" ]
        [ "${sorted[3]}" = "./file1" ]
        [ "${sorted[4]}" = "./file2" ]
        [ "${sorted[5]}" = "./file3" ]
    else
        [ "${sorted[0]}" = "./" ]
        [ "${sorted[1]}" = "./file" ]
        [ "${sorted[2]}" = "./file1" ]
        [ "${sorted[3]}" = "./file2" ]
        [ "${sorted[4]}" = "./file3" ]
        [ "${sorted[5]}" = "Expanding artifacts from incremental build..." ]
    fi

}

@test "test assemble_runtime no binaries" {
    run assemble_runtime
    [ "$status" -eq 0 ]
}

@test "test runtime_assemble" {
    mkdir -p "${KOGITO_HOME}"/bin
    mkdir -p /tmp/src/bin
    touch /tmp/src/bin/myapp.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[1]}" = "'./bin/myapp.jar' -> '"${KOGITO_HOME}"/./bin/myapp.jar'" ]
}

@test "test runtime_assemble with binary builds" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/
    touch /tmp/src/myapp.jar
    mkdir -p /tmp/src/lib
    mkdir -p /tmp/src/classes
    mkdir -p /tmp/src/maven-archiver

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[5]}" = "'./myapp.jar' -> '"${KOGITO_HOME}"/bin/myapp.jar'" ]
}

@test "test runtime_assemble with binary builds with new Quarkus 1.12+ default builds" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/quarkus-app/{app,quarkus}
    mkdir -p /tmp/src/quarkus-app/lib/{boot,main}

    touch /tmp/src/quarkus-app/my-app-run.jar

    touch /tmp/src/quarkus-app/lib/boot/my-boot-dependency-1.0.jar
    touch /tmp/src/quarkus-app/lib/main/my-main-dependency-1.0.jar

    touch /tmp/src/quarkus-app/quarkus/{generated-bytecode.jar,quarkus-application.dat}

    touch /tmp/src/quarkus-app/app/my-1.0.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[5]}" = "'./quarkus-app/app' -> '/tmp/kogito_home/bin/app'" ]
    [ "${lines[6]}" = "'./quarkus-app/app/my-1.0.jar' -> '/tmp/kogito_home/bin/app/my-1.0.jar'" ]
    [ "${lines[7]}" = "'./quarkus-app/lib' -> '/tmp/kogito_home/bin/lib'" ]
    [ "${lines[8]}" = "'./quarkus-app/lib/boot' -> '/tmp/kogito_home/bin/lib/boot'" ]
    [ "${lines[9]}" = "'./quarkus-app/lib/boot/my-boot-dependency-1.0.jar' -> '/tmp/kogito_home/bin/lib/boot/my-boot-dependency-1.0.jar'" ]
    [ "${lines[10]}" = "'./quarkus-app/lib/main' -> '/tmp/kogito_home/bin/lib/main'" ]
    [ "${lines[11]}" = "'./quarkus-app/lib/main/my-main-dependency-1.0.jar' -> '/tmp/kogito_home/bin/lib/main/my-main-dependency-1.0.jar'" ]
    [ "${lines[12]}" = "'./quarkus-app/my-app-run.jar' -> '/tmp/kogito_home/bin/my-app-run.jar'" ]
    [ "${lines[13]}" = "'./quarkus-app/quarkus' -> '/tmp/kogito_home/bin/quarkus'" ]
    [ "${lines[14]}" = "'./quarkus-app/quarkus/generated-bytecode.jar' -> '/tmp/kogito_home/bin/quarkus/generated-bytecode.jar'" ]
    [ "${lines[15]}" = "'./quarkus-app/quarkus/quarkus-application.dat' -> '/tmp/kogito_home/bin/quarkus/quarkus-application.dat'" ]
}

@test "test runtime_assemble with binary builds native binary" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/
    cp $BATS_TEST_DIRNAME/mocks/myapp-0.0.1-runner /tmp/src/myapp-0.0.1-runner

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[6]}" = "'./myapp-0.0.1-runner' -> '"${KOGITO_HOME}"/bin/myapp-0.0.1-runner'" ]

    # Only runner is located in bin directory
    [ -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-runner" ]
}

@test "test runtime_assemble with binary builds entire target!" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/target
    touch /tmp/src/target/myapp.jar
    touch /tmp/src/target/myapp-sources.jar
    mkdir -p /tmp/src/target/lib
    mkdir -p /tmp/src/target/classes
    mkdir -p /tmp/src/target/maven-archiver

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[7]}" = "'./myapp.jar' -> '"${KOGITO_HOME}"/bin/myapp.jar'" ]
}

@test "test runtime_assemble with binary builds entire target with new Quarkus 1.12+ default builds" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/target/quarkus-app/{app,quarkus}
    mkdir -p /tmp/src/target/quarkus-app/lib/{boot,main}

    touch /tmp/src/target/quarkus-app/my-app-run.jar

    touch /tmp/src/target/quarkus-app/lib/boot/my-boot-dependency-1.0.jar
    touch /tmp/src/target/quarkus-app/lib/main/my-main-dependency-1.0.jar

    touch /tmp/src/target/quarkus-app/quarkus/{generated-bytecode.jar,quarkus-application.dat}

    touch /tmp/src/target/quarkus-app/app/my-1.0.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[6]}" = "'./quarkus-app/app' -> '/tmp/kogito_home/bin/app'" ]
    [ "${lines[7]}" = "'./quarkus-app/app/my-1.0.jar' -> '/tmp/kogito_home/bin/app/my-1.0.jar'" ]
    [ "${lines[8]}" = "'./quarkus-app/lib' -> '/tmp/kogito_home/bin/lib'" ]
    [ "${lines[9]}" = "'./quarkus-app/lib/boot' -> '/tmp/kogito_home/bin/lib/boot'" ]
    [ "${lines[10]}" = "'./quarkus-app/lib/boot/my-boot-dependency-1.0.jar' -> '/tmp/kogito_home/bin/lib/boot/my-boot-dependency-1.0.jar'" ]
    [ "${lines[11]}" = "'./quarkus-app/lib/main' -> '/tmp/kogito_home/bin/lib/main'" ]
    [ "${lines[12]}" = "'./quarkus-app/lib/main/my-main-dependency-1.0.jar' -> '/tmp/kogito_home/bin/lib/main/my-main-dependency-1.0.jar'" ]
    [ "${lines[13]}" = "'./quarkus-app/my-app-run.jar' -> '/tmp/kogito_home/bin/my-app-run.jar'" ]
    [ "${lines[14]}" = "'./quarkus-app/quarkus' -> '/tmp/kogito_home/bin/quarkus'" ]
    [ "${lines[15]}" = "'./quarkus-app/quarkus/generated-bytecode.jar' -> '/tmp/kogito_home/bin/quarkus/generated-bytecode.jar'" ]
    [ "${lines[16]}" = "'./quarkus-app/quarkus/quarkus-application.dat' -> '/tmp/kogito_home/bin/quarkus/quarkus-application.dat'" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target SpringBoot build" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/target
    touch /tmp/src/target/myapp-0.0.1.jar
    touch /tmp/src/target/myapp-0.0.1.jar.original
    touch /tmp/src/target/myapp-0.0.1-sources.jar
    touch /tmp/src/target/myapp-0.0.1-tests.jar
    touch /tmp/src/target/myapp-0.0.1-tests-sources.jar
    mkdir -p /tmp/src/target/classes
    mkdir -p /tmp/src/target/generated-sources
    mkdir -p /tmp/src/target/maven-archiver

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]

    # Target directory is removed from initial location
    [ ! -d "/tmp/src/target" ]

    # Only expected runnable is located in bin directory
    [ -f ""${KOGITO_HOME}"/bin/myapp-0.0.1.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1.jar.original" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests-sources.jar" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target Quarkus build" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/target
    touch /tmp/src/target/myapp-0.0.1.jar
    touch /tmp/src/target/myapp-0.0.1-runner.jar
    touch /tmp/src/target/myapp-0.0.1-sources.jar
    touch /tmp/src/target/myapp-0.0.1-tests.jar
    touch /tmp/src/target/myapp-0.0.1-tests-sources.jar
    mkdir -p /tmp/src/target/classes
    mkdir -p /tmp/src/target/generated-sources
    mkdir -p /tmp/src/target/lib
    touch /tmp/src/target/lib/mydependency-0.0.1.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]

    # Target directory is removed from initial location
    [ ! -d "/tmp/src/target" ]

    # Only runner and dependency is located in bin directory
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1.jar" ]
    [ -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-runner.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests-sources.jar" ]
    [ -f ""${KOGITO_HOME}"/bin/lib/mydependency-0.0.1.jar" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target Quarkus native build" {
    mkdir -p "${KOGITO_HOME}"/bin
    # emulating an upload
    mkdir -p /tmp/src/target
    touch /tmp/src/target/myapp-0.0.1.jar
    cp $BATS_TEST_DIRNAME/mocks/myapp-0.0.1-runner /tmp/src/target/myapp-0.0.1-runner
    touch /tmp/src/target/myapp-0.0.1-runner.jar
    touch /tmp/src/target/myapp-0.0.1-sources.jar
    touch /tmp/src/target/myapp-0.0.1-tests.jar
    touch /tmp/src/target/myapp-0.0.1-tests-sources.jar
    mkdir -p /tmp/src/target/classes
    mkdir -p /tmp/src/target/generated-sources
    mkdir -p /tmp/src/target/lib
    touch /tmp/src/target/lib/mydependency-0.0.1.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]

    # Target directory is removed from initial location
    [ ! -d "/tmp/src/target" ]

    # Only runner and dependency is located in bin directory
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-runner.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/myapp-0.0.1-tests-sources.jar" ]
    [ ! -f ""${KOGITO_HOME}"/bin/lib/mydependency-0.0.1.jar" ]
}

@test "test copy_kogito_app default java build no jar file present" {
    run copy_kogito_app

    echo "result= ${lines[@]}"
    echo "status= $status"
    [ "$status" -eq 1 ]
    [ "${lines[0]}" = "---> Installing common application binaries" ]
    [ "${lines[1]}" = "cp: cannot stat 'target/*.jar': No such file or directory" ]
}

@test "test copy_kogito_app default java build jar file present" {
    touch target/app.jar
    run copy_kogito_app

    rm -rf target/app.jar

    echo "result= ${lines[@]}"
    echo "status= $status"
    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing common application binaries" ]
    [ "${lines[1]}" = "'target/app.jar' -> '"${KOGITO_HOME}"/bin/app.jar'" ]
}

@test "test copy_kogito_app default quarkus java build no jar file present" {
    NATIVE="false"
    touch target/app-runner.jar
    mkdir target/lib
    touch target/lib/{lib.jar,lib1.jar}

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing runner jar file" ]
    [ "${lines[1]}" = "'target/app-runner.jar' -> '"${KOGITO_HOME}"/bin/app-runner.jar'" ]
    [ "${lines[2]}" = "---> Copying application libraries" ]
}

@test "test copy_kogito_app default quarkus java build uberJar runner file present" {
    NATIVE="false"
    touch target/app-runner.jar

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing runner jar file" ]
    [ "${lines[1]}" = "'target/app-runner.jar' -> '"${KOGITO_HOME}"/bin/app-runner.jar'" ]
}

@test "test copy_kogito_app default quarkus native builds file present" {
    NATIVE="true"
    touch target/app-runner
    mkdir target/lib
    touch target/lib/{lib.jar,lib1.jar}

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    echo "${lines[1]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing native application binaries" ]
    [ "${lines[1]}" = "'target/app-runner' -> '"${KOGITO_HOME}"/bin/app-runner'" ]
}

@test "build_kogito_app only checks if it will generate the project in case there's no pom.xml" {
    mkdir /tmp/src

    run build_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Generating quarkus project structure for project..." ]
}

@test "build_kogito_app only checks if it will generate the springboot project in case there's no pom.xml" {
    mkdir /tmp/src

    run build_kogito_app $SPRINGBOOT_RUNTIME_TYPE
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Generating springboot project structure for project..." ]
    [ "${lines[1]}" = "----> Using Spring Boot to bootstrap the application." ]
}

@test "build_kogito_app only checks if it will a build will be triggered if a pom is found" {
    mkdir /tmp/src
    touch /tmp/src/pom.xml

    run build_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Building application from source..." ]
}

@test "build_kogito_app build a project from a JSON Serverless Workflow file" {
    mkdir /tmp/src
    touch /tmp/src/workflow.sw.json

    run build_kogito_app
    rm -rf target/*
    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Generating quarkus project structure for project..." ]
}

@test "build_kogito_app build a project from a YAML Serverless Workflow file" {
    mkdir /tmp/src
    touch /tmp/src/workflow.sw.yaml

    run build_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Generating quarkus project structure for project..." ]
}


@test "test get_runtime_type to make sure it returns the expected runtime_type for Quarkus using binary build" {
    echo "Main-Class: io.quarkus.bootstrap.runner.QuarkusEntryPoint" > /tmp/MANIFEST.MF
    jar -0 -c --file $KOGITO_HOME/bin/my-app.jar -m /tmp/MANIFEST.MF

    run get_runtime_type

    echo "result: ${lines[@]}"
    [ "${lines[2]}" = "quarkus" ]
}

@test "test get_runtime_type to make sure it returns the expected runtime_type for Springboot binary build" {
    echo "Main-Class: org.springframework.boot.loader.JarLauncher" > /tmp/MANIFEST.MF
    echo "public void hello(){}" > /tmp/hello.java
    jar -0 -c --file $KOGITO_HOME/bin/my-app.jar -m /tmp/MANIFEST.MF
    jar -0 -c --file /tmp/my-app.jar -m /tmp/MANIFEST.MF

    run get_runtime_type

    echo "result: ${lines[@]}"
    [ "${lines[2]}" = "springboot" ]
}


@test "test if the Quarkus platform properties are correctly returned for community version" {
    QUARKUS_PLATFORM_VERSION=1.2.3.4

    result=$(get_quarkus_platform_properties)

    expected=" -DplatformGroupId=io.quarkus.platform -DplatformArtifactId=quarkus-bom -DplatformVersion=1.2.3.4"

    echo "result  : $result  `env | grep QUARKUS`"
    echo "expected: $expected"
    [ "${result}" = "${expected}" ]
}

@test "Check if the expected message is printed if native build is enabled" {
    QUARKUS_PLATFORM_VERSION="1.2.3.4"
    JBOSS_IMAGE_NAME="rhpam-7/kogito-builder"
    NATIVE=true
    mkdir /tmp/src
    run build_kogito_app
    echo "result   = $(echo ${lines[0]} |  sed -r 's/\x1B\[(;?[0-9]{1,3})+[mGK]//g')"
    [ "$status" -eq 10 ]
    # remove color from the log_warning func
    [ "$(echo ${lines[0]} |  sed -r 's/\x1B\[(;?[0-9]{1,3})+[mGK]//g')" = "WARN Container Image rhpam-7/kogito-builder does not supports native builds, please refer to the documentation." ]
}

@test "test if the Quarkus platform properties are correctly returned for using custom values" {
    QUARKUS_PLATFORM_VERSION="12"
    QUARKUS_PLATFORM_GROUP_ID="groupId-1"
    QUARKUS_PLATFORM_ARTIFACT_ID="artifactId-2"

    result=$(get_quarkus_platform_properties)

    expected=" -DplatformGroupId=groupId-1 -DplatformArtifactId=artifactId-2 -DplatformVersion=12"

    echo "result  : $result"
    echo "expected: $expected"
    [ "${result}" = "${expected}" ]
}

