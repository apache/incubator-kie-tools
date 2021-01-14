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

export interface JitDmnPayload {
  model: string;
  context: Map<string, object>;
}

const JIT_DMN_URL = "http://localhost:8080/jitdmn";
const JIT_DMN_SCHEMA_URL = "http://localhost:8080/jitdmn/schema";
const JIT_DOWNLOAD = "https://kiegroup.github.io/kogito-online-ci/temp/runner.zip";

const ajv = new Ajv({ allErrors: true, useDefaults: true });

function createValidator(schema: any) {
  const validator = ajv.compile(schema);

  return (model: any) => {
    validator(model);
    return validator.errors?.length ? { details: validator.errors } : null;
  };
}

export class JitDmn {
  private static createValidator(schema: object) {
    const validator = ajv.compile(schema);

    return (model: object) => {
      validator(model);
      return validator.errors?.length ? { details: validator.errors } : null;
    };
  }

  public static async download() {
    try {
      const response = await fetch(JIT_DOWNLOAD, { method: "GET" });
      const blob = await response.blob();

      const objectUrl = URL.createObjectURL(blob);
      window.open(objectUrl, "_blank");
      URL.revokeObjectURL(objectUrl);
    } catch (err) {
      console.error("Automatic JIT download failed.");
    }
  }

  public static validateForm(payload: JitDmnPayload) {
    return fetch(JIT_DMN_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json, text/plain, */*"
      },
      body: JSON.stringify(payload)
    });
  }

  public static getFormSchema(model: string) {
    try {
      // const response = await fetch(JIT_DMN_SCHEMA_URL, {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/xml;"
      //   },
      //   body: model
      // });
      // const jitDmnSchema = await response.json();
      const jitDmnSchemaHard = {
        definitions: {
          Back_End_Ratio: {
            enum: ["Insufficient", "Sufficient"],
            type: "string"
          },
          Marital_Status: {
            enum: ["M", "D", "S"],
            type: "string"
          },
          Front_End_Ratio: {
            enum: ["Sufficient", "Insufficient"],
            type: "string"
          },
          Credit_Score_FICO: { type: "number" },
          Employment_Status: {
            enum: ["Unemployed", "Employed", "Self-employed", "Student"],
            type: "string"
          },
          Requested_Product: {
            type: "object",
            properties: {
              Type: { $ref: "#/definitions/Product_Type" },
              Rate: { type: "number" },
              Term: { type: "number" },
              Amount: { type: "number" }
            }
          },
          InputSet: {
            required: ["Credit Score", "Applicant Data", "Requested Product"],
            type: "object",
            properties: {
              "Credit Score": { $ref: "#/definitions/Credit_Score" },
              "Applicant Data": { $ref: "#/definitions/Applicant_Data" },
              "Requested Product": { $ref: "#/definitions/Requested_Product" }
            }
          },
          Applicant_Data: {
            type: "object",
            properties: {
              Age: { type: "number" },
              "Marital Status": { $ref: "#/definitions/Marital_Status" },
              "Employment Status": { $ref: "#/definitions/Employment_Status" },
              "Existing Customer": { type: "boolean" },
              Monthly: { $ref: "#/definitions/Applicant_Data_Monthly" }
            }
          },
          Loan_Qualification: {
            type: "object",
            properties: {
              Qualification: { $ref: "#/definitions/Loan_Qualification_Qualification" },
              Reason: { type: "string" }
            }
          },
          Loan_Qualification_Qualification: {
            enum: ["Qualified", "Not Qualified"],
            type: "string"
          },
          OutputSet: {
            type: "object",
            properties: {
              "Front End Ratio": { $ref: "#/definitions/Front_End_Ratio" },
              "Back End Ratio": { $ref: "#/definitions/Back_End_Ratio" },
              "Credit Score Rating": { $ref: "#/definitions/Credit_Score_Rating" },
              "Loan Pre-Qualification": { $ref: "#/definitions/Loan_Qualification" },
              "Credit Score": { $ref: "#/definitions/Credit_Score" },
              "Applicant Data": { $ref: "#/definitions/Applicant_Data" },
              "Requested Product": { $ref: "#/definitions/Requested_Product" }
            }
          },
          Credit_Score: {
            type: "object",
            properties: { FICO: { $ref: "#/definitions/Credit_Score_FICO" } }
          },
          Product_Type: {
            enum: ["Standard Loan", "Special Loan"],
            type: "string"
          },
          Credit_Score_Rating: {
            enum: ["Poor", "Bad", "Fair", "Good", "Excellent"],
            type: "string"
          },
          Applicant_Data_Monthly: {
            type: "object",
            properties: {
              Income: { type: "number" },
              Repayments: { type: "number" },
              Expenses: { type: "number" },
              Tax: { type: "number" },
              Insurance: { type: "number" }
            }
          }
        },
        properties: { context: { $ref: "#/definitions/InputSet" }, model: { type: "string" } },
        type: "object",
        required: ["context", "model"]
      };
      const validator = createValidator(jitDmnSchemaHard);
      const bridge = new JSONSchemaBridge(jitDmnSchemaHard, validator);
      return bridge;
    } catch (err) {
      console.error(err);
    }
  }
}
