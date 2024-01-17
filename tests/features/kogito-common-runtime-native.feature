@quay.io/kiegroup/kogito-runtime-native
Feature: Kogito-runtime-native feature.

  Scenario: Verify if the binary build is finished as expected and if it is listening on the expected port with quarkus native
    Given s2i build /tmp/kogito-examples/kogito-quarkus-examples/rules-quarkus-helloworld-native/ from target
      | variable            | value                     |
      | NATIVE              | true                      |
      | BINARY_BUILD        | true                      |
      | RUNTIME_TYPE        | quarkus                   |
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
