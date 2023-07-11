@openshift-serverless-1-tech-preview/logic-swf-devmode-rhel8
Feature: logic-swf-devmode-rhel8 feature

  Scenario: verify if all labels are correctly set on logic-swf-devmode-rhel8 image
    Given image is built
      Then the image should contain label io.openshift.expose-services with value 8080:http,5005:http
      And the image should contain label maintainer with value serverless-logic <bsig-cloud@redhat.com>
      And the image should contain label io.k8s.description with value Red Hat build of Kogito Serverless Workflow development mode image with Quarkus extensions libraries preinstalled.
      And the image should contain label io.k8s.display-name with value Red Hat OpenShift Serverless Logic SWF Devmode
      And the image should contain label io.openshift.tags with value logic,devmode,kogito,kogito,development,serverless,workflow
      And the image should contain label com.redhat.component with value openshift-serverless-1-logic-swf-devmode-rhel8-container