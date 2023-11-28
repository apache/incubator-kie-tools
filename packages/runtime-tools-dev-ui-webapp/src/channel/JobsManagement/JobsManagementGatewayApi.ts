/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Job, JobStatus, BulkCancel, JobCancel, JobsSortBy } from "@kogito-apps/management-console-shared/dist/types";
import { JobsManagementQueries } from "./JobsManagementQueries";
import { performMultipleCancel, jobCancel, handleJobReschedule } from "../apis";

export interface JobsManagementGatewayApi {
  jobsManagementState: any;
  initialLoad: (filter: JobStatus[], orderBy: JobsSortBy) => Promise<void>;
  applyFilter: (filter: JobStatus[]) => Promise<void>;
  bulkCancel: (jobsToBeActioned: Job[]) => Promise<BulkCancel>;
  cancelJob: (job: Pick<Job, "id" | "endpoint">) => Promise<JobCancel>;
  rescheduleJob: (
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  sortBy: (orderBy: JobsSortBy) => Promise<void>;
  query(offset: number, limit: number): Promise<Job[]>;
}
export interface JobsManagementState {
  filters: JobStatus[];
  orderBy: JobsSortBy | any;
}
export class JobsManagementGatewayApiImpl implements JobsManagementGatewayApi {
  private readonly queries: JobsManagementQueries;
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
    return Promise.resolve();
  };

  cancelJob = (job: Pick<Job, "id" | "endpoint">): Promise<JobCancel> => {
    return jobCancel(job);
  };

  rescheduleJob = (
    job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> => {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate);
  };

  bulkCancel = (jobsToBeActioned: (Job & { errorMessage?: string })[]): Promise<BulkCancel> => {
    return performMultipleCancel(jobsToBeActioned);
  };

  sortBy = (orderBy: JobsSortBy): Promise<void> => {
    this._JobsManagementState.orderBy = orderBy;
    return Promise.resolve();
  };

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
