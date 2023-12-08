@quay.io/kiegroup/kogito-s2i-builder
Feature: kogito-s2i-builder image tests

  Scenario: verify if all labels are correctly set on kogito-s2i-builder image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on Quarkus or Spring Boot
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus or Spring Boot
    And the image should contain label io.openshift.tags with value s2i-builder,kogito,quarkus,springboot

  Scenario: verify if community builder image does not contain the red hat maven repositories
    When container is started with command bash
    Then file /home/kogito/.m2/settings.xml should not contain <id>redhat-maven-repositories</id>
    And file /home/kogito/.m2/settings.xml should not contain <activeProfile>redhat-maven-repositories</activeProfile>
    And file /home/kogito/.m2/settings.xml should not contain <id>redhat-ga-repository</id>
    And file /home/kogito/.m2/settings.xml should not contain <url>https://maven.repository.redhat.com/ga/</url>
    And file /home/kogito/.m2/settings.xml should not contain <id>redhat-ea-repository</id>
    And file /home/kogito/.m2/settings.xml should not contain <url>https://maven.repository.redhat.com/earlyaccess/all/</url>
    And file /home/kogito/.m2/settings.xml should not contain <id>redhat-techpreview-repository</id>
    And file /home/kogito/.m2/settings.xml should not contain <url>https://maven.repository.redhat.com/techpreview/all</url>

  Scenario: Verify if the s2i build is finished as expected performing a non native build with persistence enabled
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-quarkus-examples/process-quarkus-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | NATIVE            | false         |
      | RUNTIME_TYPE      | quarkus       |
      | MAVEN_ARGS_APPEND | -Ppersistence |
    Then file /home/kogito/bin/quarkus-run.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'

  Scenario: Verify if the s2i build is finished as expected with persistence enabled
    Given s2i build https://github.com/apache/incubator-kie-kogito-examples.git from kogito-springboot-examples/process-springboot-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | MAVEN_ARGS_APPEND | -Ppersistence |
      | RUNTIME_TYPE      | springboot    |
    Then file /home/kogito/bin/process-springboot-example.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly
    Given s2i build /tmp/kogito-examples from dmn-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable       | value          |
      | RUNTIME_TYPE   | quarkus        |
      | NATIVE         | false          |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |     
    Then file /home/kogito/bin/quarkus-run.jar should exist
    And s2i build log should contain Generating quarkus project structure for project...
    And s2i build log should contain Using Quarkus io.quarkus.platform:quarkus-maven-plugin:
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


  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly with custom group id, archetype & version
    Given s2i build /tmp/kogito-examples from dmn-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable            | value          |
      | RUNTIME_TYPE        | quarkus        |
      | NATIVE              | false          |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |     
      | PROJECT_GROUP_ID    | com.mycompany  |
      | PROJECT_ARTIFACT_ID | myproject      |
      | PROJECT_VERSION     | 2.0-SNAPSHOT   |
    Then file /home/kogito/bin/quarkus-run.jar should exist
    And s2i build log should contain Generating quarkus project structure for myproject...
    And check that page is served
      | property        | value                                                                                            |
      | port            | 8080                                                                                             |
      | path            | /Traffic%20Violation                                                                             |
      | wait            | 80                                                                                               |
      | expected_phrase | Should the driver be suspended?                                                                  |
      | request_method  | POST                                                                                             |
      | content_type    | application/json                                                                                 |
      | request_body    | {"Driver": {"Points": 2}, "Violation": {"Type": "speed","Actual Speed": 120,"Speed Limit": 100}} |

  Scenario: Verify that the Kogito Quarkus Serverless Workflow Extension is building the service properly
    Given s2i build /tmp/kogito-examples from serverless-workflow-examples/serverless-workflow-order-processing/src/main/resources using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable                 | value                                                 |
      | RUNTIME_TYPE             | quarkus                                               |
      | NATIVE                   | false                                                 |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |                  
      | PROJECT_GROUP_ID         | com.mycompany                                         |
      | PROJECT_ARTIFACT_ID      | myproject                                             |
      | PROJECT_VERSION          | 2.0-SNAPSHOT                                          |
      | K_SINK                   | http://localhost:8181                                 |
      | QUARKUS_EXTRA_EXTENSIONS | org.kie.kogito:kogito-addons-quarkus-knative-eventing |
    Then file /home/kogito/bin/quarkus-run.jar should exist
    And s2i build log should contain Generating quarkus project structure for myproject...
    And s2i build log should contain Adding Kogito Quarkus Workflows extension to the generated project.
    And check that page is served
      | property              | value                         |
      | port                  | 8080                          |
      | path                  | /                             |
      | wait                  | 80                            |
      | expected_status_code  | 202                           |
      | request_method        | POST                          |
      | content_type          | application/cloudevents+json  |
      | request_body          | {"specversion": "1.0", "datacontenttype": "application/json", "source": "behave", "type": "orderEvent", "id": "12345", "data": {"id":"f0643c68-609c-48aa-a820-5df423fa4fe0","country":"Brazil","total":10000,"description":"iPhone 12"}}|

#### SpringBoot Scenarios

  Scenario: Verify that the Kogito Maven archetype is generating the project and compiling it correctly when runtime is springboot
    Given s2i build /tmp/kogito-examples from dmn-example using nightly-main and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable       | value          |
      | KOGITO_VERSION | 2.0.0-SNAPSHOT |     
      | RUNTIME_TYPE   | springboot     |
    Then file /home/kogito/bin/project-1.0-SNAPSHOT.jar should exist
    And s2i build log should contain Generating springboot project structure for project...
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
      | property        | value            |
      | port            | 8080             |
      | path            | /actuator/health |
      | wait            | 80               |
      | request_method  | GET              |
      | content_type    | application/json |
      | request_body    | {"status":"UP"}  |
