@docker.io/apache/incubator-kie-sonataflow-devmode
Feature: Serverless Workflow devmode images

  Scenario: Verify if container starts in devmode by default
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | false  |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*sonataflow-quarkus-devui
    And container log should match regex Installed features:.*kie-addon-source-files-extension
    And container log should match regex Installed features:.*kogito-addons-quarkus-jobs-service-embedded
    And container log should match regex Installed features:.*kogito-addons-quarkus-data-index-inmemory

  Scenario: Verify if container starts correctly when continuous testing is enabled
    When container is started with env
      | variable                   | value    |
      | SCRIPT_DEBUG               | false     |
      | QUARKUS_CONTINUOUS_TESTING | enabled  |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain -Dquarkus.test.continuous-testing=enabled

  Scenario: Verify if container starts correctly when QUARKUS_EXTENSIONS env is used
    When container is started with env
      | variable                   | value                                    |
      | SCRIPT_DEBUG               | false                                     |
      | QUARKUS_EXTENSIONS         | io.quarkus:quarkus-elytron-security-jdbc |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 960               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should match regex Extension io\.quarkus:quarkus-elytron-security-jdbc.* has been installed
    And container log should match regex Installed features:.*kogito-serverless-workflow
    And container log should match regex Installed features:.*kie-addon-knative-eventing-extension
    And container log should match regex Installed features:.*smallrye-health
    And container log should match regex Installed features:.*sonataflow-quarkus-devui
    And container log should match regex Installed features:.*kie-addon-source-files-extension
    And container log should match regex Installed features:.*kogito-addons-quarkus-jobs-service-embedded
    And container log should match regex Installed features:.*kogito-addons-quarkus-data-index-inmemory
    And container log should match regex Installed features:.*security-jdbc

  Scenario: verify that the embedded jobs-service is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain Embedded Postgres started at port
    And container log should contain SET Leader
    And check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /v2/jobs/1234     |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 404               |
      | expected_phrase      | Job not found     |

  Scenario: verify that the embedded data-index service is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | request_method       | GET               |
      | wait                 | 480               |
      | expected_status_code | 200               |
    And container log should contain Embedded Postgres started at port
    And check that page is served
      | property             | value                                    |
      | port                 | 8080                                     |
      | path                 | /graphql                                 |
      | request_method       | POST                                     |
      | request_body         | { "query": "{ProcessInstances{ id } }" } |
      | wait                 | 480                                      |
      | expected_status_code | 200                                      |
      | expected_phrase      | {"data":{"ProcessInstances":[]}}         |

  Scenario: verify that the serverless workflow devui is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then check that page is served
      | property             | value                                                                 |
      | port                 | 8080                                                                  |
      | path                 | /q/dev-ui/org.kie.kogito-addons-quarkus-data-index-inmemory/dataindex |
      | request_method       | GET                                                                   |
      | wait                 | 480                                                                   |
      | expected_status_code | 403                                                                   |
    And check that page is served
      | property             | value                                                                  |
      | port                 | 8080                                                                   |
      | path                 | /q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui/workflows |
      | request_method       | GET                                                                    |
      | wait                 | 480                                                                    |
      | expected_status_code | 403                                                                    |

  Scenario: Verify if container starts in devmode with service discovery enabled
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
    And container log should contain kogito-addon-microprofile-config-service-catalog-extension

  Scenario: Verify if container have the KOGITO_CODEGEN_PROCESS_FAILONERROR env set to false
    When container is started with command bash
    Then run sh -c 'echo $KOGITO_CODEGEN_PROCESS_FAILONERROR' in container and immediately check its output for false
