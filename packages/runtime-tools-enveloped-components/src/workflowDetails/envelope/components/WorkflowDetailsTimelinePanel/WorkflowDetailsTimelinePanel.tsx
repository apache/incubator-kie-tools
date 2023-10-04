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
import { Dropdown, KebabToggle, DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { UserIcon } from "@patternfly/react-icons/dist/js/icons/user-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { OnRunningIcon } from "@patternfly/react-icons/dist/js/icons/on-running-icon";
import { OutlinedClockIcon } from "@patternfly/react-icons/dist/js/icons/outlined-clock-icon";
import React, { useCallback, useState } from "react";
import "../styles.css";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowInstance, Job, NodeInstance } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { WorkflowInfoModal } from "@kie-tools/runtime-tools-components/dist/components/WorkflowInfoModal";
import { JobsDetailsModal } from "@kie-tools/runtime-tools-components/dist/components/JobsDetailsModal";
import { JobsRescheduleModal } from "@kie-tools/runtime-tools-components/dist/components/JobsRescheduleModal";
import { JobsCancelModal } from "@kie-tools/runtime-tools-components/dist/components/JobsCancelModal";
import { WorkflowDetailsDriver } from "../../../api";
import {
  handleJobRescheduleUtil,
  handleNodeInstanceCancel,
  handleNodeInstanceRetrigger,
  handleRetry,
  handleSkip,
  jobCancel,
} from "../../../utils/Utils";

export interface IOwnProps {
  data: WorkflowInstance;
  driver: WorkflowDetailsDriver;
  jobs: Job[];
}
enum TitleType {
  SUCCESS = "success",
  FAILURE = "failure",
}
const WorkflowDetailsTimelinePanel: React.FC<IOwnProps & OUIAProps> = ({ data, jobs, driver, ouiaId, ouiaSafe }) => {
  const [kebabOpenArray, setKebabOpenArray] = useState<string[]>([]);
  const [modalTitle, setModalTitle] = useState<JSX.Element>(<></>);
  const setCancelModalTitle = (): JSX.Element => {
    return <></>;
  };
  const [modalContent, setModalContent] = useState<string>("");
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState<boolean>(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [selectedJob, setSelectedJob] = useState<any>({});
  const ignoredNodeTypes = ["Join", "Split", "EndNode"];
  const editableJobStatus: string[] = ["SCHEDULED", "ERROR"];
  const [rescheduleError, setRescheduleError] = useState<string>("");

  const onKebabToggle = (isOpen: boolean, id: string) => {
    if (isOpen) {
      setKebabOpenArray([...kebabOpenArray, id]);
    } else {
      onDropdownSelect(id);
    }
  };

  const onDropdownSelect = (id: string) => {
    const tempKebabArray = [...kebabOpenArray];
    const index = tempKebabArray.indexOf(id);
    tempKebabArray.splice(index, 1);
    setKebabOpenArray(tempKebabArray);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleJobDetailsModalToggle = () => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const onShowMessage = (title: string, content: string, type: TitleType): void => {
    setModalTitle(setTitle(type, title));
    setModalContent(content);
    handleModalToggle();
  };
  const handleJobDetails = (job: Job): void => {
    setSelectedJob(job);
    handleDetailsToggle();
  };
  const handleDetailsToggle = (): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  };

  const handleJobRescheduleBySelected = (job: Job): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
    setSelectedJob(job);
  };

  const handleJobReschedule = async (
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<void> => {
    await handleJobRescheduleUtil(
      repeatInterval,
      repeatLimit,
      scheduleDate,
      selectedJob,
      handleRescheduleAction,
      driver,
      setRescheduleError
    );
  };

  const handleCancelAction = async (job: Job): Promise<void> => {
    await jobCancel(driver, job, setCancelModalTitle, setModalContent);
    handleCancelModalToggle();
  };

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  };

  const renderJobActions = (id: string, options: JSX.Element[]) => {
    if (jobs.length > 0) {
      return jobs.map((job) => {
        if (id === job.nodeInstanceId && editableJobStatus.includes(job.status)) {
          return [
            ...options,
            <DropdownItem
              key="job-details"
              data-testid="job-details"
              id="job-details"
              component="button"
              onClick={() => handleJobDetails(job)}
            >
              Job Details
            </DropdownItem>,
            <DropdownItem
              key="job-reschedule"
              data-testid="job-reschedule"
              id="job-reschedule"
              component="button"
              onClick={() => handleJobRescheduleBySelected(job)}
            >
              Job Reschedule
            </DropdownItem>,
            <DropdownItem
              key="job-cancel"
              data-testid="job-cancel"
              id="job-cancel"
              component="button"
              onClick={() => handleCancelAction(job)}
            >
              Job Cancel
            </DropdownItem>,
          ];
        } else if (id === job.nodeInstanceId && !editableJobStatus.includes(job.status)) {
          return [
            ...options,
            <DropdownItem key="job-details" id="job-details" component="button" onClick={() => handleJobDetails(job)}>
              Job Details
            </DropdownItem>,
          ];
        } else {
          return [];
        }
      });
    } else {
      return options;
    }
  };

  const dropdownItems = (workflowInstanceData: WorkflowInstance, node: NodeInstance): JSX.Element[] => {
    if (workflowInstanceData.error && node.definitionId === workflowInstanceData.error.nodeDefinitionId) {
      const options = [
        <DropdownItem
          data-testid="retry"
          key="retry"
          component="button"
          onClick={() =>
            handleRetry(
              workflowInstanceData,
              driver,
              () =>
                onShowMessage(
                  "Retry operation",
                  `The node ${node.name} was successfully re-executed.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  "Retry operation",
                  `The node ${node.name} failed to re-execute. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retry
        </DropdownItem>,
        <DropdownItem
          data-testid="skip"
          key="skip"
          component="button"
          onClick={() =>
            handleSkip(
              workflowInstanceData,
              driver,
              () =>
                onShowMessage("Skip operation", `The node ${node.name} was successfully skipped.`, TitleType.SUCCESS),
              (errorMessage: string) =>
                onShowMessage(
                  "Skip operation",
                  `The node ${node.name} failed to skip. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Skip
        </DropdownItem>,
      ];
      const items = renderJobActions(node.id, options);
      return items.flat();
    } else if (node.exit === null && !ignoredNodeTypes.includes(node.type)) {
      const options = [
        <DropdownItem
          data-testid="retrigger"
          key="retrigger"
          component="button"
          onClick={() =>
            handleNodeInstanceRetrigger(
              workflowInstanceData,
              driver,
              node,
              () =>
                onShowMessage(
                  "Node retrigger operation",
                  `The node ${node.name} was successfully retriggered.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  "Node retrigger operation",
                  `The node ${node.name} failed to retrigger. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retrigger node
        </DropdownItem>,
        <DropdownItem
          data-testid="cancel"
          key="cancel"
          component="button"
          onClick={() =>
            handleNodeInstanceCancel(
              workflowInstanceData,
              driver,
              node,
              () =>
                onShowMessage(
                  "Node cancel operation",
                  `The node ${node.name} was successfully canceled.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  "Node cancel operation",
                  `The node ${node.name} failed to cancel. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Cancel node
        </DropdownItem>,
      ];
      const items = renderJobActions(node.id, options);
      return items.flat();
    } else {
      const items = renderJobActions(node.id, []);
      return items.flat();
    }
  };
  const workflowManagementKebabButtons = (node: NodeInstance, index: number): JSX.Element => {
    const dropdownItemsValue: JSX.Element[] = dropdownItems(data, node);
    if (
      data.addons?.includes("workflow-management") &&
      data.serviceUrl !== null &&
      dropdownItemsValue &&
      dropdownItemsValue.length !== 0
    ) {
      return (
        <Dropdown
          onSelect={() => onDropdownSelect("timeline-kebab-toggle-" + index)}
          toggle={
            <KebabToggle
              onToggle={(isOpen) => onKebabToggle(isOpen, "timeline-kebab-toggle-" + index)}
              id={"timeline-kebab-toggle-" + index}
              data-testid={"timeline-kebab-toggle-" + index}
            />
          }
          position="right"
          isOpen={kebabOpenArray.includes("timeline-kebab-toggle-" + index)}
          isPlain
          dropdownItems={dropdownItemsValue}
        />
      );
    }
    return <></>;
  };

  const renderTimerIcon = (id: string): JSX.Element => {
    if (jobs.length > 0) {
      return jobs
        .filter((job) => id === job.nodeInstanceId)
        .map((job, idx) => {
          return (
            <Tooltip content={"Node has job"} key={idx}>
              <OutlinedClockIcon
                className="pf-u-ml-sm"
                color="var(--pf-global--icon--Color--dark)"
                onClick={() => handleJobDetails(job)}
              />
            </Tooltip>
          );
        })[0];
    }
    return <></>;
  };

  const detailsAction: JSX.Element[] = [
    <Button key="confirm-selection" variant="primary" onClick={handleModalToggle}>
      OK
    </Button>,
  ];

  const handleRescheduleAction = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const rescheduleActions: JSX.Element[] = [
    <Button key="cancel-reschedule" variant="secondary" onClick={handleRescheduleAction}>
      Cancel
    </Button>,
  ];

  const compareNodes = useCallback((nodeA, nodeB) => {
    if (nodeA?.enter < nodeB?.enter) {
      return -1;
    } else if (nodeA?.enter > nodeB?.enter) {
      return 1;
    } else if (nodeA?.exit < nodeB?.exit) {
      return -1;
    } else if (nodeA?.exit > nodeB?.exit) {
      return 1;
    } else if (nodeA?.id < nodeB?.id) {
      return -1;
    } else if (nodeA?.id > nodeB?.id) {
      return 1;
    }

    return 0;
  }, []);

  return (
    <Card {...componentOuiaProps(ouiaId ? ouiaId : data.id, "timeline", ouiaSafe)}>
      <WorkflowInfoModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalTitle={modalTitle}
        modalContent={modalContent}
      />
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Timeline
        </Title>
      </CardHeader>
      <CardBody>
        <Stack hasGutter className="kogito-workflow-details--timeline">
          {data.nodes &&
            data.nodes
              .filter((content) => !["EmbeddedStart", "EmbeddedEnd", "Script"].includes(content.name))
              .sort(compareNodes)
              .map((content, idx) => {
                return (
                  <Split hasGutter className={"kogito-workflow-details--timeline-item"} key={content.id}>
                    <SplitItem>
                      {
                        <>
                          {data.error && content.definitionId === data.error.nodeDefinitionId ? (
                            <Tooltip content={data.error.message}>
                              <ErrorCircleOIcon
                                color="var(--pf-global--danger-color--100)"
                                className="kogito-workflow-details--timeline-status"
                              />
                            </Tooltip>
                          ) : content.exit === null ? (
                            <Tooltip content={"Active"}>
                              <OnRunningIcon className="kogito-workflow-details--timeline-status" />
                            </Tooltip>
                          ) : (
                            <Tooltip content={"Completed"}>
                              <CheckCircleIcon
                                color="var(--pf-global--success-color--100)"
                                className="kogito-workflow-details--timeline-status"
                              />
                            </Tooltip>
                          )}
                        </>
                      }
                    </SplitItem>
                    <SplitItem isFilled>
                      <TextContent>
                        <Text component={TextVariants.p}>
                          {content.name}
                          <span>
                            {content.type === "HumanTaskNode" && (
                              <Tooltip content={"Human task"}>
                                <UserIcon className="pf-u-ml-sm" color="var(--pf-global--icon--Color--light)" />
                              </Tooltip>
                            )}
                            {renderTimerIcon(content.id)}
                          </span>

                          <Text component={TextVariants.small}>
                            {content.exit === null ? "Active" : <Moment fromNow>{new Date(`${content.exit}`)}</Moment>}
                          </Text>
                        </Text>
                      </TextContent>
                    </SplitItem>
                    <SplitItem>{workflowManagementKebabButtons(content, idx)}</SplitItem>
                  </Split>
                );
              })}
        </Stack>
      </CardBody>
      <JobsDetailsModal
        actionType="Job Details"
        modalTitle={setTitle("success", "Job Details")}
        isModalOpen={isDetailsModalOpen}
        handleModalToggle={handleJobDetailsModalToggle}
        modalAction={detailsAction}
        job={selectedJob}
      />
      {Object.keys(selectedJob).length > 0 && (
        <JobsRescheduleModal
          actionType="Job Reschedule"
          isModalOpen={isRescheduleModalOpen}
          handleModalToggle={handleRescheduleAction}
          modalAction={rescheduleActions}
          job={selectedJob}
          rescheduleError={rescheduleError}
          setRescheduleError={setRescheduleError}
          handleJobReschedule={handleJobReschedule}
        />
      )}
      <JobsCancelModal
        actionType="Job Cancel"
        isModalOpen={isCancelModalOpen}
        handleModalToggle={handleCancelModalToggle}
        modalTitle={modalTitle}
        modalContent={modalContent}
      />
    </Card>
  );
};

export default WorkflowDetailsTimelinePanel;
