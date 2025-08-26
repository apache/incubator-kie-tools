@docker.io/apache/incubator-kie-sonataflow-management-console
Feature: Serverless Workflow Management Console images

  Scenario: verify that the home page is served
    When container is ready
    Then container log should contain httpd -D FOREGROUND
    And run curl -fsS -o /dev/null -w %{http_code} http://127.0.0.1:8080/ in container and immediately check its output contains 200

  Scenario: Verify that the rewrite rule for /graphql is in httpd.conf
    When container is ready
    Then container log should contain httpd -D FOREGROUND
    Then file /etc/httpd/conf/httpd.conf should contain /graphql


  Scenario: verify that the data-index is available from the container
    When container is started with env
      | variable                    | value                                                 |
      | SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT | http://${DOCKER_HOST}:4000/graphql |
    Then container log should contain httpd -D FOREGROUND
    And run curl -X POST -sS -H Content-Type:application/json --data-binary '{"query":"{ProcessInstances{id}}"}' http://127.0.0.1:8080/graphql in container and check its output contains "data":{"ProcessInstances"
