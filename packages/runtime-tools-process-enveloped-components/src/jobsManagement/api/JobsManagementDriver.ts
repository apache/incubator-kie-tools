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
  JobCancel,
  JobsSortBy,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export interface JobsManagementDriver {
  initialLoad(filter: JobStatus[], orderBy: JobsSortBy): Promise<void>;
  applyFilter(filter: JobStatus[]): Promise<void>;
  bulkCancel(jobsToBeActioned: Job[]): Promise<BulkCancel>;
  cancelJob(job: Pick<Job, "id" | "endpoint">): Promise<JobCancel>;
  rescheduleJob(
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }>;
  sortBy(orderBy: JobsSortBy): Promise<void>;
  query(offset: number, limit: number): Promise<Job[]>;
}
