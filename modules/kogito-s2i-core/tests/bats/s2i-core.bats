#!/usr/bin/env bats

export KOGITO_HOME=$BATS_TMPDIR/kogito_home
mkdir -p ${KOGITO_HOME}/launch/

cp $BATS_TEST_DIRNAME/../../../kogito-logging/added/logging.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-persistence/added/kogito-persistence.sh $KOGITO_HOME/launch/
cp $BATS_TEST_DIRNAME/../../../kogito-kubernetes-client/added/kogito-kubernetes-client.sh $KOGITO_HOME/launch/

# imports
source $BATS_TEST_DIRNAME/../../added/s2i-core

setup() {
    export HOME=$KOGITO_HOME
    mkdir -p target
}

teardown() {
    rm -rf ${KOGITO_HOME}/bin
    rm -rf /tmp/.s2i
    rm -rf /tmp/src
    rm -rf target
    rm -rf $KOGITO_HOME/bin/*
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
    echo "result= ${lines[@]}"
    [ "${lines[0]}" = "---> Application binaries NOT found, failing build..." ]
    [ "$status" -eq 1 ]
}


@test "test assemble_runtime with binaries binaries" {
    mkdir $KOGITO_HOME/bin
    touch $KOGITO_HOME/bin/artifact.jar

    run assemble_runtime

    rm -rf $KOGITO_HOME/bin/image_metadata.json
    rm -rf $KOGITO_HOME/bin/artifact.jar

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Application binaries found and ready to use" ]
    [ "${lines[1]}" = "---> [s2i-core] Adding custom labels..." ]
    [ "${lines[2]}" = "-----> Failed to copy metadata file, ${KOGITO_HOME}/bin/image_metadata.json does not exist" ]
}


@test "test assemble_runtime with binaries binaries and metadata" {
    mkdir $KOGITO_HOME/bin
    touch $KOGITO_HOME/bin/image_metadata.json
    touch $KOGITO_HOME/bin/artifact.jar

    run assemble_runtime

    echo "result= ${lines[@]}"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Application binaries found and ready to use" ]
    [ "${lines[1]}" = "---> [s2i-core] Adding custom labels..." ]
    [ "${lines[2]}" = "mkdir: created directory '/tmp/.s2i'" ]
    [ "${lines[3]}" = "mkdir: created directory '/tmp/src'" ]
    [ "${lines[4]}" = "mkdir: created directory '/tmp/src/.s2i/'" ]
    [ "${lines[5]}" = "'$KOGITO_HOME/bin/image_metadata.json' -> '/tmp/.s2i/image_metadata.json'" ]
    [ "${lines[6]}" = "'$KOGITO_HOME/bin/image_metadata.json' -> '/tmp/src/.s2i/image_metadata.json'" ]

}

@test "test runtime_assemble" {
    mkdir -p ${KOGITO_HOME}/bin
    mkdir -p /tmp/src/bin
    touch /tmp/src/bin/myapp.jar

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[1]}" = "'./bin/myapp.jar' -> '$KOGITO_HOME/./bin/myapp.jar'" ]
}

@test "test runtime_assemble with binary builds" {
    mkdir -p ${KOGITO_HOME}/bin
    # emulating an upload
    mkdir -p /tmp/src/
    touch /tmp/src/myapp.jar
    mkdir -p /tmp/src/lib
    mkdir -p /tmp/src/classes
    mkdir -p /tmp/src/maven-archiver

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[7]}" = "'./myapp.jar' -> '$KOGITO_HOME/bin/myapp.jar'" ]
}

@test "test runtime_assemble with binary builds native binary" {
    mkdir -p ${KOGITO_HOME}/bin
    # emulating an upload
    mkdir -p /tmp/src/
    cp $BATS_TEST_DIRNAME/mocks/myapp-0.0.1-runner /tmp/src/myapp-0.0.1-runner

    run runtime_assemble

    echo "result= ${lines[@]}"
    [ "$status" -eq 0 ]
    [ "${lines[8]}" = "'./myapp-0.0.1-runner' -> '$KOGITO_HOME/bin/myapp-0.0.1-runner'" ]

    # Only runner is located in bin directory
    [ -f "$KOGITO_HOME/bin/myapp-0.0.1-runner" ]
}

@test "test runtime_assemble with binary builds entire target!" {
    mkdir -p ${KOGITO_HOME}/bin
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
    [ "${lines[9]}" = "'./myapp.jar' -> '$KOGITO_HOME/bin/myapp.jar'" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target SpringBoot build" {
    mkdir -p ${KOGITO_HOME}/bin
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
    [ -f "$KOGITO_HOME/bin/myapp-0.0.1.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1.jar.original" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests-sources.jar" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target Quarkus build" {
    mkdir -p ${KOGITO_HOME}/bin
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
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1.jar" ]
    [ -f "$KOGITO_HOME/bin/myapp-0.0.1-runner.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests-sources.jar" ]
    [ -f "$KOGITO_HOME/bin/lib/mydependency-0.0.1.jar" ]
}

# Check that the irrelevant binaries are excluded
@test "test runtime_assemble with binary builds entire target Quarkus native build" {
    mkdir -p ${KOGITO_HOME}/bin
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
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-runner.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-sources.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests.jar" ]
    [ ! -f "$KOGITO_HOME/bin/myapp-0.0.1-tests-sources.jar" ]
    [ ! -f "$KOGITO_HOME/bin/lib/mydependency-0.0.1.jar" ]
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
    [ "${lines[2]}" = "'/tmp/src/target/image_metadata.json' -> '/tmp/src/.s2i/image_metadata.json'" ]
    [ "${lines[3]}" = "'/tmp/src/target/image_metadata.json' -> '$KOGITO_HOME/bin'" ]
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
    [ "${lines[1]}" = "'target/app.jar' -> '$KOGITO_HOME/bin'" ]
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
    [ "${lines[1]}" = "'target/app-runner.jar' -> '$KOGITO_HOME/bin/app-runner.jar'" ]
    [ "${lines[2]}" = "---> Copying application libraries" ]
}

@test "test copy_kogito_app default quarkus java build uberJar runner file present" {
    NATIVE="false"
    mkdir $KOGITO_HOME/bin
    touch target/app-runner.jar

    run copy_kogito_app
    rm -rf target/*

    echo "result= ${lines[@]}"
    echo "status= $status"

    [ "$status" -eq 0 ]
    [ "${lines[0]}" = "---> Installing jar file" ]
    [ "${lines[1]}" = "'target/app-runner.jar' -> '$KOGITO_HOME/bin/app-runner.jar'" ]
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
    [ "${lines[1]}" = "'target/app-runner' -> '$KOGITO_HOME/bin/app-runner'" ]
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
