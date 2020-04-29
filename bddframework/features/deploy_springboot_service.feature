@springboot
Feature: Deploy spring boot service

  Background:
    Given Namespace is created

  @smoke
  Scenario: Deploy process-springboot-example service without persistence
    Given Kogito Operator is deployed

    When Deploy springboot example service "process-springboot-example" with configuration:
      | config | persistence | disabled |
    Then Kogito application "process-springboot-example" has 1 pods running within 10 minutes
    And Service "process-springboot-example" with process name "orders" is available within 2 minutes

#####

  @persistence
  Scenario: Deploy process-springboot-example service with persistence
    Given Kogito Operator is deployed with Infinispan operator
    And Deploy springboot example service "process-springboot-example" with configuration:
      | config | persistence | enabled |
    And Kogito application "process-springboot-example" has 1 pods running within 10 minutes
    And Start "orders" process on service "process-springboot-example" within 3 minutes with body:
      """json
      {
        "approver" : "john", 
        "order" : {
          "orderNumber" : "12345", 
          "shipped" : false
        }
      }
      """
    And Service "process-springboot-example" contains 1 instance of process with name "orders"
    
    When Scale Kogito application "process-springboot-example" to 0 pods within 2 minutes
    And Scale Kogito application "process-springboot-example" to 1 pods within 2 minutes
    
    Then Service "process-springboot-example" contains 1 instance of process with name "orders" within 2 minutes

#####

  # Disabled as long as https://issues.redhat.com/browse/KOGITO-1163 and https://issues.redhat.com/browse/KOGITO-1166 is not solved
  @disabled
  @jobsservice
  Scenario: Deploy process-timer-springboot service with Jobs service
    Given Kogito Operator is deployed
    And Install Kogito Jobs Service with 1 replicas
    And Deploy springboot example service "process-timer-springboot" with configuration:
      | config | persistence | disabled |
    And Kogito application "process-timer-springboot" has 1 pods running within 10 minutes

    When Start "timer" process on service "process-timer-springboot" within 2 minutes with body:
      """json
      { }
      """

    # Implement retrieving of job information from Jobs service once https://issues.redhat.com/browse/KOGITO-1163 is fixed
    Then Kogito application "process-timer-springboot" log contains text "Before timer" within 1 minutes
    And Kogito application "process-timer-springboot" log contains text "After timer" within 1 minutes

#####

  @events
  @persistence
  Scenario: Data Index retrieves Spring Boot process' events
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Data Index with 1 replicas
    And Deploy springboot example service "process-springboot-example" with configuration:
      | config | persistence | enabled  |
      | config | events      | enabled  |
    And Kogito application "process-springboot-example" has 1 pods running within 10 minutes

    When Start "orders" process on service "process-springboot-example" within 3 minutes with body:
      """json
      {
        "approver" : "john", 
        "order" : {
          "orderNumber" : "12345", 
          "shipped" : false
        }
      }
      """

    Then GraphQL request on Data Index service returns ProcessInstances processName "orders" within 2 minutes

  @usertasks
  Scenario: Deploy process-springboot-example service to complete user tasks
    Given Kogito Operator is deployed
    And Deploy springboot example service "process-springboot-example" with configuration:
      | config | persistence | disabled |
    And Kogito application "process-springboot-example" has 1 pods running within 10 minutes

    When Start "orders" process on service "process-springboot-example" within 3 minutes with body:
      """json
      {
        "approver" : "john", 
        "order" : {
          "orderNumber" : "12345", 
          "shipped" : false
        }
      }
      """
    Then Service "process-springboot-example" contains 1 instance of process with name "orders"

    When Complete "Verify order" task on service "process-springboot-example" and process with name "orderItems" by user "john" with body:
	  """json
	  {}
    """

    Then Service "process-springboot-example" contains 0 instance of process with name "orders"
    And Service "process-springboot-example" contains 0 instance of process with name "orderItems"
