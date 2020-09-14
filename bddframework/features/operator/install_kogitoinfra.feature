@infra
Feature: Kogito Infra

  Background:
    Given Namespace is created

  Scenario Outline: Install/Remove Kogito Infra <component>
    Given Kogito Operator is deployed with <component> operator
    When Install Kogito Infra "<component>"
    Then Kogito Infra "<component>" should be running within <installTimeoutInMinutes> minutes

    When Remove Kogito Infra "<component>"
    Then Kogito Infra "<component>" should NOT be running within <removeTimeoutInMinutes> minutes

    Examples:
      | component  | installTimeoutInMinutes | removeTimeoutInMinutes |
      | Infinispan | 10                      | 5                      |
      | Kafka      | 10                      | 5                      |
      | Keycloak   | 10                      | 5                      |