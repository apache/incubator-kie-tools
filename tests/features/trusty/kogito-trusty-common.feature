@quay.io/kiegroup/kogito-trusty-infinispan @quay.io/kiegroup/kogito-trusty-redis
Feature: Kogito-trusty common feature.
  Scenario:   Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain + exec java -XshowSettings:properties -Dtrusty.explainability.enabled=true -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080