@quay.io/kiegroup/kogito-jobs-service-mongodb
Feature: Kogito-jobs-service-mongodb feature.

  Scenario: verify if all labels are correctly set kogito-jobs-service image image
    Given image is built
    Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
    And the image should contain label io.openshift.expose-services with value 8080:http
    And the image should contain label io.k8s.description with value Runtime image for Kogito Jobs Service based on MongoDB
    And the image should contain label io.k8s.display-name with value Kogito Jobs Service based on MongoDB
    And the image should contain label io.openshift.tags with value kogito,jobs-service-mongodb

  Scenario: Verify if the application jar exists
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/bin/mongodb/quarkus-app/quarkus-run.jar' in container and immediately check its output for /home/kogito/bin/mongodb/quarkus-app/quarkus-run.jar

  Scenario: verify if the container is correctly started with mongo parameters
    When container is started with env
      | variable                          | value                                          |
      | SCRIPT_DEBUG                      | true                                           |
      | QUARKUS_MONGODB_CONNECTION_STRING | mongodb://user:password@localhost:27017/admin  |
      | QUARKUS_MONGODB_DATABASE          | kogito                                         |
    Then container log should contain -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/mongodb/quarkus-app/quarkus-run.jar
    And container log should not contain Application failed to start
