@jobsservice
Feature: Install Kogito Jobs Service

  Background:
    Given Namespace is created

  @smoke
  Scenario: Install Kogito Jobs Service without persistence
    Given Kogito Operator is deployed

    When Install Kogito Jobs Service with 1 replicas
    And Kogito Jobs Service has 1 pods running within 10 minutes
    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """

    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @persistence
  Scenario: Install Kogito Jobs Service with persistence
    Given Kogito Operator is deployed with Infinispan operator
    
    When Install Kogito Jobs Service with 1 replicas and persistence
    And Kogito Jobs Service has 1 pods running within 10 minutes
    And HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "1",
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
  @persistence
  Scenario: Jobs service events are stored in Data Index
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Data Index with 1 replicas
    And Install Kogito Jobs Service with 1 replicas and persistence and events
    And Kogito Data Index has 1 pods running within 10 minutes
    And Kogito Jobs Service has 1 pods running within 10 minutes

    When HTTP POST request on service "jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      {
        "id": "jobs-service-data-index-id",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """

    Then GraphQL request on Data Index service returns Jobs ID "jobs-service-data-index-id" within 2 minutes
