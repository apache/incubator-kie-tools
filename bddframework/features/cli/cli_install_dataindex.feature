@cli
@dataindex
Feature: CLI: Install Kogito Data Index

  @smoke
  Scenario: CLI install Kogito Data Index
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators

    When CLI install Kogito Data Index with 1 replicas

    Then Kogito Data Index has 1 pods running within 10 minutes
    And GraphQL request on service "kogito-data-index" is successful within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """
