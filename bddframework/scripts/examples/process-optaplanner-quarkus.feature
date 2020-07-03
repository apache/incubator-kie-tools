@quarkus
Feature: Build process-optaplanner-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build process-optaplanner-quarkus images
    Then Local example service "process-optaplanner-quarkus" is built by Maven using profile "default" and deployed to runtime registry

  @native
  Scenario: Build native process-optaplanner-quarkus images
    Then Local example service "process-optaplanner-quarkus" is built by Maven using profile "native" and deployed to runtime registry
