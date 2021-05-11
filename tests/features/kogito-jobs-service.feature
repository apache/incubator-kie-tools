@quay.io/kiegroup/kogito-jobs-service
Feature: Kogito-jobs-service feature.

  Scenario: verify if all labels are correctly set kogito-jobs-service image image
    Given image is built
    Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Jobs Service
    And the image should contain label io.k8s.display-name with value Kogito Jobs Service
    And the image should contain label io.openshift.tags with value kogito,jobs-service

  Scenario: verify if the jobs service common binary is available on /home/kogito/bin
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/jobs-service-common-runner.jar' in container and immediately check its output for /home/kogito/bin/jobs-service-common-runner.jar
  
  Scenario: verify if the jobs service infinispan binary is available on /home/kogito/bin
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/jobs-service-infinispan-runner.jar' in container and immediately check its output for /home/kogito/bin/jobs-service-infinispan-runner.jar

  Scenario: Verify if the debug is correctly enabled
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/jobs-service-common-runner.jar
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: verify if the events is correctly enabled
    When container is started with env
      | variable                | value                                     |
      | SCRIPT_DEBUG            | true                                      |
      | ENABLE_EVENTS           | true                                      |
      | KOGITO_JOBS_PROPS       | -Dkafka.bootstrap.servers=localhost:11111 |
    Then container log should contain bootstrap.servers = [localhost:11111]
    And container log should contain started in
    And container log should contain Connection to node -1 (localhost/127.0.0.1:11111) could not be established.

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable                      | value           |
      | SCRIPT_DEBUG                  | true            |
      | ENABLE_PERSISTENCE            | true            |
      | QUARKUS_INFINISPAN_CLIENT_SERVER_LIST     | 172.18.0.1:11222  |
      | QUARKUS_INFINISPAN_CLIENT_USE_AUTH        | true              |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME   | IamNotExist       |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD   | hard2guess        |
      | QUARKUS_INFINISPAN_CLIENT_AUTH_REALM      | SecretRealm       |
      | QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM  | COOLGSSAPI        |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/jobs-service-infinispan-runner.jar
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=172.18.0.1:11222
    Then container log should contain QUARKUS_INFINISPAN_CLIENT_USE_AUTH=true
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD=hard2guess
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME=IamNotExist
    And container log should contain QUARKUS_INFINISPAN_CLIENT_AUTH_REALM=SecretReal
    And container log should contain QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM=COOLGSSAPI
    And container log should not contain Application failed to start
