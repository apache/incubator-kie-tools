@quarkus
Feature: Build ruleunit-quarkus-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build ruleunit-quarkus-example image
    Then Local example service "ruleunit-quarkus-example" is built by Maven and deployed to runtime registry