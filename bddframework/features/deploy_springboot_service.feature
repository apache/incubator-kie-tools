@springboot
Feature: Deploy spring boot service

  Background:
    Given Namespace is created

  @smoke
  Scenario: Deploy process-springboot-example service without persistence
    Given Kogito Operator is deployed
    
    When Deploy spring boot example service "process-springboot-example"

    Then Kogito application "process-springboot-example" has 1 pods running within 10 minutes
    And HTTP GET request on service "process-springboot-example" with path "orders" is successful within 2 minutes

#####

  @persistence
  Scenario: Deploy process-springboot-example service with persistence
    Given Kogito Operator is deployed with Infinispan operator
    And Deploy spring boot example service "process-springboot-example" with persistence
    And Kogito application "process-springboot-example" has 1 pods running within 10 minutes
    And HTTP GET request on service "process-springboot-example" with path "orders" is successful within 3 minutes
    And HTTP POST request on service "process-springboot-example" with path "orders" and body:
      """json
      {
        "approver" : "john", 
        "order" : {
          "orderNumber" : "12345", 
          "shipped" : false
        }
      }
      """
    And HTTP GET request on service "process-springboot-example" with path "orders" should return an array of size 1 within 1 minutes
    
    When Scale Kogito application "process-springboot-example" to 0 pods within 2 minutes
    And Scale Kogito application "process-springboot-example" to 1 pods within 2 minutes
    
    Then HTTP GET request on service "process-springboot-example" with path "orders" should return an array of size 1 within 2 minutes

#####

  # Disabled as long as https://issues.redhat.com/browse/KOGITO-1163 and https://issues.redhat.com/browse/KOGITO-1166 is not solved
  @disabled
  @jobsservice
  Scenario: Deploy process-timer-springboot service with Jobs service
    Given Kogito Operator is deployed
    And Install Kogito Jobs Service with 1 replicas
    And Deploy spring boot example service "process-timer-springboot"
    And Kogito application "process-timer-springboot" has 1 pods running within 10 minutes

    When HTTP POST request on service "process-timer-springboot" is successful within 2 minutes with path "timer" and body:
      """json
      { }
      """

    # Implement retrieving of job information from Jobs service once https://issues.redhat.com/browse/KOGITO-1163 is fixed
    Then Kogito application "process-timer-springboot" log contains text "Before timer" within 1 minutes
    And Kogito application "process-timer-springboot" log contains text "After timer" within 1 minutes
