@quay.io/kiegroup/kogito-quarkus-ubi8
Feature: Kogito-quarkus-ubi8 feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
     And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
     And the image should contain label io.openshift.s2i.destination with value /tmp
     And the image should contain label io.openshift.expose-services with value 8080:http
     And the image should contain label io.k8s.description with value Runtime image for Kogito based on Quarkus native image
     And the image should contain label io.k8s.display-name with value Kogito based on Quarkus native image
     And the image should contain label io.openshift.tags with value builder,runtime,kogito,quarkus,native
     And the image should contain label io.openshift.s2i.assemble-input-files with value /home/kogito/bin

  Scenario: verify java cacerts and libsunec are available in the given container.
    When container is started with command bash
    Then  file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: Verify if the binary build is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples/rules-quarkus-helloworld-native/ from target
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
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist

  Scenario: Verify if the binary build is finished as expected and if it is listening on the custom port
    Given s2i build /tmp/kogito-examples/rules-quarkus-helloworld-native/ from target
      | variable            | value                     |
      | NATIVE              | false                     |
      | JAVA_OPTIONS        | -Dquarkus.log.level=DEBUG |
      | HTTP_PORT           | 9090                      |
    Then check that page is served
      | property        | value                    |
      | port            | 9090                     |
      | path            | /hello                   |
      | request_method  | POST                     |
      | content_type    | application/json         |
      | request_body    | {"strings":["hello"]}    |
      | wait            | 80                       |
      | expected_phrase | ["hello","world"]        |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist