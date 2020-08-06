Feature: Build onboarding-example images

  Background:
    Given Clone Kogito examples into local directory

  @quarkus
  Scenario: Build onboarding-example quarkus images
    Then Local example service "onboarding-example/hr" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "onboarding-example/payroll" is built by Maven using profile "default" and deployed to runtime registry
    And Local example service "onboarding-example/onboarding-quarkus" is built by Maven using profile "default" and deployed to runtime registry

  @springboot
  Scenario: Build onboarding-example springboot images
    Then Local example service "onboarding-example/onboarding-springboot" is built by Maven using profile "default" and deployed to runtime registry

  @quarkus
  @native
  Scenario: Build native onboarding-example quarkus images
    Then Local example service "onboarding-example/hr" is built by Maven using profile "native" and deployed to runtime registry
    And Local example service "onboarding-example/payroll" is built by Maven using profile "native" and deployed to runtime registry
    And Local example service "onboarding-example/onboarding-quarkus" is built by Maven using profile "native" and deployed to runtime registry

