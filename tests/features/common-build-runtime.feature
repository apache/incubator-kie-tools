@quay.io/kiegroup/kogito-builder @quay.io/kiegroup/kogito-runtime-native @quay.io/kiegroup/kogito-runtime-jvm
Feature: Common tests for Kogito builder and runtime images

  Scenario: Verify if usage help is correctly called
    When container is started with command /home/kogito/kogito-app-launch.sh -h
    Then container log should contain This is the
