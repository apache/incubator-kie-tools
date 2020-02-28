@cr
@quarkus
Feature: CR: Deploy quarkus service

  Background:
    Given Namespace is created

  Scenario Outline: CR deploy drools-quarkus-example service
    Given Kogito Operator is deployed
    
    When Deploy quarkus example service "drools-quarkus-example" with native <native>

    Then Kogito application "drools-quarkus-example" has 1 pods running within <minutes> minutes
    And HTTP GET request on service "drools-quarkus-example" with path "persons/all" is successful within 2 minutes
    
    Examples: Non Native
      | native | minutes |
      | "disabled" | 10 |

    @native
    Examples: Native
      | native | minutes |
      | "enabled" | 20 |

#####

  @persistence
  Scenario Outline: CR deploy jbpm-quarkus-example service with persistence
    Given Kogito Operator is deployed with Infinispan operator
    And Deploy quarkus example service "jbpm-quarkus-example" with persistence enabled and native <native>
    And Kogito application "jbpm-quarkus-example" has 1 pods running within <minutes> minutes
    And HTTP GET request on service "jbpm-quarkus-example" with path "orders" is successful within 3 minutes
    And HTTP POST request on service "jbpm-quarkus-example" with path "orders" and body:
      """json
      {
        "approver" : "john", 
        "order" : {
          "orderNumber" : "12345", 
          "shipped" : false
        }
      }
      """
    And HTTP GET request on service "jbpm-quarkus-example" with path "orders" should return an array of size 1 within 1 minutes
    
    When Scale Kogito application "jbpm-quarkus-example" to 0 pods within 2 minutes
    And Scale Kogito application "jbpm-quarkus-example" to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jbpm-quarkus-example" with path "orders" should return an array of size 1 within 2 minutes
    
    Examples: Non Native
      | native | minutes |
      | "disabled" | 10 |

    # Disabled as long as https://issues.redhat.com/browse/KOGITO-842 is not solved
    @disabled
    @native
    Examples:
      | native | minutes |
      | "enabled" | 20 |

#####

  # Disabled as long as https://issues.redhat.com/browse/KOGITO-1163 and https://issues.redhat.com/browse/KOGITO-1166 is not solved
  @disabled
  Scenario Outline: CR deploy timer-quarkus-example service with Jobs service
    Given Kogito Operator is deployed
    And Deploy Kogito Jobs Service with 1 replicas
    And Deploy quarkus example service "timer-quarkus-example" with native <native>
    And Kogito application "timer-quarkus-example" has 1 pods running within <minutes> minutes

    When HTTP POST request on service "timer-quarkus-example" is successful within 2 minutes with path "timer" and body:
      """json
      { }
      """

    # Implement retrieving of job information from Jobs service once https://issues.redhat.com/browse/KOGITO-1163 is fixed
    Then Kogito application "timer-quarkus-example" log contains text "Before timer" within 1 minutes
    And Kogito application "timer-quarkus-example" log contains text "After timer" within 1 minutes

    Examples: Non Native
      | native | minutes |
      | "disabled" | 10 |

    # Disabled as long as https://issues.redhat.com/browse/KOGITO-1179 is not solved
    @disabled
    @native
    Examples:
      | native | minutes |
      | "enabled" | 20 |
