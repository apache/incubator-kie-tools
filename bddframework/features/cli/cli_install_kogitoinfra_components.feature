@cli
Feature: CLI: Install Kogito Infra Component

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan, Kafka and Keycloak operators

  Scenario Outline: CLI install Kogito Infra Component
    When CLI install Kogito Infra <component>

    Then Kogito Infra <component> should be running within <timeoutInMinutes> minutes

    Examples:
      | component | timeoutInMinutes |
      | Infinispan | 5 |
      | Kafka | 10 |
      | Keycloak | 10 |

  Scenario Outline: CLI remove Kogito Infra Component
    Given Install Kogito Infra <component>
    And Kogito Infra <component> should be running within <installTimeoutInMinutes> minutes

    When CLI remove Kogito Infra <component>

    Then Kogito Infra <component> should NOT be running within <removeTimeoutInMinutes> minutes

    Examples:
      | component | installTimeoutInMinutes | removeTimeoutInMinutes |
      | Infinispan | 5 | 2 |
      | Kafka | 10 | 2 |
      | Keycloak | 10 | 5 |