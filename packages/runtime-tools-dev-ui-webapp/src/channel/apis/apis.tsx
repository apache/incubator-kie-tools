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

import {
  BulkWorkflowInstanceActionResponse,
  NodeInstance,
  WorkflowInstance,
  TriggerableNode,
} from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { OperationType } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { FormInfo } from "@kie-tools/runtime-tools-enveloped-components/dist/formsList";
import axios from "axios";
import { v4 as uuidv4 } from "uuid";
import { Form, FormContent } from "@kie-tools/runtime-tools-enveloped-components/dist/formDetails";
import SwaggerParser from "@apidevtools/swagger-parser";
import { createWorkflowDefinitionList } from "../../utils/Utils";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowForm/api";
import { CustomDashboardInfo } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { CloudEventRequest, KOGITO_BUSINESS_KEY } from "@kie-tools/runtime-tools-gateway-api/dist/types";

// Rest Api to fetch Process Diagram
export const getSvg = async (data: WorkflowInstance): Promise<any> => {
  return axios
    .get(`/svg/processes/${data.processId}/instances/${data.id}`)
    .then((res) => {
      return { svg: res.data };
    })
    .catch(async (error) => {
      if (data.serviceUrl) {
        return axios
          .get(`${data.serviceUrl}/svg/processes/${data.processId}/instances/${data.id}`)
          .then((res) => {
            return { svg: res.data };
          })
          .catch((err) => {
            if (err.response && err.response.status !== 404) {
              return { error: err.message };
            }
          });
      }
    });
};

// Rest Api to skip a process in error state
export const handleProcessSkip = async (processInstance: WorkflowInstance): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/skip`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => reject(error));
  });
};

// Rest Api to retrigger a process in error state
export const handleProcessRetry = async (processInstance: WorkflowInstance): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/retrigger`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => {
        reject(error);
      });
  });
};

// Rest Api to abort a process
export const handleProcessAbort = (processInstance: WorkflowInstance): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .delete(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => reject(error));
  });
};

// function to handle multiple actions(abort, skip and retry) on processes
export const handleProcessMultipleAction = async (
  processInstances: WorkflowInstance[],
  operationType: OperationType
): Promise<BulkWorkflowInstanceActionResponse> => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    let operation: (processInstance: WorkflowInstance) => Promise<void>;
    const successProcessInstances: WorkflowInstance[] = [];
    const failedProcessInstances: WorkflowInstance[] = [];
    switch (operationType) {
      case OperationType.ABORT:
        operation = handleProcessAbort;
        break;
      case OperationType.SKIP:
        operation = handleProcessSkip;
        break;
      case OperationType.RETRY:
        operation = handleProcessRetry;
        break;
    }
    for (const processInstance of processInstances) {
      await operation!(processInstance)
        .then(() => {
          successProcessInstances.push(processInstance);
        })
        .catch((error) => {
          processInstance.errorMessage = error.message;
          failedProcessInstances.push(processInstance);
        });
    }

    resolve({ successWorkflowInstances: successProcessInstances, failedWorkflowInstances: failedProcessInstances });
  });
};
export const getTriggerableNodes = async (processInstance: WorkflowInstance): Promise<TriggerableNode[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`${processInstance.serviceUrl}/management/processes/${processInstance.processId}/nodes`)
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const handleNodeTrigger = async (processInstance: WorkflowInstance, node: TriggerableNode): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodes/${node.nodeDefinitionId}`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => {
        reject(error);
      });
  });
};

// function containing Api call to update process variables
export const handleProcessVariableUpdate = (
  processInstance: WorkflowInstance,
  updatedJson: Record<string, unknown>
): Promise<Record<string, unknown>> => {
  return new Promise((resolve, reject) => {
    axios
      .put(`${processInstance.endpoint}/${processInstance.id}`, updatedJson)
      .then((response) => {
        resolve(response.data);
      })
      .catch((error) => {
        reject(error.message);
      });
  });
};

export const handleNodeInstanceCancel = async (
  processInstance: WorkflowInstance,
  node: NodeInstance
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .delete(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const handleNodeInstanceRetrigger = (
  processInstance: Pick<WorkflowInstance, "id" | "serviceUrl" | "processId">,
  node: Pick<NodeInstance, "id">
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(
        `${processInstance.serviceUrl}/management/processes/${processInstance.processId}/instances/${processInstance.id}/nodeInstances/${node.id}`
      )
      .then(() => {
        resolve();
      })
      .catch((error) => {
        reject(JSON.stringify(error.message));
      });
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

export const getWorkflowDefinitionList = (
  openApiBaseUrl: string,
  openApiPath: string
): Promise<WorkflowDefinition[]> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${openApiBaseUrl}/${openApiPath}`)
      .then((response) => {
        const workflowDefinitionObjs: { [key: string]: any }[] = [];
        const paths = response.paths;
        const regexPattern = /^\/[^\n/]+\/schema/;
        Object.getOwnPropertyNames(paths)
          .filter((path) => regexPattern.test(path.toString()))
          .forEach((url) => {
            let workflowArray = url.split("/");
            workflowArray = workflowArray.filter((name) => name.length !== 0);
            if (Object.prototype.hasOwnProperty.call(paths[`/${workflowArray[0]}`], "post")) {
              workflowDefinitionObjs.push({ [url]: paths[url] });
            }
          });
        resolve(createWorkflowDefinitionList(workflowDefinitionObjs, openApiBaseUrl));
      })
      .catch((err) => reject(err));
  });
};

export const getProcessSchema = (processDefinitionData: WorkflowDefinition): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`${processDefinitionData.endpoint}/schema`)
      .then((response) => {
        if (response.status === 200) {
          resolve(response.data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
};

export const startProcessInstance = (
  formData: any,
  businessKey: string,
  processDefinitionData: WorkflowDefinition
): Promise<string> => {
  return new Promise((resolve, reject) => {
    const requestURL = `${processDefinitionData.endpoint}${
      businessKey.length > 0 ? `?businessKey=${businessKey}` : ""
    }`;
    axios
      .post(requestURL, formData, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        resolve(response.data.id);
      })
      .catch((error) => reject(error));
  });
};

export const triggerStartCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<string> => {
  if (!event.headers.extensions[KOGITO_BUSINESS_KEY]) {
    event.headers.extensions[KOGITO_BUSINESS_KEY] = String(Math.floor(Math.random() * 100000));
  }

  return new Promise((resolve, reject) => {
    doTriggerCloudEvent(event, devUIUrl)
      .then((response) => resolve(event.headers.extensions[KOGITO_BUSINESS_KEY]))
      .catch((error) => reject(error));
  });
};

export const triggerCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<any> => {
  return doTriggerCloudEvent(event, devUIUrl);
};

const doTriggerCloudEvent = (event: CloudEventRequest, devUIUrl: string): Promise<any> => {
  const cloudEvent = {
    ...event.headers.extensions,
    specversion: "1.0",
    id: uuidv4(),
    source: event.headers.source ?? "",
    type: event.headers.type,
    data: event.data ? JSON.parse(event.data) : {},
  };

  if (devUIUrl.endsWith("/")) {
    devUIUrl = devUIUrl.slice(0, devUIUrl.length - 1);
  }

  const url = `${devUIUrl}${event.endpoint.startsWith("/") ? "" : "/"}${event.endpoint}`;

  return axios.request({
    url,
    method: event.method,
    data: cloudEvent,
  });
};

export const startWorkflowRest = (
  data: Record<string, any>,
  endpoint: string,
  businessKey: string
): Promise<string> => {
  const requestURL = `${endpoint}${businessKey.length > 0 ? `?businessKey=${businessKey}` : ""}`;
  return new Promise((resolve, reject) => {
    axios
      .post(requestURL, { workflowdata: data })
      .then((response: any) => {
        resolve(response.data.id);
      })
      .catch((err) => reject(err));
  });
};

export const getCustomWorkflowSchema = (
  openApiBaseUrl: string,
  openApiPath: string,
  workflowName: string
): Promise<Record<string, any> | null> => {
  return new Promise((resolve, reject) => {
    SwaggerParser.parse(`${openApiBaseUrl}/${openApiPath}`)
      .then((response: any) => {
        let schema = {};
        try {
          const schemaFromRequestBody =
            response.paths["/" + workflowName].post.requestBody.content["application/json"].schema;
          if (schemaFromRequestBody.type) {
            schema = {
              type: schemaFromRequestBody.type,
              properties: schemaFromRequestBody.properties,
            };
          } else {
            schema = response.components.schemas[workflowName + "_input"];
          }
        } catch (e) {
          console.log(e);
          schema = response.components.schemas[workflowName + "_input"];
        }
        if (schema) {
          resolve(schema);
        } else {
          resolve(null);
        }
      })
      .catch((err) => reject(err));
  });
};
