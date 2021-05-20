@quay.io/kiegroup/kogito-jobs-service-infinispan @quay.io/kiegroup/kogito-jobs-service-mongodb @quay.io/kiegroup/kogito-jobs-service-ephemeral
Feature: Kogito-jobs-service common feature.

  Scenario: verify if the events is correctly enabled
    When container is started with env
      | variable                | value                                     |
      | SCRIPT_DEBUG            | true                                      |
      | ENABLE_EVENTS           | true                                      |
      | KOGITO_JOBS_PROPS       | -Dkafka.bootstrap.servers=localhost:11111 |
    Then container log should contain + exec java -XshowSettings:properties -Dkafka.bootstrap.servers=localhost:11111 -Dquarkus.profile=events-support -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar
