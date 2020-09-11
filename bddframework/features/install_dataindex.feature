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

# External Kafka testing is covered in deploy_quarkus_service and deploy_springboot_service as it checks integration between Data index and KogitoRuntime

#####

  @externalcomponent
  @keycloak
  @security
  Scenario: Install Kogito Data Index with Keycloak security
    Given Kogito Operator is deployed with Keycloak operator
    And Keycloak instance with realm "kogito-realm" and client "kogito-dataindex-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed

    When Install Kogito Data Index with 1 replicas with configuration:
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-dataindex-service                       |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*                                      |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |
    And Kogito Data Index has 1 pods running within 10 minutes
    And Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-dataindex-service" into variable "my-user-token"

    Then GraphQL request on service "data-index" is successful using access token "{my-user-token}" within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """