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

import { dereferenceProperties } from "../../jsonSchema/dereference";

describe("jsonSchema::dereferenceProperties", () => {
  describe("JSON Schema with nested properties", () => {
    const mySchema = {
      definitions: {
        OutputSet: {
          type: "object",
          properties: { myOutput: { $ref: "#/definitions/tList" }, myInput: { $ref: "#/definitions/tList" } },
        },
        tList: {
          type: "array",
          items: { $ref: "#/definitions/tType" },
        },
        tNestedStruct: {
          type: "object",
          properties: { tString: { type: "string" } },
        },
        InputSet: {
          required: ["myInput"],
          type: "object",
          properties: { myInput: { $ref: "#/definitions/tList" } },
        },
        tType: {
          type: "object",
          properties: { myNestedStruct: { $ref: "#/definitions/tNestedStruct" } },
        },
      },
      $ref: "#/definitions/InputSet",
    };

    it("should correctly dereference a JSON Schema with properties", () => {
      const expectedSchema = {
        definitions: {
          InputSet: {
            required: ["myInput"],
            type: "object",
            properties: {
              myInput: {
                type: "array",
                items: {
                  type: "object",
                  properties: {
                    myNestedStruct: {
                      type: "object",
                      properties: {
                        tString: {
                          type: "string",
                        },
                      },
                    },
                  },
                },
              },
            },
          },
        },
      };

      expect(dereferenceProperties(mySchema)).toEqual(expectedSchema);
    });

    it("should correctly dereference a JSON Schema with properties - with properties path", () => {
      const expectedSchema = {
        myInput: {
          type: "array",
          items: {
            type: "object",
            properties: {
              myNestedStruct: {
                type: "object",
                properties: {
                  tString: {
                    type: "string",
                  },
                },
              },
            },
          },
        },
      };

      expect(dereferenceProperties(mySchema, mySchema.definitions.InputSet.properties)).toEqual(expectedSchema);
    });
  });

  describe("JSON Schema with items", () => {
    const mySchema = {
      definitions: {
        myString: {
          type: "string",
        },
        myList: {
          type: "array",
          items: {
            $ref: "#/definitions/myStruct",
          },
        },
        InputSet: {
          required: ["InputData-1"],
          type: "object",
          properties: {
            "InputData-1": {
              $ref: "#/definitions/myList",
            },
          },
        },
        myStruct: {
          type: "object",
          properties: {
            name: {
              $ref: "#/definitions/myString",
            },
          },
        },
      },
      $ref: "#/definitions/InputSet",
    };

    it("should correctly dereference a JSON Schema with items", () => {
      const expectedSchema = {
        definitions: {
          InputSet: {
            required: ["InputData-1"],
            type: "object",
            properties: {
              "InputData-1": {
                type: "array",
                items: {
                  type: "object",
                  properties: {
                    name: {
                      type: "string",
                    },
                  },
                },
              },
            },
          },
        },
      };

      expect(dereferenceProperties(mySchema)).toEqual(expectedSchema);
    });

    it("should correctly dereference a JSON Schema with items - with properties path", () => {
      const expectedSchema = {
        "InputData-1": {
          type: "array",
          items: {
            type: "object",
            properties: {
              name: {
                type: "string",
              },
            },
          },
        },
      };

      expect(dereferenceProperties(mySchema, mySchema.definitions.InputSet.properties)).toEqual(expectedSchema);
    });
  });
});
