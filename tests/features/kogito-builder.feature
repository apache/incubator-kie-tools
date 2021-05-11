@quay.io/kiegroup/kogito-builder
Feature: kogito-builder image tests

  Scenario: verify if all labels are correctly set on kogito-builder image
    Given image is built
    Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on Quarkus or Spring Boot
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus or Spring Boot
    And the image should contain label io.openshift.tags with value builder,kogito,quarkus,springboot

  Scenario: Verify if the s2i build is finished as expected performing a non native build with persistence enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-quarkus-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | NATIVE            | false         |
      | RUNTIME_TYPE      | quarkus       |
      | MAVEN_ARGS_APPEND | -Ppersistence |
    Then file /home/kogito/bin/quarkus-run.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'

    Scenario: Verify if the s2i build is finished as expected with persistence enabled
    Given s2i build https://github.com/kiegroup/kogito-examples.git from process-springboot-example using master and runtime-image quay.io/kiegroup/kogito-runtime-jvm:latest
      | variable          | value         |
      | MAVEN_ARGS_APPEND | -Ppersistence |
      | RUNTIME_TYPE      | springboot    |
    Then file /home/kogito/bin/process-springboot-example.jar should exist
    And s2i build log should contain '/home/kogito/bin/demo.orders.proto' -> '/home/kogito/data/protobufs/demo.orders.proto'
    And s2i build log should contain '/home/kogito/bin/persons.proto' -> '/home/kogito/data/protobufs/persons.proto'