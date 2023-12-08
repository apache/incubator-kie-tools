@quay.io/kiegroup/kogito-trusty-infinispan
@quay.io/kiegroup/kogito-trusty-redis
@quay.io/kiegroup/kogito-trusty-postgresql
Feature: Kogito-trusty common feature.
  Scenario:   Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with args
      | arg       | value                                                                   |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                   |
      | env_json  | {"SCRIPT_DEBUG":"true"}  |
    Then container log should contain -Dtrusty.explainability.enabled=true -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080
