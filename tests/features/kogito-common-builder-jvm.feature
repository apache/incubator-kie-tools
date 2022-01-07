@quay.io/kiegroup/kogito-builder @rhpam-7/rhpam-kogito-builder-rhel8
Feature: kogito-builder image JVM build tests

  Scenario: verify if the maven and java installation are correct
    When container is started with command bash
    Then run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven
    And run sh -c 'echo $MAVEN_VERSION' in container and immediately check its output for 3.8.1
    And run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-11

  Scenario: Verify if the s2i build is finished as expected with non native build and no runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main
      | variable     | value   |
      | NATIVE       | false   |
      | RUNTIME_TYPE | quarkus |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/quarkus-run.jar should exist
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: Verify if the s2i build is finished as expected with non native build and no runtime image and no RUNTIME_TYPE defined
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main
      | variable     | value   |
      | NATIVE       | false   |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/quarkus-run.jar should exist
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: Verify if the s2i build is finished as expected performing a non native build with runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable     | value                     |
      | NATIVE       | false                     |
      | RUNTIME_TYPE | quarkus                   |
      | JAVA_OPTIONS | -Dquarkus.log.level=DEBUG |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/quarkus-run.jar should exist
    And container log should contain DEBUG [io.qua.
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Dquarkus.log.level=DEBUG

  Scenario: Verify if the s2i build is finished as expected performing a non native build and if it is listening on the expected port , test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from kogito-quarkus-examples/rules-quarkus-helloworld using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/quarkus-run.jar should exist

  Scenario: Verify if the multi-module s2i build is finished as expected performing a non native build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value                                                    |
      | RUNTIME_TYPE      | quarkus                                                  |
      | NATIVE            | false                                                    |
      | ARTIFACT_DIR      | kogito-quarkus-examples/rules-quarkus-helloworld/target  |
      | MAVEN_ARGS_APPEND | -pl kogito-quarkus-examples/rules-quarkus-helloworld -am                         |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/quarkus-run.jar should exist

  Scenario: Perform a incremental s2i build using quarkus runtime type
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld with env and incremental using nightly-main
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    Then s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
    And file /home/kogito/bin/quarkus-run.jar should exist
    And check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |

  # Since the same image is used we can do a subsequent incremental build and verify if it is working as expected.
  Scenario: Perform a second incremental s2i build using quarkus runtime type
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/rules-quarkus-helloworld with env and incremental using nightly-main
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
    And file /home/kogito/bin/quarkus-run.jar should exist
    And check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |


#### SpringBoot Scenarios

  Scenario: Verify if the s2i build is finished as expected with debug enabled
      Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-springboot-examples/process-springboot-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
        | variable     | value        |
        | RUNTIME_TYPE | springboot   |
        | JAVA_OPTIONS | -Ddebug=true |
      Then check that page is served
        | property             | value                                                                         |
        | port                 | 8080                                                                          |
        | path                 | /orders                                                                       |
        | wait                 | 80                                                                            |
        | request_method       | POST                                                                          |
        | request_body         | {"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}} |
        | content_type         | application/json                                                              |
        | expected_status_code | 201                                                                           |
      And file /home/kogito/bin/process-springboot-example.jar should exist
      And container log should contain Started DemoApplication
      And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true
  
  Scenario: Verify if the s2i build is finished as expected with no runtime image and debug enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-springboot-examples/process-springboot-example using nightly-main
      | variable            | value        |
      | JAVA_OPTIONS        | -Ddebug=true |
      | RUNTIME_TYPE        | springboot   |
    Then check that page is served
      | property             | value                                                                         |
      | port                 | 8080                                                                          |
      | path                 | /orders                                                                       |
      | wait                 | 80                                                                            |
      | request_method       | POST                                                                          |
      | request_body         | {"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}} |
      | content_type         | application/json                                                              |
      | expected_status_code | 201                                                                           |
    And file /home/kogito/bin/process-springboot-example.jar should exist
    And container log should contain Started DemoApplication
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true
  
  Scenario: Verify if the s2i build is finished as expected and if it is listening on the expected port, test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from kogito-springboot-examples/process-springboot-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      # Leave those here as placeholder for scripts adding variable to the test. No impact on tests if empty.
      | variable     | value      |
      | RUNTIME_TYPE | springboot |
    Then check that page is served
      | property             | value                                                                         |
      | port                 | 8080                                                                          |
      | path                 | /orders                                                                       |
      | wait                 | 80                                                                            |
      | request_method       | POST                                                                          |
      | request_body         | {"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}} |
      | content_type         | application/json                                                              |
      | expected_status_code | 201                                                                           |
    And file /home/kogito/bin/process-springboot-example.jar should exist
    And container log should contain Tomcat initialized with port(s): 8080 (http)
  
  Scenario: Verify if the s2i build is finished as expected using multi-module build with debug enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value |
      | JAVA_OPTIONS      | -Ddebug=true                       |
      | RUNTIME_TYPE      | springboot                         |
      | ARTIFACT_DIR      | kogito-springboot-examples/process-springboot-example/target  |
      | MAVEN_ARGS_APPEND | -pl kogito-springboot-examples/process-springboot-example -am |
    Then check that page is served
      | property             | value                                                                         |
      | port                 | 8080                                                                          |
      | path                 | /orders                                                                       |
      | wait                 | 80                                                                            |
      | request_method       | POST                                                                          |
      | request_body         | {"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}} |
      | content_type         | application/json                                                              |
      | expected_status_code | 201                                                                           |
    And file /home/kogito/bin/process-springboot-example.jar should exist
    And container log should contain Started DemoApplication
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true

  Scenario: Perform a incremental s2i build using springboot runtime type
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-springboot-examples/process-springboot-example with env and incremental using nightly-main
      # Leave those here as placeholder for scripts adding variable to the test. No impact on tests if empty.
      | variable     | value      |
      | RUNTIME_TYPE | springboot |
    Then check that page is served
      | property             | value                                                                         |
      | port                 | 8080                                                                          |
      | path                 | /orders                                                                       |
      | wait                 | 80                                                                            |
      | request_method       | POST                                                                          |
      | request_body         | {"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}} |
      | content_type         | application/json                                                              |
      | expected_status_code | 201                                                                           |
    And file /home/kogito/bin/process-springboot-example.jar should exist

  # Since the same image is used we can do a subsequent incremental build and verify if it is working as expected.
  Scenario: Perform a second incremental s2i build using springboot runtime type
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-springboot-examples/process-springboot-example with env and incremental using nightly-main
      # Leave those here as placeholder for scripts adding variable to the test. No impact on tests if empty.
      | variable     | value      |
      | RUNTIME_TYPE | springboot |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts

  Scenario: Verify if the s2i build is finished as expected with uber-jar package type built
    Given s2i build https://github.com/kiegroup/kogito-examples.git from kogito-quarkus-examples/process-quarkus-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value                           |
      | MAVEN_ARGS_APPEND | -Dquarkus.package.type=uber-jar |
      | RUNTIME_TYPE      | quarkus                         |
    Then file /home/kogito/bin/process-quarkus-example-runner.jar should exist
