@quay.io/kiegroup/kogito-swf-builder
@quay.io/kiegroup/kogito-swf-devmode
Feature: Serverless Workflow images common

  Scenario: Verify if the swf and quarkus files are under /home/kogito/.m2/repository
    When container is started with command bash
    Then file /home/kogito/.m2/repository/io/quarkus/platform/quarkus-bom/3.8.4/quarkus-bom-3.8.4.pom should exist
      And file /home/kogito/.m2/repository/org/apache/kie/sonataflow/sonataflow-quarkus/ should exist and be a directory

  # This check should be enabled again once a similar check is done on runtimes
  # to make sure we only have one version of quarkus bom ...
  # See https://issues.redhat.com/browse/KOGITO-8555 to enable again
  # Scenario: verify if there is no dependencies with multiple versions in /home/kogito/.m2/repository
  #   When container is started with command bash
  #   Then run sh -c 'ls /home/kogito/.m2/repository/io/quarkus/quarkus-bom  | wc -l' in container and immediately check its output for 1
