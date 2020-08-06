@quarkus
Feature: Build kogito-travel-agency images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build kogito-travel-agency images
    Then Local example service "kogito-travel-agency/extended/travels" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "kogito-travel-agency/extended/visas" is built by Maven using profile "default" and deployed to runtime registry

  @native
  Scenario: Build native kogito-travel-agency images
    Then Local example service "kogito-travel-agency/extended/travels" is built by Maven using profile "native" and deployed to runtime registry
    And Local example service "kogito-travel-agency/extended/visas" is built by Maven using profile "native" and deployed to runtime registry
