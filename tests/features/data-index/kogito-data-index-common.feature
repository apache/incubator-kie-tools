@quay.io/kiegroup/kogito-data-index-infinispan @quay.io/kiegroup/kogito-data-index-mongodb
Feature: Kogito-data-index common feature.

  Scenario:   Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=8080 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0

  Scenario: Verify if the debug is correctly enabled and test custom http port
    When container is started with env
      | variable      | value |
      | SCRIPT_DEBUG  | true  |
      | HTTP_PORT     | 9090  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=9090 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -jar

