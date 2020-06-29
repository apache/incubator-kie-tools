# Disabled until CLI (used in PR check) supports KogitoRuntime and KogitoBuild - KOGITO-2064
@disabled
Feature: Deploy Kogito Build

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  Scenario Outline: Build <runtime> remote S2I with native <native> using KogitoBuild
    When Build <runtime> example service "<example-service>" with configuration:
      | config | native | <native> |

    Then Build "<example-service>-builder" is complete after <minutes> minutes
    And Build "<example-service>" is complete after 5 minutes
    And Deploy runtime <runtime> example service "<example-service>" with configuration:
      | infinispan | useKogitoInfra | disabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 5 minutes
    And Service "<example-service>" with process name "orders" is available within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | native   | minutes |
      | springboot | process-springboot-example | disabled | 10      |

    @smoke
    @quarkus
    Examples:
      | runtime    | example-service         | native   | minutes |
      | quarkus    | process-quarkus-example | disabled | 10      |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native  | minutes |
      | quarkus    | process-quarkus-example | enabled | 20      |

#####

  Scenario Outline: Build <runtime> binary build with native <native> using KogitoBuild
    Given Clone Kogito examples into local directory
    And Local example service "<example-service>" is built by Maven using profile "<profile>"

    When Build binary <runtime> service "<example-service>" with configuration:
      | config | native | <native> |

    Then BuildConfig "<example-service>" is created after 1 minutes
    # Once https://issues.redhat.com/browse/KOGITO-2161 is implemented then we need to refactor this scenario to CLI implementation (getting rid of manual build trigger)
    And Start build with name "<example-service>" from local example service path "<example-service>/target"
    And Deploy runtime <runtime> example service "<example-service>" with configuration:
      | infinispan | useKogitoInfra | disabled |
    And Kogito Runtime "<example-service>" has 1 pods running within 5 minutes
    And Service "<example-service>" with process name "orders" is available within 2 minutes

    @springboot
    Examples:
      | runtime    | example-service            | native   | profile |
      | springboot | process-springboot-example | disabled | default |

    @quarkus
    Examples:
      | runtime    | example-service         | native   | profile |
      | quarkus    | process-quarkus-example | disabled | default |

    @quarkus
    @native
    Examples:
      | runtime    | example-service         | native  | profile |
      | quarkus    | process-quarkus-example | enabled | native  |

#####

  Scenario Outline: Build <runtime> local source build with native <native> using KogitoBuild
    Given Clone Kogito examples into local directory

    When Build <runtime> example service "process-scripts-quarkus" from local file with configuration:
      | config | native | <native> |

    Then BuildConfig "process-scripts-quarkus-builder" is created after 1 minutes
    # Once https://issues.redhat.com/browse/KOGITO-2161 is implemented then we need to refactor this scenario to CLI implementation (getting rid of manual build trigger)
    And Start build with name "process-scripts-quarkus-builder" from local example service file "process-scripts-quarkus/src/main/resources/org/acme/travels/scripts.bpmn"
    And Build "process-scripts-quarkus-builder" is complete after <minutes> minutes
    And Build "process-scripts-quarkus" is complete after 5 minutes
    And Deploy runtime <runtime> example service "process-scripts-quarkus" with configuration:
      | infinispan | useKogitoInfra | disabled |
    And Kogito Runtime "process-scripts-quarkus" has 1 pods running within 5 minutes
    And Start "scripts" process on service "process-scripts-quarkus" within 2 minutes with body:
      """json
      {
          "name" : "john"
      }
      """

    @springboot
    Examples:
      | runtime    | native   | minutes |
      | springboot | disabled | 10      |

    @quarkus
    Examples:
      | runtime    | native   | minutes |
      | quarkus    | disabled | 10      |

    @quarkus
    @native
    Examples:
      | runtime    | native  | minutes |
      | quarkus    | enabled | 20      |
