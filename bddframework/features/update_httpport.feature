Feature: Update the HTTP Port field in Kogito Services

  Background:
    Given Namespace is created

  @jobsservice
  Scenario: Update HTTP Port for Jobs Service
    Given Kogito Operator is deployed
    When Install Kogito Jobs Service with 1 replicas with configuration:
      | config | httpPort | 9081 |
    Then Kogito Jobs Service has 1 pods running within 10 minutes

  @dataindex
  @infinispan
  @kafka
  Scenario: Update HTTP Port for Data Index Service
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    When Install Kogito Data Index with 1 replicas with configuration:
      | config | httpPort | 9082 |
    Then Kogito Data Index has 1 pods running within 10 minutes

  @managementconsole
  @infinispan
  @kafka
  Scenario: Update HTTP Port for Management Console
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Kogito Data Index has 1 pods running within 10 minutes
    When Install Kogito Management Console with 1 replicas with configuration:
      | config | httpPort | 9082 |
    Then Kogito Management Console has 1 pods running within 10 minutes

  Scenario Outline: Update HTTP Port for Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | infinispan | useKogitoInfra | disabled |
      | config     | httpPort       | 9082     |

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes

    @springboot
    Examples:
      | runtime    | example-service            | profile |
      | springboot | process-springboot-example | default |

    @quarkus
    Examples:
      | runtime    | example-service            | profile |
      | quarkus    | process-quarkus-example    | default |

    @quarkus
    @native
    Examples:
      | runtime    | example-service            | profile |
      | quarkus    | process-quarkus-example    | native  |
