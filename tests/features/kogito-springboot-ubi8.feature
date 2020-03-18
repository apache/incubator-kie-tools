@quay.io/kiegroup/kogito-springboot-ubi8

Feature: springboot-quarkus-ubi8 feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito based on SpringBoot native image
    And the image should contain label io.k8s.display-name with value Kogito based on SpringBoot native image
    And the image should contain label io.openshift.tags with value builder,runtime,kogito,springboot
    And the image should contain label io.openshift.s2i.assemble-input-files with value /home/kogito/bin

  Scenario: verify if the java installation is correct
    When container is started with command bash
    Then run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-1.8.0
    And run sh -c 'echo $JAVA_VENDOR' in container and immediately check its output for openjdk
    And run sh -c 'echo $JAVA_VERSION' in container and immediately check its output for 1.8.0


  Scenario: Verify if the binary build is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples/jbpm-springboot-example from target
      | variable            | value        |
      | JAVA_OPTIONS        | -Ddebug=true |
    Then check that page is served
      | property             | value     |
      | port                 | 8080      |
      | path                 | /orders/1 |
      | wait                 | 80        |
      | expected_status_code | 204       |
    And file /home/kogito/bin/jbpm-springboot-example-8.0.0-SNAPSHOT.jar should exist
    And container log should contain DEBUG 1 --- [           main] o.s.boot.SpringApplication
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true

  Scenario: Verify if the (forcing) binary build is finished as expected and if it is listening on the expected port
    Given s2i build /tmp/kogito-examples/jbpm-springboot-example from target
      | variable            | value        |
      | JAVA_OPTIONS        | -Ddebug=true |
      | BINARY_BUILD        | true         |
    Then check that page is served
      | property             | value     |
      | port                 | 8080      |
      | path                 | /orders/1 |
      | wait                 | 80        |
      | expected_status_code | 204       |
    And file /home/kogito/bin/jbpm-springboot-example-8.0.0-SNAPSHOT.jar should exist
    And container log should contain DEBUG 1 --- [           main] o.s.boot.SpringApplication
    And run sh -c 'echo $JAVA_OPTIONS' in container and immediately check its output for -Ddebug=true
