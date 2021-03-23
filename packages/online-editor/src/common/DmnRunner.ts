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
import { JSONSchemaBridge } from "uniforms-bridge-json-schema";
import * as metaSchemaDraft04 from "ajv/lib/refs/json-schema-draft-04.json";

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

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: object | string | null;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

export interface DmnResult {
  details?: string;
  stack?: string;
  decisionResults?: DecisionResult[];
}

const DMN_RUNNER_SERVER = "http://localhost:8080/";
const DMN_RUNNER_URL = "http://localhost:8080/jitdmn";
const DMN_RUNNER_VALIDATE_URL = "http://localhost:8080/jitdmn/validate";
const DMN_RUNNER_DMNRESULT_URL = "http://localhost:8080/jitdmn/dmnresult";
const DMN_RUNNER_FORM_URL = "http://localhost:8080/jitdmn/schema/form";
const DMN_RUNNER_DOWNLOAD = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

export const ajv = new Ajv({ allErrors: true, schemaId: "auto", useDefaults: true });
ajv.addMetaSchema(metaSchemaDraft04);

export const schema = {
  $schema: "http://json-schema.org/draft-04/schema#",
  definitions: {
    Applicant_Data: {
      type: "object",
      properties: {
        Age: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        "Marital Status": {
          $ref: "#/definitions/Marital_Status"
        },
        "Employment Status": {
          $ref: "#/definitions/Applicant_Data_Employment_Status"
        },
        "Existing Customer": {
          type: "boolean",
          "x-dmn-type": "FEEL:boolean"
        },
        Monthly: {
          $ref: "#/definitions/Applicant_Data_Monthly"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Applicant_Data }"
    },
    Requested_Product: {
      type: "object",
      properties: {
        Type: {
          $ref: "#/definitions/Product_Type"
        },
        Rate: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Term: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Amount: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Requested_Product }"
    },
    Applicant_Data_Monthly: {
      type: "object",
      properties: {
        Income: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Repayments: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Expenses: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Tax: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        },
        Insurance: {
          type: "number",
          "x-dmn-type": "FEEL:number"
        }
      }
    },
    Marital_Status: {
      enum: ["M", "D", "S"],
      type: "string",
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Marital_Status }",
      "x-dmn-allowed-values": '"M", "D", "S"'
    },
    Back_End_Ratio: {
      enum: ["Insufficient", "Sufficient"],
      type: "string",
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Back_End_Ratio }",
      "x-dmn-allowed-values": '"Insufficient", "Sufficient"'
    },
    Credit_Score: {
      required: ["FICO"],
      type: "object",
      properties: {
        FICO: {
          $ref: "#/definitions/Credit_Score_FICO"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Credit_Score }"
    },
    InputSet: {
      required: ["Credit Score", "Applicant Data", "Requested Product"],
      type: "object",
      properties: {
        "Credit Score": {
          $ref: "#/definitions/Credit_Score"
        },
        "Applicant Data": {
          $ref: "#/definitions/Applicant_Data"
        },
        "Requested Product": {
          $ref: "#/definitions/Requested_Product"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : InputSet }"
    },
    Credit_Score_Rating: {
      enum: ["Poor", "Bad", "Fair", "Good", "Excellent"],
      type: "string",
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Credit_Score_Rating }",
      "x-dmn-allowed-values": '"Poor", "Bad", "Fair", "Good", "Excellent"'
    },
    Credit_Score_FICO: {
      maximum: 850,
      exclusiveMaximum: false,
      minimum: 300,
      exclusiveMinimum: false,
      type: "number",
      "x-dmn-type": "FEEL:number",
      "x-dmn-allowed-values": "[300..850]"
    },
    OutputSet: {
      type: "object",
      properties: {
        "Front End Ratio": {
          $ref: "#/definitions/Front_End_Ratio"
        },
        "Back End Ratio": {
          $ref: "#/definitions/Back_End_Ratio"
        },
        "Credit Score Rating": {
          $ref: "#/definitions/Credit_Score_Rating"
        },
        "Loan Pre-Qualification": {
          $ref: "#/definitions/Loan_Qualification"
        },
        "Credit Score": {
          $ref: "#/definitions/Credit_Score"
        },
        "Applicant Data": {
          $ref: "#/definitions/Applicant_Data"
        },
        "Requested Product": {
          $ref: "#/definitions/Requested_Product"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : OutputSet }"
    },
    Front_End_Ratio: {
      enum: ["Sufficient", "Insufficient"],
      type: "string",
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Front_End_Ratio }",
      "x-dmn-allowed-values": '"Sufficient", "Insufficient"'
    },
    Product_Type: {
      enum: ["Standard Loan", "Special Loan"],
      type: "string",
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Product_Type }",
      "x-dmn-allowed-values": '"Standard Loan", "Special Loan"'
    },
    Loan_Qualification: {
      type: "object",
      properties: {
        Qualification: {
          $ref: "#/definitions/Loan_Qualification_Qualification"
        },
        Reason: {
          type: "string",
          "x-dmn-type": "FEEL:string"
        }
      },
      "x-dmn-type": "DMNType{ https://kiegroup.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB : Loan_Qualification }"
    },
    Loan_Qualification_Qualification: {
      enum: ["Qualified", "Not Qualified"],
      type: "string",
      "x-dmn-type": "FEEL:string",
      "x-dmn-allowed-values": '"Qualified", "Not Qualified"'
    },
    Applicant_Data_Employment_Status: {
      enum: ["Unemployed", "Employed", "Self-employed", "Student"],
      type: "string",
      "x-dmn-type": "FEEL:string",
      "x-dmn-allowed-values": '"Unemployed", "Employed", "Self-employed", "Student"'
    }
  },
  $ref: "#/definitions/InputSet"
};

function createValidator(jsonSchema: any) {
  const validator = ajv.compile(jsonSchema);

  return (model: any) => {
    validator(model);

    return validator.errors?.length
      ? {
          details: validator.errors?.map(error => {
            console.log(error)
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
      const response = await fetch(DMN_RUNNER_DMNRESULT_URL, {
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
      return new JSONSchemaBridge(schema, createValidator(schema));
    } catch (err) {
      console.error(err);
    }
  }
}
