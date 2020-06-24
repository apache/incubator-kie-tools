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
    Then run sh -c 'ls /home/kogito/bin/kogito-data-index-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-data-index-runner.jar

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.infinispan-client.use-auth=false -Dquarkus.http.port=8080 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-data-index-runner.jar

  Scenario: Verify if the debug is correctly enabled and test custom http port
    When container is started with env
      | variable      | value |
      | SCRIPT_DEBUG  | true  |
      | HTTP_PORT     | 9090  |
    Then container log should contain + exec java -XshowSettings:properties -Dquarkus.infinispan-client.use-auth=false -Dquarkus.http.port=9090 -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-data-index-runner.jar

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

  Scenario: verify if image is started
    When container is started with env
      | variable                        | value                  |
      | INFINISPAN_CREDENTIAL_SECRET    | infinispan-credentials |
      | INFINISPAN_AUTHREALM            | default                |
      | INFINISPAN_USEAUTH              | true                   |
      | INFINISPAN_SASLMECHANISM        | PLAIN                  |
     Then container log should not contain Error: Could not find or load main class [ERROR]

