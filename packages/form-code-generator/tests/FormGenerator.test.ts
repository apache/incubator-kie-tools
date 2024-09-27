/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { FormCodeGenerator } from "../dist/FormCodeGenerator";
import { FormAsset, FormSchema } from "../dist/types";
import { ApplyForVisaSchema, ConfirmTravelSchema, dummyPatternflyTheme } from "./__mocks__/partternfly";

describe("FormCodeGenerator tests", () => {
  describe("getFormGenerator tests", () => {
    it("Lookup existing formGenerator - patternfly", () => {
      const jbpmFormGenerator = new FormCodeGenerator(dummyPatternflyTheme);
      const formGenerator = jbpmFormGenerator.getFormGenerator("patternfly");

      expect(formGenerator).not.toBeUndefined();
      expect(formGenerator.theme).toStrictEqual("patternfly");
    });

    it("Lookup wrong formGenerator", () => {
      const jbpmFormGenerator = new FormCodeGenerator();
      expect(() => jbpmFormGenerator.getFormGenerator("wrong formGenerator type")).toThrow(
        `Unsupported form generation type: "wrong formGenerator type"`
      );
    });

    it("Register formGenerator & lookup", () => {
      const myCoolFormGenerator = {
        theme: "cool new formGenerator",
        generate: jest.fn(),
      };

      const jbpmFormGenerator = new FormCodeGenerator(dummyPatternflyTheme, myCoolFormGenerator);

      const customFormGenerator = jbpmFormGenerator.getFormGenerator("cool new formGenerator");
      expect(customFormGenerator).not.toBeUndefined();
      expect(customFormGenerator).toStrictEqual(myCoolFormGenerator);

      const patternflyFormGenerator = jbpmFormGenerator.getFormGenerator("patternfly");
      expect(patternflyFormGenerator).not.toBeUndefined();
      expect(patternflyFormGenerator).toStrictEqual(dummyPatternflyTheme);
    });
  });

  describe("generateForms tests", () => {
    it("Generate forms with wrong formGenerator type", () => {
      const jbpmFormGenerator = new FormCodeGenerator();
      expect(() =>
        jbpmFormGenerator.generateForms({
          formSchemas: [{ name: "", schema: {} }],
          theme: "wrong type",
        })
      ).toThrow('Unsupported form generation type: "wrong type"');
    });

    it("Generate forms for empty schema", () => {
      const jbpmFormGenerator = new FormCodeGenerator(dummyPatternflyTheme);
      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [{ name: "test", schema: {} }],
        theme: "patternfly",
      });

      expect(formAssets[0]).toEqual(
        expect.objectContaining({
          formAssets: expect.objectContaining({
            id: "test",
            assetName: "test.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: "{}" },
            type: "tsx",
          }),
          formErrors: undefined,
        })
      );
    });

    it("Generate forms project with schemas", () => {
      const jbpmFormGenerator = new FormCodeGenerator(dummyPatternflyTheme);
      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [
          { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        theme: "patternfly",
      });

      expect(formAssets[0]).toEqual(
        expect.objectContaining({
          formAssets: expect.objectContaining({
            id: "Apply#For#Visa",
            assetName: "Apply#For#Visa.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ApplyForVisaSchema) },
            type: "tsx",
          }),
          formErrors: undefined,
        })
      );
      expect(formAssets[1]).toEqual(
        expect.objectContaining({
          formAssets: expect.objectContaining({
            id: "ConfirmTravel",
            assetName: "ConfirmTravel.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ConfirmTravelSchema) },
            type: "tsx",
          }),
          formErrors: undefined,
        })
      );
    });

    it("Generate forms project with schemas and one failure", () => {
      const jbpmFormGenerator = new FormCodeGenerator({
        theme: "cool formGenerator",

        generate(schema: FormSchema): FormAsset<"txt"> {
          if (schema.name === "ApplyForVisa") {
            throw new Error("Unexpected Error!");
          }

          return {
            id: schema.name,
            content: schema.name,
            type: "txt",
            assetName: `${schema.name}.txt`,
            config: {
              schema: "",
              resources: { styles: {}, scripts: {} },
            },
          };
        },
      });

      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [
          { name: "ApplyForVisa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        theme: "cool formGenerator",
      });

      expect(formAssets[0]).toEqual({
        formAssets: undefined,
        formErrors: new Error("Unexpected Error!"),
      });
      expect(formAssets[1]).toEqual(
        expect.objectContaining({
          formAssets: expect.objectContaining({
            id: "ConfirmTravel",
            assetName: "ConfirmTravel.txt",
            config: { resources: { scripts: {}, styles: {} }, schema: "" },
            type: "txt",
          }),
          formErrors: undefined,
        })
      );
    });
  });
});
