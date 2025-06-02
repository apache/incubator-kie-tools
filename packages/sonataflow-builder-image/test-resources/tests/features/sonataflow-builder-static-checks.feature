@docker.io/apache/incubator-kie-sonataflow-builder
Feature: SonataFlow Builder Static Checks

  Scenario: Verify if the sonataflow and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom should exist and be a directory
    And file /home/kogito/.m2/repository/org/apache/kie/sonataflow/sonataflow-quarkus/ should exist and be a directory
    And file /home/kogito/.m2/repository/org/kie/kie-addons-quarkus-persistence-jdbc/ should exist and be a directory
    And file /home/kogito/.m2/repository/io/quarkus/quarkus-agroal should exist and be a directory
    And file /home/kogito/.m2/repository/io/quarkus/quarkus-jdbc-postgresql should exist and be a directory
