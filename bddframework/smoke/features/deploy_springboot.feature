Feature: Service Deployment: Spring Boot

  Background:
    Given Kogito Operator is deployed

  Scenario: Deploy jbpm-springboot-example service
    When Deploy spring boot example service "jbpm-springboot-example"

    Then Build "jbpm-springboot-example-builder" is complete after 10 minutes
    And Build "jbpm-springboot-example" is complete after 5 minutes
    And DeploymentConfig "jbpm-springboot-example" has 1 pod running within 5 minutes
    And HTTP GET request on service "jbpm-springboot-example" with path "orders" is successful within 2 minutes