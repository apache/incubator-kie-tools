@explainability
@kafka
Feature: Kogito Explainability service

  Background:
    Given Namespace is created

  Scenario Outline: Install Kogito Explainability with communication <communication>
    Given Kogito Operator is deployed with Kafka operator
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes
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
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes
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
    And Install Infinispan Kogito Infra "external-infinispan" connected to resource "external-infinispan" within 5 minutes
    And Install Kafka Kogito Infra "external-kafka" connected to resource "external-kafka" within 5 minutes
    And Install Kogito Trusty with 1 replicas with configuration:
      | config | infra | external-infinispan |
      | config | infra | external-kafka      |

    When Install Kogito Explainability with 1 replicas with configuration:
      | config | infra | external-kafka |
    And Clone Kogito examples into local directory
    And Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "dmn-tracing-quarkus" from runtime registry with configuration:
      | config | infra | external-kafka |
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
