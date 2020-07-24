# Can be enabled once https://github.com/kiegroup/kogito-cloud-operator/pull/457 is merged
@disabled
Feature: Deploy Kogito Runtime

  Background:
    Given Namespace is created

  @smoke
  Scenario Outline: Deploy <example-service> with Maven profile <profile> using Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | infinispan | useKogitoInfra | disabled |

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And Service "<example-service>" with process name "orders" is available within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | profile |
      | springboot | process-springboot-example | default |

    @quarkus
    Examples:
      | runtime    | example-service         | profile |
      | quarkus    | process-quarkus-example | default |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | profile |
      | quarkus    | process-quarkus-example | native  |

#####

  @persistence
  Scenario Outline: Deploy <example-service> with Maven profile <profile> with persistence using Kogito Runtime
    Given Kogito Operator is deployed with Infinispan operator
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | infinispan | useKogitoInfra | enabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And Start "orders" process on service "<example-service>" within 3 minutes with body:
      """json
      {
        "approver" : "john",
        "order" : {
          "orderNumber" : "12345",
          "shipped" : false
        }
      }
      """

    Then Service "<example-service>" contains 1 instances of process with name "orders"

    When Scale Kogito Runtime "<example-service>" to 0 pods within 2 minutes
    And Scale Kogito Runtime "<example-service>" to 1 pods within 2 minutes

    Then Service "<example-service>" contains 1 instances of process with name "orders" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | profile     |
      | springboot | process-springboot-example | persistence |

    @quarkus
    Examples:
      | runtime    | example-service         | profile     |
      | quarkus    | process-quarkus-example | persistence |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | profile            |
      | quarkus    | process-quarkus-example | native,persistence |

#####

  @events
  Scenario Outline: Deploy <example-service> with Maven profile <profile> with events using Kogito Runtime
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Data Index with 1 replicas
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | infinispan | useKogitoInfra | enabled |
      | kafka      | useKogitoInfra | enabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And Start "orders" process on service "<example-service>" within 3 minutes with body:
      """json
      {
        "approver" : "john",
        "order" : {
          "orderNumber" : "12345",
          "shipped" : false
        }
      }
      """

    Then GraphQL request on Data Index service returns ProcessInstances processName "orders" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | profile            |
      | springboot | process-springboot-example | persistence,events |

    @quarkus
    Examples:
      | runtime    | example-service         | profile            |
      | quarkus    | process-quarkus-example | persistence,events |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | profile                   |
      | quarkus    | process-quarkus-example | native,persistence,events |

#####

  Scenario Outline: Deploy process-optaplanner-quarkus service with Maven profile <profile> without persistence
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | infinispan | useKogitoInfra | disabled |

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And HTTP POST request on service "<example-service>" is successful within 2 minutes with path "rest/flights" and body:
      """json
      {
        "params" : {
          "origin" : "A",
          "destination" : "B",
          "departureDateTime" : "2020-05-30T17:30:43.873968",
          "seatRowSize" : 6,
          "seatColumnSize" : 10
        }
      }
      """

    @quarkus
    Examples:
      | runtime    | example-service             | profile |
      | quarkus    | process-optaplanner-quarkus | default |

    @quarkus
    @native
    Examples:
      | runtime    | example-service             | profile |
      | quarkus    | process-optaplanner-quarkus | native  |