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

import { FormCodeGenerator } from "@kie-tools/form-code-generator/dist/FormCodeGenerator";
import {
  BOOTSTRAP4_CSS_URL,
  BOOTSTRAP4_JS_URL,
  jbpmBootstrap4FormCodeGeneratorTheme,
  JQUERY_URL,
} from "../dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./__mocks__/bootstrap";

describe("jbpmBootstrap4FormCodeGeneratorTheme tests", () => {
  it("Generate", () => {
    const formAsset = jbpmBootstrap4FormCodeGeneratorTheme.generate({
      name: "ApplyFor#Visa",
      schema: ApplyForVisaSchema,
    });

    expect(formAsset).not.toBeUndefined();
    expect(formAsset.id).toStrictEqual("ApplyFor#Visa");
    expect(formAsset.sanitizedId).toStrictEqual("ApplyFor_Visa");
    expect(formAsset.assetName).toStrictEqual("ApplyFor#Visa.html");
    expect(formAsset.sanitizedAssetName).toStrictEqual("ApplyFor_Visa.html");
    expect(formAsset.content).not.toBeUndefined();
    expect(formAsset.config).not.toBeUndefined();
    expect(formAsset.config).toMatchObject({
      schema: JSON.stringify(ApplyForVisaSchema),
      resources: {
        styles: {
          "bootstrap.min.css": BOOTSTRAP4_CSS_URL,
        },
        scripts: {
          "jquery.js": JQUERY_URL,
          "bootstrap.bundle.min.js": BOOTSTRAP4_JS_URL,
        },
      },
    });
  });

  it("FormCodeGenerator - bootstrap4", () => {
    const formCodeGenerator = new FormCodeGenerator(jbpmBootstrap4FormCodeGeneratorTheme);
    const bootstrap4CodeGenerator = formCodeGenerator.getFormCodeGenerator("bootstrap");

    expect(bootstrap4CodeGenerator).not.toBeUndefined();
    expect(bootstrap4CodeGenerator.theme).toStrictEqual("bootstrap");
  });

  it("FormCodeGenerator - form assets", () => {
    const formCodeGenerator = new FormCodeGenerator(jbpmBootstrap4FormCodeGeneratorTheme);
    const formAssets = formCodeGenerator.generateForms({
      formSchemas: [
        { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
        { name: "ConfirmTravel", schema: ConfirmTravelSchema },
      ],
      theme: "bootstrap",
    });

    expect(formAssets[0]).toEqual(
      expect.objectContaining({
        formAssets: expect.objectContaining({
          id: "Apply#For#Visa",
          sanitizedId: "Apply_For_Visa",
          assetName: "Apply#For#Visa.html",
          sanitizedAssetName: "Apply_For_Visa.html",
          config: {
            resources: {
              styles: {
                "bootstrap.min.css": BOOTSTRAP4_CSS_URL,
              },
              scripts: {
                "jquery.js": JQUERY_URL,
                "bootstrap.bundle.min.js": BOOTSTRAP4_JS_URL,
              },
            },
            schema: JSON.stringify(ApplyForVisaSchema),
          },
          type: "html",
        }),
        formErrors: undefined,
      })
    );

    expect(formAssets[1]).toEqual(
      expect.objectContaining({
        formAssets: expect.objectContaining({
          id: "ConfirmTravel",
          sanitizedId: "ConfirmTravel",
          assetName: "ConfirmTravel.html",
          sanitizedAssetName: "ConfirmTravel.html",
          config: {
            resources: {
              styles: {
                "bootstrap.min.css": BOOTSTRAP4_CSS_URL,
              },
              scripts: {
                "jquery.js": JQUERY_URL,
                "bootstrap.bundle.min.js": BOOTSTRAP4_JS_URL,
              },
            },
            schema: JSON.stringify(ConfirmTravelSchema),
          },
          type: "html",
        }),
        formErrors: undefined,
      })
    );
  });
});
