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

import { GraphQL } from "../graphql";
import {
  JobOperationResult,
  JobStatus,
  Job,
  JobsSortBy,
  BulkProcessInstanceActionResponse,
  ProcessInstance,
  ProcessInstanceFilter,
  ProcessListSortBy,
  ProcessDefinition,
} from "../types";
import { NodeInstance, TriggerableNode, OperationType } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";
import { buildProcessListWhereArgument } from "./QueryUtils";

export const getProcessInstances = async (
  offset: number,
  limit: number,
  filters: ProcessInstanceFilter,
  sortBy: ProcessListSortBy,
  client: ApolloClient<any>
): Promise<ProcessInstance[]> => {
  return client
    .query({
      query: GraphQL.GetProcessInstancesDocument,
      variables: {
        where: buildProcessListWhereArgument(filters),
        offset: offset,
        limit: limit,
        orderBy: sortBy,
      },
      fetchPolicy: "network-only",
      errorPolicy: "all",
    })
    .then((value) => value.data.ProcessInstances)
    .catch((reason) => {
      throw { errorMessage: JSON.stringify(reason, null, 2) };
    });
};

export const getChildProcessInstances = async (
  rootProcessInstanceId: string,
  client: ApolloClient<any>
): Promise<ProcessInstance[]> => {
  return client
    .query({
      query: GraphQL.GetChildInstancesDocument,
      variables: {
        rootProcessInstanceId,
      },
    })
    .then((value) => value.data.ProcessInstances);
};

//Rest Api to Cancel multiple Jobs
export const performMultipleCancel = async (
  jobsToBeActioned: (Job & { errorMessage?: string })[],
  client: ApolloClient<any>
): Promise<any> => {
  const multipleCancel: Promise<any>[] = jobsToBeActioned.map((job) => {
    return client
      .mutate({
        mutation: GraphQL.JobCancelDocument,
        variables: {
          jobId: job.id,
        },
        fetchPolicy: "no-cache",
      })
      .then(() => ({ successJob: job }))
      .catch((reason) => {
        job.errorMessage = JSON.stringify(reason.message, null, 2);
        throw { failedJob: job };
      });
  });
  return Promise.all(multipleCancel.map((mc) => mc.catch((error) => error)));
};

//Rest Api to Cancel a Job
export const jobCancel = async (
  job: Pick<Job, "id" | "endpoint">,
  client: ApolloClient<any>
): Promise<JobOperationResult> => {
  let modalTitle: string;
  let modalContent: string;
  return client
    .mutate({
      mutation: GraphQL.JobCancelDocument,
      variables: {
        jobId: job.id,
      },
      fetchPolicy: "no-cache",
    })
    .then(() => {
      modalTitle = "success";
      modalContent = `The job: ${job.id} is canceled successfully`;
      return { modalTitle, modalContent };
    })
    .catch((reason) => {
      modalTitle = "failure";
      modalContent = `The job: ${job.id} failed to cancel. Error message: ${reason.message}`;
      throw { modalTitle, modalContent };
    });
};

// Rest Api to Reschedule a Job
export const handleJobReschedule = async (
  job: Job,
  repeatInterval: number | string,
  repeatLimit: number | string,
  scheduleDate: Date,
  client: ApolloClient<any>
): Promise<{ modalTitle: string; modalContent: string }> => {
  let modalTitle: string;
  let modalContent: string;
  let parameter = {};
  if (repeatInterval === null && repeatLimit === null) {
    parameter = {
      expirationTime: new Date(scheduleDate),
    };
  } else {
    parameter = {
      expirationTime: new Date(scheduleDate),
      repeatInterval,
      repeatLimit,
    };
  }

  return client
    .mutate({
      mutation: GraphQL.HandleJobRescheduleDocument,
      variables: {
        jobId: job.id,
        data: JSON.stringify(parameter),
      },
      fetchPolicy: "no-cache",
    })
    .then(() => {
      modalTitle = "success";
      modalContent = `Reschedule of job: ${job.id} is successful`;
      return { modalTitle, modalContent };
    })
    .catch((reason) => {
      modalTitle = "failure";
      modalContent = `Reschedule of job ${job.id} failed. Message: ${reason.message}`;
      throw { modalTitle, modalContent };
    });
};

// Rest Api to skip a process in error state
export const handleProcessSkip = async (processInstance: ProcessInstance, client: ApolloClient<any>): Promise<void> => {
  return client
    .mutate({
      mutation: GraphQL.AbortProcessInstanceDocument,
      variables: {
        processId: processInstance.id,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => value.data);
};

// Rest Api to retrigger a process in error state
export const handleProcessRetry = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return client
    .mutate({
      mutation: GraphQL.RetryProcessInstanceDocument,
      variables: {
        processId: processInstance.id,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => value.data);
};

// Rest Api to abort a process
export const handleProcessAbort = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return client
    .mutate({
      mutation: GraphQL.AbortProcessInstanceDocument,
      variables: {
        processId: processInstance.id,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => value.data);
};

const operations: Record<string, (processInstance: ProcessInstance, client: ApolloClient<any>) => Promise<void>> = {
  [OperationType.ABORT]: handleProcessAbort,
  [OperationType.SKIP]: handleProcessSkip,
  [OperationType.RETRY]: handleProcessRetry,
} as const;

// function to handle multiple actions(abort, skip and retry) on processes
export const handleProcessMultipleAction = async (
  processInstances: ProcessInstance[],
  operationType: OperationType,
  client: ApolloClient<any>
): Promise<BulkProcessInstanceActionResponse> => {
  if (!operations[operationType]) {
    return Promise.reject(`Invalid operation type: ${operationType}`);
  }
  const successProcessInstances: ProcessInstance[] = [];
  const failedProcessInstances: ProcessInstance[] = [];

  for (const processInstance of processInstances) {
    await operations[operationType](processInstance, client)
      .then(() => {
        successProcessInstances.push(processInstance);
      })
      .catch((error) => {
        processInstance.errorMessage = error.message;
        failedProcessInstances.push(processInstance);
      });
  }

  return { successProcessInstances, failedProcessInstances };
};

export const handleNodeTrigger = async (
  processInstance: ProcessInstance,
  node: TriggerableNode,
  client: ApolloClient<any>
): Promise<void> => {
  return client
    .mutate({
      mutation: GraphQL.HandleNodeTriggerDocument,
      variables: {
        processId: processInstance.id,
        nodeId: node.id,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => value.data);
};

// function containing Api call to update process variables
export const handleProcessVariableUpdate = async (
  processInstance: ProcessInstance,
  updatedJson: Record<string, unknown>,
  client: ApolloClient<any>
): Promise<Record<string, unknown>> => {
  return client
    .mutate({
      mutation: GraphQL.HandleProcessVariableUpdateDocument,
      variables: {
        processId: processInstance.id,
        processInstanceVariables: JSON.stringify(updatedJson, null, 2),
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => {
      throw JSON.parse(value.data.ProcessInstanceUpdateVariables);
    });
};

export const handleNodeInstanceCancel = async (
  processInstance: ProcessInstance,
  node: NodeInstance,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .mutate({
      mutation: GraphQL.HandleNodeInstanceCancelDocument,
      variables: {
        processId: processInstance.id,
        nodeInstanceId: node.id,
      },
      fetchPolicy: "no-cache",
    })
    .catch((reason) => {
      throw JSON.stringify(reason.message, null, 2);
    });
};

export const handleNodeInstanceRetrigger = async (
  processInstance: Pick<ProcessInstance, "id" | "serviceUrl" | "processId">,
  node: Pick<NodeInstance, "id">,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .mutate({
      mutation: GraphQL.HandleNodeInstanceRetriggerDocument,
      variables: {
        processId: processInstance.id,
        nodeInstanceId: node.id,
      },
      fetchPolicy: "no-cache",
    })
    .catch((reason) => {
      throw JSON.stringify(reason.message, null, 2);
    });
};

export const getProcessDetailsSVG = async (
  processInstance: ProcessInstance,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessInstanceSvgDocument,
      variables: {
        processId: processInstance.id,
      },
      fetchPolicy: "network-only",
    })
    .then((value) => ({ svg: value.data.ProcessInstances[0].diagram }))
    .catch((reason) => {
      throw { error: reason.message };
    });
};

export const getProcessDetails = async (id: string, client: ApolloClient<any>): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessInstanceByIdDocument,
      variables: {
        id,
      },
      fetchPolicy: "network-only",
    })
    .then((value) => value.data.ProcessInstances[0])
    .catch((error) => {
      throw error["graphQLErrors"][0]["message"];
    });
};

export const getJobs = async (id: string, client: ApolloClient<any>): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetJobsByProcessInstanceIdDocument,
      variables: {
        processInstanceId: id,
      },
      fetchPolicy: "network-only",
    })
    .then((value) => value.data.Jobs);
};

export const getTriggerableNodes = (processInstance: ProcessInstance, client: ApolloClient<any>): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessDefinitionNodesDocument,
      variables: {
        processId: processInstance.processId,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => value.data.ProcessDefinitions[0].nodes);
};

export const getJobsWithFilters = (
  offset: number,
  limit: number,
  filters: JobStatus[],
  orderBy: JobsSortBy,
  client: ApolloClient<any>
): Promise<Job[]> => {
  return client
    .query({
      query: GraphQL.GetJobsWithFiltersDocument,
      variables: {
        values: filters,
        offset: offset,
        limit: limit,
        orderBy,
      },
      fetchPolicy: "network-only",
    })
    .then((response) => response.data.Jobs);
};

export const getProcessDefinitions = (client: ApolloClient<any>): Promise<ProcessDefinition[]> => {
  return client
    .query({
      query: GraphQL.GetProcessDefinitionsDocument,
      fetchPolicy: "network-only",
      errorPolicy: "all",
    })
    .then((value) => {
      return (value.data.ProcessDefinitions ?? []).map((item: { id: string; endpoint: string }) => {
        return {
          processName: item.id,
          endpoint: item.endpoint,
        };
      });
    })
    .catch((reason) => {
      throw { errorMessage: JSON.stringify(reason, null, 2) };
    });
};

export const getProcessDefinitionByName = (
  processName: string,
  client: ApolloClient<any>
): Promise<ProcessDefinition> => {
  return client
    .query({
      query: GraphQL.GetProcessDefinitionByIdDocument,
      variables: {
        id: processName,
      },
      fetchPolicy: "network-only",
    })
    .then((value) => {
      return (value.data.ProcessDefinitions ?? []).map((item: { id: string; endpoint: string }) => {
        return {
          processName: item.id,
          endpoint: item.endpoint,
        };
      })[0];
    })
    .catch((error) => {
      throw error["graphQLErrors"][0]["message"];
    });
};
