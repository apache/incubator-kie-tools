/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import { ProcessDetailsDriver } from "../../../api";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  ProcessInstanceIconCreator,
  getProcessInstanceDescription,
} from "../../../../processList/envelope/components/utils/ProcessListUtils";
import { EndpointLink } from "@kie-tools/runtime-tools-components/dist/components/EndpointLink";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";

interface IOwnProps {
  processInstance: ProcessInstance;
  driver: ProcessDetailsDriver;
}
const ProcessDetailsPanel: React.FC<IOwnProps & OUIAProps> = ({ processInstance, driver, ouiaId, ouiaSafe }) => {
  return (
    <Card {...componentOuiaProps(ouiaId ? ouiaId : processInstance.id, "process-details", ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Details
        </Title>
      </CardHeader>
      <CardBody>
        <Form>
          <FormGroup label="Name" fieldId="name">
            <Text component={TextVariants.p}>{processInstance.processName}</Text>
          </FormGroup>
          {processInstance.businessKey && (
            <FormGroup label="Business key" fieldId="businessKey">
              <Text component={TextVariants.p}>{processInstance.businessKey}</Text>
            </FormGroup>
          )}
          <FormGroup label="State" fieldId="state">
            <Text component={TextVariants.p}>{ProcessInstanceIconCreator(processInstance.state)}</Text>
          </FormGroup>
          <FormGroup label="Id" fieldId="id">
            <Text component={TextVariants.p} className="kogito-management-console--u-WordBreak">
              {processInstance.id}
            </Text>
          </FormGroup>
          {processInstance.serviceUrl ? (
            <FormGroup label="Endpoint" fieldId="endpoint">
              <Text component={TextVariants.p} className="kogito-management-console--u-WordBreak">
                <EndpointLink serviceUrl={processInstance.serviceUrl} isLinkShown={true} />
              </Text>
            </FormGroup>
          ) : (
            ""
          )}
          <FormGroup label="Start" fieldId="start">
            {processInstance.start ? (
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${processInstance.start}`)}</Moment>
              </Text>
            ) : (
              ""
            )}
          </FormGroup>

          {processInstance.lastUpdate && (
            <FormGroup label="Last Updated" fieldId="lastUpdate">
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${processInstance.lastUpdate}`)}</Moment>
              </Text>
            </FormGroup>
          )}

          {processInstance.end && (
            <FormGroup label="End" fieldId="end">
              <Text component={TextVariants.p}>
                <Moment fromNow>{new Date(`${processInstance.end}`)}</Moment>
              </Text>
            </FormGroup>
          )}
          {processInstance.parentProcessInstance !== null && (
            <FormGroup label="Parent Process" fieldId="parent">
              <div>
                <Button
                  data-testid="open-parent-process"
                  variant="link"
                  icon={<LevelUpAltIcon />}
                  onClick={(): void => {
                    driver.openProcessInstanceDetails(processInstance.parentProcessInstance!.id);
                  }}
                  {...componentOuiaProps(
                    ouiaId ? ouiaId : processInstance.parentProcessInstance!.id,
                    "process-details",
                    ouiaSafe
                  )}
                >
                  <ItemDescriptor
                    itemDescription={getProcessInstanceDescription(processInstance.parentProcessInstance!)}
                  />
                </Button>
              </div>
            </FormGroup>
          )}

          {processInstance.childProcessInstances && processInstance.childProcessInstances.length !== 0 && (
            <FormGroup label="Sub Processes" fieldId="child">
              {processInstance.childProcessInstances.map((child) => (
                <div key={child.id}>
                  <Button
                    variant="link"
                    icon={<LevelDownAltIcon />}
                    onClick={(): void => {
                      driver.openProcessInstanceDetails(child.id);
                    }}
                    {...componentOuiaProps(ouiaId ? ouiaId : child.id, "process-details", ouiaSafe)}
                  >
                    <ItemDescriptor itemDescription={getProcessInstanceDescription(child)} />
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

export default ProcessDetailsPanel;
