/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as Ajv from "ajv";
import { JSONSchemaBridge } from "uniforms-bridge-json-schema";

const ajv = new Ajv({ allErrors: true, useDefaults: true });

function createValidator(schema: any) {
  const validator = ajv.compile(schema);
  return (model: any) => {
    validator(model);
    if (validator.errors && validator.errors.length) {
      throw { details: validator.errors };
    }
  };
}

const schema = {
  type: "object",
  properties: {
    flight: {
      type: "object",
      properties: {
        flightNumber: {
          type: "string",
        },
        seat: {
          type: "string",
        },
        gate: {
          type: "string",
        },
        departure: {
          type: "string",
          format: "date-time",
        },
        arrival: {
          type: "string",
          format: "date-time",
          max: "2000-04-04T10:30:00.000Z",
        },
      },
      disabled: false,
    },
    hotel: {
      type: "object",
      properties: {
        name: {
          type: "string",
        },
        addresses: {
          type: "array",
          items: {
            $ref: "#/definitions/address",
          },
        },
        phone: {
          type: "string",
        },
        bookingNumber: {
          type: "string",
        },
        room: {
          type: "string",
        },
        numberOfBeds: {
          placeholder: "Select...",
          enum: [1, 2, 3],
          type: "number",
        },
      },
    },
  },
  definitions: {
    address: {
      type: "object",
      properties: {
        street: {
          type: "string",
        },
        city: {
          type: "string",
        },
        zipCode: {
          type: "string",
        },
        country: {
          placeholder: "Select...",
          enum: ["Brazil", "Ireland", "USA"],
          type: "string",
        },
      },
    },
  },
  phases: ["complete", "release"],
};

const schemaValidator = createValidator(schema);

export const bridge = new JSONSchemaBridge(schema, schemaValidator);
