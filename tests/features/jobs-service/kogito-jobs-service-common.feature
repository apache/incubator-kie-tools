@quay.io/kiegroup/kogito-jobs-service-ephemeral
@quay.io/kiegroup/kogito-jobs-service-postgresql
@quay.io/kiegroup/kogito-jobs-service-allinone
Feature: Kogito-jobs-service common feature.

  Scenario: verify if the events is correctly enabled
    When container is started with env
      | variable                | value                                     |
      | SCRIPT_DEBUG            | true                                      |
      | ENABLE_EVENTS           | true                                      |
      | KOGITO_JOBS_PROPS       | -Dkafka.bootstrap.servers=localhost:11111 |
    Then container log should contain -Dkafka.bootstrap.servers=localhost:11111 -Dquarkus.profile=events-support -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar
