@quay.io/kiegroup/kogito-trusty-postgresql
Feature: Kogito-trusty postgresql feature.

  Scenario: verify if all labels are correctly set on kogito-trusty-postgresql image
    Given image is built
     Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
      And the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label io.k8s.description with value Runtime image for Kogito Trusty Service for PostgreSQL persistence provider
      And the image should contain label io.k8s.display-name with value Kogito Trusty Service - PostgreSQL
      And the image should contain label io.openshift.tags with value kogito,trusty,trusty-postgresql

