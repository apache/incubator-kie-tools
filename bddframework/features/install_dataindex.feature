@dataindex
@kafka
Feature: Kogito Data Index

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Kafka operator

  @smoke
  Scenario: Install Kogito Data Index with Infinispan
    Given Infinispan operator is deployed
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes

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

#####

  @externalcomponent
  @infinispan
  Scenario: Install Kogito Data Index with persistence using external Infinispan
    Given Infinispan operator is deployed
    And Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
    And Install Infinispan Kogito Infra "external-infinispan" within 5 minutes with configuration:
      | resource | name | external-infinispan |
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes

    When Install Kogito Data Index with 1 replicas with configuration:
      | config | database-type | Infinispan          |
      | config | infra         | external-infinispan |
      | config | infra         | kafka               |

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
# For infinispan/mongodb external component, we cannot merge in one scenario outline
# due to the added `database` field in external config

  @externalcomponent
  @mongodb
  Scenario: Install Kogito Data Index with persistence using external MongoDB
    Given MongoDB operator is deployed
    And MongoDB instance "external-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_dataindex     |
    And Install MongoDB Kogito Infra "external-mongodb" within 5 minutes with configuration:
      | resource | name     | external-mongodb     |
      | config   | username | developer            |
      | config   | database | kogito_dataindex     |
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes

    When Install Kogito Data Index with 1 replicas with configuration:
      | config | database-type | MongoDB                  |
      | config | infra         | external-mongodb         |
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

# External Kafka testing is covered in deploy_quarkus_service and deploy_springboot_service as it checks integration between Data index and KogitoRuntime
