Feature: Service Deployment: Spring Boot with persistence

  Background:
    Given Kogito Operator is deployed with dependencies

  Scenario: Deploy jbpm-springboot-example service
    Given Deploy spring boot example service "jbpm-springboot-example" with persistence enabled
    And DeploymentConfig "jbpm-springboot-example" has 1 pod running within 10 minutes
    And HTTP GET request on service "jbpm-springboot-example" with path "orders" is successful within 3 minutes
    And HTTP POST request on service "jbpm-springboot-example" with path "orders" and "json" body '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}'
    And HTTP GET request on service "jbpm-springboot-example" with path "orders" should return an array of size 1 within 1 minutes
    
    When Scale Kogito application "jbpm-springboot-example" to 0 pods within 2 minutes
    And Scale Kogito application "jbpm-springboot-example" to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jbpm-springboot-example" with path "orders" should return an array of size 1 within 2 minutes