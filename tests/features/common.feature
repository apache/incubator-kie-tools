@quay.io/kiegroup/kogito-data-index-ephemeral
@quay.io/kiegroup/kogito-data-index-postgresql
@quay.io/kiegroup/kogito-jit-runner
@quay.io/kiegroup/kogito-jobs-service-ephemeral
@quay.io/kiegroup/kogito-jobs-service-postgresql
@quay.io/kiegroup/kogito-swf-builder
@quay.io/kiegroup/kogito-base-builder
Feature: Common tests for Kogito images

  Scenario: Verify if Kogito user is correctly configured
    When container is started with command sh
    Then run sh -c 'echo $USER' in container and check its output for kogito
     And run sh -c 'echo $HOME' in container and check its output for /home/kogito
     And run sh -c 'id' in container and check its output for uid=1001(kogito) gid=0(root) groups=0(root),1001(kogito)

