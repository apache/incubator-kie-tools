@cli
@jobsservice
Feature: CLI: Install Kogito Jobs Service

  Background:
    Given Namespace is created

  Scenario: CLI install Kogito Jobs Service
    Given Kogito Operator is deployed

    When CLI install Kogito Jobs Service with 1 replicas
    And Kogito Jobs Service has 1 pods running within 5 minutes
    And HTTP POST request on service "kogito-jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      { 
        "id": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    
    Then HTTP GET request on service "kogito-jobs-service" with path "jobs/1" is successful within 1 minutes

#####

  @persistence
  Scenario: CLI install Kogito Jobs Service with persistence
    Given Kogito Operator is deployed with dependencies
    
    When CLI install Kogito Jobs Service with persistence and 1 replicas
    And Kogito Jobs Service has 1 pods running within 5 minutes
    And HTTP POST request on service "kogito-jobs-service" is successful within 2 minutes with path "jobs" and body:
      """json
      { 
        "id": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """
    And HTTP GET request on service "kogito-jobs-service" with path "jobs/1" is successful within 1 minutes
    And Scale Kogito Jobs Service to 0 pods within 2 minutes
    And Scale Kogito Jobs Service to 1 pods within 2 minutes

    Then HTTP GET request on service "kogito-jobs-service" with path "jobs/1" is successful within 1 minutes