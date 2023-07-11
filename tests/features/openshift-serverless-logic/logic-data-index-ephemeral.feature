@openshift-serverless-1-tech-preview/logic-data-index-ephemeral-rhel8
Feature: logic-data-index-ephemeral-rhel8 feature

  Scenario: verify if all labels are correctly set on logic-data-index-ephemeral-rhel8 image
    Given image is built
      Then the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label maintainer with value serverless-logic <bsig-cloud@redhat.com>
      And the image should contain label io.k8s.description with value Red Hat build of Runtime image for Kogito Data Index Service for ephemeral PostgreSQL persistence provider
      And the image should contain label io.k8s.display-name with value Red Hat build of Kogito Data Index Service - ephemeral PostgreSQL
      And the image should contain label io.openshift.tags with value logic-data-index,kogito,data-index,data-index-ephemeral
      And the image should contain label com.redhat.component with value openshift-serverless-1-logic-data-index-ephemeral-rhel8-container

  Scenario: verify if of logic-data-index-ephemeral-rhel8 container is correctly started
    When container is started with env
      | variable       | value  |
      | SCRIPT_DEBUG   | true   |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar
    And container log should contain Embedded Postgres started at port
    And container log should not contain Application failed to start

  Scenario: check if the default quarkus profile is correctly set on data index
    When container is started with env
      | variable                           | value               |
      | SCRIPT_DEBUG                       | true                |
    Then container log should contain -Dquarkus.profile=http-events-support

