@quarkus
Feature: Build dmn-drools-quarkus-metrics images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build dmn-drools-quarkus-metrics images
    Then Local example service "dmn-drools-quarkus-metrics" is built by Maven using profile "default" and deployed to runtime registry