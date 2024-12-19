@docker.io/apache/incubator-kie-sonataflow-management-console
Feature: Serverless Workflow Management Console images

  Scenario: verify that the home page is served
    When container is ready
    Then container log should contain httpd -D FOREGROUND
    Then check that page is served
      | property             | value             |
      | port                 | 8080              |
      | path                 | /                 |
      | request_method       | GET               |
      | wait                 | 480               |
      | expected_status_code | 200               |

  Scenario: Verify that the rewrite rule for /graphql is in httpd.conf
    When container is ready
    Then container log should contain httpd -D FOREGROUND
    Then file /etc/httpd/conf/httpd.conf should contain /graphql


  Scenario: verify that the data-index is available from the container
    When container is started with env
      | variable                    | value                                                 |
      | SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT | http://${DOCKER_HOST}:4000/graphql |
    Then container log should contain httpd -D FOREGROUND
    Then check that page is served
      | property             | value                                    |
      | port                 | 8080                                     |
      | path                 | /graphql                                 |
      | request_method       | POST                                     |
      | request_body         | { "query": "{ProcessInstances{ id } }" } |
      | content_type         | application/json                         |
      | wait                 | 480                                        |
      | expected_status_code | 200                                      |
      | expected_phrase      | "data":{"ProcessInstances                |
