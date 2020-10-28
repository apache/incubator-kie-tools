@quay.io/kiegroup/kogito-springboot-ubi8-s2i @quay.io/kiegroup/kogito-springboot-ubi8 @quay.io/kiegroup/kogito-quarkus-ubi8-s2i @quay.io/kiegroup/kogito-quarkus-ubi8 @quay.io/kiegroup/kogito-quarkus-jvm-ubi8
Feature: Common tests for Kogito builder and runtime images

  Scenario: Verify if usage help is correctly called
    When container is started with command /home/kogito/kogito-app-launch.sh -h
    Then container log should contain This is the
