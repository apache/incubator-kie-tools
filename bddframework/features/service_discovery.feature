@discovery
Feature: Discovery with onboarding

  Background:
    Given Namespace is created

  @quarkus
  Scenario Outline: Deploy Quarkus onboarding example with native <native>
    Given Kogito Operator is deployed
    
    When Deploy quarkus example service "onboarding-example/hr" with configuration:
      | config | native                    | <native> |
      | label  | department/first          | process  |
      | label  | id                        | process  |
      | label  | employee-validation/first | process  |

    And Deploy quarkus example service "onboarding-example/payroll" with configuration:
      | config | native         | <native> |
      | label  | taxes/rate     | process  |
      | label  | vacations/days | process  |
      | label  | payments/date  | process  |

    And Deploy quarkus example service "onboarding-example/onboarding-quarkus" with configuration:
      | config        | native     | <native>                |
      | label         | onboarding | process                 |
      | build-request | cpu        | <build-request-cpu>     |
      | build-request | memory     | <build-request-memory>  |
      | build-limit   | cpu        | <build-limit-cpu>       |

    And Kogito application "hr" has 1 pods running within <minutes> minutes
    And Kogito application "payroll" has 1 pods running within <minutes> minutes
    And Kogito application "onboarding-quarkus" has 1 pods running within <minutes> minutes

    Then Start "onboarding" process on service "onboarding-quarkus" within 2 minutes with body:
      """json
      { 
        "employee" : {
          "firstName" : "Mark", 
          "lastName" : "Test", 
          "personalId" : "xxx-yy-zzz", 
          "birthDate" : "1995-12-10T14:50:12.123+02:00", 
          "address" : {
            "country" : "US", 
            "city" : "Boston", 
            "street" : "any street 3", 
            "zipCode" : "10001"
          }
        }
      }
      """
    
    Examples:
      | native   | minutes | build-request-cpu | build-limit-cpu | build-request-memory |
      | disabled | 10      | 1                 | 4               | 4Gi                  |

    @native
    Examples:
      | native   | minutes | build-request-cpu | build-limit-cpu | build-request-memory |
      | enabled  | 20      | 4                 | 8               | 10Gi                 |

#####

  @springboot
  Scenario: Deploy Spring Boot onboarding example
    Given Kogito Operator is deployed
    
    When Deploy quarkus example service "onboarding-example/hr" with configuration:
      | config | native                    | disabled |
      | label  | department/first          | process  |
      | label  | id                        | process  |
      | label  | employee-validation/first | process  |

    And Deploy quarkus example service "onboarding-example/payroll" with configuration:
      | config | native         | disabled |
      | label  | taxes/rate     | process  |
      | label  | vacations/days | process  |
      | label  | payments/date  | process  |

    And Deploy springboot example service "onboarding-example/onboarding-springboot" with configuration:
      | config | native     | disabled |
      | label  | onboarding | process  |

    And Kogito application "hr" has 1 pods running within 10 minutes
    And Kogito application "payroll" has 1 pods running within 10 minutes
    And Kogito application "onboarding-springboot" has 1 pods running within 10 minutes

    Then Start "onboarding" process on service "onboarding-springboot" within 2 minutes with body:
      """json
      { 
        "employee" : {
          "firstName" : "Mark", 
          "lastName" : "Test", 
          "personalId" : "xxx-yy-zzz", 
          "birthDate" : "1995-12-10T14:50:12.123+02:00", 
          "address" : {
            "country" : "US", 
            "city" : "Boston", 
            "street" : "any street 3", 
            "zipCode" : "10001"
          }
        }
      }
      """
