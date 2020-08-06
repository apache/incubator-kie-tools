@quarkus
Feature: Build process-timer-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build process-timer-quarkus images
    Then Local example service "process-timer-quarkus" is built by Maven using profile "default" and deployed to runtime registry

  @native
  Scenario: Build native process-timer-quarkus images
    Then Local example service "process-timer-quarkus" is built by Maven using profile "native" and deployed to runtime registry
