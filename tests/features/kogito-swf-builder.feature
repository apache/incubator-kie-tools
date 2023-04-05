@quay.io/kiegroup/kogito-swf-builder
Feature: SWF and Quarkus installation

  Scenario: verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/org/acme/serverless-workflow-project/1.0.0-SNAPSHOT/serverless-workflow-project-1.0.0-SNAPSHOT.jar should exist
      And file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom/2.16.6.Final/quarkus-bom-2.16.6.Final.pom should exist
      And file /home/kogito/.m2/repository/org/kie/kogito/kogito-quarkus-serverless-workflow/ should exist and be a directory

  # This check should be enabled again once a similar check is done on runtimes
  # to make sure we only have one version of quarkus bom ...
  # See https://issues.redhat.com/browse/KOGITO-8555 to enable again
  # Scenario: verify if there is no dependencies with multiple versions in /home/kogito/.m2/repository
  #   When container is started with command bash
  #   Then run sh -c 'ls /home/kogito/.m2/repository/io/quarkus/quarkus-bom  | wc -l' in container and immediately check its output for 1

  Scenario: verify if container starts in devmode by default
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain -Duser.home=/home/kogito
    And check that page is served
      | property             | value                                                                         |
      | port                 | 8080                                                                          |
      | path                 | /q/health/ready                                                               |
      | wait                 | 480                                                                           |
      | request_method       | GET                                                                           |
      | expected_status_code | 200                                                                           |
