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

import { NodeInstance, TriggerableNode } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { Job, JobCancel, WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowDetailsQueries } from "./WorkflowDetailsQueries";

export interface OnOpenWorkflowInstanceDetailsListener {
  onOpen(id: string): void;
}

export interface WorkflowDetailsUnSubscribeHandler {
  unSubscribe: () => void;
}

export interface WorkflowDetailsState {
  id: string;
}

export interface WorkflowDetailsGatewayApi {
  workflowDetailsState: any;
  handleWorkflowAbort: (workflowInstance: WorkflowInstance) => Promise<void>;
  cancelJob: (job: Job) => Promise<JobCancel>;
  rescheduleJob: (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ) => Promise<{ modalTitle: string; modalContent: string }>;
  getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]>;
  handleNodeTrigger(workflowInstance: WorkflowInstance, node: any): Promise<void>;
  handleWorkflowVariableUpdate: (
    workflowInstance: WorkflowInstance,
    updateJson: Record<string, unknown>
  ) => Promise<Record<string, unknown>>;
  workflowDetailsQuery(id: string): Promise<WorkflowInstance>;
  jobsQuery(id: string): Promise<Job[]>;
  openWorkflowInstanceDetails(id: string): Promise<void>;
  onOpenWorkflowInstanceDetailsListener: (
    listener: OnOpenWorkflowInstanceDetailsListener
  ) => WorkflowDetailsUnSubscribeHandler;
  handleWorkflowRetry: (workflowInstance: WorkflowInstance) => Promise<void>;
  handleNodeInstanceCancel: (workflowInstance: WorkflowInstance, node: NodeInstance) => Promise<void>;
  handleWorkflowSkip: (workflowInstance: WorkflowInstance) => Promise<void>;
  handleNodeInstanceRetrigger(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void>;
}

export class WorkflowDetailsGatewayApiImpl implements WorkflowDetailsGatewayApi {
  private readonly queries: WorkflowDetailsQueries;
  private _WorkflowDetailsState: WorkflowDetailsState;
  private readonly listeners: OnOpenWorkflowInstanceDetailsListener[] = [];

  constructor(queries: WorkflowDetailsQueries) {
    this.queries = queries;
    this._WorkflowDetailsState = { id: "" };
  }

  get workflowDetailsState(): WorkflowDetailsState {
    return this._WorkflowDetailsState;
  }

  handleWorkflowAbort = (workflowInstance: WorkflowInstance): Promise<void> => {
    return this.queries.handleWorkflowAbort(workflowInstance);
  };

  cancelJob = (job: Job): Promise<JobCancel> => {
    return this.queries.jobCancel(job);
  };

  rescheduleJob = (
    job: Job,
    repeatInterval: number | string,
    repeatLimit: number | string,
    scheduleDate: Date
  ): Promise<{ modalTitle: string; modalContent: string }> => {
    return this.queries.rescheduleJob(job, repeatInterval, repeatLimit, scheduleDate);
  };

  getTriggerableNodes(workflowInstance: WorkflowInstance): Promise<TriggerableNode[]> {
    return this.queries.getTriggerableNodes(workflowInstance);
  }

  handleNodeTrigger(workflowInstance: WorkflowInstance, node: TriggerableNode): Promise<void> {
    return this.queries.handleNodeTrigger(workflowInstance, node);
  }

  handleWorkflowVariableUpdate = (workflowInstance: WorkflowInstance, updatedJson: Record<string, unknown>) => {
    return this.queries.handleWorkflowVariableUpdate(workflowInstance, updatedJson);
  };

  workflowDetailsQuery(id: string): Promise<WorkflowInstance> {
    return new Promise<any>((resolve, reject) => {
      this.queries
        .getWorkflowDetails(id)
        .then((value: WorkflowInstance) => {
          resolve(value);
        })
        .catch((reason: any) => {
          reject(reason);
        });
    });
  }

  jobsQuery(id: string): Promise<Job[]> {
    return new Promise<any>((resolve, reject) => {
      this.queries
        .getJobs(id)
        .then((value: Job[]) => {
          resolve(value);
        })
        .catch((reason: any) => {
          reject(reason);
        });
    });
  }

  openWorkflowInstanceDetails(id: string): Promise<void> {
    this._WorkflowDetailsState = { id: id };
    this.listeners.forEach((listener) => listener.onOpen(id));
    return Promise.resolve();
  }

  onOpenWorkflowInstanceDetailsListener(
    listener: OnOpenWorkflowInstanceDetailsListener
  ): WorkflowDetailsUnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe,
    };
  }

  handleWorkflowRetry(workflowInstance: WorkflowInstance): Promise<void> {
    return this.queries.handleWorkflowRetry(workflowInstance);
  }

  handleNodeInstanceCancel(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return this.queries.handleNodeInstanceCancel(workflowInstance, node);
  }

  handleWorkflowSkip(workflowInstance: WorkflowInstance): Promise<void> {
    return this.queries.handleWorkflowSkip(workflowInstance);
  }

  handleNodeInstanceRetrigger(workflowInstance: WorkflowInstance, node: NodeInstance): Promise<void> {
    return this.queries.handleNodeInstanceRetrigger(workflowInstance, node);
  }
}
