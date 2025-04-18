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
import React, { ReactElement, useCallback, useEffect, useMemo, useState } from "react";
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
import { DiagramPreviewSize, ProcessDetailsDriver } from "../../../api";
import ProcessDiagram from "../ProcessDiagram/ProcessDiagram";
import JobsPanel from "../JobsPanel/JobsPanel";
import ProcessDetailsErrorModal from "../ProcessDetailsErrorModal/ProcessDetailsErrorModal";
import SVG from "react-inlinesvg";
import "../styles.css";
import ProcessDetailsPanel from "../ProcessDetailsPanel/ProcessDetailsPanel";
import ProcessDetailsNodeTrigger from "../ProcessDetailsNodeTrigger/ProcessDetailsNodeTrigger";
import ProcessVariables from "../ProcessVariables/ProcessVariables";
import ProcessDetailsMilestonesPanel from "../ProcessDetailsMilestonesPanel/ProcessDetailsMilestonesPanel";
import ProcessDetailsTimelinePanel from "../ProcessDetailsTimelinePanel/ProcessDetailsTimelinePanel";
import {
  SvgErrorResponse,
  SvgSuccessResponse,
  TitleType,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { ItemDescriptor } from "@kie-tools/runtime-tools-components/dist/components/ItemDescriptor";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { ProcessInfoModal } from "@kie-tools/runtime-tools-components/dist/components/ProcessInfoModal";
import { Job, ProcessInstance, ProcessInstanceState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

interface ProcessDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: ProcessDetailsDriver;
  processDetails: ProcessInstance;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  showSwfDiagram: boolean;
  singularProcessLabel: string;
  pluralProcessLabel: string;
}

type svgResponse = SvgSuccessResponse | SvgErrorResponse;

const ProcessDetails: React.FC<ProcessDetailsProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  processDetails,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  showSwfDiagram,
  singularProcessLabel,
  pluralProcessLabel,
}) => {
  const [data, setData] = useState<ProcessInstance>({} as ProcessInstance);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [updateJson, setUpdateJson] = useState<any>({});
  const [displayLabel, setDisplayLabel] = useState<boolean>(false);
  const [displaySuccess, setDisplaySuccess] = useState<boolean>(false);
  const [errorModalOpen, setErrorModalOpen] = useState<boolean>(false);
  const [isConfirmationModalOpen, setIsConfirmationModalOpen] = useState<boolean>(false);
  const [variableError, setVariableError] = useState("");
  const [svg, setSvg] = useState<JSX.Element>();
  const [svgError, setSvgError] = useState<string>("");
  const [error, setError] = useState<string>("");
  const [svgErrorModalOpen, setSvgErrorModalOpen] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isInfoModalOpen, setIsInfoModalOpen] = useState<boolean>(false);
  const [infoModalTitle, setInfoModalTitle] = useState<string>("");
  const [titleType, setTitleType] = useState<string>("");
  const [infoModalContent, setInfoModalContent] = useState<string>("");

  const loadJobs = useCallback(async () => {
    const jobsResponse: Job[] = await driver.jobsQuery(processDetails.id);
    jobsResponse && setJobs(jobsResponse);
  }, [processDetails.id, driver]);

  const handleReload = useCallback(async () => {
    setIsLoading(true);
    try {
      const processResponse: ProcessInstance = await driver.processDetailsQuery(processDetails.id);
      processResponse && setData(processResponse);
      loadJobs();
      setIsLoading(false);
    } catch (errorString) {
      setError(errorString);
      setIsLoading(false);
    }
  }, [driver, loadJobs, processDetails.id]);

  const handleSvgErrorModal = useCallback(() => {
    setSvgErrorModalOpen((currentSvgErrorModalOpen) => !currentSvgErrorModalOpen);
  }, []);

  useEffect(() => {
    const handleSvgApi = async (): Promise<void> => {
      if (data && data.id === processDetails.id) {
        const response: svgResponse = await driver.getProcessDiagram(data);
        if (response && response.svg) {
          const temp = <SVG src={response.svg} />;
          setSvg(temp);
        } else if (response && response.error) {
          setSvgError(response.error);
        }
      }
    };
    const getVariableJSON = (): void => {
      if (data && data.id === processDetails.id) {
        setUpdateJson(data.variables);
      }
    };
    if (isEnvelopeConnectedToChannel) {
      handleSvgApi();
      getVariableJSON();
    }
  }, [driver, data, isEnvelopeConnectedToChannel, processDetails.id]);

  useEffect(() => {
    if (svgError && svgError.length > 0 && !showSwfDiagram) {
      setSvgErrorModalOpen(true);
    }
  }, [svgError, showSwfDiagram]);

  useEffect(() => {
    if (variableError && variableError.length > 0) {
      setErrorModalOpen(true);
    }
  }, [variableError]);

  useEffect(() => {
    if (isEnvelopeConnectedToChannel) {
      setData(processDetails);
      loadJobs();
    }
  }, [isEnvelopeConnectedToChannel, loadJobs, processDetails]);

  const handleSave = useCallback(async () => {
    return driver
      .handleProcessVariableUpdate(data, updateJson)
      .then((updatedJson: Record<string, unknown>) => {
        setUpdateJson(updatedJson);
        setDisplayLabel(false);
        setDisplaySuccess(true);
        setTimeout(() => {
          setDisplaySuccess(false);
        }, 2000);
      })
      .catch((errorMessage) => {
        setVariableError(errorMessage?.message ?? "Failed to save process instance changes.");
      });
  }, [data, driver, updateJson]);

  const updateVariablesButton = useMemo(() => {
    if (data.serviceUrl !== null) {
      return (
        <Button
          variant="secondary"
          id="save-button"
          className="kogito-process-details--details__buttonMargin"
          onClick={handleSave}
          isDisabled={!displayLabel}
          data-testid="save-button"
        >
          Save
        </Button>
      );
    }
    return <></>;
  }, [data.serviceUrl, displayLabel, handleSave]);

  const handleRefresh = useCallback(() => {
    if (displayLabel) {
      setIsConfirmationModalOpen(true);
    } else {
      handleReload();
    }
  }, [displayLabel, handleReload]);

  const refreshButton = useMemo(
    () => (
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
    ),
    [handleRefresh]
  );

  const handleInfoModalToggle = useCallback(() => {
    setIsInfoModalOpen((currentValue) => !currentValue);
  }, []);

  const onAbortClick = useCallback(
    async (processInstance: ProcessInstance): Promise<void> => {
      try {
        await driver.handleProcessAbort(processInstance);
        setTitleType(TitleType.SUCCESS);
        setInfoModalTitle("Abort operation");
        setInfoModalContent(
          `The ${singularProcessLabel.toLowerCase()} ${processInstance.processName} was successfully aborted.`
        );
      } catch (abortError) {
        setTitleType(TitleType.FAILURE);
        setInfoModalTitle("Abort operation");
        setInfoModalContent(
          `Failed to abort ${singularProcessLabel.toLowerCase()} ${processInstance.processName}. Message: ${
            abortError.message
          }`
        );
      } finally {
        setIsInfoModalOpen(true);
        handleReload();
      }
    },
    [driver, singularProcessLabel, handleReload]
  );

  const abortButton = useMemo(() => {
    if (
      (data.state === ProcessInstanceState.Active ||
        data.state === ProcessInstanceState.Error ||
        data.state === ProcessInstanceState.Suspended) &&
      data.addons!.includes("process-management") &&
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
  }, [data, onAbortClick]);

  const processDiagramBlock = useMemo(
    () => (
      <Flex>
        <FlexItem>
          {svg && svg.props.src && (
            <Card>
              {" "}
              <ProcessDiagram svg={svg} width={diagramPreviewSize?.width} height={diagramPreviewSize?.height} />{" "}
            </Card>
          )}
        </FlexItem>
      </Flex>
    ),
    [diagramPreviewSize?.height, diagramPreviewSize?.width, svg]
  );

  const processTimelineBlock = useMemo(
    () => (
      <FlexItem>
        <ProcessDetailsTimelinePanel
          data={data}
          jobs={jobs}
          driver={driver}
          omittedProcessTimelineEvents={omittedProcessTimelineEvents}
        />
      </FlexItem>
    ),
    [data, driver, jobs, omittedProcessTimelineEvents]
  );

  const processDetailsBlock = useMemo(
    () => (
      <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
        <FlexItem>
          <ProcessDetailsPanel processInstance={data} driver={driver} />
        </FlexItem>
        {data.milestones && data.milestones.length > 0 && (
          <FlexItem>
            <ProcessDetailsMilestonesPanel milestones={data.milestones} />
          </FlexItem>
        )}
      </Flex>
    ),
    [data, driver]
  );

  const processVariablesBlock = useMemo(
    () => (
      <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
        {updateJson && Object.keys(updateJson).length > 0 && (
          <FlexItem>
            <ProcessVariables
              displayLabel={displayLabel}
              displaySuccess={displaySuccess}
              setUpdateJson={setUpdateJson}
              setDisplayLabel={setDisplayLabel}
              updateJson={updateJson}
              processInstance={data}
            />
          </FlexItem>
        )}
      </Flex>
    ),
    [data, displayLabel, displaySuccess, updateJson]
  );

  const panels = useMemo(() => {
    if (svg && svg.props.src) {
      return (
        <Flex direction={{ default: "column" }}>
          {processDiagramBlock}
          <Flex>
            {processDetailsBlock}
            {processVariablesBlock}
          </Flex>
        </Flex>
      );
    } else {
      return (
        <>
          {processDetailsBlock}
          {processVariablesBlock}
        </>
      );
    }
  }, [processDetailsBlock, processVariablesBlock, processDiagramBlock, svg]);

  const handleConfirm = useCallback(() => {
    handleReload();
    setDisplayLabel(false);
    setIsConfirmationModalOpen(false);
  }, [handleReload]);

  const handleCancel = useCallback(() => {
    setVariableError("");
    handleReload();
    setIsConfirmationModalOpen(false);
  }, [handleReload]);

  const confirmationModal = useMemo(
    () => (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes["2xl"]}>
              <InfoCircleIcon className="pf-v5-u-mr-sm" color="var(--pf-v5-global--warning-color--100)" />
              Refresh
            </Title>
          </>
        }
        variant={ModalVariant.small}
        isOpen={isConfirmationModalOpen}
        onClose={handleCancel}
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
        This action discards changes made on process variables.
      </Modal>
    ),
    [handleCancel, handleConfirm, isConfirmationModalOpen]
  );

  const handleErrorModal = useCallback(() => {
    setVariableError("");
    setErrorModalOpen(false);
  }, []);

  const handleRetry = useCallback(() => {
    handleErrorModal();
    handleSave();
  }, [handleSave, handleErrorModal]);

  const handleDiscard = useCallback(() => {
    handleErrorModal();
    handleRefresh();
  }, [handleRefresh, handleErrorModal]);

  const errorModal = useMemo(
    () => (
      <Modal
        title=""
        header={
          <>
            <Title headingLevel="h1" size={TitleSizes["2xl"]}>
              <InfoCircleIcon className="pf-v5-u-mr-sm" color="var(--pf-v5-global--danger-color--100)" />
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
    ),
    [errorModalOpen, handleDiscard, handleErrorModal, handleRetry, variableError]
  );

  return (
    <>
      {!error ? (
        <>
          {!isLoading && Object.keys(data).length > 0 ? (
            <>
              <Grid hasGutter md={1} span={12} lg={6} xl={4}>
                <GridItem span={12}>
                  <Split hasGutter={true} component={"div"} className="pf-v5-u-align-items-center">
                    <SplitItem isFilled={true}>
                      <Title headingLevel="h2" size="4xl" className="kogito-process-details--details__title">
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
                              {updateVariablesButton}
                              {abortButton}
                              {refreshButton}
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
                className="kogito-process-details--details__marginSpaces"
              >
                {panels}
                <Flex direction={{ default: "column" }} flex={{ default: "flex_1" }}>
                  {processTimelineBlock}
                  <FlexItem>
                    <JobsPanel jobs={jobs} driver={driver} />
                  </FlexItem>
                  {data.addons?.includes("process-management") &&
                    data.state !== ProcessInstanceState.Completed &&
                    data.state !== ProcessInstanceState.Aborted &&
                    data.serviceUrl &&
                    data.addons.includes("process-management") && (
                      <FlexItem>
                        <ProcessDetailsNodeTrigger driver={driver} processInstanceData={data} />
                      </FlexItem>
                    )}
                </Flex>
                {errorModal}
                {confirmationModal}
              </Flex>
            </>
          ) : (
            <Card>
              <KogitoSpinner spinnerText="Loading process details..." />
            </Card>
          )}
          {svgErrorModalOpen && (
            <ProcessDetailsErrorModal
              errorString={svgError}
              errorModalOpen={svgErrorModalOpen}
              errorModalAction={[
                <Button
                  data-testid="svg-error-modal"
                  key="confirm-selection"
                  variant="primary"
                  onClick={handleSvgErrorModal}
                >
                  OK
                </Button>,
              ]}
              handleErrorModal={handleSvgErrorModal}
              label="svg error modal"
              title={setTitle("failure", "Process Diagram")}
            />
          )}
        </>
      ) : (
        <>
          {isEnvelopeConnectedToChannel && (
            <Card className="kogito-process-details__card-size">
              <Bullseye>
                <ServerErrors error={error} variant="large" />
              </Bullseye>
            </Card>
          )}
        </>
      )}
      <ProcessInfoModal
        isModalOpen={isInfoModalOpen}
        handleModalToggle={handleInfoModalToggle}
        modalTitle={setTitle(titleType, infoModalTitle)}
        modalContent={infoModalContent}
        processName={data && data.processName}
      />
    </>
  );
};

export default ProcessDetails;
