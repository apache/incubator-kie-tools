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
import { DmnRunnerJsonSchemaBridge } from "./uniforms/DmnRunnerJsonSchemaBridge";
import { NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import { OnlineI18n } from "../../common/i18n";

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

interface DmnRunnerForm {
  definitions: DmnRunnerFormDefinitions;
}

interface DmnRunnerFormDefinitions {
  InputSet?: {
    required?: string[];
    properties: object;
    type: string;
    placeholder?: string;
    title: string;
    format?: string;
  };
}

interface DmnRunnerDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
}

export class DmnRunnerService {
  private readonly ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
  private readonly DMN_RUNNER_SERVER_URL: string;
  private readonly DMN_RUNNER_PING: string;
  private readonly DMN_RUNNER_VALIDATE_URL: string;
  private readonly DMN_RUNNER_DMN_RESULT_URL: string;
  private readonly DMN_RUNNER_FORM_URL: string;
  private readonly SCHEMA_DRAFT4 = "http://json-schema.org/draft-04/schema#";

  constructor(private port: string, private readonly i18n: OnlineI18n) {
    this.setupAjv();
    this.DMN_RUNNER_SERVER_URL = `http://localhost:${port}`;
    this.DMN_RUNNER_PING = `${this.DMN_RUNNER_SERVER_URL}/ping`;
    this.DMN_RUNNER_VALIDATE_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/validate`;
    this.DMN_RUNNER_DMN_RESULT_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/dmnresult`;
    this.DMN_RUNNER_FORM_URL = `${this.DMN_RUNNER_SERVER_URL}/jitdmn/schema/form`;
  }

  // TODO: Remove form validation to days and time duration
  private setupAjv() {
    this.ajv.addMetaSchema(metaSchemaDraft04);
    this.ajv.addFormat("days and time duration", {
      type: "string",
      validate: (data: string) => !!data.match(/(P[1-9][0-9]*D[1-9][0-9]*T)|(P([1-9][0-9]*)[D|T])\b/),
    });

    this.ajv.addFormat("years and months duration", {
      type: "string",
      validate: (data: string) => !!data.match(/(P[1-9][0-9]*Y[1-9][0-9]*M)|(P([1-9][0-9]*)[Y|M])\b/),
    });
  }

  public createValidator(jsonSchema: any) {
    const validator = this.ajv.compile(jsonSchema);

    return (model: any) => {
      validator(JSON.parse(JSON.stringify(model)));

      return validator.errors?.length
        ? {
            details: validator.errors?.map((error) => {
              if (error.keyword === "format") {
                if ((error.params as any).format === "days and time duration") {
                  return { ...error, message: this.i18n.dmnRunner.form.validation.daysAndTimeError };
                }
                if ((error.params as any).format === "years and months duration") {
                  return { ...error, message: this.i18n.dmnRunner.form.validation.yearsAndMonthsError };
                }
              }
              return error;
            }),
          }
        : null;
    };
  }

  public async checkServer(): Promise<boolean> {
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

  public async validate(model: string) {
    const response = await fetch(this.DMN_RUNNER_VALIDATE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/xml;",
      },
      body: model,
    });
    return await response.json();
  }

  // Add title, missing types and placeholders
  private formDeepPreprocessing(form: DmnRunnerForm, value: DmnRunnerDeepProperty, title = "") {
    if (Object.hasOwnProperty.call(value, "$ref")) {
      const property = value.$ref!.split("/").pop()! as keyof DmnRunnerFormDefinitions;
      if (form.definitions[property] && Object.hasOwnProperty.call(form.definitions[property], "properties")) {
        Object.entries(form.definitions[property]!.properties).forEach(
          ([key, deepValue]: [string, DmnRunnerDeepProperty]) => {
            this.formDeepPreprocessing(form, deepValue, key);
          }
        );
      } else if (!Object.hasOwnProperty.call(form.definitions[property], "type")) {
        form.definitions[property]!.type = "string";
      } else if (Object.hasOwnProperty.call(form.definitions[property], "enum")) {
        form.definitions[property]!.placeholder = this.i18n.dmnRunner.form.preProcessing.selectPlaceholder;
      } else if (Object.hasOwnProperty.call(form.definitions[property], "format")) {
        this.setCustomPlaceholders(form.definitions[property]!);
      }
      form.definitions[property]!.title = title;
      return;
    }
    value.title = title;
    if (!Object.hasOwnProperty.call(value, "type")) {
      value.type = "string";
      return;
    }
    if (Object.hasOwnProperty.call(value, "format")) {
      this.setCustomPlaceholders(value);
    }
  }

  private setCustomPlaceholders(value: DmnRunnerDeepProperty) {
    if (value?.format === "days and time duration") {
      value!.placeholder = this.i18n.dmnRunner.form.preProcessing.daysAndTimePlaceholder;
    }
    if (value?.format === "years and months duration") {
      value!.placeholder = this.i18n.dmnRunner.form.preProcessing.yearsAndMonthsPlaceholder;
    }
  }

  // Remove required property
  private formPreprocessing(form: DmnRunnerForm) {
    delete form.definitions.InputSet?.required;
    if (Object.hasOwnProperty.call(form.definitions.InputSet, "properties")) {
      Object.entries(form.definitions.InputSet?.properties ?? {}).forEach(
        ([key, value]: [string, DmnRunnerDeepProperty]) => {
          this.formDeepPreprocessing(form, value, key);
        }
      );
    }
  }

  public async getJsonSchemaBridge(model: string) {
    const response = await fetch(this.DMN_RUNNER_FORM_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/xml;",
      },
      body: model,
    });
    const form = (await response.json()) as DmnRunnerForm;
    this.formPreprocessing(form);
    const formDraft4 = { ...form, $schema: this.SCHEMA_DRAFT4 };
    return new DmnRunnerJsonSchemaBridge(formDraft4, this.createValidator(formDraft4));
  }
}
