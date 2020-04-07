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

    When Create service "ruleunit-quarkus-example" with runtime resources:
      | requests  | <requests> |
      | limits    | <limits>   |
    And BuildConfig "ruleunit-quarkus-example-binary" is created after 1 minutes
    And Start build with name "ruleunit-quarkus-example-binary" from local example service path "ruleunit-quarkus-example/target"

    Then Kogito application "ruleunit-quarkus-example" has 1 pods running within 10 minutes
    And Kogito application "ruleunit-quarkus-example" has pods with runtime resources within 2 minutes:
      | requests  | <requests> |
      | limits    | <limits>   |

    Examples: Requests and Limits
      | requests             | limits                |
      | cpu=500m,memory=1Gi  | cpu=1000m,memory=2Gi  |


  @buildresources 
  @buildlimits 
  Scenario Outline: Setting build resource "requests" and "limits"
    When Deploy quarkus example service "ruleunit-quarkus-example" with build resources:
      | requests  | <requests> |
      | limits    | <limits>   |
    Then BuildConfig "ruleunit-quarkus-example-builder" is created with build resources within 2 minutes:
      | requests  | <requests> |
      | limits    | <limits>   |

    Examples: Requests and Limits
      | requests             | limits                |
      | cpu=500m,memory=1Gi  | cpu=1000m,memory=2Gi  |