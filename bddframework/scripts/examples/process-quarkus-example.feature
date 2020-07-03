@quarkus
Feature: Build process-quarkus-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build process-quarkus-example images
    Then Local example service "process-quarkus-example" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "process-quarkus-example" is built by Maven using profile "persistence" and deployed to runtime registry
    And Local example service "process-quarkus-example" is built by Maven using profile "persistence,events" and deployed to runtime registry

  @native
  Scenario: Build native process-quarkus-example images
    Then Local example service "process-quarkus-example" is built by Maven using profile "native" and deployed to runtime registry
    And Local example service "process-quarkus-example" is built by Maven using profile "native,persistence" and deployed to runtime registry
    And Local example service "process-quarkus-example" is built by Maven using profile "native,persistence,events" and deployed to runtime registry
