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

import { generateForms } from "../dist/generateForms";
import { FormAsset, FormGenerator, FormSchema } from "../dist/types";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./__mocks__/partternfly";
import { registerFormGeneratorType } from "../dist/getFormGenerator";
import { inputSanitizationUtil } from "../dist/inputSanitizationUtil";

describe("generateForms tests", () => {
  it("Generate forms with wrong tool type", () => {
    expect(() =>
      generateForms({
        forms: [{ name: "", schema: {} }],
        type: "wrong type",
      })
    ).toThrow('Unsupported form generation type: "wrong type"');
  });

  it("Generate forms for empty schema", () => {
    const formAssets = generateForms({
      forms: [{ name: "test", schema: {} }],
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
    const formAssets = generateForms({
      forms: [
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
    const testTool: FormGenerator = {
      type: "cool tool",

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

    registerFormGeneratorType(testTool);

    const formAssets = generateForms({
      forms: [
        { name: "ApplyForVisa", schema: ApplyForVisaSchema },
        { name: "ConfirmTravel", schema: ConfirmTravelSchema },
      ],
      type: testTool.type,
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
