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
import {
  BOOTSTRAP4_CSS_URL,
  BOOTSTRAP4_JS_URL,
  JQUERY_URL,
} from "@kie-tools/form-code-generator-bootstrap4-theme/dist/theme";
import { jbpmBootstrap4FormCodeGeneratorTheme } from "../dist/jbpmBootstrap4FormCodeGeneratorTheme";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./__mocks__/bootstrap";

describe("jbpmBootstrap4FormCodeGeneratorTheme tests", () => {
  it("Generate", () => {
    const formAsset = jbpmBootstrap4FormCodeGeneratorTheme.generate({
      name: "ApplyFor#Visa",
      schema: ApplyForVisaSchema,
    });

    expect(formAsset).not.toBeUndefined();
    expect(formAsset.name).toStrictEqual("ApplyFor#Visa");
    expect(formAsset.sanitizedName).toStrictEqual("ApplyFor_Visa");
    expect(formAsset.fileName).toStrictEqual("ApplyFor#Visa.html");
    expect(formAsset.sanitizedFileName).toStrictEqual("ApplyFor_Visa.html");
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

  it("generateFormCode - form assets", () => {
    const formAssets = generateFormCode({
      formSchemas: [
        { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
        { name: "ConfirmTravel", schema: ConfirmTravelSchema },
      ],
      formCodeGeneratorTheme: jbpmBootstrap4FormCodeGeneratorTheme,
    });

    expect(formAssets).toHaveLength(2);
    expect(formAssets[0]).toEqual(
      expect.objectContaining({
        formAsset: expect.objectContaining({
          name: "Apply#For#Visa",
          sanitizedName: "Apply_For_Visa",
          fileName: "Apply#For#Visa.html",
          sanitizedFileName: "Apply_For_Visa.html",
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
          fileExt: "html",
        }),
        formError: undefined,
      })
    );

    expect(formAssets[1]).toEqual(
      expect.objectContaining({
        formAsset: expect.objectContaining({
          name: "ConfirmTravel",
          sanitizedName: "ConfirmTravel",
          fileName: "ConfirmTravel.html",
          sanitizedFileName: "ConfirmTravel.html",
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
          fileExt: "html",
        }),
        formError: undefined,
      })
    );
  });
});
