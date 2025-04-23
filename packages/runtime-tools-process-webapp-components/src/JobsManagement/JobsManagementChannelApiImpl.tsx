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

import {
  Job,
  JobStatus,
  BulkCancel,
  JobOperationResult,
  JobsSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  JobsManagementChannelApi,
  JobsManagementState,
  OnUpdateJobsManagementStateListener,
  UnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/jobsManagement";
import ApolloClient from "apollo-client";
import {
  getJobsWithFilters,
  handleJobReschedule,
  jobCancel,
  performMultipleCancel,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export class JobsManagementChannelApiImpl implements JobsManagementChannelApi {
  private readonly onUpdateJobsManagementStateListeners: OnUpdateJobsManagementStateListener[] = [];
  private _jobsManagementState: JobsManagementState;

  constructor(private readonly client: ApolloClient<any>) {
    this._jobsManagementState = { filters: [], orderBy: {} };
  }

  jobList__initialLoad(filter: JobStatus[], orderBy: JobsSortBy): Promise<void> {
    this._jobsManagementState.filters = filter;
    this._jobsManagementState.orderBy = orderBy;
    return Promise.resolve();
  }

  jobList__applyFilter(filter: JobStatus[]): Promise<void> {
    this._jobsManagementState.filters = filter;
    this.onUpdateJobsManagementStateListeners.forEach((listener) => listener.onUpdate(this._jobsManagementState));
    return Promise.resolve();
  }

  jobList__bulkCancel(jobsToBeActioned: Job[]): Promise<BulkCancel> {
    return performMultipleCancel(jobsToBeActioned, this.client);
  }

  jobList__cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobOperationResult> {
    return jobCancel(job, this.client);
  }

  jobList__rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate, this.client);
  }

  jobList__sortBy(orderBy: JobsSortBy): Promise<void> {
    this._jobsManagementState.orderBy = orderBy;
    this.onUpdateJobsManagementStateListeners.forEach((listener) => listener.onUpdate(this._jobsManagementState));
    return Promise.resolve();
  }

  jobList__query(offset: number, limit: number): Promise<Job[]> {
    return getJobsWithFilters(
      offset,
      limit,
      this._jobsManagementState.filters,
      this._jobsManagementState.orderBy,
      this.client
    );
  }

  jobList__onUpdateJobsManagementState(listener: OnUpdateJobsManagementStateListener): Promise<UnSubscribeHandler> {
    this.onUpdateJobsManagementStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateJobsManagementStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateJobsManagementStateListeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }
}
