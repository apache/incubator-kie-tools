# Commented code will be addressed by further enhancements:
# https://issues.redhat.com/browse/KOGITO-1701
# https://issues.redhat.com/browse/KOGITO-1888

@performance
Feature: Kogito Service Performance

  Background:
    Given Namespace is created

  @quarkus
  Scenario Outline: Quarkus Kogito Service Performance with Maven profile <profile>, without persistence and with requests <requests>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "process-quarkus-example" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
       | runtime-env | JAVA_OPTIONS | -Xmx10G  |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes

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

    Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"


    Examples:
      | profile | requests |
      | default | 40000    |
      | default | 80000    |
#      | default | 160000   |
#      | default | 320000   |

    @native
    Examples:
      | profile | requests |
      | native  | 40000    |
      | native  | 80000    |
#      | native | 160000   |
#      | native | 320000   |

#####

  @quarkus
  @persistence
  @infinispan
  Scenario Outline: Quarkus Kogito Service Performance with Maven profile <profile>, with persistence and with requests <requests>
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Infinispan instance "external-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" targeting service "external-infinispan" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "process-quarkus-example" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G             |
      | config      | infra        | external-infinispan |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes

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

    #Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"


    Examples:
      | profile     | requests |
      | persistence | 40000    |
      | persistence | 80000    |
#      | persistence | 160000   |
#      | persistence | 320000   |

    @native
    Examples:
      | profile            | requests |
      | native,persistence | 40000    |
      | native,persistence | 80000    |
#      | native,persistence | 160000   |
#      | native,persistence | 320000   |

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

#####

  @springboot
  @persistence
  @infinispan
  Scenario Outline: Spring Boot Kogito Service Performance with persistence and with requests <requests>
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Infinispan instance "external-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" targeting service "external-infinispan" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "process-springboot-example" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy springboot example service "process-springboot-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G             |
      | config      | infra        | external-infinispan |
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

    #Then Service "process-springboot-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-springboot-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | requests |
      | 40000    |
      | 80000    |
#      | 160000   |
#      | 320000   |
