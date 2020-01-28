Feature: CLI: Install Kogito Data Index

  Scenario: CLI install Kogito Data Index
    Given Namespace is created
    And Kogito Operator is deployed with dependencies

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
