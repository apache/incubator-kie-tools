@quay.io/kiegroup/kogito-data-index-infinispan
Feature: Kogito-data-index infinispan feature.

  Scenario: verify if all labels are correctly set on kogito-data-index-infinispan image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
     And the image should contain label io.openshift.expose-services with value 8080:http
     And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service for Infinispan persistence provider
     And the image should contain label io.k8s.display-name with value Kogito Data Index Service - Infinispan
     And the image should contain label io.openshift.tags with value kogito,data-index,data-index-infinispan

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                                  | value             |
      | SCRIPT_DEBUG                              | true              |
      | QUARKUS_INFINISPAN_CLIENT_HOSTS           | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_USERNAME        | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_PASSWORD        | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM      | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_HOSTS=172.18.0.1:11222
     And container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
     And container log should contain QUARKUS_INFINISPAN_CLIENT_PASSWORD=hard2guess
     And container log should contain QUARKUS_INFINISPAN_CLIENT_USERNAME=IamNotExist
     And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
     And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI

  Scenario: check if the default quarkus profile is correctly set on data index
    When container is started with env
      | variable               | value   |
      | SCRIPT_DEBUG           | true    |
    Then container log should contain -Dquarkus.profile=kafka-events-support