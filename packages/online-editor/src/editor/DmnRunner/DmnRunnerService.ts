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

import { NotificationSeverity } from "@kie-tooling-core/notifications/dist/api";

export interface DmnRunnerPayload {
  model: string;
  context: object;
}

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DecisionResultMessage {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | object[] | string;

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: Result;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

export interface DmnResult {
  details?: string;
  stack?: string;
  decisionResults?: DecisionResult[];
  messages: DecisionResultMessage[];
}

export interface DmnFormSchema {
  definitions?: {
    InputSet?: {
      properties: object;
    };
  };
}

export class DmnRunnerService {
  private readonly DMN_RUNNER_SERVER_URL: string;
  private readonly DMN_RUNNER_PING: string;
  private readonly DMN_RUNNER_VALIDATE_URL: string;
  private readonly DMN_RUNNER_DMN_RESULT_URL: string;
  private readonly DMN_RUNNER_FORM_SCHEMA_URL: string;

  constructor(private readonly port: string) {
    this.DMN_RUNNER_SERVER_URL = `http://localhost:${port}`;
    this.DMN_RUNNER_PING = `${this.DMN_RUNNER_SERVER_URL}/ping`;
    this.DMN_RUNNER_VALIDATE_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/validate`;
    this.DMN_RUNNER_DMN_RESULT_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/dmnresult`;
    this.DMN_RUNNER_FORM_SCHEMA_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/schema/form`;
  }

  public async check(): Promise<boolean> {
    const response = await fetch(this.DMN_RUNNER_SERVER_URL, { method: "OPTIONS" });
    return response.status < 300;
  }

  public async version(): Promise<string> {
    const response = await fetch(this.DMN_RUNNER_PING, {
      method: "GET",
    });
    const json = await response.json();
    return json.App.Version;
  }

  public async result(payload: DmnRunnerPayload): Promise<DmnResult> {
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

  public async validate(model: string): Promise<[]> {
    const response = await fetch(this.DMN_RUNNER_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/xml;",
      },
      body: model,
    });
    return await response.json();
  }

  public async formSchema(model: string): Promise<DmnFormSchema> {
    const response = await fetch(this.DMN_RUNNER_FORM_SCHEMA_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/xml;",
      },
      body: model,
    });
    return await response.json();
  }
}
