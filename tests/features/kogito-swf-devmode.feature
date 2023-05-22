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