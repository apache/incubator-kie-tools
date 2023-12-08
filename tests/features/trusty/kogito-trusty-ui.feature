@quay.io/kiegroup/kogito-trusty-ui
Feature: kogito-trusty-ui feature

  Scenario: verify if all labels are correctly set on kogito-trusty-ui image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Trusty UI, manage your Business Process easily.
    And the image should contain label io.k8s.display-name with value Kogito Trusty UI
    And the image should contain label io.openshift.tags with value kogito,trusty,trusty-ui

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain -Dkogito.trusty.http.url=http://localhost:8180 -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar
    And container log should contain Trusty url not set, default will be used: http://localhost:8180
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: Verify if the debug is correctly enabled and set trusty url
    When container is started with env
      | variable                  | value            |
      | SCRIPT_DEBUG              | true             |
      | KOGITO_TRUSTY_ENDPOINT    | http://test:9090 |
    Then container log should contain -Dkogito.trusty.http.url=http://test:9090 -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar
    And container log should not contain Trusty url not set, default will be used: http://localhost:8180
    And container log should contain started in
    And container log should not contain Application failed to start

