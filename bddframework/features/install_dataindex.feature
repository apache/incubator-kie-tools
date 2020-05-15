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

  @externalcomponent
  @infinispan
  Scenario: Install Kogito Data Index with persistence using external Infinispan
    Given Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass |

    When Install Kogito Data Index with 1 replicas with configuration:
      | infinispan | username | developer                 |
      | infinispan | password | mypass                    |
      | infinispan | uri      | external-infinispan:11222 |

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

  # Used just to confirm that integration between Data index and external Kafka works.
  # Will be removed once external Kafka integration is implemented for Kogito service.
  # Kogito service scenario will cover both Kogito service and Data index integration with external Kafka.
  @externalcomponent
  @kafka
  Scenario Outline: Install Kogito Data Index using external Kafka
    Given Kafka instance "external-kafka" is deployed

    When Install Kogito Data Index with 1 replicas with configuration:
      | kafka | <kafka-key> | <kafka-value> |

    Then Kogito Data Index has 1 pods running within 10 minutes
    And GraphQL request on service "data-index" is successful within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """

    Examples:
      | kafka-key   | kafka-value                         |
      | externalURI | external-kafka-kafka-bootstrap:9092 |
      | instance    | external-kafka                      |
