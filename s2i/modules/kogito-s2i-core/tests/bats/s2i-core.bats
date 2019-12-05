#!/usr/bin/env bats

# imports
source $BATS_TEST_DIRNAME/../../added/s2i-core

setup() {
    export KOGITO_HOME=/tmp/kogito
    export HOME=$KOGITO_HOME
    mkdir -p ${KOGITO_HOME}
}

teardown() {
    rm -rf ${KOGITO_HOME}
    rm -rf /tmp/.s2i
    rm -rf /tmp/src
    rm -rf $KOGITO_HOME/bin/*
}


@test "test manage_incremental_builds" {
    rm -rf /tmp/artifacts && mkdir /tmp/artifacts
    touch /tmp/artifacts/{file,file1,file2,file3}
    run manage_incremental_build

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "Expanding artifacts from incremental build..." ]
    [ "${lines[1]}" = "./" ]
    [ "${lines[2]}" = "./file3" ]
    [ "${lines[3]}" = "./file2" ]
    [ "${lines[4]}" = "./file1" ]
    [ "${lines[5]}" = "./file" ]
}


@test "test assemble_runtime no binaries" {
    run assemble_runtime
    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Application binaries NOT found, failing build..." ]
    [ "$status" -eq 1 ]
}


@test "test assemble_runtime with binaries binaries" {
    mkdir $KOGITO_HOME/bin
    touch $KOGITO_HOME/bin/artifact.jar
    KOGITO_HOME=/tmp/kogito

    run assemble_runtime

    rm -rf $KOGITO_HOME/bin/image_metadata.json
    rm -rf $KOGITO_HOME/bin/artifact.jar

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Application binaries found and ready to use" ]
    [ "${lines[1]}" = "---> [s2i-core] Adding custom labels..." ]
    [ "${lines[2]}" = "-----> Failed to copy metadata file, /tmp/kogito/bin/image_metadata.json does not exist" ]
}


@test "test assemble_runtime with binaries binaries and metadata" {
    mkdir $KOGITO_HOME/bin
    touch $KOGITO_HOME/bin/image_metadata.json
    touch $KOGITO_HOME/bin/artifact.jar
    KOGITO_HOME=/tmp/kogito

    run assemble_runtime

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Application binaries found and ready to use" ]
    [ "${lines[1]}" = "---> [s2i-core] Adding custom labels..." ]
    [ "${lines[2]}" = "mkdir: created directory '/tmp/.s2i'" ]
    [ "${lines[3]}" = "'/tmp/kogito/bin/image_metadata.json' -> '/tmp/.s2i/image_metadata.json'" ]
}

@test "test runtime_assemble" {
    mkdir -p ${KOGITO_HOME}/bin
    mkdir /tmp/src
    touch /tmp/src/myapp.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "'./myapp.jar' -> '/tmp/kogito/bin/./myapp.jar'" ]
}


@test "test handle_image_metadata_json no metadata" {
    mkdir -p /tmp/src/target
    run handle_image_metadata_json

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> [s2i-core] Copy image metadata file..." ]
    [ "${lines[1]}" = "-----> Failed to copy metadata file, /tmp/src/target/image_metadata.json not found." ]
}


@test "test handle_image_metadata_json with metadata" {
    mkdir -p /tmp/src/target
    touch /tmp/src/target/image_metadata.json
    run handle_image_metadata_json

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> [s2i-core] Copy image metadata file..." ]
    [ "${lines[1]}" = "'/tmp/src/target/image_metadata.json' -> '/tmp/.s2i/image_metadata.json'" ]
    [ "${lines[2]}" = "'/tmp/src/target/image_metadata.json' -> '/tmp/kogito/bin'" ]
}

@test "test copy_kogito_app default java build no jar file present" {
    run copy_kogito_app

    echo "result= ${lines[@]}"
    echo "status= $status"
    [ "$status" -eq 1 ]
    [ "${lines[0]}" = "---> Installing application binaries" ]
    [ "${lines[1]}" = "cp: cannot stat 'target/*.jar': No such file or directory" ]
}

@test "test copy_kogito_app default java build jar file present" {
    touch target/app.jar
    run copy_kogito_app

    rm -rf target/app.jar

    echo "result= ${lines[@]}"
    echo "status= $status"
    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing application binaries" ]
    [ "${lines[1]}" = "'target/app.jar' -> '/tmp/kogito/bin'" ]
}


@test "test copy_kogito_app default quarkus java build no jar file present" {
    NATIVE="false"
    mkdir $KOGITO_HOME/bin
    touch target/app-runner.jar
    mkdir target/lib
    touch target/lib/{lib.jar,lib1.jar}

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing jar file" ]
    [ "${lines[1]}" = "'target/app-runner.jar' -> '/tmp/kogito/bin/app-runner.jar'" ]
    [ "${lines[2]}" = "---> Copying application libraries" ]
}

@test "test copy_kogito_app default quarkus native builds file present" {

    mkdir $KOGITO_HOME/bin
    touch target/app-runner
    mkdir target/lib
    touch target/lib/{lib.jar,lib1.jar}

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing application binaries" ]
    [ "${lines[1]}" = "'target/app-runner' -> '/tmp/kogito/bin/app-runner'" ]
}

@test "build_kogito_app only checks if it will generated the project in case there's no pom.xml" {
    mkdir /tmp/src

    run build_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Generating project structure..." ]
}


@test "build_kogito_app only checks if it will a build will be triggered if a pom is found" {
    mkdir /tmp/src
    touch /tmp/src/pom.xml

    run build_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Building application from source..." ]
}
