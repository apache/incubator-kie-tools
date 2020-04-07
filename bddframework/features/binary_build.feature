@binary
Feature: Use binary build to deploy the service

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @springboot
  # process-springboot-example is used here as it is the quickest example to be built
  Scenario: Deploy process-springboot-example using binary build
    Given Clone Kogito examples into local directory
    And Local example service "process-springboot-example" is built by Maven

    When Create service "process-springboot-example"
    And BuildConfig "process-springboot-example-binary" is created after 1 minutes
    And Start build with name "process-springboot-example-binary" from local example service path "process-springboot-example/target"

    Then Kogito application "process-springboot-example" has 1 pods running within 5 minutes
    And HTTP GET request on service "process-springboot-example" with path "orders" is successful within 2 minutes
