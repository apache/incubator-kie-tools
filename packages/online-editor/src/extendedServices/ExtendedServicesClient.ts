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
  ExtendedServicesDmnResult,
  ExtendedServicesDmnJsonSchema,
  ExtendedServicesValidateResponse,
  ExtendedServicesModelPayload,
} from "@kie-tools/extended-services-api";

export class ExtendedServicesClient {
  private readonly DMN_JIT_EXECUTOR_VALIDATE_URL: string;
  private readonly DMN_JIT_EXECUTOR_DMN_RESULT_URL: string;
  private readonly DMN_JIT_EXECUTOR_FORM_SCHEMA_URL: string;
  private readonly BPMN_JIT_EXECUTOR_VALIDATE_URL: string;

  constructor(private readonly jitExecutorUrl: string) {
    this.DMN_JIT_EXECUTOR_VALIDATE_URL = `${this.jitExecutorUrl}jitdmn/validate`;
    this.DMN_JIT_EXECUTOR_DMN_RESULT_URL = `${this.jitExecutorUrl}jitdmn/dmnresult`;
    this.DMN_JIT_EXECUTOR_FORM_SCHEMA_URL = `${this.jitExecutorUrl}jitdmn/schema/form`;
    this.BPMN_JIT_EXECUTOR_VALIDATE_URL = `${this.jitExecutorUrl}jitbpmn/validate`;
  }

  public async result(payload: ExtendedServicesModelPayload): Promise<ExtendedServicesDmnResult> {
    if (!this.isPayloadValid(payload)) {
      return { messages: [] };
    }

    const response = await fetch(this.DMN_JIT_EXECUTOR_DMN_RESULT_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json, text/plain, */*",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  public async validateDmn(payload: ExtendedServicesModelPayload): Promise<ExtendedServicesValidateResponse[]> {
    if (!this.isPayloadValid(payload)) {
      return [];
    }

    const response = await fetch(this.DMN_JIT_EXECUTOR_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  public async validateBpmn(payload: ExtendedServicesModelPayload): Promise<ExtendedServicesValidateResponse[]> {
    if (!this.isPayloadValid(payload)) {
      return [];
    }

    const response = await fetch(this.BPMN_JIT_EXECUTOR_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  public async formSchema(payload: ExtendedServicesModelPayload): Promise<ExtendedServicesDmnJsonSchema> {
    if (!this.isPayloadValid(payload)) {
      return {};
    }

    const response = await fetch(this.DMN_JIT_EXECUTOR_FORM_SCHEMA_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new Error(`${response.status} ${response.statusText}`);
    }

    // The input set property associated with the mainURI can be InputSetX or nsYInputSetX, where X and Y are a number.
    // So replace all occurrences InputSetX/nsYInputSetX -> InputSet to keep compatibility with the current DmnForm.
    const json = await response.json();
    const inputSet = json["$ref"].replace("#/definitions/", "");
    const outputSet = inputSet.replace("InputSet", "OutputSet");
    return JSON.parse(
      JSON.stringify(json)
        .replace(new RegExp(`${inputSet}`, "g"), "InputSet")
        .replace(new RegExp(`${outputSet}`, "g"), "OutputSet")
    );
  }

  private isPayloadValid(payload: ExtendedServicesModelPayload): boolean {
    return payload.resources.every((resource) => resource.content !== "");
  }
}
