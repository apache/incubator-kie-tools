@quarkus
Feature: Build dmn-tracing-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build dmn-tracing-quarkus images
    Then Local example service "dmn-tracing-quarkus" is built by Maven using profile "default" and deployed to runtime registry