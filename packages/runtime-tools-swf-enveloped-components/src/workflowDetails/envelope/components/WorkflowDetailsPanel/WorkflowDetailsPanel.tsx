/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import Moment from "react-moment";
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import React from "react";
import { LevelDownAltIcon } from "@patternfly/react-icons/dist/js/icons/level-down-alt-icon";
import { LevelUpAltIcon } from "@patternfly/react-icons/dist/js/icons/level-up-alt-icon";
import { WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";
import { EndpointLink } from "@kie-tools/runtime-tools-components/dist/components/EndpointLink";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowDetailsDriver } from "../../../api";
import { WorkflowInstanceIconCreator, getWorkflowInstanceDescription } from "../../../utils/Utils";

interface IOwnProps {
  workflowInstance: WorkflowInstance;
  driver: WorkflowDetailsDriver;
}
const WorkflowDetailsPanel: React.FC<IOwnProps & OUIAProps> = ({ workflowInstance, driver, ouiaId, ouiaSafe }) => {
  return (
    <Card {...componentOuiaProps(ouiaId ? ouiaId : workflowInstance.id, "workflow-details", ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Details
        </Title>
      </CardHeader>
      <CardBody>
        <Form>
          <FormGroup label="Name" fieldId="name">
            <Text component={TextVariants.p}>{workflowInstance.processName}</Text>
          </FormGroup>
          {workflowInstance.businessKey && (
            <FormGroup label="Business key" fieldId="businessKey">
              <Text component={TextVariants.p}>{workflowInstance.businessKey}</Text>
            </FormGroup>
          )}
          <FormGroup label="State" fieldId="state">
            <Text component={TextVariants.p}>{WorkflowInstanceIconCreator(workflowInstance.state)}</Text>
          </FormGroup>
          <FormGroup label="Id" fieldId="id">
            <Text component={TextVariants.p} className="kogito-management-console--u-WordBreak">
              {workflowInstance.id}
            </Text>
          </FormGroup>
          {workflowInstance.serviceUrl ? (
            <FormGroup label="Endpoint" fieldId="endpoint">
              <Text component={TextVariants.p} className="kogito-management-console--u-WordBreak">
                <EndpointLink serviceUrl={workflowInstance.serviceUrl} isLinkShown={true} />
              </Text>
            </FormGroup>
          ) : (
            ""
          )}
          <FormGroup label="Start" fieldId="start">
            {workflowInstance.start ? (
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${workflowInstance.start}`)}</Moment>
              </Text>
            ) : (
              ""
            )}
          </FormGroup>

          {workflowInstance.lastUpdate && (
            <FormGroup label="Last Updated" fieldId="lastUpdate">
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${workflowInstance.lastUpdate}`)}</Moment>
              </Text>
            </FormGroup>
          )}

          {workflowInstance.end && (
            <FormGroup label="End" fieldId="end">
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${workflowInstance.end}`)}</Moment>
              </Text>
            </FormGroup>
          )}
          {workflowInstance.parentWorkflowInstance && (
            <FormGroup label="Parent Workflow" fieldId="parent">
              <div>
                <Button
                  data-testid="open-parent-workflow"
                  variant="link"
                  icon={<LevelUpAltIcon />}
                  onClick={(): void => {
                    driver.openWorkflowInstanceDetails(workflowInstance.parentWorkflowInstance!.id);
                  }}
                  {...componentOuiaProps(
                    ouiaId ? ouiaId : workflowInstance.parentWorkflowInstance.id,
                    "workflow-details",
                    ouiaSafe
                  )}
                >
                  <ItemDescriptor
                    itemDescription={getWorkflowInstanceDescription(workflowInstance.parentWorkflowInstance)}
                  />
                </Button>
              </div>
            </FormGroup>
          )}

          {workflowInstance.childWorkflowInstances && workflowInstance.childWorkflowInstances.length !== 0 && (
            <FormGroup label="Sub Workflows" fieldId="child">
              {workflowInstance.childWorkflowInstances.map((child) => (
                <div key={child.id}>
                  <Button
                    variant="link"
                    icon={<LevelDownAltIcon />}
                    onClick={(): void => {
                      driver.openWorkflowInstanceDetails(child.id);
                    }}
                    {...componentOuiaProps(ouiaId ? ouiaId : child.id, "workflow-details", ouiaSafe)}
                  >
                    <ItemDescriptor itemDescription={getWorkflowInstanceDescription(child)} />
                  </Button>
                </div>
              ))}
            </FormGroup>
          )}
        </Form>
      </CardBody>
    </Card>
  );
};

export default WorkflowDetailsPanel;
