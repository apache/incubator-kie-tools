@quay.io/kiegroup/kogito-jobs-service-infinispan
Feature: Kogito-jobs-service-infinispan feature.

  Scenario: verify if all labels are correctly set kogito-jobs-service image image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Jobs Service based on Infinispan
    And the image should contain label io.k8s.display-name with value Kogito Jobs Service based on Infinispan
    And the image should contain label io.openshift.tags with value kogito,jobs-service-infinispan

  Scenario: Verify if the application jar exists
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/infinispan/quarkus-app/quarkus-run.jar' in container and immediately check its output for /home/kogito/bin/infinispan/quarkus-app/quarkus-run.jar

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable                                  | value             |
      | SCRIPT_DEBUG                              | true              |
      | QUARKUS_INFINISPAN_CLIENT_HOSTS           | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_USERNAME        | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_PASSWORD        | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM      | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/infinispan/quarkus-app/quarkus-run.jar
    And container log should contain QUARKUS_INFINISPAN_CLIENT_HOSTS=172.18.0.1:11222
    And container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
    And container log should contain QUARKUS_INFINISPAN_CLIENT_PASSWORD=hard2guess
    And container log should contain QUARKUS_INFINISPAN_CLIENT_USERNAME=IamNotExist
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
    And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI
    And container log should not contain Application failed to start
