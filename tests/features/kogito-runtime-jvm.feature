@quay.io/kiegroup/kogito-runtime-jvm
Feature: kogito-runtime-jvm feature.

  Scenario: verify if all labels are correctly set on kogito-runtime-jvm image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito based on Quarkus or Spring Boot JVM image
    And the image should contain label io.k8s.display-name with value Kogito based on Quarkus or Spring Boot JVM image
    And the image should contain label io.openshift.tags with value runtime,kogito,quarkus,springboot,jvm
    And the image should contain label io.openshift.s2i.assemble-input-files with value /home/kogito/bin
