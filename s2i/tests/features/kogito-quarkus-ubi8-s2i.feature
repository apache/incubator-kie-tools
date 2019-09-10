@quay.io/kiegroup/kogito-quarkus-ubi8-s2i

Feature: kogito-quarkus-ubi8-s2i image tests

  Scenario: Verify if the s2i build is finished as expected
    Given s2i build https://github.com/kiegroup/kogito-examples.git from jbpm-quarkus-example using master and runtime-image quay.io/kiegroup/kogito-quarkus-ubi8:latest
    Then file /home/kogito/bin/jbpm-quarkus-example-8.0.0-SNAPSHOT-runner should exist

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Platform for building Kogito based on Quarkus
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus
    And the image should contain label io.openshift.tags with value builder,kogito,quarkus

  Scenario: verify if the maven and graal vm settings are correct
    When container is started with command bash
    Then run sh -c 'echo $MAVEN_HOME' in container and immediately check its output for /usr/share/maven
    And run sh -c 'echo $MAVEN_VERSION' in container and immediately check its output for 3.6.0
    And run sh -c 'echo $JAVA_HOME' in container and immediately check its output for /usr/share/graalvm
    And run sh -c 'echo $GRAALVM_HOME' in container and immediately check its output for /usr/share/graalvm
    And run sh -c 'echo $GRAALVM_VERSION' in container and immediately check its output for 19.1.1


