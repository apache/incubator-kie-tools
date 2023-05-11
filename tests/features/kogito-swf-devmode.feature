@quay.io/kiegroup/kogito-swf-devmode
Feature: SWF and Quarkus installation

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
