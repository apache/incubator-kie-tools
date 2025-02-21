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
  JobsManagementState,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { JobsManagementQueries } from "./JobsManagementQueries";

export interface JobsManagementGatewayApi {
  jobsManagementState: any;
  initialLoad: (filter: JobStatus[], orderBy: JobsSortBy) => Promise<void>;
  applyFilter: (filter: JobStatus[]) => Promise<void>;
  bulkCancel: (jobsToBeActioned: Job[]) => Promise<BulkCancel>;
  cancelJob: (job: Pick<Job, "id" | "endpoint">) => Promise<JobOperationResult>;
  rescheduleJob: (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  sortBy: (orderBy: JobsSortBy) => Promise<void>;
  query(offset: number, limit: number): Promise<Job[]>;
  onUpdateJobsManagementState: (listener: OnUpdateJobsManagementStateListener) => UnSubscribeHandler;
}

export interface OnUpdateJobsManagementStateListener {
  onUpdate: (jobsManagementState: JobsManagementState) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class JobsManagementGatewayApiImpl implements JobsManagementGatewayApi {
  private readonly queries: JobsManagementQueries;
  private readonly onUpdateJobsManagementStateListeners: OnUpdateJobsManagementStateListener[] = [];
  private _JobsManagementState: JobsManagementState;

  constructor(queries: JobsManagementQueries) {
    this.queries = queries;
    this._JobsManagementState = { filters: [], orderBy: {} };
  }

  get jobsManagementState(): JobsManagementState {
    return this._JobsManagementState;
  }

  initialLoad = (filter: JobStatus[], orderBy: JobsSortBy): Promise<any> => {
    this._JobsManagementState.filters = filter;
    this._JobsManagementState.orderBy = orderBy;
    return Promise.resolve();
  };

  applyFilter = (filter: JobStatus[]): Promise<void> => {
    this._JobsManagementState.filters = filter;
    this.onUpdateJobsManagementStateListeners.forEach((listener) => listener.onUpdate(this._JobsManagementState));
    return Promise.resolve();
  };

  cancelJob = async (job: Pick<Job, "id" | "endpoint">): Promise<JobOperationResult> => {
    const cancelResult: JobOperationResult = await this.queries.cancelJob(job);
    return cancelResult;
  };

  rescheduleJob = async (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> => {
    const rescheduleResult = await this.queries.rescheduleJob(job, repeatInterval, repeatLimit, scheduleDate);
    return rescheduleResult;
  };

  bulkCancel = (jobsToBeActioned: (Job & { errorMessage?: string })[]): Promise<BulkCancel> => {
    return this.queries.bulkCancel(jobsToBeActioned);
  };

  sortBy = (orderBy: JobsSortBy): Promise<void> => {
    this._JobsManagementState.orderBy = orderBy;
    this.onUpdateJobsManagementStateListeners.forEach((listener) => listener.onUpdate(this._JobsManagementState));
    return Promise.resolve();
  };

  onUpdateJobsManagementState(listener: OnUpdateJobsManagementStateListener): UnSubscribeHandler {
    this.onUpdateJobsManagementStateListeners.push(listener);

    const unSubscribe = () => {
      const index = this.onUpdateJobsManagementStateListeners.indexOf(listener);
      if (index > -1) {
        this.onUpdateJobsManagementStateListeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  query(offset: number, limit: number): Promise<Job[]> {
    return new Promise<Job[]>((resolve, reject) => {
      this.queries
        .getJobs(offset, limit, this._JobsManagementState.filters, this._JobsManagementState.orderBy)
        .then((value) => {
          resolve(value);
        })
        .catch((reason) => {
          reject(reason);
        });
    });
  }
}
