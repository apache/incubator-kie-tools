@resources
Feature: Deploy the service by configuring the resource requests and limits

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @runtimerequests 
  @runtimelimits
  Scenario Outline: Setting runtime resource "requests" and "limits"
    Given Clone Kogito examples into local directory
    And Local example service "ruleunit-quarkus-example" is built by Maven

    When Create service "ruleunit-quarkus-example" with configuration:
      | runtime-request | cpu    | <runtime-cpu-request>    |
      | runtime-request | memory | <runtime-memory-request> |
      | runtime-limit   | cpu    | <runtime-cpu-limit>      |
      | runtime-limit   | memory | <runtime-memory-limit>   |
    And BuildConfig "ruleunit-quarkus-example-binary" is created after 1 minutes
    And Start build with name "ruleunit-quarkus-example-binary" from local example service path "ruleunit-quarkus-example/target"

    Then Kogito application "ruleunit-quarkus-example" has 1 pods running within 10 minutes
    And Kogito application "ruleunit-quarkus-example" has pods with runtime resources within 2 minutes:
      | runtime-request | cpu    | <runtime-cpu-request>    |
      | runtime-request | memory | <runtime-memory-request> |
      | runtime-limit   | cpu    | <runtime-cpu-limit>      |
      | runtime-limit   | memory | <runtime-memory-limit>   |

    Examples: Requests and Limits
      | runtime-cpu-request | runtime-memory-request | runtime-cpu-limit | runtime-memory-limit |
      | 500m                | 1Gi                    | 1000m             | 2Gi                  |


  @buildresources 
  @buildlimits 
  Scenario Outline: Setting build resource "requests" and "limits"
    When Deploy quarkus example service "ruleunit-quarkus-example" with configuration:
      | build-request | cpu    | <build-cpu-request>    |
      | build-request | memory | <build-memory-request> |
      | build-limit   | cpu    | <build-cpu-limit>      |
      | build-limit   | memory | <build-memory-limit>   |
    Then BuildConfig "ruleunit-quarkus-example-builder" is created with build resources within 2 minutes:
      | build-request | cpu    | <build-cpu-request>    |
      | build-request | memory | <build-memory-request> |
      | build-limit   | cpu    | <build-cpu-limit>      |
      | build-limit   | memory | <build-memory-limit>   |

    Examples: Requests and Limits
      | build-cpu-request | build-memory-request | build-cpu-limit | build-memory-limit |
      | 500m              | 1Gi                  | 1000m           | 2Gi                |