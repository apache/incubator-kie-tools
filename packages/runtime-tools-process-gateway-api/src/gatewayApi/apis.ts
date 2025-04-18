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

import { ProcessDefinition } from "../types";
import { FormInfo, Form, FormContent } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import axios from "axios";

export const buildHeaders = (token?: string) => ({
  ...(token ? { Authorization: `Bearer ${token}` } : {}),
});

export const getForms = (baseUrl: string, formFilter: string[], token?: string): Promise<FormInfo[]> => {
  return new Promise((resolve, reject) => {
    axios
      .get(new URL(`forms/list`, baseUrl).toString(), {
        params: {
          names: formFilter.join(";"),
        },
        headers: buildHeaders(token),
      })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const getFormContent = (baseUrl: string, formName: string, token?: string): Promise<Form> => {
  return new Promise((resolve, reject) => {
    axios
      .get(new URL(`forms/${formName}`, baseUrl).toString(), { headers: buildHeaders(token) })
      .then((result) => {
        resolve(result.data);
      })
      .catch((error) => reject(error));
  });
};

export const saveFormContent = (
  baseUrl: string,
  formName: string,
  content: FormContent,
  token?: string
): Promise<void> => {
  return new Promise((resolve, reject) => {
    axios
      .post(new URL(`forms/${formName}`, baseUrl).toString(), content, { headers: buildHeaders(token) })
      .then((result) => {
        resolve();
      })
      .catch((error) => reject(error));
  });
};

export const getProcessSchema = (
  processDefinitionData: ProcessDefinition,
  token?: string
): Promise<Record<string, any>> => {
  return new Promise((resolve, reject) => {
    axios
      .get(`${processDefinitionData.endpoint}/schema`, { headers: buildHeaders(token) })
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

export const getCustomForm = (processDefinitionData: ProcessDefinition, token?: string): Promise<Form> => {
  return new Promise((resolve, reject) => {
    const lastIndex = processDefinitionData.endpoint.lastIndexOf(`/${processDefinitionData.processName}`);
    const baseEndpoint = processDefinitionData.endpoint.slice(0, lastIndex);
    axios
      .get(`${baseEndpoint}/forms/${processDefinitionData.processName}`, { headers: buildHeaders(token) })
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

export const getProcessSvg = (processDefinitionData: ProcessDefinition, token?: string): Promise<string> => {
  return new Promise((resolve, reject) => {
    const lastIndex = processDefinitionData.endpoint.lastIndexOf(`/${processDefinitionData.processName}`);
    const baseEndpoint = processDefinitionData.endpoint.slice(0, lastIndex);
    axios
      .get(`${baseEndpoint}/svg/processes/${processDefinitionData.processName}`, { headers: buildHeaders(token) })
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
  processDefinitionData: ProcessDefinition,
  formData: any,
  businessKey: string,
  token?: string
): Promise<string> => {
  return new Promise((resolve, reject) => {
    const requestURL = `${processDefinitionData.endpoint}${
      businessKey.length > 0 ? `?businessKey=${businessKey}` : ""
    }`;
    axios
      .post(requestURL, formData, {
        headers: {
          "Content-Type": "application/json",
          ...buildHeaders(token),
        },
      })
      .then((response) => {
        resolve(response.data.id);
      })
      .catch((error) => reject(error));
  });
};
