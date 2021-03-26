@rhpam
@springboot
Feature: Build dmn-springboot-example images

  Background:
    Given Clone Kogito examples into local directory

  Scenario Outline: Build dmn-springboot-example image
    Then Local example service "dmn-springboot-example" is built by Maven and deployed to runtime registry
