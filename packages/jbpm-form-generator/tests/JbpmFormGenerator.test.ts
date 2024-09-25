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

import { JbpmFormGenerator } from "../dist/JbpmFormGenerator";
import { FormAsset, FormGenerator, FormSchema, FormStyle } from "../dist/types";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./__mocks__/partternfly";
import { inputSanitizationUtil } from "../dist/inputSanitizationUtil";

describe("JbpmFormGenerator tests", () => {
  describe("getFormGenerator tests", () => {
    it("Lookup existing formGenerator - patternfly", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      const formGenerator = jbpmFormGenerator.getFormGenerator(FormStyle.PATTERNFLY);

      expect(formGenerator).not.toBeUndefined();
      expect(formGenerator.type).toStrictEqual(FormStyle.PATTERNFLY);
    });

    it("Lookup wrong formGenerator", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      expect(() => jbpmFormGenerator.getFormGenerator("wrong formGenerator type")).toThrow(
        `Unsupported form generation type: "wrong formGenerator type"`
      );
    });

    it("Register formGenerator & lookup", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      const customFormGeneratorImpl: FormGenerator = {
        type: "cool new formGenerator",
        generate: jest.fn(),
      };

      jbpmFormGenerator.registerFormGeneratorType(customFormGeneratorImpl);

      const customFormGenerator = jbpmFormGenerator.getFormGenerator(customFormGeneratorImpl.type);
      expect(customFormGenerator).not.toBeUndefined();
      expect(customFormGenerator).toStrictEqual(customFormGeneratorImpl);

      const patternflyFormGenerator = jbpmFormGenerator.getFormGenerator(FormStyle.PATTERNFLY);
      expect(patternflyFormGenerator).not.toBeUndefined();
    });
  });

  describe("generateForms tests", () => {
    it("Generate forms with wrong formGenerator type", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      expect(() =>
        jbpmFormGenerator.generateForms({
          formSchemas: [{ name: "", schema: {} }],
          type: "wrong type",
        })
      ).toThrow('Unsupported form generation type: "wrong type"');
    });

    it("Generate forms for empty schema", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [{ name: "test", schema: {} }],
        type: "patternfly",
      });

      expect(formAssets[0]).toEqual(
        expect.objectContaining({
          id: "test",
          sanitizedId: "test",
          assetName: "test.tsx",
          sanitizedAssetName: "test.tsx",
          config: { resources: { scripts: {}, styles: {} }, schema: "{}" },
          type: "tsx",
        })
      );
    });

    it("Generate forms project with schemas", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [
          { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        type: "patternfly",
      });

      expect(formAssets[0]).toEqual(
        expect.objectContaining({
          id: "Apply#For#Visa",
          sanitizedId: "Apply_For_Visa",
          assetName: "Apply#For#Visa.tsx",
          sanitizedAssetName: "Apply_For_Visa.tsx",
          config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ApplyForVisaSchema) },
          type: "tsx",
        })
      );
      expect(formAssets[1]).toEqual(
        expect.objectContaining({
          id: "ConfirmTravel",
          sanitizedId: "ConfirmTravel",
          assetName: "ConfirmTravel.tsx",
          sanitizedAssetName: "ConfirmTravel.tsx",
          config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ConfirmTravelSchema) },
          type: "tsx",
        })
      );
    });

    it("Generate forms project with schemas and one failure", () => {
      const jbpmFormGenerator = new JbpmFormGenerator();
      const testFormGeneratorImpl: FormGenerator = {
        type: "cool formGenerator",

        generate(schema: FormSchema): FormAsset {
          if (schema.name === "ApplyForVisa") {
            throw new Error("Unexpected Error!");
          }

          return {
            id: schema.name,
            sanitizedId: inputSanitizationUtil(schema.name),
            content: schema.name,
            type: "txt",
            assetName: `${schema.name}.txt`,
            sanitizedAssetName: `${inputSanitizationUtil(schema.name)}.txt`,
            config: {
              schema: "",
              resources: { styles: {}, scripts: {} },
            },
          };
        },
      };

      jbpmFormGenerator.registerFormGeneratorType(testFormGeneratorImpl);

      const formAssets = jbpmFormGenerator.generateForms({
        formSchemas: [
          { name: "ApplyForVisa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        type: testFormGeneratorImpl.type,
      });

      expect(formAssets[0]).toEqual({ error: new Error("Unexpected Error!") });
      expect(formAssets[1]).toEqual(
        expect.objectContaining({
          id: "ConfirmTravel",
          sanitizedId: "ConfirmTravel",
          assetName: "ConfirmTravel.txt",
          sanitizedAssetName: "ConfirmTravel.txt",
          config: { resources: { scripts: {}, styles: {} }, schema: "" },
          type: "txt",
        })
      );
    });
  });
});
