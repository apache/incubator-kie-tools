Feature: Install Kogito jobs service

  Scenario: Install Kogito jobs service
    Given Kogito Operator is deployed
    And Deploy Kogito jobs service with 1 replicas within 5 minutes

    When HTTP POST request on service "jobs-service" with path "jobs" and "json" body '{"id": "1","priority": "1","expirationTime": "2100-01-29T18:19:00Z","callbackEndpoint": "http://localhost:8080/callback"}'

    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes

  Scenario: Install Kogito jobs service with persistence
    Given Kogito Operator is deployed with dependencies
    And Deploy Kogito jobs service with persistence and 1 replicas within 5 minutes
    
    When HTTP POST request on service "jobs-service" with path "jobs" and "json" body '{"id": "1","priority": "1","expirationTime": "2100-01-29T18:19:00Z","callbackEndpoint": "http://localhost:8080/callback"}' within 1 minutes
    And HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes
    And Scale Kogito jobs service to 0 pods within 2 minutes
    And Scale Kogito jobs service to 1 pods within 2 minutes

    Then HTTP GET request on service "jobs-service" with path "jobs/1" is successful within 1 minutes