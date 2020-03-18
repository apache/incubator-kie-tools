@quay.io/kiegroup/kogito-management-console

Feature: kogito-management-console feature

  Scenario: verify if all labels are correctly set.
    Given image is built
    Then the image should contain label maintainer with value kogito <kogito@kiegroup.com>
    And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
    And the image should contain label io.openshift.s2i.destination with value /tmp
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Management Console, manage your Business Process easily.
    And the image should contain label io.k8s.display-name with value Kogito Management Console
    And the image should contain label io.openshift.tags with value kogito,management,management-console

  Scenario: verify if the management console jar is available on /home/kogito
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/kogito-management-console-runner.jar' in container and immediately check its output for /home/kogito/bin/kogito-management-console-runner.jar

  Scenario: Verify if the debug is correctly enabled and test default http port
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain + exec java -XshowSettings:properties -Dkogito.dataindex.http.url=http://localhost:8180 -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-management-console-runner.jar

  Scenario: Verify if the debug is correctly enabled and set data-index url
    When container is started with env
      | variable                     | value             |
      | SCRIPT_DEBUG                 | true              |
      | KOGITO_DATAINDEX_HTTP_URL    | http://test:9090  |
    Then container log should contain + exec java -XshowSettings:properties -Dkogito.dataindex.http.url=http://test:9090 -Dquarkus.http.host=0.0.0.0 -jar /home/kogito/bin/kogito-management-console-runner.jar


