@discovery
Feature: Discovery with onboarding

  Background:
    Given Namespace is created

  Scenario Outline: Deploy onboarding example
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

    And Deploy quarkus example service "onboarding-example/onboarding" with configuration:
      | config        | native     | <native>                |
      | label         | onboarding | process                 |
      | build-request | cpu        | <build-request-cpu>     |
      | build-request | memory     | <build-request-memory>  |
      | build-limit   | cpu        | <build-limit-cpu>       |

    And Kogito application "hr" has 1 pods running within <minutes> minutes
    And Kogito application "payroll" has 1 pods running within <minutes> minutes
    And Kogito application "onboarding" has 1 pods running within <minutes> minutes

    Then HTTP POST request on service "onboarding" is successful within 2 minutes with path "onboarding" and body:
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
    
    Examples: Non Native
      | native   | minutes | build-request-cpu | build-limit-cpu | build-request-memory |
      | disabled | 10      | 1                 | 4               | 4Gi                  |

    @native
    Examples: Native
      | native   | minutes | build-request-cpu | build-limit-cpu | build-request-memory |
      | enabled  | 20      | 4                 | 8               | 10Gi                 |