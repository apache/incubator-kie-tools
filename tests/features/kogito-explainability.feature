@quay.io/kiegroup/kogito-explainability
Feature: Kogito-explainability feature.

  Scenario: verify if all labels are correctly set on kogito-explainability image
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Explainability Service
    And the image should contain label io.k8s.display-name with value Kogito Explainability Service
    And the image should contain label io.openshift.tags with value kogito,explainability

  Scenario: verify if the messaging binary is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-explainability-messaging-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-explainability-messaging-runner.jar
    And run sh -c 'ls /home/kogito/bin/kogito-explainability-rest-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-explainability-rest-runner.jar


  Scenario: verify if the rest binary is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-explainability-rest-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-explainability-rest-runner.jar

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/kogito-explainability-messaging-runner.jar

  Scenario: Verify if the explainability rest binary is selected by the enviroment variable EXPLAINABILITY_COMMUNICATION
    When container is started with env
      | variable      | value |
      | EXPLAINABILITY_COMMUNICATION  | rest |
      | SCRIPT_DEBUG  | true  |
    Then container log should contain + exec java -XshowSettings:properties -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/kogito-explainability-rest-runner.jar

  Scenario: Verify if the communication is correctly set to its default value if a wrong communication type is set
    When container is started with env
      | variable               | value    |
      | SCRIPT_DEBUG           | true     |
      | EXPLAINABILITY_COMMUNICATION | nonsense |
    Then container log should contain WARN Explainability communication type nonsense is not allowed, the allowed types are [REST MESSAGING]. Defaulting to MESSAGING.
