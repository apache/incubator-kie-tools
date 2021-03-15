@quarkus
Feature: Build dmn-tracing-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build dmn-tracing-quarkus image
    Then Local example service "dmn-tracing-quarkus" is built by Maven and deployed to runtime registry