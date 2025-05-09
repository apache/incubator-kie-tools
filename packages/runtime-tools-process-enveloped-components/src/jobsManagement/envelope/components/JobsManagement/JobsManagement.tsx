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
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { ISortBy } from "@patternfly/react-table/dist/js/components/Table";
import { LoadMore } from "@kie-tools/runtime-tools-components/dist/components/LoadMore";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { BulkCancel, Job, JobStatus, JobsSortBy } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  BulkListItem,
  BulkListType,
  IOperationResults,
  IOperations,
} from "@kie-tools/runtime-tools-components/dist/components/BulkList";
import { OperationType, OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { JobsManagementChannelApi, JobsManagementState } from "../../../api";
import JobsManagementTable from "../JobsManagementTable/JobsManagementTable";
import JobsManagementToolbar from "../JobsManagementToolbar/JobsManagementToolbar";
import "../styles.css";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { JobsDetailsModal } from "../JobsDetailsModal";
import { JobsRescheduleModal } from "../JobsRescheduleModal";
import { JobsCancelModal } from "../JobsCancelModal";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";

export const formatForBulkListJob = (jobsList: (Job & { errorMessage?: string })[]): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  jobsList.forEach((item: Job & { errorMessage?: string }) => {
    const formattedObj: BulkListItem = {
      id: item.id,
      name: item.processId,
      description: item.id,
      errorMessage: item.errorMessage ? item.errorMessage : undefined,
    };
    formattedItems.push(formattedObj);
  });
  return formattedItems;
};

interface JobsManagementProps {
  isEnvelopeConnectedToChannel: boolean;
  channelApi: MessageBusClientApi<JobsManagementChannelApi>;
  initialState?: JobsManagementState;
}

const defaultPageSize: number = 10;
const defaultSortBy: ISortBy = { index: 6, direction: "asc" };

const JobsManagement: React.FC<JobsManagementProps> = ({ channelApi, isEnvelopeConnectedToChannel, initialState }) => {
  const defaultStatus: JobStatus[] = useMemo(
    () => (initialState && initialState.filters ? [...initialState.filters] : [JobStatus.Scheduled]),
    [initialState]
  );

  const defaultOrderBy: JobsSortBy = useMemo(
    () =>
      initialState && initialState.orderBy
        ? initialState.orderBy
        : {
            lastUpdate: OrderBy.DESC,
          },
    [initialState]
  );

  const [chips, setChips] = useState<JobStatus[]>(defaultStatus);
  const [selectedStatus, setSelectedStatus] = useState<JobStatus[]>(defaultStatus);
  const [selectedJobInstances, setSelectedJobInstances] = useState<Job[]>([]);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState("");
  const [isActionPerformed, setIsActionPerformed] = useState<boolean>(false);
  const [modalTitle, setModalTitle] = useState<JSX.Element>();
  const [modalContent, setModalContent] = useState<string>("");
  const [sortBy, setSortBy] = useState<ISortBy>(defaultSortBy);
  const [orderBy, setOrderBy] = useState<JobsSortBy>(defaultOrderBy);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [offset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState<boolean>(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState<boolean>(false);
  const [isRescheduleModalOpen, setIsRescheduleModalOpen] = useState<boolean>(false);
  const [rescheduleError, setRescheduleError] = useState<string>("");
  const [selectedJob, setSelectedJob] = useState<any>({});
  const [jobOperationResults, setJobOperationResults] = useState<IOperationResults>({
    CANCEL: {
      successItems: [],
      failedItems: [],
      ignoredItems: [],
    },
  });
  const [isInitialLoadDone, setIsInitialLoadDone] = useState(false);

  const doQueryJobs = useCallback(
    async (_offset: number, _limit: number) => {
      try {
        const jobsResponse: Job[] = await channelApi.requests.jobList__query(_offset, _limit);
        setLimit(jobsResponse.length);
        setJobs((currentJobs) => {
          if (_offset > 0 && currentJobs.length > 0) {
            const tempData: Job[] = currentJobs.concat(jobsResponse);
            return tempData;
          } else {
            return jobsResponse;
          }
        });
      } catch (err) {
        setError(err);
      }
    },
    [channelApi.requests]
  );

  const onRefresh = useCallback(async () => {
    setIsLoading(true);
    await channelApi.requests.jobList__applyFilter(selectedStatus);
    await channelApi.requests.jobList__sortBy(orderBy);
    setOffset(0);
    await doQueryJobs(0, 10);
    setIsLoading(false);
  }, [doQueryJobs, channelApi.requests, orderBy, selectedStatus]);

  const onApplyFilter = useCallback(async () => {
    setIsLoading(true);
    await channelApi.requests.jobList__applyFilter(selectedStatus);
    await channelApi.requests.jobList__sortBy(orderBy);
    setChips(selectedStatus);
    setOffset(0);
    await doQueryJobs(0, 10);
    setIsLoading(false);
  }, [doQueryJobs, channelApi.requests, orderBy, selectedStatus]);

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsInitialLoadDone(false);
    }
  }, [isEnvelopeConnectedToChannel]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isEnvelopeConnectedToChannel) {
          setIsLoading(true);
          setSelectedStatus(defaultStatus);
          setChips(defaultStatus);
          setOrderBy(defaultOrderBy);
          if (canceled.get()) {
            return;
          }
          channelApi.requests
            .jobList__initialLoad(defaultStatus, defaultOrderBy)
            .then(() => {
              if (canceled.get()) {
                return;
              }
              return doQueryJobs(0, 10);
            })
            .then(() => {
              if (canceled.get()) {
                return;
              }
              setIsLoading(false);
              setIsInitialLoadDone(true);
            });
        }
      },
      [defaultOrderBy, defaultStatus, doQueryJobs, channelApi.requests, isEnvelopeConnectedToChannel]
    )
  );

  const handleCancelModalToggle = useCallback((): void => {
    setIsCancelModalOpen(!isCancelModalOpen);
  }, [isCancelModalOpen]);

  const handleCancelModalCloseToggle = useCallback(async () => {
    setIsCancelModalOpen(!isCancelModalOpen);
    setIsLoading(true);
    await doQueryJobs(0, 10);
    setIsLoading(false);
  }, [doQueryJobs, isCancelModalOpen]);

  const handleDetailsToggle = useCallback((): void => {
    setIsDetailsModalOpen(!isDetailsModalOpen);
  }, [isDetailsModalOpen]);

  const handleRescheduleToggle = useCallback((): void => {
    setIsRescheduleModalOpen(!isRescheduleModalOpen);
  }, [isRescheduleModalOpen]);

  const onGetMoreInstances = useCallback(
    async (initVal: number, _pageSize: number): Promise<void> => {
      setIsLoadingMore(true);
      setOffset(initVal);
      setPageSize(_pageSize);
      await channelApi.requests.jobList__initialLoad(selectedStatus, orderBy);
      await doQueryJobs(initVal, _pageSize);
      setIsLoadingMore(false);
    },
    [doQueryJobs, channelApi.requests, orderBy, selectedStatus]
  );

  const handleBulkCancel = useCallback(
    (cancelResults: BulkCancel, ignoredJobs: Job[]): void => {
      setIsActionPerformed(true);
      setModalTitle(setTitle("success", "Job Cancel"));
      setModalContent("");
      setJobOperationResults({
        ...jobOperationResults,
        [OperationType.CANCEL]: {
          ...jobOperationResults[OperationType.CANCEL],
          successItems: formatForBulkListJob(cancelResults.successJobs),
          failedItems: formatForBulkListJob(cancelResults.failedJobs),
          ignoredItems: formatForBulkListJob(ignoredJobs),
        },
      });
      handleCancelModalToggle();
    },
    [handleCancelModalToggle, jobOperationResults]
  );

  const jobOperations: IOperations = useMemo(
    () => ({
      CANCEL: {
        type: BulkListType.JOB,
        results: jobOperationResults[OperationType.CANCEL],
        messages: {
          successMessage: "Canceled jobs: ",
          noItemsMessage: "No jobs were canceled",
          warningMessage:
            "Note: The job status has been updated. The list may appear inconsistent until you refresh any applied filters.",
          ignoredMessage: "These jobs were ignored because they were already canceled or executed.",
        },
        functions: {
          perform: async () => {
            const ignoredJobs: Job[] = [];
            const remainingInstances = selectedJobInstances.filter((job) => {
              if (job.status === JobStatus.Canceled || job.status === JobStatus.Executed) {
                ignoredJobs.push(job);
              } else {
                return true;
              }
            });
            const cancelResults = await channelApi.requests.jobList__bulkCancel(remainingInstances);
            handleBulkCancel(cancelResults, ignoredJobs);
          },
        },
      },
    }),
    [channelApi.requests, handleBulkCancel, jobOperationResults, selectedJobInstances]
  );

  const detailsAction: JSX.Element[] = useMemo(
    () => [
      <Button key="confirm-selection" variant="primary" onClick={handleDetailsToggle}>
        OK
      </Button>,
    ],
    [handleDetailsToggle]
  );

  const rescheduleActions: JSX.Element[] = useMemo(
    () => [
      <Button key="cancel-reschedule" variant="secondary" onClick={handleRescheduleToggle}>
        Cancel
      </Button>,
    ],
    [handleRescheduleToggle]
  );

  const onResetToDefault = useCallback(async () => {
    const defaultState: any = {
      filters: ["SCHEDULED"],
      orderBy: { lastUpdate: "ASC" },
    };
    setSelectedStatus(defaultState.filters);
    setChips(defaultState.filters);
    setOrderBy(defaultState.orderBy);
    setIsLoading(true);
    await channelApi.requests.jobList__initialLoad(defaultState.filters, defaultState.orderBy);
    await doQueryJobs(0, 10);
    setIsLoading(false);
  }, [doQueryJobs, channelApi.requests]);

  const handleJobReschedule = useCallback(
    async (job: Job, repeatInterval: string | number, repeatLimit: string | number, scheduleDate: Date) => {
      const response = await channelApi.requests.jobList__rescheduleJob(job, repeatInterval, repeatLimit, scheduleDate);
      setIsLoading(true);
      if (response && response.modalTitle === "success") {
        handleRescheduleToggle();
        await doQueryJobs(0, 10);
      } else if (response && response.modalTitle === "failure") {
        handleRescheduleToggle();
        setRescheduleError(response.modalContent);
        await doQueryJobs(0, 10);
      }
      setIsLoading(false);
    },
    [doQueryJobs, channelApi.requests, handleRescheduleToggle]
  );

  return (
    <div>
      {error.length === 0 ? (
        <>
          <JobsManagementToolbar
            chips={chips}
            onResetToDefault={onResetToDefault}
            onApplyFilter={onApplyFilter}
            jobOperations={jobOperations}
            onRefresh={onRefresh}
            selectedStatus={selectedStatus}
            selectedJobInstances={selectedJobInstances}
            setChips={setChips}
            setSelectedJobInstances={setSelectedJobInstances}
            setSelectedStatus={setSelectedStatus}
          />
          <Divider />
          {isLoading ? (
            <Card>
              <KogitoSpinner spinnerText="Loading Jobs..." />
            </Card>
          ) : isInitialLoadDone && selectedStatus.length === 0 ? (
            <div className="kogito-jobs-management__emptyState">
              <KogitoEmptyState
                type={KogitoEmptyStateType.Reset}
                title="No filter applied."
                body="Try applying at least one filter to see results"
                onClick={() => onResetToDefault()}
              />
            </div>
          ) : (
            <JobsManagementTable
              jobs={jobs}
              channelApi={channelApi.requests}
              doQueryJobs={doQueryJobs}
              handleCancelModalToggle={handleCancelModalToggle}
              handleDetailsToggle={handleDetailsToggle}
              handleRescheduleToggle={handleRescheduleToggle}
              isActionPerformed={isActionPerformed}
              isLoading={isLoadingMore ? false : isLoading}
              setIsActionPerformed={setIsActionPerformed}
              selectedJobInstances={selectedJobInstances}
              setModalTitle={setModalTitle}
              setModalContent={setModalContent}
              setSelectedJobInstances={setSelectedJobInstances}
              setSelectedJob={setSelectedJob}
              setSortBy={setSortBy}
              sortBy={sortBy}
              setOrderBy={setOrderBy}
            />
          )}
          {isInitialLoadDone && (!isLoading || isLoadingMore) && (limit === pageSize || isLoadingMore) && (
            <LoadMore
              offset={offset}
              setOffset={setOffset}
              getMoreItems={onGetMoreInstances}
              pageSize={pageSize}
              isLoadingMore={isLoadingMore}
            />
          )}
        </>
      ) : (
        <ServerErrors error={error} variant="large" />
      )}
      {selectedJob && Object.keys(selectedJob).length > 0 && (
        <JobsDetailsModal
          actionType="Job Details"
          modalTitle={setTitle("success", "Job Details")}
          isModalOpen={isDetailsModalOpen}
          handleModalToggle={handleDetailsToggle}
          modalAction={detailsAction}
          job={selectedJob}
        />
      )}
      {selectedJob && Object.keys(selectedJob).length > 0 && (
        <JobsRescheduleModal
          actionType="Job Reschedule"
          isModalOpen={isRescheduleModalOpen}
          handleModalToggle={handleRescheduleToggle}
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
        handleModalToggle={handleCancelModalCloseToggle}
        modalTitle={modalTitle ?? <></>}
        modalContent={modalContent}
        jobOperations={jobOperations[OperationType.CANCEL]}
      />
    </div>
  );
};

export default JobsManagement;
