@trusty
@infinispan
@kafka
Feature: Kogito Trusty

  Background:
    Given Namespace is created
    And Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed

  Scenario: Install Kogito Trusty
    Given Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes

    When Install Kogito Trusty with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |

    Then Kogito Trusty has 1 pods running within 10 minutes

#####

  @events
  @kafka
  @infinispan
  Scenario: Trusty retrieves tracing events using external Infinispan and Kafka
    Given Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass |
    And Kafka instance "external-kafka" is deployed
    And Install Infinispan Kogito Infra "external-infinispan" within 5 minutes with configuration:
      | resource | name | external-infinispan |
    And Install Kafka Kogito Infra "external-kafka" within 5 minutes with configuration:
      | resource | name | external-kafka |
    And Install Kogito Trusty with 1 replicas with configuration:
      | config | infra | external-infinispan |
      | config | infra | external-kafka      |
    And Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "dmn-tracing-quarkus" from runtime registry with configuration:
      | config | infra | external-kafka |
    And Kogito Runtime "dmn-tracing-quarkus" has 1 pods running within 10 minutes

    When HTTP POST request on service "dmn-tracing-quarkus" is successful within 2 minutes with path "LoanEligibility" and body:
      """json
      {
      "Bribe": 100,
      "Client": {
        "age": 45,
        "existing payments": 2000,
        "salary": 2000
      },
      "Loan": {
        "duration": 40,
        "installment": 1000
      },
      "SupremeDirector": "yes"
      }
      """

    Then HTTP GET request on service "trusty" with path "/executions" should contain a string "DECISION" within 3 minutes
