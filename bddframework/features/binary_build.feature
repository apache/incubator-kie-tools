@binary
Feature: Use binary build to deploy the service

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @smoke
  @springboot
  Scenario: Deploy process-springboot-example using binary build
    Given Clone Kogito examples into local directory
    And Local example service "process-springboot-example" is built by Maven

    When Create springboot service "process-springboot-example"
    And BuildConfig "process-springboot-example-binary" is created after 1 minutes
    And Start build with name "process-springboot-example-binary" from local example service path "process-springboot-example/target"

    Then Kogito application "process-springboot-example" has 1 pods running within 5 minutes
    And Service "process-springboot-example" with process name "orders" is available within 2 minutes

#####

  @quarkus
  Scenario Outline: Deploy process-quarkus-example using binary build with native <native>
    Given Clone Kogito examples into local directory
    And Local example service "process-quarkus-example" is built by Maven using profile "<profile>"

    When Create quarkus service "process-quarkus-example" with configuration:
      | config | native | <native> |
    And BuildConfig "process-quarkus-example-binary" is created after 1 minutes
    And Start build with name "process-quarkus-example-binary" from local example service path "process-quarkus-example/target"

    Then Kogito application "process-quarkus-example" has 1 pods running within 5 minutes
    And Service "process-quarkus-example" with process name "orders" is available within 2 minutes

  Examples:
      | profile | native   |
      | default | disabled |
  
  @native
  Examples:
      | profile | native   |
      | native  | enabled  |