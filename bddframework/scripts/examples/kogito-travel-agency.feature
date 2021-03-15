@quarkus
Feature: Build kogito-travel-agency images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build kogito-travel-agency <example-name> image with native <native>
    Then Local example service "kogito-travel-agency/extended/<example-name>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    Examples:
      | example-name | native   |
      | visas        | disabled |
      | travels      | disabled |

    @native
    Examples:
      | example-name | native   |
      | visas        | enabled |
      | travels      | enabled |
