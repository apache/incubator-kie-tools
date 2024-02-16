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

import { OpenAPI } from "openapi-types";
import { GraphQL } from "../graphql";
import {
  BulkWorkflowInstanceActionResponse,
  WorkflowInstance,
  WorkflowInstanceFilter,
  WorkflowListSortBy,
  WorkflowDefinition,
  CloudEventRequest,
  WorkflowResponse,
  JobStatus,
  Job,
  JobsSortBy,
  JobCancel,
} from "../types";
import {
  NodeInstance,
  TriggerableNode,
  OperationType,
  KOGITO_BUSINESS_KEY,
  CustomDashboardInfo,
  FormInfo,
  Form,
  FormContent,
} from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { ApolloClient } from "apollo-client";
import { buildWorkflowListWhereArgument } from "./QueryUtils";
import axios from "axios";
import { v4 as uuidv4 } from "uuid";
import SwaggerParser from "@apidevtools/swagger-parser";

export const getWorkflowInstances = async (
  offset: number,
  limit: number,
  filters: WorkflowInstanceFilter,
  sortBy: WorkflowListSortBy,
  client: ApolloClient<any>
): Promise<WorkflowInstance[]> => {
  return new Promise<WorkflowInstance[]>((resolve, reject) => {
    client
      .query({
        query: GraphQL.GetProcessInstancesDocument,
        variables: {
          where: buildWorkflowListWhereArgument(filters),
          offset: offset,
          limit: limit,
          orderBy: sortBy,
        },
        fetchPolicy: "network-only",
        errorPolicy: "all",
      })
      .then((value) => {
        resolve(value.data.ProcessInstances);
      })
      .catch((reason) => {
        reject({ errorMessage: JSON.stringify(reason) });
      });
  });
};

export const getChildWorkflowInstances = async (
  rootWorkflowInstanceId: string,
  client: ApolloClient<any>
): Promise<WorkflowInstance[]> => {
  return new Promise<WorkflowInstance[]>((resolve, reject) => {
    client
      .query({
        query: GraphQL.GetChildInstancesDocument,
        variables: {
          rootProcessInstanceId: rootWorkflowInstanceId,
        },
      })
      .then((value) => {
        resolve(value.data.ProcessInstances);
      })
      .catch((reason) => reject(reason));
  });
};

//Rest Api to Cancel multiple Jobs
export const performMultipleCancel = async (
  jobsToBeActioned: (GraphQL.Job & { errorMessage?: string })[],
  client: ApolloClient<any>
): Promise<any> => {
  const multipleCancel: Promise<any>[] = [];
  for (const job of jobsToBeActioned) {
    multipleCancel.push(
      new Promise((resolve, reject) => {
        client
          .mutate({
            mutation: GraphQL.JobCancelDocument,
            variables: {
              jobId: job.id,
            },
            fetchPolicy: "no-cache",
          })
          .then((value) => {
            resolve({ successJob: job });
          })
          .catch((reason) => {
            job.errorMessage = JSON.stringify(reason.message);
            reject({ failedJob: job });
          });
      })
    );
  }
  return Promise.all(multipleCancel.map((mc) => mc.catch((error) => error))).then((result) => {
    return Promise.resolve(result);
  });
};

//Rest Api to Cancel a Job
export const jobCancel = async (
  job: Pick<GraphQL.Job, "id" | "endpoint">,
  client: ApolloClient<any>
): Promise<JobCancel> => {
  let modalTitle: string;
  let modalContent: string;
  return new Promise<JobCancel>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.JobCancelDocument,
        variables: {
          jobId: job.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        modalTitle = "success";
        modalContent = `The job: ${job.id} is canceled successfully`;
        resolve({ modalTitle, modalContent });
      })
      .catch((reason) => {
        modalTitle = "failure";
        modalContent = `The job: ${job.id} failed to cancel. Error message: ${reason.message}`;
        reject({ modalTitle, modalContent });
      });
  });
};

// Rest Api to Reschedule a Job
export const handleJobReschedule = async (
  job: any,
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

  return new Promise<JobCancel>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleJobRescheduleDocument,
        variables: {
          jobId: job.id,
          data: JSON.stringify(parameter),
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        modalTitle = "success";
        modalContent = `Reschedule of job: ${job.id} is successful`;
        resolve({ modalTitle, modalContent });
      })
      .catch((reason) => {
        modalTitle = "failure";
        modalContent = `Reschedule of job ${job.id} failed. Message: ${reason.message}`;
        reject({ modalTitle, modalContent });
      });
  });
};

// Rest Api to skip a workflow in error state
export const handleWorkflowSkip = async (
  workflowInstance: WorkflowInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.AbortProcessInstanceDocument,
        variables: {
          processId: workflowInstance.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve(value.data);
      })
      .catch((reason) => reject(reason));
  });
};

// Rest Api to retrigger a workflow in error state
export const handleWorkflowRetry = async (
  workflowInstance: WorkflowInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.RetryProcessInstanceDocument,
        variables: {
          processId: workflowInstance.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve(value.data);
      })
      .catch((reason) => reject(reason));
  });
};

// Rest Api to abort a workflow
export const handleWorkflowAbort = async (
  workflowInstance: WorkflowInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise<void>((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.AbortProcessInstanceDocument,
        variables: {
          processId: workflowInstance.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve(value.data);
      })
      .catch((reason) => reject(reason));
  });
};

// function to handle multiple actions(abort, skip and retry) on workflows
export const handleWorkflowMultipleAction = async (
  workflowInstances: WorkflowInstance[],
  operationType: OperationType,
  client: ApolloClient<any>
): Promise<BulkWorkflowInstanceActionResponse> => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    let operation: (workflowInstance: WorkflowInstance, client: ApolloClient<any>) => Promise<void>;
    const successWorkflowInstances: WorkflowInstance[] = [];
    const failedWorkflowInstances: WorkflowInstance[] = [];
    switch (operationType) {
      case OperationType.ABORT:
        operation = handleWorkflowAbort;
        break;
      case OperationType.SKIP:
        operation = handleWorkflowSkip;
        break;
      case OperationType.RETRY:
        operation = handleWorkflowRetry;
        break;
    }
    for (const workflowInstance of workflowInstances) {
      await operation!(workflowInstance, client)
        .then(() => {
          successWorkflowInstances.push(workflowInstance);
        })
        .catch((error) => {
          workflowInstance.errorMessage = error.message;
          failedWorkflowInstances.push(workflowInstance);
        });
    }

    resolve({ successWorkflowInstances, failedWorkflowInstances });
  });
};

export const handleNodeTrigger = async (
  workflowInstance: WorkflowInstance,
  node: TriggerableNode,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeTriggerDocument,
        variables: {
          processId: workflowInstance.id,
          nodeId: node.nodeDefinitionId,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve(value.data);
      })
      .catch((reason) => reject(reason));
  });
};

// function containing Api call to update workflow variables
export const handleWorkflowVariableUpdate = async (
  workflowInstance: WorkflowInstance,
  updatedJson: Record<string, unknown>,
  client: ApolloClient<any>
): Promise<Record<string, unknown>> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleProcessVariableUpdateDocument,
        variables: {
          processId: workflowInstance.id,
          processInstanceVariables: JSON.stringify(updatedJson),
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve(JSON.parse(value.data.ProcessInstanceUpdateVariables));
      })
      .catch((reason) => reject(reason));
  });
};

export const handleNodeInstanceCancel = async (
  workflowInstance: WorkflowInstance,
  node: NodeInstance,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeInstanceCancelDocument,
        variables: {
          processId: workflowInstance.id,
          nodeInstanceId: node.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve();
      })
      .catch((reason) => reject(JSON.stringify(reason.message)));
  });
};

export const handleNodeInstanceRetrigger = async (
  workflowInstance: Pick<WorkflowInstance, "id" | "serviceUrl" | "processId">,
  node: Pick<NodeInstance, "id">,
  client: ApolloClient<any>
): Promise<void> => {
  return new Promise((resolve, reject) => {
    client
      .mutate({
        mutation: GraphQL.HandleNodeInstanceRetriggerDocument,
        variables: {
          processId: workflowInstance.id,
          nodeInstanceId: node.id,
        },
        fetchPolicy: "no-cache",
      })
      .then((value) => {
        resolve();
      })
      .catch((reason) => reject(JSON.stringify(reason.message)));
  });
};

export const getWorkflowDetails = async (id: string, client: ApolloClient<any>): Promise<any> => {
  return new Promise((resolve, reject) => {
    client
      .query({
        query: GraphQL.GetProcessInstanceByIdDocument,
        variables: {
          id,
        },
        fetchPolicy: "network-only",
      })
      .then((value) => {
        resolve(value.data.ProcessInstances[0]);
      })
      .catch((error) => {
        reject(error["graphQLErrors"][0]["message"]);
      });
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
    .then((value) => {
      return value.data.Jobs;
    })
    .catch((error) => {
      return error;
    });
};

export const getTriggerableNodes = async (
  workflowInstance: WorkflowInstance,
  client: ApolloClient<any>
): Promise<any> => {
  return client
    .query({
      query: GraphQL.GetProcessInstanceNodeDefinitionsDocument,
      variables: {
        processId: workflowInstance.id,
      },
      fetchPolicy: "no-cache",
    })
    .then((value) => {
      return value.data.ProcessInstances[0].nodeDefinitions;
    })
    .catch((reason) => {
      return reason;
    });
};

export const getJobsWithFilters = async (
  offset: number,
  limit: number,
  filters: JobStatus[],
  orderBy: JobsSortBy,
  client: ApolloClient<any>
): Promise<Job[]> => {
  try {
    const response = await client.query({
      query: GraphQL.GetJobsWithFiltersDocument,
      variables: {
        values: filters,
        offset: offset,
        limit: limit,
        orderBy,
      },
      fetchPolicy: "network-only",
    });
    return Promise.resolve(response.data.Jobs);
  } catch (error) {
    return Promise.reject(error);
  }
};

const doTriggerCloudEvent = (event: CloudEventRequest, baseUrl: string, proxyEndpoint?: string): Promise<any> => {
  const cloudEvent = {
    ...event.headers.extensions,
    specversion: "1.0",
    id: uuidv4(),
    source: event.headers.source ?? "",
    type: event.headers.type,
    data: event.data ? JSON.parse(event.data) : {},
  };

  if (baseUrl.endsWith("/")) {
    baseUrl = baseUrl.slice(0, baseUrl.length - 1);
  }

  const url = `${baseUrl}${event.endpoint.startsWith("/") ? "" : "/"}${event.endpoint}`;

  return axios.request({
    url: proxyEndpoint || url,
    method: event.method,
    data: cloudEvent,
    headers: {
      ...(proxyEndpoint ? { "Target-Url": url } : {}),
    },
  });
};

export const triggerStartCloudEvent = (
  event: CloudEventRequest,
  baseUrl: string,
  proxyEndpoint?: string
): Promise<string> => {
  if (!event.headers.extensions[KOGITO_BUSINESS_KEY]) {
    event.headers.extensions[KOGITO_BUSINESS_KEY] = String(Math.floor(Math.random() * 100000));
  }

  return new Promise((resolve, reject) => {
    doTriggerCloudEvent(event, baseUrl, proxyEndpoint)
      .then((response: any) => resolve(event.headers.extensions[KOGITO_BUSINESS_KEY]))
      .catch((error) => reject(error));
  });
};

export const triggerCloudEvent = (event: CloudEventRequest, baseUrl: string, proxyEndpoint?: string): Promise<any> => {
  return doTriggerCloudEvent(event, baseUrl, proxyEndpoint);
};

export const createWorkflowDefinitionList = (
  workflowDefinitionObjs: WorkflowDefinition[],
  url: string
): WorkflowDefinition[] => {
  const workflowDefinitionList: WorkflowDefinition[] = [];
  workflowDefinitionObjs.forEach((workflowDefObj) => {
    const workflowName = Object.keys(workflowDefObj)[0].split("/")[1];
    const endpoint = `${url}/${workflowName}`;
    workflowDefinitionList.push({
      workflowName,
      endpoint,
    });
  });
  return workflowDefinitionList;
};

export const getWorkflowDefinitionList = (baseUrl: string, openApiPath: string): Promise<WorkflowDefinition[]> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${baseUrl}/${openApiPath}`)
      .then((response) => {
        const workflowDefinitionObjs: any[] = [];
        const paths = response.paths;
        const regexPattern = /^\/[^\n/]+\/schema/;
        Object.getOwnPropertyNames(paths)
          .filter((path) => regexPattern.test(path.toString()))
          .forEach((url) => {
            let workflowArray = url.split("/");
            workflowArray = workflowArray.filter((name) => name.length !== 0);
            /* istanbul ignore else*/
            if (Object.prototype.hasOwnProperty.call(paths![`/${workflowArray[0]}`], "post")) {
              workflowDefinitionObjs.push({ [url]: paths![url] });
            }
          });
        resolve(createWorkflowDefinitionList(workflowDefinitionObjs, baseUrl));
      })
      .catch((err: any) => reject(err));
  });
};

export const getWorkflowSchema = (workflowDefinitionData: WorkflowDefinition): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`${workflowDefinitionData.endpoint}/schema`)
      .then((response) => {
        /* istanbul ignore else*/
        if (response.status === 200) {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const startWorkflowInstance = (
  formData: any,
  businessKey: string,
  workflowDefinitionData: WorkflowDefinition,
  proxyEndpoint: string
): Promise<string> => {
  return new Promise((resolve, reject) => {
    const requestURL = `${workflowDefinitionData.endpoint}${
      businessKey.length > 0 ? `?businessKey=${businessKey}` : ""
    }`;
    axios
      .post(proxyEndpoint, formData, {
        headers: {
          "Target-Url": requestURL,
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        resolve(response.data.id);
      })
      .catch((error) => reject(error));
  });
};

export const startWorkflowRest = (
  data: Record<string, any>,
  endpoint: string,
  businessKey: string,
  proxyEndpoint?: string
): Promise<WorkflowResponse> => {
  const requestURL = `${endpoint}${businessKey.length > 0 ? `?businessKey=${businessKey}` : ""}`;
  return new Promise((resolve, reject) => {
    axios
      .post(
        proxyEndpoint || endpoint,
        { workflowdata: data },
        {
          headers: {
            ...(proxyEndpoint ? { "Target-Url": requestURL } : {}),
          },
        }
      )
      .then((response: any) => {
        resolve(response.data);
      })
      .catch((err) => reject(err));
  });
};

export const getCustomDashboard = (customDashboardFilter: string[]): Promise<CustomDashboardInfo[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get("/customDashboard/list", {
        params: {
          names: customDashboardFilter.join(";"),
        },
      })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getCustomDashboardContent = (name: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`/customDashboard/${name}`)
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getCustomWorkflowSchemaFromApi = async (
  api: OpenAPI.Document,
  workflowName: string
): Promise<Record<string, any>> => {
  let schema = {};

  try {
    const schemaFromRequestBody = api.paths["/" + workflowName].post.requestBody.content["application/json"].schema;

    if (schemaFromRequestBody.type) {
      schema = {
        type: schemaFromRequestBody.type,
        properties: schemaFromRequestBody.properties,
      };
    } else {
      schema = (api as any).components.schemas[workflowName + "_input"];
    }
  } catch (e) {
    console.log(e);
    schema = (api as any).components.schemas[workflowName + "_input"];
  }

  // Components can contain the content of internal refs ($ref)
  // This keeps the refs working while avoiding circular refs with the workflow itself
  if (schema) {
    const { [workflowName + "_input"]: _, ...schemas } = (api as any).components?.schemas ?? {};
    (schema as any)["components"] = { schemas };
  }

  return schema ?? null;
};

export const getCustomWorkflowSchema = async (
  baseUrl: string,
  openApiPath: string,
  workflowName: string
): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${baseUrl}/${openApiPath}`)
      .then(async (response: any) => {
        resolve(await getCustomWorkflowSchemaFromApi(response, workflowName));
      })
      .catch((err) => reject(err));
  });
};

export const getForms = (formFilter: string[]): Promise<FormInfo[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get("/forms/list", {
        params: {
          names: formFilter.join(";"),
        },
      })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getFormContent = (formName: string): Promise<Form> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`/forms/${formName}`)
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const saveFormContent = (formName: string, content: FormContent): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(`/forms/${formName}`, content)
      .then((result) => {
        resolve();
      })
      .catch((error) => reject(error));
  });
};
