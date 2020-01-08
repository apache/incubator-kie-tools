Feature: Service Deployment: Quarkus with persistence

  Background:
    Given Kogito Operator is deployed with dependencies

  Scenario Outline: Deploy jbpm-quarkus-example service with persistence
    Given Deploy quarkus example service "jbpm-quarkus-example" with persistence enabled and native <native>
    And DeploymentConfig "jbpm-quarkus-example" has 1 pod running within <minutes> minutes
    And HTTP GET request on service "jbpm-quarkus-example" with path "orders" is successful within 3 minutes
    And HTTP POST request on service "jbpm-quarkus-example" with path "orders" and "json" body '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}'
    And HTTP GET request on service "jbpm-quarkus-example" with path "orders" should return an array of size 1 within 1 minutes
    
    When Scale Kogito application "jbpm-quarkus-example" to 0 pods within 2 minutes
    And Scale Kogito application "jbpm-quarkus-example" to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jbpm-quarkus-example" with path "orders" should return an array of size 1 within 2 minutes
    
    Examples:
      | native | minutes |
      # Commented as long as https://issues.redhat.com/browse/KOGITO-842 is not solved
      #| "enabled" | 20 |
      | "disabled" | 10 |