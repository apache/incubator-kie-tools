Feature: Deploy Kogito Runtime

  Background:
    Given Namespace is created

  @rhpam
  Scenario Outline: Deploy <example-service> with native <native> using Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    When Deploy <runtime> example service "<example-service>" from runtime registry

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And Service "<example-service>" with process name "orders" is available within 2 minutes

    @smoke
    @springboot
    Examples:
      | runtime    | example-service            | native   |
      | springboot | process-springboot-example | disabled |

    @smoke
    @quarkus
    Examples:
      | runtime    | example-service         | native   |
      | quarkus    | process-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native   |
      | quarkus    | process-quarkus-example | enabled  |

#####

  @rhpam
  Scenario Outline: Deploy DMN <example-service> with native <native> using Kogito Runtime
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    When Deploy <runtime> example service "<example-service>" from runtime registry

    Then Kogito Runtime "<example-service>" has 1 pods running within 10 minutes
    And HTTP POST request on service "<example-service>" is successful within 2 minutes with path "Traffic Violation" and body:
      """json
      {
          "Driver":{"Points":2},
          "Violation":{
              "Type":"speed",
              "Actual Speed":120,
              "Speed Limit":100
          }
      }
      """

    @springboot
    Examples:
      | runtime    | example-service        | native   |
      | springboot | dmn-springboot-example | disabled |

    @quarkus
    Examples:
      | runtime    | example-service     | native   |
      | quarkus    | dmn-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service     | native   |
      | quarkus    | dmn-quarkus-example | enabled  |

#####

  @persistence
  @infinispan
  Scenario Outline: Deploy <example-service> with persistence and native <native> using Kogito Runtime
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence |
      | native  | <native>  |
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes

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
      | runtime    | example-service            | native   |
      | springboot | process-springboot-example | disabled |

    @quarkus
    Examples:
      | runtime    | example-service         | native   |
      | quarkus    | process-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native  |
      | quarkus    | process-quarkus-example | enabled |

#####

  @persistence
  @mongodb
  Scenario: Deploy <example-service> service with native <native> using MongoDB
    Given Kogito Operator is deployed
    And MongoDB Operator is deployed
    And MongoDB instance "kogito-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_dataindex     |
    And Install MongoDB Kogito Infra "kogito-mongodb" targeting service "kogito-mongodb" within 5 minutes with configuration:
      | config   | username | developer            |
      | config   | database | kogito_dataindex     |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | kogito-mongodb   |
      # Setup short name as it can create some problems with route name too long ...
      | config | name  | process-mongodb  |
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
      | runtime    | example-service                        | native   |
      | springboot | process-mongodb-persistence-springboot | disabled |

    @quarkus
    Examples:
      | runtime    | example-service                     | native   |
      | quarkus    | process-mongodb-persistence-quarkus | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service                     | native   |
      | quarkus    | process-mongodb-persistence-quarkus | enabled  |

#####

  @persistence
  @postgresql
  Scenario Outline: Deploy <example-service> service with native <native> using PostgreSQL
    Given Kogito Operator is deployed
    Given PostgreSQL instance "postgresql" is deployed within 3 minutes with configuration:
      | username | myuser |
      | password | mypass |
      | database | mydb   |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native  | <native>    |
      | profile | persistence |

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      # Setup short name as it can create some problems with route name too long ...
      | config      | name                                         | process-postgres                                |
      # For Quarkus
      | runtime-env | QUARKUS_DATASOURCE_USERNAME                  | myuser                                          |
      | runtime-env | QUARKUS_DATASOURCE_PASSWORD                  | mypass                                          |
      | runtime-env | QUARKUS_DATASOURCE_REACTIVE_URL              | postgresql://postgresql:5432/mydb               |
      # For Spring Boot
      | runtime-env | KOGITO_PERSISTENCE_POSTGRESQL_CONNECTION_URI | postgresql://myuser:mypass@postgresql:5432/mydb |
    And Kogito Runtime "process-postgres" has 1 pods running within 10 minutes
    And Start "deals" process on service "process-postgres" within 3 minutes with body:
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

    Then Service "process-postgres" contains 1 instances of process with name "dealreviews"

    When Scale Kogito Runtime "process-postgres" to 0 pods within 2 minutes
    And Scale Kogito Runtime "process-postgres" to 1 pods within 2 minutes

    Then Service "process-postgres" contains 1 instances of process with name "dealreviews" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service                           | native   |
      | springboot | process-postgresql-persistence-springboot | disabled |

    @quarkus
    Examples:
      | runtime    | example-service                        | native   |
      | quarkus    | process-postgresql-persistence-quarkus | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service                        | native   |
      | quarkus    | process-postgresql-persistence-quarkus | enabled  |

#####

  @jobsservice
  Scenario Outline: Deploy <example-service> service with Jobs service and native <native>
    Given Kogito Operator is deployed
    And Install Kogito Jobs Service with 1 replicas
    And Kogito Jobs Service has 1 pods running within 10 minutes
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |
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
    
    # Commented until https://issues.redhat.com/browse/KOGITO-6200 is solved
    # And Kogito Jobs Service log contains text "<example-service>" within 1 minutes

    @springboot
    Examples:
      | runtime    | example-service          | native   |
      | springboot | process-timer-springboot | disabled |

    @quarkus
    Examples:
      | runtime | example-service       | native   |
      | quarkus | process-timer-quarkus | disabled |

    @quarkus
    @native
    Examples:
      | runtime | example-service       | native   |
      | quarkus | process-timer-quarkus | enabled  |

#####

  @events
  @kafka
  @rhpam
  Scenario Outline: Deploy <example-service> with events and native <native> using Kogito Runtime
    Given Kogito Operator is deployed
    And Kafka Operator is deployed
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | events   |
      | native  | <native> |

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
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

    # 2 messages in output => orders and orderItems
    Then Kafka instance "kogito-kafka" should contain at least 2 messages on topic "kogito-processinstances-events" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | native   |
      | springboot | process-springboot-example | disabled |

    @quarkus
    Examples:
      | runtime    | example-service         | native   |
      | quarkus    | process-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native  |
      | quarkus    | process-quarkus-example | enabled |

#####

  @events
  @infinispan
  @kafka
  Scenario Outline: Deploy <example-service> with events and native <native> using Kogito Runtime and Infinispan
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence,events |
      | native  | <native>           |

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
      | runtime    | example-service            | native   |
      | springboot | process-springboot-example | disabled |

    @quarkus
    Examples:
      | runtime    | example-service         | native   |
      | quarkus    | process-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native  |
      | quarkus    | process-quarkus-example | enabled |

#####

  @events
  @mongodb
  @kafka
  Scenario Outline: Deploy <example-service> with events and native <native> using Kogito Runtime and MongoDB
    Given Kogito Operator is deployed
    And MongoDB Operator is deployed
    And Kafka Operator is deployed
    And MongoDB instance "kogito-mongodb" is deployed with configuration:
      | username | developer            |
      | password | mypass               |
      | database | kogito_dataindex     |
    And Install MongoDB Kogito Infra "kogito-mongodb" targeting service "kogito-mongodb" within 5 minutes with configuration:
      | config   | username | developer            |
      | config   | database | kogito_dataindex     |
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | database-type | MongoDB          |
      | config | infra         | kogito-mongodb |
      | config | infra         | kafka            |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | events   |
      | native  | <native> |

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | kogito-mongodb   |
      | config | infra | kafka            |
      # Setup short name as it can create some problems with route name too long ...
      | config | name  | process-mongodb  |
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

    Then GraphQL request on Data Index service returns ProcessInstances processName "Deal Review" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service                        | native   |
      | springboot | process-mongodb-persistence-springboot | disabled |

    @quarkus
    Examples:
      | runtime    | example-service                     | native   |
      | quarkus    | process-mongodb-persistence-quarkus | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service                     | native  |
      | quarkus    | process-mongodb-persistence-quarkus | enabled |

#####

  @events
  @postgresql
  @kafka
  Scenario Outline: Deploy <example-service> with events and native <native> using Kogito Runtime and PostgreSQL
    Given Kogito Operator is deployed
    And Kafka Operator is deployed
    Given PostgreSQL instance "postgresql" is deployed within 3 minutes with configuration:
      | username | myuser |
      | password | mypass |
      | database | mydb   |
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config      | database-type                             | PostgreSQL                             |
      | config      | infra                                     | kafka                                  |
      | runtime-env | QUARKUS_DATASOURCE_JDBC_URL               | jdbc:postgresql://postgresql:5432/mydb |
      | runtime-env | QUARKUS_DATASOURCE_USERNAME               | myuser                                 |
      | runtime-env | QUARKUS_DATASOURCE_PASSWORD               | mypass                                 |
      | runtime-env | quarkus.hibernate-orm.database.generation | update                                 |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | events,persistence |
      | native  | <native>           |

    When Deploy <runtime> example service "<example-service>" from runtime registry with configuration:
      | config | infra | kafka            |
      # Setup short name as it can create some problems with route name too long ...
      | config      | name                                         | process-postgres                                |
      # For Quarkus
      | runtime-env | QUARKUS_DATASOURCE_USERNAME                  | myuser                                          |
      | runtime-env | QUARKUS_DATASOURCE_PASSWORD                  | mypass                                          |
      | runtime-env | QUARKUS_DATASOURCE_REACTIVE_URL              | postgresql://postgresql:5432/mydb               |
      # For Spring Boot
      | runtime-env | KOGITO_PERSISTENCE_POSTGRESQL_CONNECTION_URI | postgresql://myuser:mypass@postgresql:5432/mydb |
    And Kogito Runtime "process-postgres" has 1 pods running within 10 minutes
    And Start "deals" process on service "process-postgres" within 3 minutes with body:
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

    Then GraphQL request on Data Index service returns ProcessInstances processName "Deal Review" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service                           | native   |
      | springboot | process-postgresql-persistence-springboot | disabled |

    @quarkus
    Examples:
      | runtime    | example-service                        | native   |
      | quarkus    | process-postgresql-persistence-quarkus | disabled |

    @quarkus
    @native
    Examples:
      | runtime    | example-service                        | native  |
      | quarkus    | process-postgresql-persistence-quarkus | enabled |

#####

  @usertasks
  @rhpam
  Scenario Outline: Deploy <example-service> service to complete user tasks with native <native>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |
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
      | runtime    | example-service            | native   |
      | springboot | process-springboot-example | disabled |

    @quarkus
    Examples:
      | runtime | example-service         | native   |
      | quarkus | process-quarkus-example | disabled |

    @quarkus
    @native
    Examples:
      | runtime | example-service         | native   |
      | quarkus | process-quarkus-example | enabled  |

#####

  @failover
  @persistence
  @infinispan
  Scenario Outline: Test Kogito Runtime <example-service> failover with Infinispan
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence |
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes

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
      | runtime    | example-service            |
      | springboot | process-springboot-example |

    @quarkus
    Examples:
      | runtime    | example-service         |
      | quarkus    | process-quarkus-example |

#####

  @failover
  @events
  @infinispan
  @kafka
  Scenario Outline: Test Kogito Runtime <example-service> failover with Kafka
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Kafka Operator is deployed
    And Infinispan instance "kogito-infinispan" is deployed with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "infinispan" targeting service "kogito-infinispan" within 5 minutes
    And Kafka instance "kogito-kafka" is deployed
    And Install Kafka Kogito Infra "kafka" targeting service "kogito-kafka" within 5 minutes
    And Install Kogito Data Index with 1 replicas with configuration:
      | config | infra | infinispan |
      | config | infra | kafka      |
    And Clone Kogito examples into local directory
    And Local example service "kogito-<runtime>-examples/<example-service>" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence,events |

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

    Then Service "<example-service>" contains 1 instances of process with name "orders"
    And GraphQL request on Data Index service returns 1 instance of process with name "orders" within 2 minutes

    When Scale Kafka instance "kogito-kafka" down
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
    Then Service "<example-service>" contains 2 instances of process with name "orders"
    And Kafka instance "kogito-kafka" has 1 kafka pod running within 2 minutes
    And GraphQL request on Data Index service returns 2 instances of process with name "orders" within 2 minutes

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
    Then Service "<example-service>" contains 3 instances of process with name "orders"
    And GraphQL request on Data Index service returns 3 instances of process with name "orders" within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            |
      | springboot | process-springboot-example |

    @quarkus
    Examples:
      | runtime | example-service         |
      | quarkus | process-quarkus-example |

#####

  @knative
  Scenario: Deploy process-knative-quickstart-quarkus using Kogito Runtime
    Given Install Knative eventing
    And Install Knative eventing KogitoSource
    And Kogito Operator is deployed
    And Deploy Knative Broker "default"
    And Deploy Event display "event-display"
    And Create Knative Trigger "event-display" receiving events from Broker "default" delivering to Service "event-display"
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/process-knative-quickstart-quarkus" is built by Maven and deployed to runtime registry

    When Create quarkus KogitoSource "process-knative-quickstart-quarkus" sinking events to Broker "default" from runtime registry
    And Kogito Runtime "ks-process-knative-quickstart-quarkus" has 1 pods running within 10 minutes
    And HTTP POST request on service "ks-process-knative-quickstart-quarkus" is successful within 2 minutes with path "", headers "ce-specversion=1.0,ce-source=/from/localhost,ce-type=travellers,ce-id=12345" and body:
      """json
      {
      "firstName": "Jan",
      "lastName": "Kowalski",
      "email": "jan.kowalski@example.com",
      "nationality": "Polish"
      }
      """

      Then Deployment "event-display" pods log contains text "Kowalski" within 3 minutes