@quay.io/kiegroup/kogito-swf-builder
@openshift-serverless-1-tech-preview/logic-swf-builder-rhel8
Feature: Serverless Workflow builder images

  Scenario: Verify that the application is built and started correctly
    When container is started with command bash -c '/home/kogito/launch/build-app.sh && java -jar target/quarkus-app/quarkus-run.jar'
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain --no-transfer-progress
    And container log should contain -Duser.home=/home/kogito
    And container log should match regex Installed features:.*kubernetes
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kogito-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health

  Scenario: Verify that the application is built and started correctly when QUARKUS_EXTENSIONS env is used
    When container is started with command bash -c '/home/kogito/launch/build-app.sh && java -jar target/quarkus-app/quarkus-run.jar'
      | variable            | value                                    |
      | SCRIPT_DEBUG        | true                                     |
      | QUARKUS_EXTENSIONS  | io.quarkus:quarkus-elytron-security-jdbc |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain -Duser.home=/home/kogito
    And container log should contain Extension io.quarkus:quarkus-elytron-security-jdbc has been installed
    And container log should match regex Installed features:.*kubernetes
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kogito-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*security-jdbc