/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

export const SW_SPEC_YARD_SCEMA = {
  $schema: "http://json-schema.org/draft-07/schema#",
  definitions: {
    "DecisionTable-1": {
      type: "object",
      properties: {
        inputs: {
          type: "array",
          items: {
            type: "string",
          },
        },
        hitPolicy: {
          type: "string",
          default: "ANY",
        },
        rules: {
          type: "array",
          items: {
            anyOf: [
              {
                $ref: "#/definitions/InlineRule",
              },
              {
                $ref: "#/definitions/WhenThenRule",
              },
            ],
          },
        },
        outputComponents: {
          description: "deprecated",
          type: "array",
          items: {
            type: "string",
          },
        },
      },
      required: ["inputs", "rules"],
    },
    "DecisionTable-2": {
      allOf: [
        {
          $ref: "#/definitions/DecisionTable-1",
        },
        {
          type: "object",
          properties: {
            type: {
              const: "DecisionTable",
            },
          },
          required: ["type"],
        },
      ],
    },
    Element: {
      type: "object",
      properties: {
        name: {
          type: "string",
        },
        type: {
          type: "string",
        },
        requirements: {
          type: "array",
          items: {
            type: "string",
          },
        },
        logic: {
          anyOf: [
            {
              $ref: "#/definitions/DecisionTable-2",
            },
            {
              $ref: "#/definitions/LiteralExpression-2",
            },
          ],
        },
      },
      required: ["name", "type", "logic"],
    },
    InlineRule: {
      type: "array",
      items: {},
    },
    Input: {
      type: "object",
      properties: {
        name: {
          type: "string",
        },
        type: {
          type: "string",
        },
      },
      required: ["name", "type"],
    },
    "LiteralExpression-1": {
      type: "object",
      properties: {
        expression: {
          type: "string",
        },
      },
      required: ["expression"],
    },
    "LiteralExpression-2": {
      allOf: [
        {
          $ref: "#/definitions/LiteralExpression-1",
        },
        {
          type: "object",
          properties: {
            type: {
              const: "LiteralExpression",
            },
          },
          required: ["type"],
        },
      ],
    },
    WhenThenRule: {
      type: "object",
      properties: {
        when: {
          type: "array",
          items: {},
        },
        then: {},
      },
      required: ["when", "then"],
    },
  },
  type: "object",
  properties: {
    specVersion: {
      type: "string",
      default: "alpha",
    },
    kind: {
      type: "string",
      default: "YaRD",
    },
    name: {
      type: "string",
      description:
        "when not provided explicitly, implementation will attempt to deduce the name from the runtime context; if a name cannot be deduced it is an error.",
    },
    expressionLang: {
      type: "string",
      description:
        "An implementation is free to assume a default expressionLang if not explicitly set. For the purpose of a User sharing a YaRD definition, is best to valorise this field explicit.",
    },
    inputs: {
      type: "array",
      items: {
        $ref: "#/definitions/Input",
      },
    },
    elements: {
      type: "array",
      items: {
        $ref: "#/definitions/Element",
      },
    },
  },
  required: ["inputs", "elements"],
};
