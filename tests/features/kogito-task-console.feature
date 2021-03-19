@quay.io/kiegroup/kogito-task-console
Feature: kogito-task-console feature

  Scenario: verify if all labels are correctly set on kogito-task-console image
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Task Console, manage your Business Process easily.
    And the image should contain label io.k8s.display-name with value Kogito Task Console
    And the image should contain label io.openshift.tags with value kogito,task,task-console

  Scenario: verify if the task console jar is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-task-console-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-task-console-runner.jar

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dkogito.dataindex.http.url=http://localhost:8180 -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/kogito-task-console-runner.jar
    And container log should contain Data index url not set, default will be used: http://localhost:8180
    And container log should contain started in
    And container log should not contain Application failed to start

  Scenario: Verify if the debug is correctly enabled and set data-index url
    When container is started with env
      | variable                  | value            |
      | SCRIPT_DEBUG              | true             |
      | KOGITO_DATAINDEX_HTTP_URL | http://test:9090 |
    Then container log should contain + exec java -XshowSettings:properties -Dkogito.dataindex.http.url=http://test:9090 -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/kogito-task-console-runner.jar
    And container log should not contain Data index url not set, default will be used: http://localhost:8180
    And container log should contain started in
    And container log should not contain Application failed to start

