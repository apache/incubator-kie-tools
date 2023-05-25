@quay.io/kiegroup/kogito-swf-devmode
Feature: SWF and Quarkus installation

  Scenario: verify if container starts in devmode by default
    When container is started with env
      | variable     | value |
      | SCRIPT_DEBUG | true  |
    Then container log should contain --no-transfer-progress
    And container log should contain -Duser.home=/home/kogito -o
    And check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |

  Scenario: verify if container starts correctly when QUARKUS_EXTENSIONS env is used
    When container is started with env
      | variable            | value                                    |
      | SCRIPT_DEBUG        | true                                     |
      | QUARKUS_EXTENSIONS  | io.quarkus:quarkus-elytron-security-jdbc |
    Then container log should contain -Duser.home=/home/kogito
    And container log should not contain /bin/mvn -B -X --batch-mode -o
    And container log should contain Extension io.quarkus:quarkus-elytron-security-jdbc has been installed
    And check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |

  Scenario: verify that the embedded jobs-service is running
    When container is started with env
      | variable                    | value |
      | QUARKUS_DEVSERVICES_ENABLED | false |
    Then container log should contain Embedded Postgres started at port
    And container log should contain SET Leader
    And check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | wait                 | 480               |
      | request_method       | GET               |
      | expected_status_code | 200               |
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
    Then container log should contain Embedded Postgres started at port
    And check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /q/health/ready   |
      | request_method       | GET               |
      | wait                 | 480               |
      | expected_status_code | 200               |
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
      | property             | value                                                              |
      | port                 | 8080                                                               |
      | path                 | /q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow/dataindex |
      | request_method       | GET                                                                |
      | wait                 | 480                                                                |
      | expected_status_code | 200                                                                |
    And check that page is served
      | property             | value                                                                            |
      | port                 | 8080                                                                             |
      | path                 | /q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances |
      | request_method       | GET                                                                              |
      | wait                 | 480                                                                              |
      | expected_status_code | 200                                                                              |
