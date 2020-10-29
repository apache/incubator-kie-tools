@quay.io/kiegroup/kogito-quarkus-jvm-ubi8
Feature: Kogito-quarkus-ubi8 feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
     And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
     And the image should contain label io.openshift.s2i.destination with value /tmp
     And the image should contain label io.openshift.expose-services with value 8080:http
     And the image should contain label io.k8s.description with value Runtime image for Kogito based on Quarkus JVM image
     And the image should contain label io.k8s.display-name with value Kogito based on Quarkus JVM image
     And the image should contain label io.openshift.tags with value builder,runtime,kogito,quarkus,jvm
     And the image should contain label io.openshift.s2i.assemble-input-files with value /home/kogito/bin

  Scenario: verify if the java installation is correct
    When container is started with command bash
    Then run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-11
    And run sh -c 'echo $JAVA_VENDOR' in container and immediately check its output for openjdk
    And run sh -c 'echo $JAVA_VERSION' in container and immediately check its output for 11

  Scenario: Verify if the binary build is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples/rules-quarkus-helloworld from target
      | variable            | value                     |
      | NATIVE              | false                     |
      | JAVA_OPTIONS        | -Dquarkus.log.level=DEBUG |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | request_method  | POST                     | 
      | content_type    | application/json         |
      | request_body    | {"strings":["hello"]}    |
      | wait            | 80                       |
      | expected_phrase | ["hello","world"]        |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist

  Scenario: Verify if the binary build (forcing) is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples/rules-quarkus-helloworld from target
      | variable            | value                     |
      | NATIVE              | false                     |
      | JAVA_OPTIONS        | -Dquarkus.log.level=DEBUG |
      | BINARY_BUILD        | true                      |
    Then check that page is served
      | property        | value                    |
      | port            | 8080                     |
      | path            | /hello                   |
      | request_method  | POST                     | 
      | content_type    | application/json         |
      | request_body    | {"strings":["hello"]}    |
      | wait            | 80                       |
      | expected_phrase | ["hello","world"]        |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
