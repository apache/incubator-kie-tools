@binary
Feature: Use binary build to deploy the service

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @springboot
  # jbpm-springboot-example is used here as it is the quickest example to be built
  Scenario: Deploy jbpm-springboot-example using binary build
    Given Clone Kogito examples into local directory
    And Local example service "jbpm-springboot-example" is built by Maven

    When Create service "jbpm-springboot-example"
    And BuildConfig "jbpm-springboot-example-binary" is created after 1 minutes
    And Start build with name "jbpm-springboot-example-binary" from local example service path "jbpm-springboot-example/target"

    Then Kogito application "jbpm-springboot-example" has 1 pods running within 5 minutes
    And HTTP GET request on service "jbpm-springboot-example" with path "orders" is successful within 2 minutes
