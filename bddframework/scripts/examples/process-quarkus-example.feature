@quarkus
Feature: Build process-quarkus-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build process-quarkus-example image with native <native>
    Then Local example service "process-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | native | <native> |

    @rhpam
    Examples:
      | native  |
      | disabled |

    @native
    Examples:
      | native  |
      | enabled |

  Scenario Outline: Build native process-quarkus-example image with profile <profile> and native <native>
    Then Local example service "process-quarkus-example" is built by Maven and deployed to runtime registry with Maven configuration:
      | profile | <profile> |
      | native  | <native>  |

    Examples:
      | profile            | native  |
      | persistence        | disabled |
      | persistence,events | disabled |

    @native
    Examples:
      | profile            | native  |
      | persistence        | enabled |
      | persistence,events | enabled |
