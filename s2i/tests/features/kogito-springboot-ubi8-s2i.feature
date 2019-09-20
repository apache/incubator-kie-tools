@quay.io/kiegroup/kogito-springboot-ubi8-s2i

Feature: kogito-springboot-ubi8-s2i image tests

  Scenario: Verify if the s2i build is finished as expected
    Given s2i build https://github.com/kiegroup/kogito-examples.git from jbpm-springboot-example using master and runtime-image quay.io/kiegroup/kogito-springboot-ubi8:latest
    Then check that page is served
      | property             | value     |
      | port                 | 8080      |
      | path                 | /orders/1 |
      | wait                 | 80        |
      | expected_status_code | 204       |
    And file /home/kogito/bin/jbpm-springboot-example-8.0.0-SNAPSHOT.jar should exist

  Scenario: Scenario: Perform a incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from jbpm-springboot-example with env and incremental using master
    Then check that page is served
      | property             | value     |
      | port                 | 8080      |
      | path                 | /orders/1 |
      | wait                 | 80        |
      | expected_status_code | 204       |
    And file /home/kogito/bin/jbpm-springboot-example-8.0.0-SNAPSHOT.jar should exist

  # Since the same image is used we can do a subsequent incremental build and verify if it is working as expected.
  Scenario: Perform a second incremental s2i build
    Given s2i build https://github.com/kiegroup/kogito-examples.git from jbpm-springboot-example with env and incremental using master
    Then s2i build log should contain Expanding artifacts from incremental build...


  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on SpringBoot
    And the image should contain label io.k8s.display-name with value Kogito based on SpringBoot
    And the image should contain label io.openshift.tags with value builder,kogito,springboot

  Scenario: verify if the maven and java installation is correct
    When container is started with command bash
    Then run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/lib/jvm/java-1.8.0
    And run sh -c 'echo $JAVA_VENDOR' in container and immediately check its output for openjdk
    And run sh -c 'echo $JAVA_VERSION' in container and immediately check its output for 1.8.0
    And run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven

