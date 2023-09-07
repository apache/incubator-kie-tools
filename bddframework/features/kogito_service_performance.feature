# Commented code will be addressed by further enhancements:
# https://issues.redhat.com/browse/KOGITO-1701
# https://issues.redhat.com/browse/KOGITO-1888

@performance
Feature: Kogito Service Performance

  Background:
    Given Namespace is created

  @quarkus
  Scenario Outline: Quarkus Kogito Service Performance with native disabled, without persistence with usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/process-quarkus-example" is built by Maven and deployed to runtime registry
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
       | runtime-env | JAVA_OPTIONS | -Xmx10G  |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-hotspot
      agents:
      - driver01
      - driver02
      http:
        host: http://process-quarkus-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            startAfter: rampUp
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      # Ramp up necessary for JVM workloads to be fully optimised
      #
      - rampUp:
          increasingRate:
            initialUsersPerSec: 1
            targetUsersPerSec: 100
            maxSessions: 110
            duration: 1m
            isWarmup: true
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-hotspot" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "quarkus-hotspot.json"

    #Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |

#####

  @quarkus
  @native
  Scenario Outline: Quarkus Kogito Service Performance with native enabled, without persistence, with usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/process-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | enabled |
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
       | runtime-env | JAVA_OPTIONS | -Xmx10G  |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-native
      agents:
      - driver01
      - driver02
      http:
        host: http://process-quarkus-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-native" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "quarkus-native.json"

    #Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |

#####

  @quarkus
  @persistence
  @infinispan
  Scenario Outline: Quarkus Kogito Service Performance with native disabled, with persistence, usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Infinispan instance "external-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" targeting service "external-infinispan" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/process-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence |
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G             |
      | config      | infra        | external-infinispan |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-hotspot
      agents:
      - driver01
      - driver02
      http:
        host: http://process-quarkus-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            startAfter: rampUp
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      # Ramp up necessary for JVM workloads to be fully optimised
      #
      - rampUp:
          increasingRate:
            initialUsersPerSec: 1
            targetUsersPerSec: 100
            maxSessions: 110
            duration: 1m
            isWarmup: true
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-hotspot" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "quarkus-infinispan-hotspot.json"

    #Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |

#####

  @quarkus
  @native
  @persistence
  @infinispan
  Scenario Outline: Quarkus Kogito Service Performance with native enabled, with persistence, usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Infinispan instance "external-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" targeting service "external-infinispan" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/process-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | persistence |
      | native  | enabled     |
    And Deploy quarkus example service "process-quarkus-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G             |
      | config      | infra        | external-infinispan |
    And Kogito Runtime "process-quarkus-example" has 1 pods running within 10 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-native
      agents:
      - driver01
      - driver02
      http:
        host: http://process-quarkus-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-native" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "quarkus-infinispan-native.json"

    #Then Service "process-quarkus-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-quarkus-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |

#####

  @springboot
  Scenario Outline: Spring Boot Kogito Service Performance without persistence, with usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "kogito-springboot-examples/process-springboot-example" is built by Maven and deployed to runtime registry
    And Deploy springboot example service "process-springboot-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G |
    And Kogito Runtime "process-springboot-example" has 1 pods running within 10 minutes
    And Service "process-springboot-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-hotspot
      agents:
      - driver01
      - driver02
      http:
        host: http://process-springboot-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            startAfter: rampUp
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      # Ramp up necessary for JVM workloads to be fully optimised
      #
      - rampUp:
          increasingRate:
            initialUsersPerSec: 1
            targetUsersPerSec: 100
            maxSessions: 110
            duration: 1m
            isWarmup: true
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-hotspot" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "springboot-hotspot.json"

    #Then Service "process-springboot-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-springboot-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |

#####

  @springboot
  @persistence
  @infinispan
  Scenario Outline: Spring Boot Kogito Service Performance with persistence, usersPerSec <usersPerSec> and maxSessions <maxSessions>
    Given Kogito Operator is deployed
    And Infinispan Operator is deployed
    And Infinispan instance "external-infinispan" is deployed for performance within 5 minute(s) with configuration:
      | username | developer |
      | password | mypass    |
    And Install Infinispan Kogito Infra "external-infinispan" targeting service "external-infinispan" within 5 minutes
    And Clone Kogito examples into local directory
    And Local example service "kogito-springboot-examples/process-springboot-example" is built by Maven and deployed to runtime registry
    And Deploy springboot example service "process-springboot-example" from runtime registry with configuration:
      | runtime-env | JAVA_OPTIONS | -Xmx10G             |
      | config      | infra        | external-infinispan |
    And Kogito Runtime "process-springboot-example" has 1 pods running within 10 minutes
    And Service "process-springboot-example" with process name "orders" is available within 3 minutes
    And Hyperfoil Node scraper is deployed
    And Hyperfoil Operator is deployed
    And Hyperfoil instance "hf-controller" is deployed within 5 minutes
    And Create benchmark on Hyperfoil instance "hf-controller" within 2 minutes with content:
      """yaml
      # Kogito PoC tests
      name: PoC-test-hotspot
      agents:
      - driver01
      - driver02
      http:
        host: http://process-springboot-example:80
        sharedConnections: 30
      phases:
      - steadyState:
          constantRate:
            usersPerSec: <usersPerSec>
            maxSessions: <maxSessions>
            startAfter: rampUp
            duration: 5m
            maxDuration: 6m
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      # Ramp up necessary for JVM workloads to be fully optimised
      #
      - rampUp:
          increasingRate:
            initialUsersPerSec: 1
            targetUsersPerSec: 100
            maxSessions: 110
            duration: 1m
            isWarmup: true
            scenario:
            - jsonBody:
              - httpRequest:
                  POST: /orders
                  headers:
                    content-type: application/json
                  body: |
                    {
                      "approver" : "john",
                      "order" : {
                        "orderNumber" : "12345",
                        "shipped" : false
                      }
                    }
      """

    When Start benchmark "PoC-test-hotspot" on Hyperfoil instance "hf-controller" within 1 minutes

    Then Benchmark run on Hyperfoil instance "hf-controller" finished within 15 minutes
    And Store benchmark statistics of Hyperfoil instance "hf-controller" as "springboot-infinispan-hotspot.json"

    #Then Service "process-springboot-example" contains <requests> instances of process with name "orders" within 1 minutes
    #And Service "process-springboot-example" contains <requests> instances of process with name "orderItems" within 1 minutes
    #And All human tasks on path "orderItems" with path task name "Verify_order" are successfully "completed" with timing "true"

    Examples:
      | usersPerSec | maxSessions |
      | 100         | 110         |
