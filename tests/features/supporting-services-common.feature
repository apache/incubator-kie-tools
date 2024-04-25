@quay.io/kiegroup/kogito-data-index-ephemeral
@quay.io/kiegroup/kogito-data-index-postgresql
@quay.io/kiegroup/kogito-jit-runner
Feature: kogito supporting services common feature

Scenario: Verify if the application jar exists
  When container is started with command bash
  Then run sh -c 'ls /home/kogito/bin/quarkus-app/quarkus-run.jar' in container and immediately check its output for /home/kogito/bin/quarkus-app/quarkus-run.jar