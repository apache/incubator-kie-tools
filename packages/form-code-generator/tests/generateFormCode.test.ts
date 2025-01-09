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

import { generateFormCode } from "../dist/generateFormCode";
import { FormAsset, FormSchema } from "../dist/types";
import { ApplyForVisaSchema, ConfirmTravelSchema, dummyPatternflyTheme } from "./__mocks__/partternfly";

describe("FormCodeGenerator tests", () => {
  describe("generateFormCode tests", () => {
    it("Generate forms with empty theme", () => {
      const formCode = generateFormCode({
        formSchemas: [{ name: "", schema: {} }],
        formCodeGeneratorTheme: {
          generate: ({ name, schema }) => ({
            fileName: "",
            config: { schema: "", resources: { scripts: {}, styles: {} } },
            content: "",
            name: "",
            fileExt: "",
          }),
        },
      });

      expect(formCode).toHaveLength(1);
      expect(formCode[0]).toEqual(
        expect.objectContaining({
          formAsset: expect.objectContaining({
            name: "",
            fileName: "",
            config: { resources: { scripts: {}, styles: {} }, schema: "" },
            fileExt: "",
          }),
          formError: undefined,
        })
      );
    });

    it("Generate forms for empty schema", () => {
      const formCode = generateFormCode({
        formSchemas: [{ name: "test", schema: {} }],
        formCodeGeneratorTheme: dummyPatternflyTheme,
      });

      expect(formCode).toHaveLength(1);
      expect(formCode[0]).toEqual(
        expect.objectContaining({
          formAsset: expect.objectContaining({
            name: "test",
            fileName: "test.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: "{}" },
            fileExt: "tsx",
          }),
          formError: undefined,
        })
      );
    });

    it("Generate forms project with schemas", () => {
      const formCode = generateFormCode({
        formSchemas: [
          { name: "Apply#For#Visa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        formCodeGeneratorTheme: dummyPatternflyTheme,
      });

      expect(formCode).toHaveLength(2);
      expect(formCode[0]).toEqual(
        expect.objectContaining({
          formAsset: expect.objectContaining({
            name: "Apply#For#Visa",
            fileName: "Apply#For#Visa.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ApplyForVisaSchema) },
            fileExt: "tsx",
          }),
          formError: undefined,
        })
      );
      expect(formCode[1]).toEqual(
        expect.objectContaining({
          formAsset: expect.objectContaining({
            name: "ConfirmTravel",
            fileName: "ConfirmTravel.tsx",
            config: { resources: { scripts: {}, styles: {} }, schema: JSON.stringify(ConfirmTravelSchema) },
            fileExt: "tsx",
          }),
          formError: undefined,
        })
      );
    });

    it("Generate forms project with schemas and one failure", () => {
      const formCode = generateFormCode({
        formSchemas: [
          { name: "ApplyForVisa", schema: ApplyForVisaSchema },
          { name: "ConfirmTravel", schema: ConfirmTravelSchema },
        ],
        formCodeGeneratorTheme: {
          generate(schema: FormSchema): FormAsset<"txt"> {
            if (schema.name === "ApplyForVisa") {
              throw new Error("Unexpected Error!");
            }

            return {
              name: schema.name,
              content: schema.name,
              fileExt: "txt",
              fileName: `${schema.name}.txt`,
              config: {
                schema: "",
                resources: { styles: {}, scripts: {} },
              },
            };
          },
        },
      });

      expect(formCode).toHaveLength(2);
      expect(formCode[0]).toEqual({
        formAsset: undefined,
        formError: new Error("Unexpected Error!"),
      });
      expect(formCode[1]).toEqual(
        expect.objectContaining({
          formAsset: expect.objectContaining({
            name: "ConfirmTravel",
            fileName: "ConfirmTravel.txt",
            config: { resources: { scripts: {}, styles: {} }, schema: "" },
            fileExt: "txt",
          }),
          formError: undefined,
        })
      );
    });
  });
});
