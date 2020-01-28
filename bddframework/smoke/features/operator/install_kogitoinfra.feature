Feature: Install Keycloak

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with dependencies

  Scenario Outline: Install Component
    When Install Kogito Infra <component>

    Then Kogito Infra <component> should be running within <timeoutInMinutes> minutes

    Examples:
      | component | timeoutInMinutes |
      | Infinispan | 5 |
      | Kafka | 5 |
      | Keycloak | 5 |

  Scenario Outline: Remove Component
    Given Install Kogito Infra <component>
    And Kogito Infra <component> should be running within <installTimeoutInMinutes> minutes

    When Remove Kogito Infra <component>

    Then Kogito Infra <component> should NOT be running within <removeTimeoutInMinutes> minutes

    Examples:
      | component | installTimeoutInMinutes | removeTimeoutInMinutes |
      | Infinispan | 5 | 2 |
      | Kafka | 10 | 2 |
      | Keycloak | 5 | 2 |