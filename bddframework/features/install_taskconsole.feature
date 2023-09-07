@taskconsole
@infinispan
@kafka
Feature: Install Kogito Task Console

  Background:
    Given Namespace is created
    And Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed

  Scenario: Install Kogito Task Console
    Given Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Data Index has 1 pods running within 10 minutes

    When Install Kogito Task Console with 1 replicas

    Then Kogito Task Console has 1 pods running within 10 minutes
    And HTTP GET request on service "task-console" with path "" is successful within 2 minutes
