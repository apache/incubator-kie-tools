@quarkus
Feature: Build dmn-quarkus-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build dmn-quarkus-example image with native <native>
    Then Local example service "dmn-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    @rhpam
    Examples:
      | native  |
      | disabled |

    @native
    Examples:
      | native  |
      | enabled |
