@discovery
Feature: Discovery with onboarding

  Background:
    Given Namespace is created

  @quarkus
  Scenario Outline: Deploy Quarkus onboarding example with Maven profile <profile>
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "onboarding-example/hr" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Local example service "onboarding-example/payroll" is built by Maven using profile "<profile>" and deployed to runtime registry
    And Local example service "onboarding-example/onboarding-quarkus" is built by Maven using profile "<profile>" and deployed to runtime registry

    When Deploy quarkus example service "hr" from runtime registry with configuration:
      | service-label  | department/first          | process  |
      | service-label  | id                        | process  |
      | service-label  | employee-validation/first | process  |
    And Deploy quarkus example service "payroll" from runtime registry with configuration:
      | service-label  | taxes/rate     | process  |
      | service-label  | vacations/days | process  |
      | service-label  | payments/date  | process  |
    And Deploy quarkus example service "onboarding-quarkus" from runtime registry with configuration:
      | service-label  | onboarding | process |

    And Kogito Runtime "hr" has 1 pods running within 10 minutes
    And Kogito Runtime "payroll" has 1 pods running within 10 minutes
    And Kogito Runtime "onboarding-quarkus" has 1 pods running within 10 minutes

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
      | profile |
      | default |

    @native
    Examples:
      | profile |
      | native  |

#####

  @springboot
  Scenario: Deploy Spring Boot onboarding example
    Given Kogito Operator is deployed
    And Clone Kogito examples into local directory
    And Local example service "onboarding-example/hr" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "onboarding-example/payroll" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "onboarding-example/onboarding-springboot" is built by Maven using profile "default" and deployed to runtime registry
    
    When Deploy quarkus example service "hr" from runtime registry with configuration:
      | service-label  | department/first          | process  |
      | service-label  | id                        | process  |
      | service-label  | employee-validation/first | process  |
    And Deploy quarkus example service "payroll" from runtime registry with configuration:
      | service-label  | taxes/rate     | process  |
      | service-label  | vacations/days | process  |
      | service-label  | payments/date  | process  |
    And Deploy springboot example service "onboarding-springboot" from runtime registry with configuration:
      | service-label  | onboarding | process  |

    And Kogito Runtime "hr" has 1 pods running within 10 minutes
    And Kogito Runtime "payroll" has 1 pods running within 10 minutes
    And Kogito Runtime "onboarding-springboot" has 1 pods running within 10 minutes

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
