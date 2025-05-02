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
import React, { ReactElement, useMemo, useState } from "react";
import { DropdownItem, Dropdown, KebabToggle } from "@patternfly/react-core/deprecated";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ProcessDetailsChannelApi } from "../../../api";
import { handleJobRescheduleUtil, jobCancel } from "../../../utils/Utils";
import { Job } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { JobsDetailsModal } from "../../../../jobsManagement/envelope/components/JobsDetailsModal";
import { JobsRescheduleModal } from "../../../../jobsManagement/envelope/components/JobsRescheduleModal";
import { JobsCancelModal } from "../../../../jobsManagement/envelope/components/JobsCancelModal";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

interface IOwnProps {
  job: Job;
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>;
}

const JobActionsKebab: React.FC<IOwnProps> = ({ job, channelApi }) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [rescheduleError, setRescheduleError] = useState<string>("");
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<ReactElement>();
  const [modalContent, setModalContent] = useState<string>("");
  const RescheduleJobs: string[] = ["SCHEDULED", "ERROR"];

  const handleModalToggle = (): void => {
    setIsModalOpen(!isModalOpen);
  };

  const handleCancelModalToggle = (): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  };

  const onSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = (isOpen): void => {
    setIsKebabOpen(isOpen);
  };

  const onDetailsClick = (): void => {
    handleModalToggle();
  };

  const handleRescheduleAction = (): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  };

  const handleJobReschedule = async (
    _job: Job,
    repeatInterval: string | number,
    repeatLimit: string | number,
    scheduleDate: Date
  ): Promise<void> => {
    await handleJobRescheduleUtil(
      repeatInterval,
      repeatLimit,
      scheduleDate,
      job,
      handleRescheduleAction,
      channelApi,
      setRescheduleError
    );
  };

  const handleCancelAction = async (): Promise<void> => {
    await jobCancel(channelApi, job, setModalTitle, setModalContent);
    handleCancelModalToggle();
  };

  const rescheduleActions: JSX.Element[] = [
    <Button key="cancel-reschedule" variant="secondary" onClick={handleRescheduleAction}>
      Cancel
    </Button>,
  ];

  const detailsAction: JSX.Element[] = [
    <Button key="confirm-selection" variant="primary" onClick={handleModalToggle}>
      OK
    </Button>,
  ];

  const dropdownItems = (): JSX.Element[] => {
    if (job.endpoint !== null && RescheduleJobs.includes(job.status)) {
      return [
        <DropdownItem data-testid="job-details" key="details" component="button" onClick={onDetailsClick}>
          Details
        </DropdownItem>,
        <DropdownItem
          data-testid="job-reschedule"
          key="reschedule"
          component="button"
          id="reschedule-option"
          onClick={handleRescheduleAction}
        >
          Reschedule
        </DropdownItem>,
        <DropdownItem
          data-testid="job-cancel"
          key="cancel"
          component="button"
          id="cancel-option"
          onClick={handleCancelAction}
        >
          Cancel
        </DropdownItem>,
      ];
    } else {
      return [
        <DropdownItem data-testid="job-details" key="details" component="button" onClick={onDetailsClick}>
          Details
        </DropdownItem>,
      ];
    }
  };
  return (
    <>
      <JobsDetailsModal
        actionType="Job Details"
        modalTitle={setTitle("success", "Job Details")}
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        modalAction={detailsAction}
        job={job}
      />
      <JobsRescheduleModal
        actionType="Job Reschedule"
        isModalOpen={isRescheduleModalOpen}
        handleModalToggle={handleRescheduleAction}
        modalAction={rescheduleActions}
        job={job}
        rescheduleError={rescheduleError}
        setRescheduleError={setRescheduleError}
        handleJobReschedule={handleJobReschedule}
      />
      <JobsCancelModal
        actionType="Job Cancel"
        isModalOpen={isCancelModalOpen}
        handleModalToggle={handleCancelModalToggle}
        modalTitle={modalTitle!}
        modalContent={modalContent}
      />

      <Dropdown
        onSelect={onSelect}
        toggle={<KebabToggle onToggle={(_event, isOpen) => onToggle(isOpen)} id="kebab-toggle" />}
        isOpen={isKebabOpen}
        isPlain
        position="right"
        aria-label="Job actions dropdown"
        aria-labelledby="Job actions dropdown"
        dropdownItems={dropdownItems()}
        className="job-actions-kebab"
      />
    </>
  );
};

export default JobActionsKebab;
