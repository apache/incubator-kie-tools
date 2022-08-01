@quay.io/kiegroup/kogito-builder
@quay.io/kiegroup/kogito-runtime-jvm
@quay.io/kiegroup/kogito-runtime-native
@quay.io/kiegroup/kogito-data-index-infinispan
@quay.io/kiegroup/kogito-data-index-ephemeral
@quay.io/kiegroup/kogito-data-index-mongodb
@quay.io/kiegroup/kogito-data-index-postgresql
@quay.io/kiegroup/kogito-trusty-infinispan
@quay.io/kiegroup/kogito-trusty-redis
@quay.io/kiegroup/kogito-trusty-postgresql
@quay.io/kiegroup/kogito-trusty-ui
@quay.io/kiegroup/kogito-explainability
@quay.io/kiegroup/kogito-jit-runner
@quay.io/kiegroup/kogito-jobs-service-ephemeral
@quay.io/kiegroup/kogito-jobs-service-infinispan
@quay.io/kiegroup/kogito-jobs-service-mongodb
@quay.io/kiegroup/kogito-jobs-service-postgresql
@quay.io/kiegroup/kogito-management-console
@quay.io/kiegroup/kogito-task-console
@rhpam-7/rhpam-kogito-runtime-jvm-rhel8
@rhpam-7/rhpam-kogito-builder-rhel8
@rhpam-7/rhpam-kogito-runtime-native-rhel8
@openshift-serverless-1/logic-data-index-ephemeral-rhel8
Feature: Common tests for Kogito images

  Scenario: Verify if Kogito user is correctly configured
    When container is started with command bash
    Then run bash -c 'echo $USER' in container and check its output for kogito
     And run sh -c 'echo $HOME' in container and check its output for /home/kogito
     And run sh -c 'id' in container and check its output for uid=1001(kogito) gid=0(root) groups=0(root),1001(kogito)

