@infra
Feature: Kogito Infra

  Background:
    Given Namespace is created

  Scenario Outline: Install/Remove Kogito Infra
    Given Kogito Operator is deployed with <component> operator
    When "<installer>" install Kogito Infra "<component>"
    Then Kogito Infra "<component>" should be running within <installTimeoutInMinutes> minutes

    When "<installer>" remove Kogito Infra "<component>"
    Then Kogito Infra "<component>" should NOT be running within <removeTimeoutInMinutes> minutes

    @cr
    Examples: CR install/Remove
      | installer | component  | installTimeoutInMinutes | removeTimeoutInMinutes |
      | CR        | Infinispan | 10                      | 5                      |
      | CR        | Kafka      | 10                      | 5                      |
      | CR        | Keycloak   | 10                      | 5                      |
    
    @cli
    Examples: CLI install/Remove
      | installer | component  | installTimeoutInMinutes | removeTimeoutInMinutes |
      | CLI       | Infinispan | 10                      | 5                      |
      | CLI       | Kafka      | 10                      | 5                      |
      | CLI       | Keycloak   | 10                      | 5                      |