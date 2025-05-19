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
  getCustomForm,
  getProcessSchema,
  getProcessSvg,
  startProcessInstance,
} from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  ProcessFormChannelApi,
  OnStartProcessListener,
  UnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/processForm";

export class ProcessFormChannelApiImpl implements ProcessFormChannelApi {
  private readonly onStartProcessListeners: OnStartProcessListener[] = [];
  private businessKey: string;
  private token?: string;

  constructor(token?: string) {
    this.token = token;
    this.businessKey = "";
  }

  processForm__getProcessFormSchema(processDefinitionData: ProcessDefinition): Promise<Record<string, any>> {
    return getProcessSchema(processDefinitionData, this.token);
  }

  processForm__getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form> {
    return getCustomForm(processDefinitionData, this.token);
  }

  processForm__startProcess(processDefinitionData: ProcessDefinition, formData: any): Promise<string> {
    return startProcessInstance(processDefinitionData, formData, this.businessKey, this.token)
      .then((processInstanceId) => {
        this.onStartProcessListeners.forEach((listener) => listener.onSuccess(processInstanceId));
        return processInstanceId;
      })
      .catch((error) => {
        this.onStartProcessListeners.forEach((listener) => listener.onError(error));
        throw error;
      });
  }

  processForm__getProcessDefinitionSvg(processDefinitionData: ProcessDefinition): Promise<string> {
    return getProcessSvg(processDefinitionData, this.token);
  }

  processForm__setBusinessKey(bk: string): void {
    this.businessKey = bk;
  }

  processForm__getBusinessKey(): Promise<string> {
    return Promise.resolve(this.businessKey);
  }

  processForm__onStartProcessListen(listener: OnStartProcessListener): Promise<UnSubscribeHandler> {
    this.onStartProcessListeners.push(listener);

    return Promise.resolve({
      unSubscribe: () => {
        const index = this.onStartProcessListeners.indexOf(listener);
        if (index > -1) {
          this.onStartProcessListeners.splice(index, 1);
        }
      },
    });
  }
}
