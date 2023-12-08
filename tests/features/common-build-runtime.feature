@quay.io/kiegroup/kogito-s2i-builder
@quay.io/kiegroup/kogito-runtime-native
@quay.io/kiegroup/kogito-runtime-jvm
@rhpam-7/rhpam-kogito-builder-rhel8
@rhpam-7/rhpam-kogito-runtime-jvm-rhel8
Feature: Common tests for Kogito builder and runtime images

  Scenario: Verify if usage help is correctly called
    When container is started with command bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh -h"
    Then container log should contain This is the
