@rhpam
Feature: Service Deployment: Grafana

  Background:
    Given Namespace is created 
    And Kogito Operator is deployed
    And Grafana Operator is deployed

  # Testing just simple Grafana connection without Prometheus as I didn't figure out how to test that the dasboard is actually filled with proper data
  # Integration with Grafana and Prometheus together was tested manually.
  @smoke
  Scenario: Deploy dmn-drools-quarkus-metrics service and verify that it successfully exports dashboard to Grafana
    Given Grafana instance is deployed, monitoring services with label name "app" and value "dmn-drools-quarkus-metrics"
    And Clone Kogito examples into local directory
    And Local example service "dmn-drools-quarkus-metrics" is built by Maven and deployed to runtime registry
    And Deploy quarkus example service "dmn-drools-quarkus-metrics" from runtime registry
    And Kogito Runtime "dmn-drools-quarkus-metrics" has 1 pods running within 10 minutes

    When HTTP POST request on service "dmn-drools-quarkus-metrics" is successful within 2 minutes with path "hello" and body:
      """json
      {
        "strings":["world"]
      }
      """

    Then HTTP GET request on service "grafana-service" with path "api/search" should contain a string "hello - Operational Dashboard" within 3 minutes