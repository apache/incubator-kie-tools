@quay.io/kiegroup/kogito-data-index

Feature: Kogito-data-index feature.

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service
    And the image should contain label io.k8s.display-name with value Kogito Data Index Service
    And the image should contain label io.openshift.tags with value kogito,data-index

  Scenario: verify if the binary index is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/data-index-service-*-runner.jar' in container and immediately check its output for /home/kogito/bin/data-index-service-0.6.1-runner.jar

  Scenario: Verify data-index default configuration
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain quarkus.infinispan-client.use-auth = false

  Scenario: verify if auth is correctly set
    When container is started with env
      | variable            | value       |
      | SCRIPT_DEBUG        | true        |
      | INFINISPAN_USEAUTH  | true        |
      | INFINISPAN_USERNAME | IamNotExist |
      | INFINISPAN_PASSWORD | hard2guess  |
    Then container log should contain quarkus.infinispan-client.use-auth = true
    And container log should contain quarkus.infinispan-client.auth-password = hard2guess
    And container log should contain quarkus.infinispan-client.auth-username = IamNotExist

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                 | value       |
      | SCRIPT_DEBUG             | true        |
      | INFINISPAN_USEAUTH       | true        |
      | INFINISPAN_USERNAME      | IamNotExist |
      | INFINISPAN_PASSWORD      | hard2guess  |
      | INFINISPAN_AUTHREALM     | SecretRealm |
      | INFINISPAN_SASLMECHANISM | COOLGSSAPI  |
    Then container log should contain quarkus.infinispan-client.use-auth = true
    And container log should contain quarkus.infinispan-client.auth-password = hard2guess
    And container log should contain quarkus.infinispan-client.auth-username = IamNotExist
    And container log should contain quarkus.infinispan-client.auth-realm = SecretRealm
    And container log should contain quarkus.infinispan-client.sasl-mechanism = COOLGSSAPI

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                                                               | value                  |
      | INFINISPAN_CREDENTIAL_SECRET                                           | infinispan-credentials |
      | INFINISPAN_AUTHREALM                                                   | default                |
      | INFINISPAN_USEAUTH                                                     | true                   |
      | INFINISPAN_SASLMECHANISM                                               | PLAIN                  |
      | QUARKUS_INFINISPAN_CLIENT_SERVER_LIST                                  | server:11222           |
      | MP_MESSAGING_INCOMING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS | mycluster:9092         |
      | KOGITO_PROTOBUF_FOLDER                                                 | /home/kogito           |
      | KOGITO_PROTOBUF_WATCH                                                  | true                   |
     Then container log should contain Error: Could not find or load main class [ERROR]
