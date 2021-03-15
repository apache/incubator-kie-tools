Feature: Build onboarding-example images

  Background:
    Given Clone Kogito examples into local directory

  
  Scenario Outline: Build onboarding-example <example-name> image with native <native>
    Then Local example service "onboarding-example/<example-name>" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    @quarkus
    Examples:
      | example-name       | native   |
      | hr                 | disabled |
      | payroll            | disabled |
      | onboarding-quarkus | disabled |
    
    @springboot
    Examples:
      | example-name          | native   |
      | onboarding-springboot | disabled |

    @native
    @quarkus
    Examples:
      | example-name       | native  |
      | hr                 | enabled |
      | payroll            | enabled |
      | onboarding-quarkus | enabled |