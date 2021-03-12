# This file is not finished yet. Further tuning of Kafka infrastructure is needed to get to the desired numbers

@performance
@dataindex
@infinispan
@kafka
Feature: Data Index Performance

  Background:
    Given Namespace is created
    And Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |

  @quarkus
  Scenario Outline: Quarkus Kogito Service Performance with Maven profile <profile>, without persistence and with requests <requests>
    Given Clone Kogito examples into local directory
    And Local example service "process-quarkus-example" is built by Maven using profile "<profile>" and deployed to runtime registry with Maven options:
      | -Dmp.messaging.emitter.default-buffer-size=1024 |
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
      | config      | infra        | kafka   |
      | runtime-env | JAVA_OPTIONS | -Xmx10G |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes
    # Check Data Index once again as it is restarted after a new service is deployed (protobuf files registration)
    And Kogito Data Index has 1 pods running within 2 minutes
    And GraphQL request on service "data-index" is successful within 2 minutes with path "graphql" and query:
    """
    {
      ProcessInstances{
        id
      }
    }
    """

    When <requests> HTTP POST requests with report using 100 threads on service "process-quarkus-example" with path "orders" and body:
      """json
      {
        "approver" : "john",
        "order" : {
          "orderNumber" : "12345",
          "shipped" : false
        }
      }
      """


    Then GraphQL request on Data Index service returns <requests> instances of process with name "orders" within 2 minutes
    And GraphQL request on Data Index service returns <requests> instances of process with name "orderItems" within 2 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | profile | requests |
      | events | 500   |
#      | events | 80000    |
#      | events | 160000   |
#      | events | 320000   |

#####

  @springboot
  Scenario Outline: Spring Boot Kogito Service Performance without persistence and with requests <requests>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "process-springboot-example" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy springboot example service "process-springboot-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G |
    And Kogito Runtime "process-springboot-example" has 1 pods running within 10 minutes
    And Service "process-springboot-example" with process name "orders" is available within 3 minutes

    When <requests> HTTP POST requests with report using 100 threads on service "process-springboot-example" with path "orders" and body:
      """json
      {
        "approver" : "john",
        "order" : {
          "orderNumber" : "12345",
          "shipped" : false
        }
      }
      """

    Then Service "process-springboot-example" contains <requests> instances of process with name "orders" within 1 minutes
    And Service "process-springboot-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | requests |
      | 40000    |
      | 80000    |
#      | 160000   |
#      | 320000   |
