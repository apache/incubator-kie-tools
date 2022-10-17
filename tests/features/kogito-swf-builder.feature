@quay.io/kiegroup/kogito-swf-builder
Feature: SWF and Quarkus installation

  Scenario: verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/org/acme/serverless-workflow-project/1.0.0-SNAPSHOT/serverless-workflow-project-1.0.0-SNAPSHOT.jar should exist
      And file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom/2.13.0.Final/quarkus-bom-2.13.0.Final.pom should exist
      And file /home/kogito/.m2/repository/org/kie/kogito/kogito-quarkus-serverless-workflow/ should exist and be a directory


  Scenario: Verify if a build run correctly
    When container is started with command bash
    Then file /home/kogito/serverless-workflow-project/target/quarkus-app/quarkus-run.jar should exist
      And file /home/kogito/serverless-workflow-project/target/classes/greet.sw.json should exist


