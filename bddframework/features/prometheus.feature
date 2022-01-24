@rhpam
@metrics
@prometheus
Feature: Service Deployment: Prometheus

  Background:
    Given Namespace is created 
    And Kogito Operator is deployed
    And Prometheus Operator is deployed

  @smoke
  Scenario: Deploy dmn-drools-quarkus-metrics service and verify that it successfully connects to Prometheus
    Given Prometheus instance is deployed, monitoring services with label name "app" and value "dmn-drools-quarkus-metrics"
    And Clone Kogito examples into local directory
    And Local example service "kogito-quarkus-examples/dmn-drools-quarkus-metrics" is built by Maven and deployed to runtime registry
    And Deploy quarkus example service "dmn-drools-quarkus-metrics" from runtime registry
    And Kogito Runtime "dmn-drools-quarkus-metrics" has 1 pods running within 10 minutes

    When HTTP POST request on service "dmn-drools-quarkus-metrics" is successful within 2 minutes with path "hello" and body:
      """json
      {
        "strings":["world"]
      }
      """

    Then HTTP GET request on service "prometheus-operated" with path "/api/v1/query?query=api_execution_elapsed_seconds" should contain a string "hello" within 3 minutes