@quarkus
Feature: Build process-mongodb-persistence-quarkus images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build process-mongodb-persistence-quarkus images
    Then Local example service "process-mongodb-persistence-quarkus" is built by Maven using profile "default" and deployed to runtime registry

  @native
  Scenario: Build native process-mongodb-persistence-quarkus images
    Then Local example service "process-mongodb-persistence-quarkus" is built by Maven using profile "native" and deployed to runtime registry
