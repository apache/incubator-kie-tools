@quarkus
Feature: Build ruleunit-quarkus-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build ruleunit-quarkus-example images
    Then Local example service "ruleunit-quarkus-example" is built by Maven using profile "default" and deployed to runtime registry