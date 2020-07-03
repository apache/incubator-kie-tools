@springboot
Feature: Build process-springboot-example images

  Scenario: Build process-springboot-example images
    Given Clone Kogito examples into local directory

    Then Local example service "process-springboot-example" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "process-springboot-example" is built by Maven using profile "persistence" and deployed to runtime registry
    And Local example service "process-springboot-example" is built by Maven using profile "persistence,events" and deployed to runtime registry
