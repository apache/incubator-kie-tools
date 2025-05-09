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
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface OnStartProcessListener {
  onSuccess: (processInstanceId: string) => void;
  onError: (error: {
    response: {
      ok?: boolean;
      statusText?: string;
      data?: {
        message: string;
      };
    };
    message: string;
  }) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export interface ProcessFormChannelApi {
  processForm__getProcessFormSchema(processDefinitionData: ProcessDefinition): Promise<Record<string, any>>;
  processForm__getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form>;
  processForm__startProcess(processDefinitionData: ProcessDefinition, formData: any): Promise<string>;
  processForm__getProcessDefinitionSvg(processDefinitionData: ProcessDefinition): Promise<string>;
  processForm__setBusinessKey(bk: string): void;
  processForm__getBusinessKey(): Promise<string>;
  processForm__onStartProcessListen(listener: OnStartProcessListener): Promise<UnSubscribeHandler>;
}
