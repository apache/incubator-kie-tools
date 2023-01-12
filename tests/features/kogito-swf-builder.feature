@quay.io/kiegroup/kogito-swf-builder
Feature: SWF and Quarkus installation

  Scenario: verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/org/acme/serverless-workflow-project/1.0.0-SNAPSHOT/serverless-workflow-project-1.0.0-SNAPSHOT.jar should exist
      And file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom/2.15.0.Final/quarkus-bom-2.15.0.Final.pom should exist
      And file /home/kogito/.m2/repository/org/kie/kogito/kogito-quarkus-serverless-workflow/ should exist and be a directory

  Scenario: verify if there is no dependencies with multiple versions in /home/kogito/.m2/repository
    When container is started with command bash
    Then run sh -c 'ls /home/kogito/.m2/repository/io/quarkus/quarkus-bom  | wc -l' in container and immediately check its output for 1