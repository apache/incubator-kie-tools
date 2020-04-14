@dataindex
Feature: Kogito Data Index

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators

  @smoke
  Scenario: Install Kogito Data Index
    When Install Kogito Data Index with 1 replicas
    Then Kogito Data Index has 1 pods running within 10 minutes
    And GraphQL request on service "data-index" is successful within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """

#####

  @events
  @persistence
  Scenario Outline: Process instance events are stored in Data Index
    Given Install Kogito Data Index with 1 replicas
    And Deploy quarkus example service "process-quarkus-example" with configuration:
      | config | native      | <native> |
      | config | persistence | enabled  |
      | config | events      | enabled  |
    And Kogito application "process-quarkus-example" has 1 pods running within <minutes> minutes
    And HTTP GET request on service "process-quarkus-example" with path "orders" is successful within 3 minutes

    When HTTP POST request on service "process-quarkus-example" with path "orders" and body:
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

    Examples: Non native
      | native   | minutes |
      | disabled | 10      |

    @native
    Examples: Native
      | native  | minutes |
      | enabled | 20      |