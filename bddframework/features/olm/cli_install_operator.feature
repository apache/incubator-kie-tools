# Disabled until OLM dev deployment is in place => https://issues.redhat.com/browse/KOGITO-940
@disabled
@olm
@cli
Feature: CLI: Install Kogito Operator

  Background:
    Given Namespace is created

  Scenario: CLI install Kogito operator
    When CLI install Kogito operator

    Then Kogito operator should be installed with dependencies

#####

  Scenario: CLI install operator with Kogito Data Index
    When CLI install Kogito operator with Kogito Data Index

    Then Kogito Data Index has 1 pods running within 5 minutes
    And GraphQL request on service "kogito-data-index" is successful within 2 minutes with path "graphql" and query:
      """
      {
        ProcessInstances{
          id
        }
      }
      """

#####

  # Disabled until https://issues.redhat.com/browse/KOGITO-910 has been implemented
  @disabled
  Scenario: CLI install operator with Kogito Jobs Service
    When CLI install Kogito operator with Kogito Jobs Service

    Then Kogito Jobs Service has 1 pods running within 5 minutes
    And HTTP POST request on service "kogito-jobs-service" is successful within 2 minutes with path "jobs" and body:
      """
      { 
        "id": "1",
        "priority": "1",
        "expirationTime": "2100-01-29T18:19:00Z",
        "callbackEndpoint": "http://localhost:8080/callback"
      }
      """

    Then HTTP GET request on service "kogito-jobs-service" with path "jobs/1" is successful within 1 minutes