@explainability
@kafka
Feature: Kogito Explainability service

  Background:
    Given Namespace is created

  Scenario Outline: Install Kogito Explainability with communication <communication>
    Given Kogito Operator is deployed
    And Kafka Operator is deployed
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Kafka topic "trusty-explainability-result" is deployed

    When Install Kogito Explainability with 1 replicas with configuration:
      | config       | infra                          | kafka             |
      | runtime-env  | EXPLAINABILITY_COMMUNICATION   | <communication>   |
    Then Kogito Explainability has 1 pods running within 10 minutes

    Examples:
      | communication | 
      | messaging     | 
      | rest          | 

#####

  @events
  @infinispan
  @kafka
  Scenario: Explainability retrieves explainability requests events
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Trusty with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |

    When Install Kogito Explainability with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Clone Kogito examples into local directory
    And Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "dmn-tracing-quarkus" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Kogito Runtime "dmn-tracing-quarkus" has 1 pods running within 10 minutes
    And HTTP POST request on service "dmn-tracing-quarkus" is successful within 2 minutes with path "LoanEligibility" and body:
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

    Then Explainability result for execution "LoanEligibility" in the Trusty service within 3 minutes with saliences:
      | Eligibility  | 
      | Is Enough?  |
