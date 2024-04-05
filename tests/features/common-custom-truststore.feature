@quay.io/kiegroup/kogito-data-index-ephemeral
@quay.io/kiegroup/kogito-data-index-postgresql
@quay.io/kiegroup/kogito-task-console 
@quay.io/kiegroup/kogito-management-console 
@quay.io/kiegroup/kogito-jit-runner 
@quay.io/kiegroup/kogito-jobs-service-ephemeral
@quay.io/kiegroup/kogito-jobs-service-postgresql
Feature: Common tests for Custom TrustStore configuration
  # This test sets an invalid certificate to the container, it fails to start, and if timing is bad cekit hangs on 'Running command ps -C java in container'
  # See https://github.com/apache/incubator-kie-kogito-images/issues/1722
  @ignore
  Scenario: Verify if a custom certificate is correctly handled
    When container is started with command bash -c "/home/kogito/kogito-app-launch.sh"
      | variable            | value              |
      | CUSTOM_TRUSTSTORE   | my-truststore.jks  |
      | RUNTIME_TYPE        | quarkus            |
    Then container log should contain INFO ---> Configuring custom Java Truststore 'my-truststore.jks' in the path /home/kogito/certs/custom-truststore
    Then container log should contain ERROR ---> A custom truststore was specified ('my-truststore.jks'), but wasn't found in the path /home/kogito/certs/custom-truststore
