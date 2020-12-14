Feature: Deploy Kogito Runtime

  Background:
    Given Namespace is created

  Scenario Outline: Deploy <example-service> with Maven profile <profile> using Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry

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
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | infinispan |
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

  @jobsservice
  Scenario Outline: Deploy <example-service> service with Jobs service and Maven profile <profile>
    Given Kogito Operator is deployed
    And Install Kogito Jobs Service with 1 replicas
    And Kogito Jobs Service has 1 pods running within 10 minutes
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy <runtime> example service "<example-service>" from runtime registry
    And Kogito Runtime "<example-service>" has 1 pods running within 10 minutes

    When Start "timers" process on service "<example-service>" within 2 minutes with body:
      """json
      {
        "delay" : "PT1S"
      }
      """

    Then Kogito Runtime "<example-service>" log contains text "Before timer" within 1 minutes
    And Kogito Runtime "<example-service>" log contains text "After timer" within 1 minutes
    And Kogito Jobs Service log contains text "<example-service>" within 1 minutes

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
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Install Kafka Kogito Infra "kafka" within 10 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes
    And Kafka instance "kogito-kafka" has 1 pod running within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
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

    When Deploy <runtime> example service "<example-service>" from runtime registry

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

    # Disabled due to https://issues.redhat.com/browse/PLANNER-2084
    @disabled
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
    And Deploy <runtime> example service "<example-service>" from runtime registry
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
  @externalcomponent
  @infinispan
  Scenario: Deploy <example-service> service with Maven profile <profile> using external Infinispan
    Given Kogito Operator is deployed with Infinispan operator
    And Infinispan instance "external-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" within 5 minutes with configuration:
      | resource | name | external-infinispan |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | external-infinispan |
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

  @persistence
  @externalcomponent
  @mongodb
  Scenario: Deploy <example-service> service with Maven profile <profile> using external MongoDB
    Given Kogito Operator is deployed with MongoDB operator
    And MongoDB instance "external-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_dataindex     |
    And Install MongoDB Kogito Infra "external-mongodb" within 5 minutes with configuration:
      | resource | name     | external-mongodb  |
      | config   | username | developer            |
      | config   | database | kogito_dataindex     |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | external-mongodb         |
      # Setup short name as it can create some problems with route name too long ...
      | config | name  | process-mongodb |         
    And Kogito Runtime "process-mongodb" has 1 pods running within 10 minutes
    And Start "deals" process on service "process-mongodb" within 3 minutes with body:
      """json
      {
        "name" : "my fancy deal",
        "traveller" : {
          "firstName" : "John",
          "lastName" : "Doe",
          "email" : "jon.doe@example.com",
          "nationality" : "American",
          "address" : {
            "street" : "main street",
            "city" : "Boston",
            "zipCode" : "10005",
            "country" : "US" 
          }
        }
      }
      """

    Then Service "process-mongodb" contains 1 instances of process with name "dealreviews"

    When Scale Kogito Runtime "process-mongodb" to 0 pods within 2 minutes
    And Scale Kogito Runtime "process-mongodb" to 1 pods within 2 minutes

    Then Service "process-mongodb" contains 1 instances of process with name "dealreviews" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service                        | profile |
      | springboot | process-mongodb-persistence-springboot | default |

    @quarkus
    Examples:
      | runtime    | example-service                     | profile |
      | quarkus    | process-mongodb-persistence-quarkus | default |

    @quarkus
    @native
    Examples:
      | runtime    | example-service                     | profile |
      | quarkus    | process-mongodb-persistence-quarkus | native  |

#####

  @events
  @kafka
  @infinispan
  Scenario: Deploy <example-service> with Maven profile <profile> with events using external Kafka
    Given Kogito Operator is deployed with Infinispan and Kafka operators
    And Kafka instance "external-kafka" is deployed
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes
    And Install Kafka Kogito Infra "external-kafka" within 5 minutes with configuration:
      | resource | name | external-kafka |
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan     |
      | config | infra | external-kafka |
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | infinispan     |
      | config | infra | external-kafka |
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

#####

  @failover
  @persistence
  @infinispan
  Scenario Outline: Test Kogito Runtime <example-service> failover with Infinispan
    Given Kogito Operator is deployed with Infinispan operator
    And Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Install Infinispan Kogito Infra "infinispan" within 5 minutes
    And Infinispan instance "kogito-infinispan" has 1 pod running within 5 minutes

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | infinispan |
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

    When Scale Infinispan instance "kogito-infinispan" to 0 pods within 2 minutes
    Then HTTP GET request on service "<example-service>" with path "orders" fails within 2 minutes

    When Scale Infinispan instance "kogito-infinispan" to 1 pods within 2 minutes
    Then Service "<example-service>" contains 0 instances of process with name "orders" within 2 minutes

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

    @springboot
    Examples:
      | runtime    | example-service            | profile     |
      | springboot | process-springboot-example | persistence |

    @quarkus
    Examples:
      | runtime    | example-service         | profile     |
      | quarkus    | process-quarkus-example | persistence |
