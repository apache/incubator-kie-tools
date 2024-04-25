# Run only on images that won't die instantly
# See https://github.com/apache/incubator-kie-kogito-images/issues/1722
@quay.io/kiegroup/kogito-data-index-ephemeral
#@quay.io/kiegroup/kogito-data-index-postgresql
#@quay.io/kiegroup/kogito-jit-runner
@quay.io/kiegroup/kogito-jobs-service-ephemeral
#@quay.io/kiegroup/kogito-jobs-service-postgresql
Feature: Common tests for Kogito images

  Scenario: Verify if the properties were correctly set using DEFAULT MEM RATIO
    When container is started with args
      | arg       | value                                                                           |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                           |
      | mem_limit | 1073741824                                                                      |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 80, "JAVA_INITIAL_MEM_RATIO": 25} |
    Then container log should match regex -Xms205m
    And container log should match regex -Xmx819m

  Scenario: Verify if the DEFAULT MEM RATIO properties are overridden with different values
    When container is started with args
      | arg       | value                                                                           |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                           |
      | mem_limit | 1073741824                                                                      |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 50, "JAVA_INITIAL_MEM_RATIO": 10} |
    Then container log should match regex -Xms51m
    And container log should match regex -Xmx512m

  Scenario: Verify if the properties were correctly set when aren't passed
    When container is started with args
      | arg       | value                                                 |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh" |
      | mem_limit | 1073741824                                            |
      | env_json  | {"SCRIPT_DEBUG":"true"}                               |
    Then container log should match regex -Xms128m
    And container log should match regex -Xmx512m

  Scenario: Verify if Java Remote Debug is correctly configured
    When container is started with args
      | arg       | value                                                                   |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                   |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_DEBUG":"true", "JAVA_DEBUG_PORT":"9222"}  |
    Then container log should match regex -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9222

  Scenario: Verify if the DEFAULT MEM RATIO properties are overridden with different values from user provided Xmx and Xms
    When container is started with args
      | arg       | value                                                                                                                 |
      | command   | bash -c "sleep 5s; /home/kogito/kogito-app-launch.sh"                                                                 |
      | mem_limit | 1073741824                                                                                                            |
      | env_json  | {"SCRIPT_DEBUG":"true", "JAVA_MAX_MEM_RATIO": 50, "JAVA_INITIAL_MEM_RATIO": 10, "JAVA_OPTIONS":"-Xms4000m -Xmx8000m"} |
    Then container log should match regex -Xms4000m
    And container log should match regex -Xmx8000m

