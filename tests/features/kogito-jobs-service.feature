@quay.io/kiegroup/kogito-jobs-service
Feature: Kogito-jobs-service feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Jobs Service
    And the image should contain label io.k8s.display-name with value Kogito Jobs Service
    And the image should contain label io.openshift.tags with value kogito,jobs-service

  Scenario: verify if the jobs service binary is available on /home/kogito/bin
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-jobs-service-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-jobs-service-runner.jar

  Scenario: Verify if the debug is correctly enabled
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=8080 -Dquarkus.infinispan-client.use-auth=false -jar /home/kogito/bin/kogito-jobs-service-runner.jar
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: Verify if the debug is correctly enabled and test custom http port
    When container is started with env
      | variable      | value |
      | SCRIPT_DEBUG  | true  |
      | HTTP_PORT     | 9090  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.http.port=9090 -Dquarkus.infinispan-client.use-auth=false -jar /home/kogito/bin/kogito-jobs-service-runner.jar

  Scenario: verify if container fails if persistence is enabled but there is no infinispan server list.
    When container is started with env
      | variable           | value |
      | ENABLE_PERSISTENCE | true  |
    Then container log should contain INFINISPAN_CLIENT_SERVER_LIST env not found, please set it.

  Scenario: verify if the persistence is correctly enabled without auth
    When container is started with env
      | variable                      | value           |
      | SCRIPT_DEBUG                  | true            |
      | ENABLE_PERSISTENCE            | true            |
      | INFINISPAN_CLIENT_SERVER_LIST | localhost:11111 |
    Then container log should contain quarkus.infinispan-client.server-list = localhost:11111
    And container log should contain quarkus.infinispan-client.use-auth = false
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable                      | value           |
      | SCRIPT_DEBUG                  | true            |
      | ENABLE_PERSISTENCE            | true            |
      | INFINISPAN_CLIENT_SERVER_LIST | localhost:11111 |
      | INFINISPAN_USEAUTH            | true            |
      | INFINISPAN_USERNAME           | IamNotExist     |
      | INFINISPAN_PASSWORD           | hard2guess      |
    Then container log should contain quarkus.infinispan-client.use-auth = true
    And container log should contain quarkus.infinispan-client.auth-password = hard2guess
    And container log should contain quarkus.infinispan-client.auth-username = IamNotExist
    And container log should contain quarkus.infinispan-client.server-list = localhost:11111
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                      | value           |
      | SCRIPT_DEBUG                  | true            |
      | ENABLE_PERSISTENCE            | true            |
      | INFINISPAN_CLIENT_SERVER_LIST | localhost:11111 |
      | INFINISPAN_USEAUTH            | true            |
      | INFINISPAN_USERNAME           | IamNotExist     |
      | INFINISPAN_PASSWORD           | hard2guess      |
      | INFINISPAN_AUTHREALM          | SecretRealm     |
      | INFINISPAN_SASLMECHANISM      | COOLGSSAPI      |
    Then container log should contain quarkus.infinispan-client.use-auth = true
    And container log should contain quarkus.infinispan-client.auth-password = hard2guess
    And container log should contain quarkus.infinispan-client.auth-username = IamNotExist
    And container log should contain quarkus.infinispan-client.auth-realm = SecretRealm
    And container log should contain quarkus.infinispan-client.sasl-mechanism = COOLGSSAPI
    And container log should contain quarkus.infinispan-client.server-list = localhost:11111
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: verify if container fails if event is enabled but there is no Kafka bootstrap server set.
    When container is started with env
      | variable      | value |
      | ENABLE_EVENTS | true  |
    Then container log should contain KAFKA_BOOTSTRAP_SERVERS env not found, please set it.

  Scenario: verify if the events is correctly enabled
    When container is started with env
      | variable                | value           |
      | SCRIPT_DEBUG            | true            |
      | ENABLE_EVENTS           | true            |
      | KAFKA_BOOTSTRAP_SERVERS | localhost:11111 |
    Then container log should contain bootstrap.servers = [localhost:11111]
    And container log should contain started in
    And container log should contain Connection to node -1 (localhost/127.0.0.1:11111) could not be established.

