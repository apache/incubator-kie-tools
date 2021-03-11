@infinispan
@kafka
@keycloak
@security
Feature: Kogito integration with Keycloak

  Background:
    Given Namespace is created
    And Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Keycloak Operator is deployed

  @dataindex
  Scenario: Install Kogito Data Index with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-dataindex-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes

    When Install Kogito Data Index with 1 replicas with configuration:
      | config       | infra                                          | infinispan                                     |
      | config       | infra                                          | kafka                                          |
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-dataindex-service                       |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
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

#####

  @jobsservice
  Scenario: Install Kogito Jobs Service with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-jobs-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed

    When Install Kogito Jobs Service with 1 replicas with configuration:
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-jobs-service                            |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |
    And Kogito Jobs Service has 1 pods running within 10 minutes

    Then HTTP GET request on service "jobs-service" with path "jobs" is forbidden within 1 minutes
    
    When Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-jobs-service" into variable "my-user-token"
    And HTTP POST request on service "jobs-service" using access token "{my-user-token}" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """

    Then HTTP GET request on service "jobs-service" using access token "{my-user-token}" with path "jobs/1" is successful within 1 minutes
    
#####

  @explainability
  Scenario: Install Kogito Explainability with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-explainability-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes

    When Install Kogito Explainability with 1 replicas with configuration:
      | config       | infra                                          | kafka                                          |
      | runtime-env  | EXPLAINABILITY_COMMUNICATION                   | rest                                           |
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-explainability-service                  |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |
    And Kogito Explainability has 1 pods running within 10 minutes

    Then HTTP GET request on service "explainability" with path "/v1/explain" is forbidden within 1 minutes
    
    When Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-explainability-service" into variable "my-user-token"
    Then HTTP POST request on service "explainability" using access token "{my-user-token}" is successful within 2 minutes with path "/v1/explain" and body:
      """json
      {
        "executionId": "any",
        "serviceUrl": "http://localhost:8080",
        "modelIdentifier": {
          "resourceType": "dmn",
          "resourceId": "namespace:name"
        }
      }
      """

#####

  @trusty
  Scenario: Install Kogito Trusty with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-trusty-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes

    When Install Kogito Trusty with 1 replicas with configuration:
      | config       | infra                                          | infinispan                                     |
      | config       | infra                                          | kafka                                          |
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-trusty-service                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |
    And Kogito Trusty has 1 pods running within 10 minutes

    Then HTTP GET request on service "trusty" with path "/executions" is forbidden within 3 minutes

    When Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-trusty-service" into variable "my-user-token"
    Then HTTP GET request on service "trusty" using access token "{my-user-token}" with path "/executions" is successful within 3 minutes

#####

  @managementconsole
  Scenario: Install Kogito Management Console with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-mgmt-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Data Index has 1 pods running within 10 minutes

    When Install Kogito Management Console with 1 replicas with configuration:
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-mgmt-service                            |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |

    And Kogito Management Console has 1 pods running within 10 minutes

    Then HTTP GET request on service "management-console" with path "" is forbidden within 2 minutes

    When Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-mgmt-service" into variable "my-user-token"

    Then HTTP GET request on service "management-console" using access token "{my-user-token}" with path "" is successful within 2 minutes

#####

  @taskconsole
  Scenario: Install Kogito Task Console with Keycloak security
    Given Keycloak instance with realm "kogito-realm" and client "kogito-task-service" is deployed
    And Keycloak user "my-user" with password "my-password" is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Data Index has 1 pods running within 10 minutes

    When Install Kogito Task Console with 1 replicas with configuration:
      | runtime-env  | quarkus.oidc.tenant-enabled                    | true                                           |
      | runtime-env  | quarkus.oidc.tls.verification                  | none                                           |
      | runtime-env  | quarkus.oidc.auth-server-url                   | https://keycloak:8443/auth/realms/kogito-realm |
      | runtime-env  | quarkus.oidc.client-id                         | kogito-task-service                            |
      | runtime-env  | quarkus.http.auth.permission.unsecure.paths    | /health/*,/q/health/*                          |
      | runtime-env  | quarkus.http.auth.permission.unsecure.policy   | permit                                         |
      | runtime-env  | quarkus.http.auth.permission.secure.paths      | /*                                             |
      | runtime-env  | quarkus.http.auth.permission.secure.policy     | authenticated                                  |

    And Kogito Task Console has 1 pods running within 10 minutes

    Then HTTP GET request on service "task-console" with path "" is forbidden within 2 minutes

    When Stores access token for user "my-user" and password "my-password" on realm "kogito-realm" and client "kogito-task-service" into variable "my-user-token"

    Then HTTP GET request on service "task-console" using access token "{my-user-token}" with path "" is successful within 2 minutes