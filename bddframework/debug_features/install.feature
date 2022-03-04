Feature: Install feature

  @install-operator
  Scenario: Install Operator
    Given Kogito Operator is deployed

  @install-supporting
  Scenario: Install Supporting service
    Given Namespace is created
    And Kogito Operator is deployed
    And Kafka Operator is deployed
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Infinispan Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes

    When Install Kogito Data Index with 1 replicas with configuration:
      | config | database-type | Infinispan               |
      | config | infra         | infinispan               |
      | config | infra         | kafka                    |

    Then Kogito Data Index has 1 pods running within 10 minutes
    And GraphQL request on service "data-index" is successful within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """

    When Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | Infinispan |
      | config | infra         | infinispan |
    Then Kogito Jobs Service has 1 pods running within 10 minutes
