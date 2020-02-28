@cli
@quarkus
Feature: CLI: Deploy quarkus service

  Background:
    Given Namespace is created

  Scenario Outline: CLI deploy drools-quarkus-example service
    Given Kogito Operator is deployed

    When CLI deploy quarkus example service "drools-quarkus-example" with native <native>

    Then Kogito application "drools-quarkus-example" has 1 pods running within <minutes> minutes
    And HTTP GET request on service "drools-quarkus-example" with path "persons/all" is successful within 2 minutes
    
    @smoke
    Examples: Non Native
      | native | minutes |
      | "disabled" | 10 |

    @native
    Examples: Native
      | native | minutes |
      | "enabled" | 20 |

#####

  @persistence
  Scenario Outline: CLI deploy jbpm-quarkus-example service with persistence
    Given Kogito Operator is deployed with Infinispan operator
    And CLI deploy quarkus example service "jbpm-quarkus-example" with persistence enabled and native <native>
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
    Examples: Native
      | native | minutes |
      | "enabled" | 20 |