@quarkus
Feature: Build process-timer-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build native process-timer-quarkus image with native <native>
    Then Local example service "process-timer-quarkus" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |
    
    Examples:
      | native  |
      | disabled |

    @native
    Examples:
      | native  |
      | enabled |
