@quay.io/kiegroup/kogito-data-index-infinispan
@quay.io/kiegroup/kogito-data-index-ephemeral
@quay.io/kiegroup/kogito-data-index-mongodb
@quay.io/kiegroup/kogito-data-index-postgresql
@openshift-serverless-1-tech-preview/logic-data-index-ephemeral-rhel8
Feature: Kogito-data-index common feature.

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080

  Scenario: check if the default quarkus profile is correctly set on data index
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Dquarkus.profile=kafka-events-support

  Scenario: check if a provided data index quarkus profile is correctly set on data index
    When container is started with env
      | variable                           | value               |
      | SCRIPT_DEBUG                       | true                |
      | KOGITO_DATA_INDEX_QUARKUS_PROFILE  | http-events-support |
    Then container log should contain -Dquarkus.profile=http-events-support