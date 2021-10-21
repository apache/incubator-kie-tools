@quay.io/kiegroup/kogito-data-index-ephemeral
Feature: Kogito-data-index ephemeral postgresql feature.

  Scenario: verify if all labels are correctly set on kogito-data-index-ephemeral image
    Given image is built
     Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
      And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
      And the image should contain label io.openshift.s2i.destination with value /tmp
      And the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service for ephemeral PostgreSQL persistence provider
      And the image should contain label io.k8s.display-name with value Kogito Data Index Service - ephemeral PostgreSQL
      And the image should contain label io.openshift.tags with value kogito,data-index,data-index-ephemeral

  Scenario: verify if the indexing service binaries are available on /home/kogito/bin
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/data-index-service-inmemory-runner.jar' in container and immediately check its output for /home/kogito/bin/data-index-service-inmemory-runner.jar

  Scenario: verify if of kogito-data-index-ephemeral container is correctly started
    When container is started with env
      | variable       | value  |
      | SCRIPT_DEBUG   | true   |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/data-index-service-inmemory-runner.jar
    And container log should contain Embedded Postgres started at port
    And container log should not contain Application failed to start

