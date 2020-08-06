# Disabled until image metadata label processing is fixed in OCP 4.x or https://issues.redhat.com/browse/KOGITO-731 is implemented
@disabled
Feature: Service Deployment: Prometheus

  Background:
    Given Namespace is created 
    And Kogito Operator is deployed
    And Prometheus Operator is deployed

  Scenario: Deploy hr service and verify that it successfully connects to Prometheus
    Given Prometheus instance is deployed, monitoring services with label name "app" and value "hr"
    And Clone Kogito examples into local directory
    And Local example service "onboarding-example/hr" is built by Maven using profile "default" and deployed to runtime registry
    And Deploy quarkus example service "hr" from runtime registry with configuration:
      | config | enablePersistence | disabled |
    And Kogito Runtime "hr" has 1 pods running within 10 minutes

    When Start "id" process on service "hr" within 2 minutes with body:
      """
      {
        "employee" : {
          "firstName" : "Mark", 
          "lastName" : "Test", 
          "personalId" : "xxx-yy-zzz", 
          "birthDate" : "2012-12-10T14:50:12.123+02:00", 
          "address" : {
            "country" : "US", 
            "city" : "Boston", 
            "street" : "any street 3", 
            "zipCode" : "10001"
          }
        }
      }
      """

    Then HTTP GET request on service "prometheus-operated" with path "/api/v1/query?query=drl_match_fired_nanosecond_count" should contain a string "Assign Employee ID" within 3 minutes