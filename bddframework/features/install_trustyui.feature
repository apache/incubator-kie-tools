@trustyui
Feature: Install Kogito Trusty UI

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  Scenario: Install Kogito Trusty UI
    When Install Kogito Trusty UI with 1 replicas
    Then Kogito Trusty UI has 1 pods running within 10 minutes
    And HTTP GET request on service "trusty-ui" with path "" is successful within 2 minutes
