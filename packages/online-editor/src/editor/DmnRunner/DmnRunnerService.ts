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

import Ajv from "ajv";
import * as metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { NotificationSeverity } from "@kogito-tooling/notifications/dist/api";

export interface DmnRunnerPayload {
  model: string;
  context: object;
}

export interface DmnRunnerVersion {
  version: string;
}

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED"
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

interface DmnRunnerForm {
  definitions: DmnRunnerFormDefinitions;
}

interface DmnRunnerFormDefinitions {
  InputSet?: {
    required: string[];
    properties: object;
    type: string;
    placeholder?: string;
  };
}

interface DmnRunnerDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
}

export class DmnRunnerService {
  private readonly ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
  private readonly DMN_RUNNER_SERVER_URL: string;
  private readonly DMN_RUNNER_PING: string;
  private readonly DMN_RUNNER_VALIDATE_URL: string;
  private readonly DMN_RUNNER_DMN_RESULT_URL: string;
  private readonly DMN_RUNNER_FORM_URL: string;
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor(private port: string) {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.DMN_RUNNER_SERVER_URL = `http://localhost:${port}`;
    this.DMN_RUNNER_PING = `${this.DMN_RUNNER_SERVER_URL}/ping`;
    this.DMN_RUNNER_VALIDATE_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/validate`;
    this.DMN_RUNNER_DMN_RESULT_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/dmnresult`;
    this.DMN_RUNNER_FORM_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/schema/form`;
  }

  public createValidator(jsonSchema: any) {
    const validator = this.ajv.compile(jsonSchema);

    return (model: any) => {
      validator(model);

      return validator.errors?.length
        ? {
            details: validator.errors?.map(error => {
              if (error.keyword === "required") {
                return { ...error, message: "" };
              }
              return error;
            })
          }
        : null;
    };
  }

  public async checkServer(): Promise<boolean> {
    const response = await fetch(this.DMN_RUNNER_SERVER_URL, { method: "OPTIONS" });
    return response.status < 300;
  }

  public async version(): Promise<DmnRunnerVersion | undefined> {
    try {
      // const response = await fetch(this.DMN_RUNNER_PING, { method: "GET" });
      // return await response.json();
      return await Promise.resolve().then(() => ({ version: "0.0.1" }));
    } catch (err) {
      console.error(err);
    }
  }

  public async result(payload: DmnRunnerPayload): Promise<DmnResult | undefined> {
    try {
      const response = await fetch(this.DMN_RUNNER_DMN_RESULT_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json, text/plain, */*"
        },
        body: JSON.stringify(payload)
      });
      return await response.json();
    } catch (err) {
      console.error(err);
    }
  }

  public async validate(model: string) {
    try {
      const response = await fetch(this.DMN_RUNNER_VALIDATE_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/xml;"
        },
        body: model
      });
      return await response.json();
    } catch (err) {
      console.error(err);
    }
  }

  private addMissingTypesToDeepProperties(form: DmnRunnerForm, value: DmnRunnerDeepProperty) {
    if (Object.hasOwnProperty.call(value, "$ref")) {
      const property = value.$ref!.split("/").pop()! as keyof DmnRunnerFormDefinitions;
      if (form.definitions[property] && Object.hasOwnProperty.call(form.definitions[property], "properties")) {
        Object.values(form.definitions[property]!.properties).forEach((deepValue: DmnRunnerDeepProperty) => {
          this.addMissingTypesToDeepProperties(form, deepValue);
        });
      } else if (!Object.hasOwnProperty.call(form.definitions[property], "type")) {
        form.definitions[property]!.type = "string";
      } else if (Object.hasOwnProperty.call(form.definitions[property], "enum")) {
        form.definitions[property]!.placeholder = "Select...";
      }
    } else if (!Object.hasOwnProperty.call(value, "type")) {
      value.type = "string";
    }
  }

  private removeRequirementAndAddMissingTypes(form: DmnRunnerForm) {
    delete form.definitions.InputSet?.required;
    if (Object.hasOwnProperty.call(form.definitions.InputSet, "properties")) {
      Object.values(form.definitions.InputSet?.properties!).forEach((value: DmnRunnerDeepProperty) => {
        this.addMissingTypesToDeepProperties(form, value);
      });
    }
  }

  public async getJsonSchemaBridge(model: string) {
    const response = await fetch(this.DMN_RUNNER_FORM_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/xml;"
      },
      body: model
    });
    const form = (await response.json()) as DmnRunnerForm;
    this.removeRequirementAndAddMissingTypes(form);
    const formDraft4 = { ...form, $schema: this.SCHEMA_DRAFT4 };
    return new JSONSchemaBridge(formDraft4, this.createValidator(formDraft4));
  }
}
