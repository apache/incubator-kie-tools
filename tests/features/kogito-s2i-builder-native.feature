@quay.io/kiegroup/kogito-s2i-builder
Feature: kogito-s2i-builder image native build tests

  Scenario: verify java cacerts and libsunec are available in the given kogito builder container.
    When container is started with command bash
    Then file /home/kogito/cacerts should exist

  Scenario: verify if the maven and graal vm settings are correct on kogito-s2i-builder image for native
    When container is started with command bash
    Then run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven
    And run sh -c 'echo $MAVEN_VERSION' in container and immediately check its output for 3.9.3
    And run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-17
    And run sh -c 'echo $GRAALVM_HOME' in container and immediately check its output for /usr/share/graalvm
    And run sh -c 'echo $GRAALVM_VERSION' in container and immediately check its output for 23.0.2.1

  @ignore
  Scenario: Verify if the s2i build is finished as expected using native build and runtime image
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable     | value      |
      | NATIVE       | true       |
      | RUNTIME_TYPE | quarkus    |
      | LIMIT_MEMORY | 3221225472 |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist
    And s2i build log should contain -J-Xmx2576980378

  Scenario: Verify if the s2i build is finished as expected using native build and no runtime image
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main
      | variable | value          |
      | NATIVE       | true       |
      | RUNTIME_TYPE | quarkus    |
      | LIMIT_MEMORY | 3221225472 |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist
    And file /home/kogito/cacerts should exist
    And s2i build log should contain -J-Xmx2576980378

  @ignore
  Scenario: Verify if the s2i build is finished as expected performing a native build and if it is listening on the expected port, test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable     | value      |
      | NATIVE       | true       |
      | RUNTIME_TYPE | quarkus    |
      | LIMIT_MEMORY | 6442450944 |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist
    And s2i build log should contain -J-Xmx5153960755

  @ignore
  Scenario: Verify if the s2i build is finished as expected performing a native build with persistence enabled - Step 1: build the application and copy to the runtime image
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/process-quarkus-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable          | value         |
      | RUNTIME_TYPE      | quarkus       |
      | NATIVE            | true          |
      | LIMIT_MEMORY      | 6442450944    |
      | MAVEN_ARGS_APPEND | -Ppersistence |
    When container integ- is started with command bash
    Then file /home/kogito/bin/process-quarkus-example-runner should exist
     And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
     And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'
     And s2i build log should contain -J-Xmx5153960755

  Scenario: Perform an incremental s2i build for native test
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld with env and incremental using nightly-main
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    And s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld with env and incremental using nightly-main
      | variable     | value      |
      | RUNTIME_TYPE | quarkus    |
      | NATIVE       | true       |
      | LIMIT_MEMORY | 6442450944 |
    Then s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
    And s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should contain -J-Xmx5153960755
    And file /home/kogito/bin/rules-quarkus-helloworld-runner should exist
    And check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |    

  @ignore
  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly using native build
    Given s2i build /tmp/kogito-examples from dmn-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable       | value          |
      | RUNTIME_TYPE   | quarkus        |
      | NATIVE         | true           |
      | LIMIT_MEMORY   | 6442450944     |
      | KOGITO_VERSION | 999-SNAPSHOT |      
    Then file /home/kogito/bin/project-1.0-SNAPSHOT-runner should exist
    And check that page is served
      | property        | value                                                                                            |
      | port            | 8080                                                                                             |
      | path            | /Traffic%20Violation                                                                             |
      | wait            | 80                                                                                               |
      | expected_phrase | Should the driver be suspended?                                                                  |
      | request_method  | POST                                                                                             |
      | content_type    | application/json                                                                                 |
      | request_body    | {"Driver": {"Points": 2}, "Violation": {"Type": "speed","Actual Speed": 120,"Speed Limit": 100}} |
    And check that page is served
      | property        | value                           |
      | port            | 8080                            |
      | path            | /q/health/live                  |
      | wait            | 80                              |
      | request_method  | GET                             |
      | content_type    | application/json                |
      | request_body    | {"status": "UP", "checks": []}  |
    And s2i build log should contain -J-Xmx5153960755
