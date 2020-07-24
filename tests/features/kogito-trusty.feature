@quay.io/kiegroup/kogito-trusty
Feature: Kogito-trusty feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Trusty Service
    And the image should contain label io.k8s.display-name with value Kogito Trusty Service
    And the image should contain label io.openshift.tags with value kogito,trusty

  Scenario: verify if the binary index is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-trusty-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-trusty-runner.jar

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=8080 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-trusty-runner.jar

  Scenario: Verify if the debug is correctly enabled and test custom http port
    When container is started with env
      | variable      | value |
      | SCRIPT_DEBUG  | true  |
      | HTTP_PORT     | 9090  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=9090 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-trusty-runner.jar

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable                      | value           |
      | SCRIPT_DEBUG                  | true            |
      | ENABLE_PERSISTENCE            | true            |
      | QUARKUS_INFINISPAN_CLIENT_SERVER_LIST     | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME   | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD   | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM       | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=172.18.0.1:11222
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD=hard2guess
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME=IamNotExist
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
    And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI
    And container log should not contain Application failed to start
