@quay.io/kiegroup/kogito-data-index-postgresql
Feature: Kogito-data-index postgresql feature.

  Scenario: verify if all labels are correctly set on kogito-data-index-postgresql image
    Given image is built
     Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
      And the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service for PostgreSQL persistence provider
      And the image should contain label io.k8s.display-name with value Kogito Data Index Service - PostgreSQL
      And the image should contain label io.openshift.tags with value kogito,data-index,data-index-postgresql

  Scenario: check if the default quarkus profile is correctly set on data index
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Dquarkus.profile=kafka-events-support