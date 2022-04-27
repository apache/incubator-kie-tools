@quay.io/kiegroup/kogito-jobs-service-postgresql
Feature: Kogito-jobs-service-postgresql feature.

  Scenario: verify if all labels are correctly set kogito-jobs-service image image
    Given image is built
    Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Jobs Service based on Postgresql
    And the image should contain label io.k8s.display-name with value Kogito Jobs Service based on Postgresql
    And the image should contain label io.openshift.tags with value kogito,jobs-service-postgresql
