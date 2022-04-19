/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const SW_SPEC_RETRIES_SCHEMA = {
  $id: "https://serverlessworkflow.io/schemas/0.8/retries.json",
  $schema: "http://json-schema.org/draft-07/schema#",
  description: "Serverless Workflow specification - retries schema",
  type: "object",
  retries: {
    oneOf: [
      {
        type: "string",
        format: "uri",
        description: "URI to a resource containing retry definitions (json or yaml)",
      },
      {
        type: "array",
        description:
          "Workflow Retry definitions. Define retry strategies that can be referenced in states onError definitions",
        items: {
          type: "object",
          $ref: "#/definitions/retrydef",
        },
        additionalItems: false,
        minItems: 1,
      },
    ],
  },
  required: ["retries"],
  definitions: {
    retrydef: {
      type: "object",
      properties: {
        name: {
          type: "string",
          description: "Unique retry strategy name",
          minLength: 1,
        },
        delay: {
          type: "string",
          description: "Time delay between retry attempts (ISO 8601 duration format)",
        },
        maxDelay: {
          type: "string",
          description: "Maximum time delay between retry attempts (ISO 8601 duration format)",
        },
        increment: {
          type: "string",
          description: "Static value by which the delay increases during each attempt (ISO 8601 time format)",
        },
        multiplier: {
          type: ["number", "string"],
          minimum: 0,
          minLength: 1,
          multipleOf: 0.01,
          description: "Numeric value, if specified the delay between retries is multiplied by this value.",
        },
        maxAttempts: {
          type: ["number", "string"],
          minimum: 1,
          minLength: 0,
          description: "Maximum number of retry attempts.",
        },
        jitter: {
          type: ["number", "string"],
          minimum: 0,
          maximum: 1,
          description:
            "If float type, maximum amount of random time added or subtracted from the delay between each retry relative to total delay (between 0 and 1). If string type, absolute maximum amount of random time added or subtracted from the delay between each retry (ISO 8601 duration format)",
        },
      },
      additionalProperties: false,
      required: ["name", "maxAttempts"],
    },
  },
};
