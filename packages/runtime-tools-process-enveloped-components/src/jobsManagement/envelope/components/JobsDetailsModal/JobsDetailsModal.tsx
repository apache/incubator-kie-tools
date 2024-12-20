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

import React, { useCallback } from "react";
import { TextContent, Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import Moment from "react-moment";
import "../styles.css";
import { Job } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface IOwnProps {
  actionType: string;
  modalTitle: JSX.Element;
  isModalOpen: boolean;
  handleModalToggle: () => void;
  modalAction: JSX.Element[];
  job: Job;
}
export const JobsDetailsModal: React.FC<IOwnProps & OUIAProps> = ({
  actionType,
  modalTitle,
  isModalOpen,
  modalAction,
  handleModalToggle,
  job,
  ouiaId,
  ouiaSafe,
}) => {
  const checkNumericProperty = useCallback((propertyValue?: number) => {
    return propertyValue !== undefined && propertyValue !== null;
  }, []);

  const modalContent = useCallback(() => {
    return (
      <div className="kogito-management-console-shared--jobsModal__detailsModal">
        <TextContent>
          <Flex direction={{ default: "column" }}>
            {job.processId && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Process Id: </Text>{" "}
                  </SplitItem>
                  <SplitItem>{job.processId}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.processInstanceId && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    {" "}
                    <Text component={TextVariants.h6}>Process Instance Id: </Text>{" "}
                  </SplitItem>
                  <SplitItem>{job.processInstanceId}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.nodeInstanceId && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Node Instance Id: </Text>
                  </SplitItem>
                  <SplitItem>{job.nodeInstanceId}</SplitItem>
                </Split>
              </FlexItem>
            )}
            <FlexItem>
              <Split hasGutter>
                <SplitItem>
                  <Text component={TextVariants.h6}>Status: </Text>{" "}
                </SplitItem>
                <SplitItem>{job.status}</SplitItem>
              </Split>
            </FlexItem>
            {checkNumericProperty(job.priority) && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Priority: </Text>{" "}
                  </SplitItem>
                  <SplitItem>{job.priority}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {checkNumericProperty(job.repeatInterval) && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Repeat Interval: </Text>
                  </SplitItem>
                  <SplitItem>{job.repeatInterval}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {checkNumericProperty(job.repeatLimit) && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Repeat Limit: </Text>
                  </SplitItem>
                  <SplitItem>{job.repeatLimit}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.scheduledId && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Scheduled Id: </Text>
                  </SplitItem>
                  <SplitItem>{job.scheduledId}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {checkNumericProperty(job.retries) && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Retries: </Text>
                  </SplitItem>
                  <SplitItem>{job.retries}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {checkNumericProperty(job.executionCounter) && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Execution counter: </Text>
                  </SplitItem>
                  <SplitItem>{job.executionCounter}</SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.lastUpdate && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6}>Last Updated: </Text>
                  </SplitItem>
                  <SplitItem>
                    <Moment fromNow>{new Date(`${job.lastUpdate}`)}</Moment>
                  </SplitItem>
                </Split>
              </FlexItem>
            )}
            {job.callbackEndpoint && (
              <FlexItem>
                <Split hasGutter>
                  <SplitItem>
                    <Text component={TextVariants.h6} className="kogito-management-console-shared--jobsModal__text">
                      Callback Endpoint:{" "}
                    </Text>
                  </SplitItem>
                  <SplitItem>{job.callbackEndpoint}</SplitItem>
                </Split>
              </FlexItem>
            )}
          </Flex>
        </TextContent>
      </div>
    );
  }, [job, checkNumericProperty]);
  return (
    <Modal
      variant={"large"}
      aria-labelledby={actionType + "modal"}
      aria-label={actionType + "modal"}
      title=""
      header={modalTitle}
      isOpen={isModalOpen}
      onClose={handleModalToggle}
      actions={modalAction}
      {...componentOuiaProps(ouiaId, "job-details-modal", ouiaSafe)}
    >
      {modalContent()}
    </Modal>
  );
};
