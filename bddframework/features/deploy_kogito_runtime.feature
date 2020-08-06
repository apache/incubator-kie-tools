Feature: Deploy Kogito Runtime

  Background:
    Given Namespace is created

  Scenario Outline: Deploy <example-service> with Maven profile <profile> using Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | enablePersistence | disabled |

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And Service "<example-service>" with process name "orders" is available within 2 minutes

    @smoke
    @springboot
    Examples:
      | runtime    | example-service            | profile |
      | springboot | process-springboot-example | default |

    @smoke
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
  @infinispan
  Scenario Outline: Deploy <example-service> with Maven profile <profile> with persistence using Kogito Runtime
    Given Kogito Operator is deployed with Infinispan operator
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config     | enablePersistence | enabled |
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

  # Need to synchronize time between OCP nodes to enable the test, also Jobs service URL env variable is missing in KogitoRuntime pods, will be fixed in https://github.com/kiegroup/kogito-cloud-operator/pull/479
  @disabled
  @jobsservice
  Scenario Outline: Deploy <example-service> service with Jobs service and Maven profile <profile>
    Given Kogito Operator is deployed
    And Install Kogito Jobs Service with 1 replicas
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | enablePersistence | disabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes

    When Start "timers" process on service "<example-service>" within 2 minutes with body:
      """json
      {
        "delay" : "PT1S"
      }
      """

    # Implement retrieving of job information from Jobs service once https://issues.redhat.com/browse/KOGITO-1163 is fixed
    Then Kogito Runtime "<example-service>" log contains text "Before timer" within 1 minutes
    And Kogito Runtime "<example-service>" log contains text "After timer" within 1 minutes

    @springboot
    Examples:
      | runtime    | example-service          | profile |
      | springboot | process-timer-springboot | default |

    @quarkus
    Examples:
      | runtime | example-service       | profile |
      | quarkus | process-timer-quarkus | default |

    # Disabled as long as https://issues.redhat.com/browse/KOGITO-1179 is not solved
    @disabled
    @quarkus
    @native
    Examples:
      | runtime | example-service       | profile |
      | quarkus | process-timer-quarkus | native  |

#####

  @events
  @infinispan
  @kafka
  Scenario Outline: Deploy <example-service> with Maven profile <profile> with events using Kogito Runtime
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Install Kogito Data Index with 1 replicas
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config     | enablePersistence | enabled |
      | config     | enableEvents      | enabled |
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
      | config | enablePersistence | disabled |

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

#####

  @usertasks
  Scenario Outline: Deploy <example-service> service to complete user tasks with Maven profile <profile>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | enablePersistence | disabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes

    When Start "orders" process on service "<example-service>" within 3 minutes with body:
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

    When Complete "Verify order" task on service "<example-service>" and process with name "orderItems" by user "john" with body:
	  """json
	  {}
    """

    Then Service "<example-service>" contains 0 instances of process with name "orders"
    And Service "<example-service>" contains 0 instances of process with name "orderItems"

    @springboot
    Examples:
      | runtime    | example-service            | profile |
      | springboot | process-springboot-example | default |

    @quarkus
    Examples:
      | runtime | example-service         | profile |
      | quarkus | process-quarkus-example | default |

    @quarkus
    @native
    Examples:
      | runtime | example-service         | profile |
      | quarkus | process-quarkus-example | native  |

#####

  @persistence
  @infinispan
  Scenario: Deploy <example-service> service with Maven profile <profile> using external Infinispan
    Given Kogito Operator is deployed with Infinispan operator
    And Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config     | enablePersistence | enabled                   |
      | infinispan | username          | developer                 |
      | infinispan | password          | mypass                    |
      | infinispan | uri               | external-infinispan:11222 |
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
    And Service "<example-service>" contains 1 instances of process with name "orders"

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
  @kafka
  @infinispan
  Scenario: Deploy <example-service> with Maven profile <profile> with events using external Kafka
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Kafka instance "external-kafka" is deployed
    And Install Kogito Data Index with 1 replicas with configuration:
      | kafka | externalURI | external-kafka-kafka-bootstrap:9092 |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | enableEvents      | enabled                             |
      | config | enablePersistence | enabled                             |
      | kafka  | externalURI       | external-kafka-kafka-bootstrap:9092 |
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes

    When Start "orders" process on service "<example-service>" within 3 minutes with body:
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
