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

import React, { useEffect, useState } from "react";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import {
  OverflowMenu,
  OverflowMenuContent,
  OverflowMenuGroup,
} from "@patternfly/react-core/dist/js/components/OverflowMenu";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { SyncIcon } from "@patternfly/react-icons/dist/js/icons/sync-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { TitleType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { WorkflowInfoModal } from "@kie-tools/runtime-tools-components/dist/components/WorkflowInfoModal";
import { WorkflowDetailsDriver } from "../../../api";
import JobsPanel from "../JobsPanel/JobsPanel";
import "../styles.css";
import WorkflowDetailsPanel from "../WorkflowDetailsPanel/WorkflowDetailsPanel";
import WorkflowDetailsNodeTrigger from "../WorkflowDetailsNodeTrigger/WorkflowDetailsNodeTrigger";
import WorkflowVariables from "../WorkflowVariables/WorkflowVariables";
import WorkflowDetailsMilestonesPanel from "../WorkflowDetailsMilestonesPanel/WorkflowDetailsMilestonesPanel";
import WorkflowDetailsTimelinePanel from "../WorkflowDetailsTimelinePanel/WorkflowDetailsTimelinePanel";
import SwfCombinedEditor from "../SwfCombinedEditor/SwfCombinedEditor";
import { Job, WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

interface WorkflowDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: WorkflowDetailsDriver;
  workflowDetails: WorkflowInstance;
}

const WorkflowDetails: React.FC<WorkflowDetailsProps> = ({ isEnvelopeConnectedToChannel, driver, workflowDetails }) => {
  const [data, setData] = useState<WorkflowInstance>({} as WorkflowInstance);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [updateJson, setUpdateJson] = useState<Record<string, unknown>>({});
  const [displayLabel, setDisplayLabel] = useState<boolean>(false);
  const [displaySuccess, setDisplaySuccess] = useState<boolean>(false);
  const [errorModalOpen, setErrorModalOpen] = useState<boolean>(false);
  const [confirmationModal, setConfirmationModal] = useState<boolean>(false);
  const [variableError, setVariableError] = useState("");
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isInfoModalOpen, setIsInfoModalOpen] = useState<boolean>(false);
  const [infoModalTitle, setInfoModalTitle] = useState<string>("");
  const [titleType, setTitleType] = useState<string>("");
  const [infoModalContent, setInfoModalContent] = useState<string>("");
  const handleReload = async (): Promise<void> => {
    setIsLoading(true);
    try {
      const workflowResponse: WorkflowInstance = await driver.workflowDetailsQuery(workflowDetails.id);
      workflowResponse && setData(workflowResponse);
      getAllJobs();
      setIsLoading(false);
    } catch (errorString) {
      setError(errorString);
      setIsLoading(false);
    }
  };

  const getAllJobs = async (): Promise<void> => {
    const jobsResponse: Job[] = await driver.jobsQuery(workflowDetails.id);
    jobsResponse && setJobs(jobsResponse);
  };

  useEffect(() => {
    const getVariableJSON = (): void => {
      if (data && data.id === workflowDetails.id) {
        setUpdateJson(typeof data.variables === "string" ? JSON.parse(data.variables) : data.variables);
      }
    };

    if (isEnvelopeConnectedToChannel) {
      getVariableJSON();
    }
  }, [data]);

  useEffect(() => {
    if (variableError && variableError.length > 0) {
      setErrorModalOpen(true);
    }
  }, [variableError]);

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      setData(workflowDetails);
      getAllJobs();
    }
  }, [isEnvelopeConnectedToChannel]);

  const handleSave = (): void => {
    driver
      .handleWorkflowVariableUpdate(data, updateJson)
      .then((updatedJson: Record<string, unknown>) => {
        setUpdateJson(updatedJson);
        setDisplayLabel(false);
        setDisplaySuccess(true);
        setTimeout(() => {
          setDisplaySuccess(false);
        }, 2000);
      })
      .catch((errorMessage: string) => {
        setVariableError(errorMessage);
      });
  };

  const updateVariablesButton = (): JSX.Element => {
    if (data.serviceUrl !== null) {
      return (
        <Button
          variant="secondary"
          id="save-button"
          className="kogito-workflow-details--details__buttonMargin"
          onClick={handleSave}
          isDisabled={!displayLabel}
          data-testid="save-button"
        >
          Save
        </Button>
      );
    }

    return <></>;
  };

  const handleRefresh = (): void => {
    if (displayLabel === true) {
      setConfirmationModal(true);
    } else {
      handleReload();
    }
  };

  const refreshButton = (): JSX.Element => {
    return (
      <Button
        variant="plain"
        onClick={() => {
          handleRefresh();
        }}
        id="refresh-button"
        data-testid="refresh-button"
        aria-label={"Refresh list"}
      >
        <SyncIcon />
      </Button>
    );
  };

  const handleInfoModalToggle = (): void => {
    setIsInfoModalOpen(!isInfoModalOpen);
  };

  const onAbortClick = async (workflowInstance: WorkflowInstance): Promise<void> => {
    try {
      await driver.handleWorkflowAbort(workflowInstance);
      setTitleType(TitleType.SUCCESS);
      setInfoModalTitle("Abort operation");
      setInfoModalContent(`The workflow ${workflowInstance.processName} was successfully aborted.`);
    } catch (abortError) {
      setTitleType(TitleType.FAILURE);
      setInfoModalTitle("Abort operation");
      setInfoModalContent(`Failed to abort workflow ${workflowInstance.processName}. Message: ${abortError.message}`);
    } finally {
      handleInfoModalToggle();
    }
  };

  const abortButton = (): JSX.Element => {
    if (
      (data.state === WorkflowInstanceState.Active ||
        data.state === WorkflowInstanceState.Error ||
        data.state === WorkflowInstanceState.Suspended) &&
      data.addons?.includes("workflow-management") &&
      data.serviceUrl !== null
    ) {
      return (
        <Button variant="secondary" id="abort-button" data-testid="abort-button" onClick={() => onAbortClick(data)}>
          Abort
        </Button>
      );
    } else {
      return (
        <Button variant="secondary" isDisabled>
          Abort
        </Button>
      );
    }
  };

  const renderSwfDiagram = (): JSX.Element => {
    return (
      <Flex>
        <FlexItem>
          <SwfCombinedEditor height={1000} width={1000} workflowInstance={data} />
        </FlexItem>
      </Flex>
    );
  };

  const renderWorkflowTimeline = (): JSX.Element => {
    return (
      <FlexItem>
        <WorkflowDetailsTimelinePanel data={data} jobs={jobs} driver={driver} />
      </FlexItem>
    );
  };

  const renderWorkflowDetails = (): JSX.Element => {
    return (
      <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
        <FlexItem>
          <WorkflowDetailsPanel workflowInstance={data} driver={driver} />
        </FlexItem>
        {data.milestones && data.milestones.length > 0 && (
          <FlexItem>
            <WorkflowDetailsMilestonesPanel milestones={data.milestones} />
          </FlexItem>
        )}
      </Flex>
    );
  };

  const renderWorkflowVariables = (): JSX.Element => {
    return (
      <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
        {Object.keys(updateJson).length > 0 && (
          <FlexItem>
            <WorkflowVariables
              displayLabel={displayLabel}
              displaySuccess={displaySuccess}
              setUpdateJson={setUpdateJson}
              setDisplayLabel={setDisplayLabel}
              updateJson={updateJson}
              workflowInstance={data}
            />
          </FlexItem>
        )}
      </Flex>
    );
  };

  const renderPanels = (): JSX.Element => {
    return (
      <Flex direction={{ default: "column" }}>
        {renderSwfDiagram()}
        <Flex>
          {renderWorkflowDetails()}
          {renderWorkflowVariables()}
        </Flex>
      </Flex>
    );
  };

  const handleConfirmationModal = (): void => {
    setConfirmationModal(!confirmationModal);
  };

  const handleConfirm = (): void => {
    window.location.reload();
    handleConfirmationModal();
  };

  const handleCancel = (): void => {
    handleConfirmationModal();
  };

  const RenderConfirmationModal = (): JSX.Element => {
    return (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes["2xl"]}>
              <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--warning-color--100)" />
              Refresh
            </Title>
          </>
        }
        variant={ModalVariant.small}
        isOpen={confirmationModal}
        onClose={handleConfirmationModal}
        actions={[
          <Button key="Ok" variant="primary" id="confirm-button" onClick={handleConfirm}>
            Ok
          </Button>,
          <Button key="Cancel" variant="link" id="cancel-button" onClick={handleCancel}>
            Cancel
          </Button>,
        ]}
        aria-label="Confirmation modal"
        aria-labelledby="Confirmation modal"
      >
        This action discards changes made on workflow variables.
      </Modal>
    );
  };

  const handleErrorModal = (): void => {
    setErrorModalOpen(!errorModalOpen);
  };

  const handleRetry = (): void => {
    handleErrorModal();
    setVariableError("");
    // tslint:disable-next-line: no-floating-promises
    handleSave();
  };

  const handleDiscard = (): void => {
    handleErrorModal();
    handleRefresh();
  };

  const errorModal = (): JSX.Element => {
    return (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes["2xl"]}>
              <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--danger-color--100)" />
              Error
            </Title>
          </>
        }
        variant={ModalVariant.small}
        isOpen={errorModalOpen}
        onClose={handleErrorModal}
        actions={[
          <Button key="Retry" variant="primary" id="retry-button" onClick={handleRetry}>
            Retry
          </Button>,
          <Button key="Discard" variant="link" id="discard-button" onClick={handleDiscard}>
            Discard
          </Button>,
        ]}
        aria-label="Error modal"
        aria-labelledby="Error modal"
      >
        {variableError}
      </Modal>
    );
  };

  return (
    <>
      {!error ? (
        <>
          {!isLoading && Object.keys(data).length > 0 ? (
            <>
              <Grid hasGutter md={1} span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Split hasGutter={true} component={"div"} className="pf-u-align-items-center">
                    <SplitItem isFilled={true}>
                      <Title headingLevel="h2" size="4xl" className="kogito-workflow-details--details__title">
                        <ItemDescriptor
                          itemDescription={{
                            id: data.id,
                            name: data.processName,
                            description: data.businessKey,
                          }}
                        />
                      </Title>
                    </SplitItem>
                    <SplitItem>
                      <OverflowMenu breakpoint="lg">
                        <OverflowMenuContent isPersistent>
                          <OverflowMenuGroup groupType="button" isPersistent>
                            <>
                              {updateVariablesButton()}
                              {abortButton()}
                              {refreshButton()}
                            </>
                          </OverflowMenuGroup>
                        </OverflowMenuContent>
                      </OverflowMenu>
                    </SplitItem>
                  </Split>
                </GridItem>
              </Grid>
              <Flex
                direction={{ default: "column", lg: "row" }}
                className="kogito-workflow-details--details__marginSpaces"
              >
                {renderPanels()}
                <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
                  {renderWorkflowTimeline()}
                  <FlexItem>
                    <JobsPanel jobs={jobs} driver={driver} />
                  </FlexItem>
                  {data.addons?.includes("workflow-management") &&
                    data.state !== WorkflowInstanceState.Completed &&
                    data.state !== WorkflowInstanceState.Aborted &&
                    data.serviceUrl &&
                    data.addons.includes("workflow-management") && (
                      <FlexItem>
                        <WorkflowDetailsNodeTrigger driver={driver} workflowInstanceData={data} />
                      </FlexItem>
                    )}
                </Flex>
                {errorModal()}
                {RenderConfirmationModal()}
              </Flex>
            </>
          ) : (
            <Card>
              <KogitoSpinner spinnerText="Loading workflow details..." />
            </Card>
          )}
        </>
      ) : (
        <>
          {isEnvelopeConnectedToChannel && (
            <Card className="kogito-workflow-details__card-size">
              <Bullseye>
                <ServerErrors error={error} variant="large" />
              </Bullseye>
            </Card>
          )}
        </>
      )}
      <WorkflowInfoModal
        isModalOpen={isInfoModalOpen}
        handleModalToggle={handleInfoModalToggle}
        modalTitle={setTitle(titleType, infoModalTitle)}
        modalContent={infoModalContent}
        workflowName={data && data.processName}
      />
    </>
  );
};

export default WorkflowDetails;
