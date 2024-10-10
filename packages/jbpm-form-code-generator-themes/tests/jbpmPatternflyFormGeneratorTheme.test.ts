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

import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { jbpmPatternflyFormCodeGeneratorTheme } from "../dist/jbpmPatternflyFormCodeGeneratorTheme";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./__mocks__/partternfly";

describe("jbpmPatternflyFormCodeGeneratorTheme tests", () => {
  it("Generate", () => {
    const formAsset = jbpmPatternflyFormCodeGeneratorTheme.generate({
      name: "ApplyFor#Visa",
      schema: ApplyForVisaSchema,
    });

    expect(formAsset).not.toBeUndefined();
    expect(formAsset.id).toStrictEqual("ApplyFor#Visa");
    expect(formAsset.sanitizedId).toStrictEqual("ApplyFor_Visa");
    expect(formAsset.assetName).toStrictEqual("ApplyFor#Visa.tsx");
    expect(formAsset.sanitizedAssetName).toStrictEqual("ApplyFor_Visa.tsx");
    expect(formAsset.content).not.toBeUndefined();
    expect(formAsset.content).toContain("const Form__ApplyFor_Visa");
    expect(formAsset.content).toContain("export default Form__ApplyFor_Visa;");
    expect(formAsset.config).not.toBeUndefined();
    expect(formAsset.config).toMatchObject({
      schema: JSON.stringify(ApplyForVisaSchema),
      resources: {
        styles: {},
        scripts: {},
      },
    });
  });

  it("generateFormCode - form assets", () => {
    const formAssets = generateFormCode({
      formSchemas: [
        { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
        { name: "ConfirmTravel", schema: ConfirmTravelSchema },
      ],
      formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme,
    });

    expect(formAssets).toHaveLength(2);
    expect(formAssets[0]).toEqual(
      expect.objectContaining({
        formAsset: expect.objectContaining({
          id: "Apply#For#Visa",
          sanitizedId: "Apply_For_Visa",
          assetName: "Apply#For#Visa.tsx",
          sanitizedAssetName: "Apply_For_Visa.tsx",
          config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ApplyForVisaSchema) },
          type: "tsx",
        }),
        formError: undefined,
      })
    );

    expect(formAssets[1]).toEqual(
      expect.objectContaining({
        formAsset: expect.objectContaining({
          id: "ConfirmTravel",
          sanitizedId: "ConfirmTravel",
          assetName: "ConfirmTravel.tsx",
          sanitizedAssetName: "ConfirmTravel.tsx",
          config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ConfirmTravelSchema) },
          type: "tsx",
        }),
        formError: undefined,
      })
    );
  });
});
