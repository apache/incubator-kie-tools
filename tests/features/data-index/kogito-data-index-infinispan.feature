@quay.io/kiegroup/kogito-data-index-infinispan
Feature: Kogito-data-index infinispan feature.

  Scenario: verify if all labels are correctly set on kogito-data-index-infinispan image
    Given image is built
    Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
     And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
     And the image should contain label io.openshift.s2i.destination with value /tmp
     And the image should contain label io.openshift.expose-services with value 8080:http
     And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service for Infinispan persistence provider
     And the image should contain label io.k8s.display-name with value Kogito Data Index Service - Infinispan
     And the image should contain label io.openshift.tags with value kogito,data-index,data-index-infinispan

  Scenario: verify if the indexing service binaries are available on /home/kogito/bin
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/data-index-service-infinispan-runner.jar' in container and immediately check its output for /home/kogito/bin/data-index-service-infinispan-runner.jar

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                                  | value             |
      | SCRIPT_DEBUG                              | true              |
      | QUARKUS_INFINISPAN_CLIENT_SERVER_LIST     | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME   | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD   | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM      | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=172.18.0.1:11222
     And container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
     And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD=hard2guess
     And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME=IamNotExist
     And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
     And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI

