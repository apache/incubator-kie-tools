@rhpam-7/rhpam-kogito-builder-rhel8
Feature: rhpam-kogito-builder-rhel8 feature.

  Scenario: verify if all labels are correctly set on rhpam-kogito-builder-rhel8 image
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value RHPAM Platform for building Kogito based on Quarkus or Spring Boot
    And the image should contain label io.k8s.display-name with value Red Hat build of Kogito builder based on Quarkus or SpringBoot
    And the image should contain label io.openshift.tags with value rhpam-kogito,builder,kogito,quarkus,springboot
    And the image should contain label io.openshift.s2i.assemble-input-files with value /home/kogito/bin
    And the image should contain label com.redhat.component with value rhpam-7-kogito-runtime-jvm-rhel8-container
