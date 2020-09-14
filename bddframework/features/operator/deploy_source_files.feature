@cli
Feature: Deploy source files (dmn, drl, bpmn, bpmn2, ...) with CLI

  Background:
    Given Namespace is created
    And Kogito Operator is deployed

  @smoke
  Scenario: Deploy .dmn source files with CLI using runtime <runtime>
    Given Clone Kogito examples into local directory
    
    When Deploy <runtime> file "Traffic Violation.dmn" from example service "dmn-<runtime>-example"
    And Build "dmn-<runtime>-example-builder" is complete after 10 minutes

    Then HTTP POST request on service "dmn-<runtime>-example" is successful within 2 minutes with path "Traffic Violation" and body:
      """json
      {
          "Driver":{"Points":2},
          "Violation":{
              "Type":"speed",
              "Actual Speed":120,
              "Speed Limit":100
          }
      }
      """

    @springboot
    Examples:
      | runtime    |
      | springboot |

    @quarkus
    Examples:
      | runtime    |
      | quarkus    |
  
  Scenario: Deploy .bpmn source files with CLI
    Given Clone Kogito examples into local directory
    
    When Deploy quarkus file "org/acme/travels/scripts.bpmn" from example service "process-scripts-quarkus"
    And Build "process-scripts-quarkus-builder" is complete after 10 minutes

    Then HTTP POST request on service "process-scripts-quarkus" is successful within 2 minutes with path "scripts" and body:
      """json
      {
          "name" : "john"
      }
      """

  Scenario: Deploy source files in folder with CLI
    Given Clone Kogito examples into local directory
    
    When Deploy quarkus folder from example service "process-timer-quarkus"
    And Build "process-timer-quarkus-builder" is complete after 10 minutes

    Then HTTP POST request on service "process-timer-quarkus" is successful within 2 minutes with path "timers" and body:
      """json
      {
          "delay" : "PT30S"
      }
      """
