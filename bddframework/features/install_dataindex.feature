@dataindex
@infinispan
@kafka
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

# External Kafka testing is covered in deploy_quarkus_service and deploy_springboot_service as it checks integration between Data index and KogitoApp
