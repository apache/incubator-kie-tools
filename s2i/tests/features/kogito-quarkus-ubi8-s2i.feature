@quay.io/kiegroup/kogito-quarkus-ubi8-s2i
Feature: kogito-quarkus-ubi8-s2i image tests

  Scenario: Verify if the s2i build is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples from drools-quarkus-example using 0.6.0 and runtime-image quay.io/kiegroup/kogito-quarkus-ubi8:latest
      | variable       | value                     |
      | LIMIT_MEMORY   | 2147483648                |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | wait            | 80                       |
      | expected_phrase | Mario is older than Mark |
    And file /home/kogito/bin/drools-quarkus-example-0.6.0-runner should exist
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist
    And s2i build log should contain -J-Xmx1717986918

  Scenario: Verify if the s2i build is finished as expected performing a non native build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from drools-quarkus-example using 0.6.0 and runtime-image quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest
      | variable            | value                     |
      | NATIVE              | false                     |
      | JAVA_OPTIONS        | -Dquarkus.log.level=DEBUG |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | wait            | 80                       |
      | expected_phrase | Mario is older than Mark |
    And file /home/kogito/bin/drools-quarkus-example-0.6.0-runner.jar should exist
    And container log should contain DEBUG [io.qua.
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Dquarkus.log.level=DEBUG

  Scenario: Verify if the s2i build is finished as expected performing a non native build and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples from drools-quarkus-example using 0.6.0 and runtime-image quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest
      | variable | value |
      | NATIVE   | false |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | wait            | 80                       |
      | expected_phrase | Mario is older than Mark |
    And file /home/kogito/bin/drools-quarkus-example-0.6.0-runner.jar should exist

    Scenario: Verify if the multi-module s2i build is finished as expected performing a non native build and if it is listening on the expected port
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using 0.6.0 and runtime-image quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest
      | variable          | value                           |
      | NATIVE            | false                           |
      | ARTIFACT_DIR      | drools-quarkus-example/target   |
      | MAVEN_ARGS_APPEND | -pl drools-quarkus-example -am  |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | wait            | 80                       |
      | expected_phrase | Mario is older than Mark |
    And file /home/kogito/bin/drools-quarkus-example-0.6.0-runner.jar should exist

  Scenario: Perform a incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from drools-quarkus-example with env and incremental using 0.6.0
      | variable          | value                  |
      | NATIVE            | false                  |
    Then s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts

  # Since the same image is used we can do a subsequent incremental build and verify if it is working as expected.
  Scenario: Perform a second incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from drools-quarkus-example with env and incremental using 0.6.0
      | variable          | value    |
      | NATIVE            | false    |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on Quarkus
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus
    And the image should contain label io.openshift.tags with value builder,kogito,quarkus

  Scenario: verify java cacerts and libsunec are available in the given container.
    When container is started with command bash
    Then  file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: verify if the maven and graal vm settings are correct
    When container is started with command bash
    Then run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven
    And run sh -c 'echo $MAVEN_VERSION' in container and immediately check its output for 3.6.0
    And run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-1.8.0
    And run sh -c 'echo $GRAALVM_HOME' in container and immediately check its output for /usr/share/graalvm
    And run sh -c 'echo $GRAALVM_VERSION' in container and immediately check its output for 19.2.1

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly
    Given s2i build /tmp/kogito-examples from dmn-quarkus-example using 0.6.0 and runtime-image quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest
      | variable          | value                           |
      | NATIVE            | false                           |
      | KOGITO_VERSION    | 8.0.0-SNAPSHOT                  |
    Then file /home/kogito/bin/project-1.0-SNAPSHOT-runner.jar should exist

