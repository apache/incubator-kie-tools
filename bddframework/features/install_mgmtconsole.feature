@managementconsole
Feature: Install Kogito Management Console

  Background:
    Given Namespace is created
    And Kogito Operator is deployed with Infinispan and Kafka operators

  Scenario: Install Kogito Management Console
    Given Install Kogito Data Index with 1 replicas
    And Kogito Data Index has 1 pods running within 10 minutes
    When Install Kogito Management Console with 1 replicas
    Then Kogito Management Console has 1 pods running within 10 minutes
    And HTTP GET request on service "management-console" with path "" is successful within 2 minutes
