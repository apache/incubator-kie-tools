@docker.io/apache/incubator-kie-sonataflow-devmode
Feature: Serverless Workflow images common

  Scenario: Verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom should exist and be a directory
      And file /home/kogito/.m2/repository/org/apache/kie/sonataflow/sonataflow-quarkus/ should exist and be a directory
