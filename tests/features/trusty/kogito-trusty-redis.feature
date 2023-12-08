@quay.io/kiegroup/kogito-trusty-redis
Feature: Kogito-trusty redis feature.

  Scenario: verify if all labels are correctly set on kogito-trusty-redis image
    Given image is built
     Then the image should contain label maintainer with value Apache KIE <dev@kie.apache.org>
      And the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label io.k8s.description with value Runtime image for Kogito Trusty Service for Redis persistence provider
      And the image should contain label io.k8s.display-name with value Kogito Trusty Service - Redis
      And the image should contain label io.openshift.tags with value kogito,trusty,trusty-redis

  Scenario: verify if all parameters are correctly set
    When container is started with env
      | variable                                   | value                       |
      | SCRIPT_DEBUG                               | true                        |
      | KOGITO_PERSISTENCE_REDIS_URL               | redis://127.0.0.1:6379      |
    Then container log should contain KOGITO_PERSISTENCE_REDIS_URL=redis://127.0.0.1:6379
    And container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar