@quay.io/kiegroup/kogito-springboot-ubi8-s2i @quay.io/kiegroup/kogito-springboot-ubi8 @quay.io/kiegroup/kogito-quarkus-ubi8-s2i @quay.io/kiegroup/kogito-quarkus-ubi8 @quay.io/kiegroup/kogito-quarkus-jvm-ubi8 @quay.io/kiegroup/kiegroup/kogito-data-index

Feature: Common tests for Kogito images

  Scenario: Verify if Kogito user is correctly configured
    When container is started with command bash
    Then run bash -c 'echo $USER' in container and check its output for kogito
     And run sh -c 'echo $HOME' in container and check its output for /home/kogito
     And run sh -c 'id' in container and check its output for uid=1001(kogito) gid=1001(kogito) groups=1001(kogito)
