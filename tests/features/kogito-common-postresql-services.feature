@quay.io/kiegroup/kogito-data-index-postgresql
Feature: Kogito-data-index postgresql feature.

  Scenario: verify if of container is correctly started with postgresql parameters
    When container is started with env
      | variable                     | value                                     |
      | SCRIPT_DEBUG                 | true                                      |
      | QUARKUS_DATASOURCE_JDBC_URL  | jdbc:postgresql://localhost:5432/quarkus  |
      | QUARKUS_DATASOURCE_USERNAME  | kogito                                    |
      | QUARKUS_DATASOURCE_PASSWORD  | s3cr3t                                    |
    Then container log should contain -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar
     And container log should contain Datasource '<default>': Connection to localhost:5432 refused