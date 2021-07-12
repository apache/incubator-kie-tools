/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

export const ApplyForVisaSchema = {
  $schema: "http://json-schema.org/draft-07/schema#",
  type: "object",
  properties: {
    traveller: {
      type: "object",
      properties: {
        address: {
          type: "object",
          properties: {
            city: { type: "string" },
            country: { type: "string" },
            street: { type: "string" },
            zipCode: { type: "string" },
          },
        },
        email: { type: "string" },
        firstName: { type: "string" },
        lastName: { type: "string" },
        nationality: { type: "string" },
      },
      input: true,
    },
    trip: {
      type: "object",
      properties: {
        begin: { type: "string", format: "date-time" },
        city: { type: "string" },
        country: { type: "string" },
        end: { type: "string", format: "date-time" },
        visaRequired: { type: "boolean" },
      },
      input: true,
    },
    visaApplication: {
      type: "object",
      properties: {
        approved: { type: "boolean" },
        city: { type: "string" },
        country: { type: "string" },
        duration: { type: "integer" },
        firstName: { type: "string" },
        lastName: { type: "string" },
        nationality: { type: "string" },
        passportNumber: { type: "string" },
      },
      output: true,
    },
  },
};

export const ConfirmTravelSchema = {
  $schema: "http://json-schema.org/draft-07/schema#",
  type: "object",
  properties: {
    flight: {
      type: "object",
      properties: {
        arrival: {
          type: "string",
          format: "date-time",
        },
        departure: {
          type: "string",
          format: "date-time",
        },
        flightNumber: {
          type: "string",
        },
        gate: {
          type: "string",
        },
        seat: {
          type: "string",
        },
      },
      input: true,
    },
    hotel: {
      type: "object",
      properties: {
        address: {
          type: "object",
          properties: {
            city: {
              type: "string",
            },
            country: {
              type: "string",
            },
            street: {
              type: "string",
            },
            zipCode: {
              type: "string",
            },
          },
        },
        bookingNumber: {
          type: "string",
        },
        name: {
          type: "string",
        },
        phone: {
          type: "string",
        },
        room: {
          type: "string",
        },
      },
      input: true,
    },
  },
};
