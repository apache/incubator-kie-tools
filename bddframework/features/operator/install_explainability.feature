@explainability
@kafka
Feature: Kogito Explainability service

  Background:
    Given Namespace is created

  @smoke
  Scenario Outline: Install Kogito Explainability with communication <communication>
    Given Kogito Operator is deployed with Kafka operator
    And Kafka topic "trusty-explainability-result" is deployed
    When Install Kogito Explainability with 1 replicas with configuration:
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
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Trusty with 1 replicas

    When Install Kogito Explainability with 1 replicas
    And Clone Kogito examples into local directory
    And Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "dmn-tracing-quarkus" from runtime registry with configuration:
      | config     | enablePersistence | enabled |
      | config     | enableEvents      | enabled |
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
      | Is Enought?  |

#####

  @externalcomponent
  @events
  @infinispan
  @kafka
  Scenario: Explainability retrieves explainability requests events using external Kafka
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Kafka instance "external-kafka" is deployed
    And Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass |
    And Install Kogito Trusty with 1 replicas with configuration:
      | infinispan | username    | developer                           |
      | infinispan | password    | mypass                              |
      | infinispan | uri         | external-infinispan:11222           |
      | kafka      | externalURI | external-kafka-kafka-bootstrap:9092 |

    When Install Kogito Explainability with 1 replicas with configuration:
      | kafka      | externalURI | external-kafka-kafka-bootstrap:9092 |
    And Clone Kogito examples into local directory
    And Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "dmn-tracing-quarkus" from runtime registry with configuration:
      | config     | enableEvents | enabled                             |
      | kafka      | externalURI  | external-kafka-kafka-bootstrap:9092 |
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
      | Is Enought?  |
