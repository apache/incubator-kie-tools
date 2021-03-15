@quarkus
Feature: Build process-springboot-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build process-springboot-example image
    Then Local example service "process-springboot-example" is built by Maven and deployed to runtime registry

  Scenario Outline: Build native process-quarkus-example image with profile <profile>
    Then Local example service "process-springboot-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | <profile> |

    Examples:
      | profile            |
      | persistence        |
      | persistence,events |