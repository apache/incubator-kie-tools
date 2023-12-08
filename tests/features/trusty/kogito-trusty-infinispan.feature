@quay.io/kiegroup/kogito-trusty-infinispan
Feature: Kogito-trusty infinispan feature.

  Scenario: verify if all labels are correctly set on kogito-trusty-infinispan image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Trusty Service for Infinispan persistence provider
    And the image should contain label io.k8s.display-name with value Kogito Trusty Service - Infinispan
    And the image should contain label io.openshift.tags with value kogito,trusty,trusty-infinispan

  Scenario: Verify if the explainability messaging is disabled
    When container is started with env
      | variable      | value |
      | SCRIPT_DEBUG  | true  |
      | EXPLAINABILITY_ENABLED     | false  |
    Then container log should contain -Dtrusty.explainability.enabled=false -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable                                  | value             |
      | SCRIPT_DEBUG                              | true              |
      | ENABLE_PERSISTENCE                        | true              |
      | QUARKUS_INFINISPAN_CLIENT_HOSTS           | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_USERNAME        | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_PASSWORD        | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM      | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_HOSTS=172.18.0.1:11222
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
    And container log should contain QUARKUS_INFINISPAN_CLIENT_PASSWORD=hard2guess
    And container log should contain QUARKUS_INFINISPAN_CLIENT_USERNAME=IamNotExist
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
    And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI
    And container log should not contain Application failed to start
