@quay.io/kiegroup/kogito-builder
Feature: kogito-builder image tests

  Scenario: Verify if the s2i build is finished as expected using native build and runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld using master and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
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
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist
    And s2i build log should contain -J-Xmx4g

  Scenario: Verify if the s2i build is finished as expected using native build and no runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld using master
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
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist
    And s2i build log should contain -J-Xmx4g

  Scenario: Verify if the s2i build is finished as expected with non native build and no runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld using master
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
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: Verify if the s2i build is finished as expected with non native build and no runtime image and no RUNTIME_TYPE defined
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld using master
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
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
    And file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: Verify if the s2i build is finished as expected performing a non native build with runtime image
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
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
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
    And container log should contain DEBUG [io.qua.
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Dquarkus.log.level=DEBUG

  Scenario: Verify if the s2i build is finished as expected performing a non native build and if it is listening on the expected port , test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from rules-quarkus-helloworld using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
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
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist

  Scenario: Verify if the s2i build is finished as expected performing a native build and if it is listening on the expected port, test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from rules-quarkus-helloworld using master and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
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

  Scenario: Verify if the s2i build is finished as expected performing a non native build with persistence enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-quarkus-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | NATIVE            | false         |
      | RUNTIME_TYPE      | quarkus       |
      | MAVEN_ARGS_APPEND | -Ppersistence |
    Then file /home/kogito/bin/process-quarkus-example-runner.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'

  #ignore until https://issues.redhat.com/browse/KOGITO-3638 is resolved
  @ignore
  Scenario: Verify if the s2i build is finished as expected performing a native build with persistence enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-quarkus-example using master and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable          | value         |
      | RUNTIME_TYPE      | quarkus       |
      | NATIVE            | true          |
      | LIMIT_MEMORY      | 6442450944    |
      | MAVEN_ARGS_APPEND | -Ppersistence |
    Then file /home/kogito/bin/process-quarkus-example-runner should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'

  Scenario: Verify if the multi-module s2i build is finished as expected performing a non native build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value                            |
      | RUNTIME_TYPE      | quarkus                          |
      | NATIVE            | false                            |
      | ARTIFACT_DIR      | rules-quarkus-helloworld/target  |
      | MAVEN_ARGS_APPEND | -pl rules-quarkus-helloworld -am |
    Then check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist

  Scenario: Verify if the multi-module s2i build is finished as expected performing a native build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using master and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable          | value                            |
      | RUNTIME_TYPE      | quarkus                          |
      | NATIVE            | true                             |
      | LIMIT_MEMORY      | 6442450944                       |
      | ARTIFACT_DIR      | rules-quarkus-helloworld/target  |
      | MAVEN_ARGS_APPEND | -pl rules-quarkus-helloworld -am |
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

  Scenario: Perform a incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld with env and incremental using master
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    Then s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
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
  Scenario: Perform a second incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld with env and incremental using master
      | variable     | value   |
      | RUNTIME_TYPE | quarkus |
      | NATIVE       | false   |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
    And file /home/kogito/bin/rules-quarkus-helloworld-runner.jar should exist
    And check that page is served
      | property        | value                 |
      | port            | 8080                  |
      | path            | /hello                |
      | request_method  | POST                  |
      | content_type    | application/json      |
      | request_body    | {"strings":["hello"]} |
      | wait            | 80                    |
      | expected_phrase | ["hello","world"]     |

  Scenario: Perform a third incremental s2i build, this time, with native enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from rules-quarkus-helloworld with env and incremental using master
      | variable     | value      |
      | RUNTIME_TYPE | quarkus    |
      | NATIVE       | true       |
      | LIMIT_MEMORY | 6442450944 |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts
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

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on Quarkus or Spring Boot
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus or Spring Boot
    And the image should contain label io.openshift.tags with value builder,kogito,quarkus,springboot

  Scenario: verify java cacerts and libsunec are available in the given container.
    When container is started with command bash
    Then  file /home/kogito/ssl-libs/libsunec.so should exist
    And file /home/kogito/cacerts should exist

  Scenario: verify if the maven and graal vm settings are correct
    When container is started with command bash
    Then run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven
    And run sh -c 'echo $MAVEN_VERSION' in container and immediately check its output for 3.6.2
    And run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-11
    And run sh -c 'echo $GRAALVM_HOME' in container and immediately check its output for /usr/share/graalvm
    And run sh -c 'echo $GRAALVM_VERSION' in container and immediately check its output for 20.2.0

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly
    Given s2i build /tmp/kogito-examples from dmn-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable       | value          |
      | RUNTIME_TYPE   | quarkus        |
      | NATIVE         | false          |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |  
    Then file /home/kogito/bin/project-1.0-SNAPSHOT-runner.jar should exist
    And check that page is served
      | property        | value                                                                                            |
      | port            | 8080                                                                                             |
      | path            | /Traffic%20Violation                                                                             |
      | wait            | 80                                                                                               |
      | expected_phrase | Should the driver be suspended?                                                                  |
      | request_method  | POST                                                                                             |
      | content_type    | application/json                                                                                 |
      | request_body    | {"Driver": {"Points": 2}, "Violation": {"Type": "speed","Actual Speed": 120,"Speed Limit": 100}} |

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly using native build
    Given s2i build /tmp/kogito-examples from dmn-example using master and runtime-image quay.io/kiegroup/kogito-runtime-native:latest
      | variable       | value          |
      | RUNTIME_TYPE   | quarkus        |
      | NATIVE         | true           |
      | LIMIT_MEMORY   | 6442450944     |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |  
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

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly with custom group id, archetype & version
    Given s2i build /tmp/kogito-examples from dmn-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable            | value          |
      | RUNTIME_TYPE        | quarkus        |
      | NATIVE              | false          |
      | KOGITO_VERSION      | 2.0.0-SNAPSHOT |  
      | PROJECT_GROUP_ID    | com.mycompany  |
      | PROJECT_ARTIFACT_ID | myproject      |
      | PROJECT_VERSION     | 2.0-SNAPSHOT   |
    Then file /home/kogito/bin/myproject-2.0-SNAPSHOT-runner.jar should exist
    And check that page is served
      | property        | value                                                                                            |
      | port            | 8080                                                                                             |
      | path            | /Traffic%20Violation                                                                             |
      | wait            | 80                                                                                               |
      | expected_phrase | Should the driver be suspended?                                                                  |
      | request_method  | POST                                                                                             |
      | content_type    | application/json                                                                                 |
      | request_body    | {"Driver": {"Points": 2}, "Violation": {"Type": "speed","Actual Speed": 120,"Speed Limit": 100}} |

#### SpringBoot Scenarios

  Scenario: Verify if the s2i build is finished as expected with debug enabled
      Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
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
      And container log should contain main] .c.l.ClasspathLoggingApplicationListener
      And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true
  
  Scenario: Verify if the s2i build is finished as expected with no runtime image and debug enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example using master
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
    And container log should contain main] .c.l.ClasspathLoggingApplicationListener
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true
  
  Scenario: Verify if the s2i build is finished as expected and if it is listening on the expected port, test uses custom properties file to test the port configuration.
    Given s2i build /tmp/kogito-examples from process-springboot-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
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

  Scenario: Verify if the s2i build is finished as expected with persistence enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | MAVEN_ARGS_APPEND | -Ppersistence |
      | RUNTIME_TYPE      | springboot    |
    Then file /home/kogito/bin/process-springboot-example.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'
  
  Scenario: Verify if the s2i build is finished as expected using multi-module build with debug enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from . using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value |
      | JAVA_OPTIONS      | -Ddebug=true                       |
      | RUNTIME_TYPE      | springboot                         |
      | ARTIFACT_DIR      | process-springboot-example/target  |
      | MAVEN_ARGS_APPEND | -pl process-springboot-example -am |
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
    And container log should contain main] .c.l.ClasspathLoggingApplicationListener
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true

  Scenario: Perform a incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example with env and incremental using master
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
  Scenario: Perform a second incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example with env and incremental using master
      # Leave those here as placeholder for scripts adding variable to the test. No impact on tests if empty.
      | variable     | value      |
      | RUNTIME_TYPE | springboot |
    Then s2i build log should contain Expanding artifacts from incremental build...
    And s2i build log should not contain WARNING: Clean build will be performed because of error saving previous build artifacts

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly when runtime is springboot
    Given s2i build /tmp/kogito-examples from dmn-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable       | value          |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |
      | RUNTIME_TYPE   | springboot     |  
    Then file /home/kogito/bin/project-1.0-SNAPSHOT.jar should exist


