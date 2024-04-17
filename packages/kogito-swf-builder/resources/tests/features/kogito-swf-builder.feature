@quay.io/kie-tools/kogito-swf-builder
Feature: Serverless Workflow builder images
  Scenario: Verify if Kogito user is correctly configured
    When container is started with command sh
    Then run sh -c 'echo $USER' in container and check its output for kogito
    And run sh -c 'echo $HOME' in container and check its output for /home/kogito
    And run sh -c 'id' in container and check its output for uid=1001(kogito) gid=0(root) groups=0(root),1001(kogito)

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
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health

  Scenario: Verify that the application is built and started correctly when QUARKUS_EXTENSIONS env is used
    When container is started with command bash -c '/home/kogito/launch/build-app.sh && java -jar target/quarkus-app/quarkus-run.jar'
      | variable            | value                                    |
      | SCRIPT_DEBUG        | true                                     |
      | QUARKUS_EXTENSIONS  | io.quarkus:quarkus-kubernetes            |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain -Duser.home=/home/kogito
    And container log should contain Extension io.quarkus:quarkus-kubernetes has been installed
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addons-quarkus-knative-eventing
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*security-jdbc