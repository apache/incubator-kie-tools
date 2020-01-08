Feature: Service Deployment: Quarkus

  Background:
    Given Kogito Operator is deployed

  Scenario Outline: Deploy drools-quarkus-example service
    When Deploy quarkus example service "drools-quarkus-example" with native <native>

    Then Build "drools-quarkus-example-builder" is complete after <minutes> minutes
    And Build "drools-quarkus-example" is complete after 5 minutes
    And DeploymentConfig "drools-quarkus-example" has 1 pod running within 5 minutes
    And HTTP GET request on service "drools-quarkus-example" with path "persons/all" is successful within 2 minutes
    
    Examples:
      | native | minutes |
      | "enabled" | 20 |
      | "disabled" | 10 |