@quarkus
Feature: Build process-optaplanner-quarkus images

  Background:
    Given Clone Kogito examples into local directory
    
  Scenario Outline: Build native process-optaplanner-quarkus image with native <native>
    Then Local example service "process-optaplanner-quarkus" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    @rhpam
    Examples:
      | native  |
      | disabled |

    # Disabled due to https://issues.redhat.com/browse/PLANNER-2084
    @disabled
    @native
    Examples:
      | native  |
      | enabled |