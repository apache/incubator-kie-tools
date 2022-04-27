@quay.io/kiegroup/kogito-data-index-oracle
Feature: Kogito-data-index oracle feature.

  Scenario: verify if all labels are correctly set on kogito-data-index-oracle image
    Given image is built
     Then the image should contain label maintainer with value kogito <bsig-cloud@redhat.com>
      And the image should contain label io.openshift.s2i.scripts-url with value image:///usr/local/s2i
      And the image should contain label io.openshift.s2i.destination with value /tmp
      And the image should contain label io.openshift.expose-services with value 8080:http
      And the image should contain label io.k8s.description with value Runtime image for Kogito Data Index Service for Oracle persistence provider
      And the image should contain label io.k8s.display-name with value Kogito Data Index Service - Oracle
      And the image should contain label io.openshift.tags with value kogito,data-index,data-index-oracle


  Scenario: verify if of container is correctly started with oracle parameters
    When container is started with env
      | variable                     | value                                     |
      | SCRIPT_DEBUG                 | true                                      |
      | QUARKUS_DATASOURCE_JDBC_URL  | jdbc:oracle:thin:@//10.1.1.53:1521/quarkus |
      | QUARKUS_DATASOURCE_USERNAME  | kogito                                    |
      | QUARKUS_DATASOURCE_PASSWORD  | s3cr3t                                    |
    Then container log should contain -Djava.library.path=/home/kogito/lib -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080 -jar /home/kogito/bin/quarkus-app/quarkus-run.jar
    And container log should contain Datasource '<default>': IO Error: The Network Adapter could not establish the connection
    And container log should not contain Application failed to start
