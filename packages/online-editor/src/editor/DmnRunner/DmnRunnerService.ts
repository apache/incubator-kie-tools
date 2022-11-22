/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { DmnResult, DmnSchema } from "@kie-tools/form-dmn";

export interface DmnRunnerModelResource {
  URI: string;
  content: string;
}

export interface DmnRunnerModelPayload {
  mainURI: string;
  resources: DmnRunnerModelResource[];
  context?: any;
}

export class DmnRunnerService {
  private readonly DMN_RUNNER_VALIDATE_URL: string;
  private readonly DMN_RUNNER_DMN_RESULT_URL: string;
  private readonly DMN_RUNNER_FORM_SCHEMA_URL: string;

  constructor(private readonly jitExecutorUrl: string) {
    this.DMN_RUNNER_VALIDATE_URL = `${this.jitExecutorUrl}jitdmn/validate`;
    this.DMN_RUNNER_DMN_RESULT_URL = `${this.jitExecutorUrl}jitdmn/dmnresult`;
    this.DMN_RUNNER_FORM_SCHEMA_URL = `${this.jitExecutorUrl}jitdmn/schema/form`;
  }

  public async result(payload: DmnRunnerModelPayload): Promise<DmnResult> {
    if (!this.isPayloadValid(payload)) {
      return { messages: [] };
    }

    const response = await fetch(this.DMN_RUNNER_DMN_RESULT_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json, text/plain, */*",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  public async validate(payload: DmnRunnerModelPayload): Promise<[]> {
    if (!this.isPayloadValid(payload)) {
      return [];
    }

    const response = await fetch(this.DMN_RUNNER_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    return await response.json();
  }

  public async formSchema(payload: DmnRunnerModelPayload): Promise<DmnSchema> {
    if (!this.isPayloadValid(payload)) {
      return {};
    }

    const response = await fetch(this.DMN_RUNNER_FORM_SCHEMA_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      throw new Error(`${response.status} ${response.statusText}`);
    }

    // The input set property associated with the mainURI is InputSetX, where X is a number not always 1.
    // So replace all occurrences InputSetX -> InputSet to keep compatibility with the current DmnForm.
    const json = await response.json();
    const refIndex = json["$ref"].replace("#/definitions/InputSet", "");
    return JSON.parse(
      JSON.stringify(json)
        .replace(new RegExp(`InputSet${refIndex}`, "g"), "InputSet")
        .replace(new RegExp(`OutputSet${refIndex}`, "g"), "OutputSet")
    );
  }

  private isPayloadValid(payload: DmnRunnerModelPayload): boolean {
    return payload.resources.every((resource) => resource.content !== "");
  }
}
