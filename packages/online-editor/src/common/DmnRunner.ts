/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import JSONSchemaBridge from "./Bridge";

export interface DmnRunnerPayload {
  model: string;
  context: Map<string, object>;
}

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED"
}

interface DecisionResultMessage {
  severity: string;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | string;

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
}

const DMN_RUNNER_SERVER = "http://localhost:8080/";
const DMN_RUNNER_VALIDATE_URL = "http://localhost:8080/jitdmn/validate";
const DMN_RUNNER_DMN_RESULT_URL = "http://localhost:8080/jitdmn/dmnresult";
const DMN_RUNNER_FORM_URL = "http://localhost:8080/jitdmn/schema/form";
const DMN_RUNNER_DOWNLOAD = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

export const ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
ajv.addMetaSchema(metaSchemaDraft04);

function createValidator(jsonSchema: any) {
  const validator = ajv.compile(jsonSchema);

  return (model: any) => {
    validator(model);

    return validator.errors?.length
      ? {
          details: validator.errors?.map(error => {
            if (error.keyword === "required") {
              return { ...error, message: "Required" };
            }
            return error;
          })
        }
      : null;
  };
}

export class DmnRunner {
  public static async checkServer(): Promise<boolean> {
    const response = await fetch(DMN_RUNNER_SERVER, { method: "OPTIONS" });
    return response.status < 300;
  }

  public static async download() {
    try {
      const response = await fetch(DMN_RUNNER_DOWNLOAD, { method: "GET" });
      const blob = await response.blob();

      const objectUrl = URL.createObjectURL(blob);
      window.open(objectUrl, "_blank");
      URL.revokeObjectURL(objectUrl);
    } catch (err) {
      console.error("Automatic JIT download failed.");
    }
  }

  public static async result(payload: DmnRunnerPayload): Promise<DmnResult | undefined> {
    try {
      const response = await fetch(DMN_RUNNER_DMN_RESULT_URL, {
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

  public static async validate(model: string) {
    try {
      const response = await fetch(DMN_RUNNER_VALIDATE_URL, {
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

  public static async getJsonSchemaBridge(model: string) {
    try {
      const response = await fetch(DMN_RUNNER_FORM_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/xml;"
        },
        body: model
      });
      const form = await response.json();
      const formDraft4 = { ...form, $schema: "http://json-schema.org/draft-04/schema#" };
      return new JSONSchemaBridge(formDraft4, createValidator(formDraft4));
    } catch (err) {
      console.error(err);
    }
  }
}
