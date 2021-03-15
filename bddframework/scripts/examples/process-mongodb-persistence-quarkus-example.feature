@quarkus
Feature: Build process-mongodb-persistence-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build native process-mongodb-persistence-quarkus image with native <native>
    Then Local example service "process-mongodb-persistence-quarkus" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |
    
    Examples:
      | native  |
      | disabled |

    @native
    Examples:
      | native  |
      | enabled |
