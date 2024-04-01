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

import { ApolloClient } from "apollo-client";
import {
  BulkCancel,
  Job,
  JobCancel,
  JobStatus,
  JobsSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  getJobsWithFilters,
  handleJobReschedule,
  jobCancel,
  performMultipleCancel,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export interface JobsManagementQueries {
  getJobs(start: number, end: number, filters: JobStatus[], sortBy: JobsSortBy | any): Promise<Job[]>;
  cancelJob: (job: Pick<Job, "id" | "endpoint">) => Promise<JobCancel>;
  bulkCancel: (jobsToBeActioned: (Job & { errorMessage?: string })[]) => Promise<BulkCancel>;
  rescheduleJob: (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
}

export class GraphQLJobsManagementQueries implements JobsManagementQueries {
  private readonly client: ApolloClient<any>;

  constructor(client: ApolloClient<any>) {
    this.client = client;
  }

  async getJobs(offset: number, limit: number, filters: JobStatus[], orderBy: JobsSortBy): Promise<Job[]> {
    return getJobsWithFilters(offset, limit, filters, orderBy, this.client);
  }

  async cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobCancel> {
    return jobCancel(job, this.client);
  }

  async bulkCancel(jobsToBeActioned: (Job & { errorMessage?: string })[]): Promise<BulkCancel> {
    return performMultipleCancel(jobsToBeActioned, this.client);
  }

  async rescheduleJob(
    job: Job,
    repeatInterval: string | number,
    repeatLimit: string | number,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> {
    return handleJobReschedule(job, repeatInterval, repeatLimit, scheduleDate, this.client);
  }
}
