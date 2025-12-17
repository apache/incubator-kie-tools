@docker.io/apache/incubator-kie-sonataflow-devmode
Feature: Serverless Workflow devmode images

  Scenario: Verify if container starts in devmode by default
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | false  |
    Then container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*sonataflow-quarkus-devui
    And container log should match regex Installed features:.*kie-addon-source-files-extension
    And container log should match regex Installed features:.*kogito-addons-quarkus-jobs-service-embedded
    And container log should match regex Installed features:.*kogito-addons-quarkus-data-index-inmemory
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: Verify if container starts correctly when continuous testing is enabled
    When container is started with env
      | variable                   | value    |
      | SCRIPT_DEBUG               | false     |
      | QUARKUS_CONTINUOUS_TESTING | enabled  |
    Then container log should contain -Dquarkus.test.continuous-testing=enabled
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: Verify if container starts correctly when QUARKUS_EXTENSIONS env is used
    When container is started with env
      | variable                   | value                                    |
      | SCRIPT_DEBUG               | false                                     |
      | QUARKUS_EXTENSIONS         | io.quarkus:quarkus-elytron-security-jdbc |
    Then container log should match regex Extension io\.quarkus:quarkus-elytron-security-jdbc.* has been installed
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*sonataflow-quarkus-devui
    And container log should match regex Installed features:.*kie-addon-source-files-extension
    And container log should match regex Installed features:.*kogito-addons-quarkus-jobs-service-embedded
    And container log should match regex Installed features:.*kogito-addons-quarkus-data-index-inmemory
    And container log should match regex Installed features:.*security-jdbc
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    Then run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: verify that the embedded jobs-service is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then container log should contain Embedded Postgres started at port
    And container log should contain SET Leader
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200
    And run curl -sS -o /dev/null -w %{http_code} http://127.0.0.1:8080/v2/jobs/1234 in container and immediately check its output contains 404

  Scenario: verify that the embedded data-index service is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then container log should contain Embedded Postgres started at port
    And container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS http://127.0.0.1:8080/graphql?query=%7BProcessInstances%7Bid%7D%7D in container and check its output contains {"data":{"ProcessInstances":[]}}
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: verify that the serverless workflow devui is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then container log should match regex Listening on: http://0\.0\.0\.0:8080
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/dev-ui/org.kie.kogito-addons-quarkus-data-index-inmemory/dataindex in container and immediately check its output contains 200
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui/workflows in container and immediately check its output contains 200

  Scenario: Verify if container starts in devmode with service discovery enabled
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then container log should match regex Listening on: http://0\.0\.0\.0:8080
    And container log should contain kogito-addon-microprofile-config-service-catalog-extension
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/q/health/ready in container and immediately check its output contains 200

  Scenario: Verify if container have the KOGITO_CODEGEN_PROCESS_FAILONERROR env set to false
    When container is started with command bash
    Then run sh -c 'echo $KOGITO_CODEGEN_PROCESS_FAILONERROR' in container and immediately check its output for false
