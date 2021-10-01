@quay.io/kiegroup/kogito-trusty-infinispan @quay.io/kiegroup/kogito-trusty-redis
Feature: Kogito-trusty common feature.
  Scenario:   Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Dtrusty.explainability.enabled=true -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080

  Scenario: Verify if a custom certificate is correctly handled
    When container is started with command /home/kogito/kogito-app-launch.sh
      | variable            | value              |
      | CUSTOM_TRUSTSTORE   | my-truststore.jks  |
      | RUNTIME_TYPE        | quarkus            |
    Then container log should contain INFO ---> Configuring custom Java Truststore
    Then container log should contain ERROR ---> A custom truststore was specified