@springboot
Feature: Build process-timer-springboot images

  Background:
    Given Clone Kogito examples into local directory

  Scenario: Build process-timer-springboot image
    Then Local example service "process-timer-springboot" is built by Maven and deployed to runtime registry
