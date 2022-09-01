@jobsservice
Feature: Install Kogito Jobs Service

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @smoke
  Scenario: Install Kogito Jobs Service without persistence
    Given Install Kogito Jobs Service with 1 replicas
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @persistence
  @infinispan
  Scenario: Install Kogito Jobs Service with persistence using Infinispan
    Given Infinispan Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes

    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | Infinispan |
      | config | infra         | infinispan |
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes
    
    And Scale Kogito Jobs Service to 0 pods within 2 minutes
    And Scale Kogito Jobs Service to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @persistence
  @postgresql
  Scenario: Install Kogito Jobs Service with persistence using PostgreSQL
    Given PostgreSQL instance "postgresql" is deployed within 3 minutes with configuration:
      | username | myuser |
      | password | mypass |
      | database | mydb   |

    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config      | database-type                   | PostgreSQL                             |
      | runtime-env | QUARKUS_DATASOURCE_JDBC_URL     | jdbc:postgresql://postgresql:5432/mydb |
      | runtime-env | QUARKUS_DATASOURCE_REACTIVE_URL | postgresql://postgresql:5432/mydb      |
      | runtime-env | QUARKUS_DATASOURCE_USERNAME     | myuser                                 |
      | runtime-env | QUARKUS_DATASOURCE_PASSWORD     | mypass                                 |
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes
    
    And Scale Kogito Jobs Service to 0 pods within 2 minutes
    And Scale Kogito Jobs Service to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @persistence
  @mongodb
  Scenario: Install Kogito Jobs Service with persistence using MongoDB
    Given MongoDB Operator is deployed
    And MongoDB instance "kogito-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_jobsservice     |
    And Install MongoDB Kogito Infra "mongodb" targeting service "kogito-mongodb" within 5 minutes with configuration:
      | config   | username | developer            |
      | config   | database | kogito_jobsservice     |
    
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | MongoDB  |
      | config | infra         | mongodb  |
    And Kogito Jobs Service has 1 pods running within 10 minutes

    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes
    
    And Scale Kogito Jobs Service to 0 pods within 2 minutes
    And Scale Kogito Jobs Service to 1 pods within 2 minutes
    
    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @events
  @kafka
  @infinispan
  Scenario: Jobs service events are stored in Data Index
    Given Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | Infinispan |
      | config | infra         | infinispan |
      | config | infra         | kafka      |
    And Kogito Data Index has 1 pods running within 10 minutes
    And Kogito Jobs Service has 1 pods running within 10 minutes

    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "jobs-service-data-index-id",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """

    Then GraphQL request on Data Index service returns Jobs ID "jobs-service-data-index-id" within 2 minutes

#####

  @failover
  @events
  @infinispan
  @kafka
  Scenario Outline: Test Jobs service failover with Kafka
    Given Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | Infinispan |
      | config | infra         | infinispan |
      | config | infra         | kafka |
    And Kogito Jobs Service has 1 pods running within 10 minutes

    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "jobs-service-data-index-id",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/jobs-service-data-index-id" is successful within 1 minutes
    And GraphQL request on Data Index service returns Jobs ID "jobs-service-data-index-id" within 2 minutes

    And Scale Kafka instance "kogito-kafka" down
    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "jobs-service-data-index-id-2",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/jobs-service-data-index-id-2" is successful within 1 minutes
    
    And Kafka instance "kogito-kafka" has 1 kafka pod running within 2 minutes
    And GraphQL request on Data Index service returns Jobs ID "jobs-service-data-index-id-2" within 2 minutes

    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "jobs-service-data-index-id-3",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    
    Then HTTP GET request on service "jobs-service" with path "jobs/jobs-service-data-index-id-3" is successful within 1 minutes
    And GraphQL request on Data Index service returns Jobs ID "jobs-service-data-index-id-3" within 2 minutes

#####

  @failover
  @infinispan
  Scenario: Test Kogito Jobs service failover with Infinispan
    Given Infinispan Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | Infinispan |
      | config | infra         | infinispan |
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And Scale Infinispan instance "kogito-infinispan" to 0 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" fails within 2 minutes

    And Scale Infinispan instance "kogito-infinispan" to 1 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "2",
        "processId": "2",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    
    Then HTTP GET request on service "jobs-service" with path "jobs/2" is successful within 1 minutes

#####

  @failover
  @mongodb
  Scenario: Test Kogito Jobs service failover with MongoDB
    Given MongoDB Operator is deployed
    And MongoDB instance "kogito-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_jobsservice     |
    And Install MongoDB Kogito Infra "mongodb" targeting service "kogito-mongodb" within 5 minutes with configuration:
      | config   | username | developer            |
      | config   | database | kogito_jobsservice   |
    
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config | database-type | MongoDB |
      | config | infra         | mongodb |
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And Scale MongoDB instance "kogito-mongodb" to 0 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" fails within 2 minutes

    And Scale MongoDB instance "kogito-mongodb" to 1 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "2",
        "processId": "2",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    
    Then HTTP GET request on service "jobs-service" with path "jobs/2" is successful within 1 minutes

#####

  @failover
  @postgresql
  Scenario: Test Kogito Jobs service failover with PostgreSQL
    Given PostgreSQL instance "postgresql" is deployed within 3 minutes with configuration:
      | username | myuser |
      | password | mypass |
      | database | mydb   |
    
    And Install Kogito Jobs Service with 1 replicas with configuration:
      | config      | database-type                   | PostgreSQL                             |
      | runtime-env | QUARKUS_DATASOURCE_JDBC_URL     | jdbc:postgresql://postgresql:5432/mydb |
      | runtime-env | QUARKUS_DATASOURCE_REACTIVE_URL | postgresql://postgresql:5432/mydb      |
      | runtime-env | QUARKUS_DATASOURCE_USERNAME     | myuser                                 |
      | runtime-env | QUARKUS_DATASOURCE_PASSWORD     | mypass                                 |
    And Kogito Jobs Service has 1 pods running within 10 minutes
    
    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "processId": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And Scale PostgreSQL instance "postgresql" to 0 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" fails within 2 minutes

    And Scale PostgreSQL instance "postgresql" to 1 pods within 2 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "2",
        "processId": "2",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    
    Then HTTP GET request on service "jobs-service" with path "jobs/2" is successful within 1 minutes