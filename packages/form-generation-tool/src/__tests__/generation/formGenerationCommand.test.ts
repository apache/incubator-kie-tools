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

import { generateForms } from "../../generation";
import * as fs from "../../generation/fs";
import { FormAsset, FormGenerationTool, FormSchema } from "../../generation/types";
import { ApplyForVisaSchema, ConfirmTravelSchema } from "./tools/uniforms/patternfly/mock";
import { registerFormGenerationTool } from "../../generation/tools";

jest.mock("../../generation/fs");

describe("formGenerationCommand tests", () => {
  const loadProjectSchemasMock = jest.spyOn(fs, "loadProjectSchemas");
  const storeFormAssetsMock = jest.spyOn(fs, "storeFormAsset");

  const sourcePath = "/a/test/path";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("Generate forms with wrong tool type", () => {
    generateForms({
      path: sourcePath,
      type: "wrong type",
      overwrite: true,
    });

    expect(loadProjectSchemasMock).not.toBeCalled();
    expect(storeFormAssetsMock).not.toBeCalled();
  });

  it("Generate forms for empty project", () => {
    loadProjectSchemasMock.mockReturnValueOnce([]);

    generateForms({
      path: sourcePath,
      type: "patternfly",
      overwrite: true,
    });

    expect(loadProjectSchemasMock).toBeCalledTimes(1);
    expect(storeFormAssetsMock).not.toBeCalled();
  });

  it("Generate forms project with schemas", () => {
    const schemas: FormSchema[] = [
      {
        name: "ApplyForVisa",
        schema: ApplyForVisaSchema,
      },
      {
        name: "ConfirmTravel",
        schema: ConfirmTravelSchema,
      },
    ];

    loadProjectSchemasMock.mockReturnValueOnce(schemas);

    generateForms({
      path: sourcePath,
      type: "patternfly",
      overwrite: true,
    });

    expect(loadProjectSchemasMock).toBeCalledTimes(1);
    expect(storeFormAssetsMock).toBeCalledTimes(2);

    const applyForVisaAsset: FormAsset = storeFormAssetsMock.mock.calls[0][0];
    expect(applyForVisaAsset.id).toEqual("ApplyForVisa");
    expect(applyForVisaAsset.assetName).toEqual("ApplyForVisa.tsx");
    expect(applyForVisaAsset.content).toContain("const Form__ApplyForVisa");
    expect(storeFormAssetsMock.mock.calls[0][1]).toEqual(sourcePath);
    expect(storeFormAssetsMock.mock.calls[0][2]).toBeTruthy();

    const confirmTravelAsset: FormAsset = storeFormAssetsMock.mock.calls[1][0];
    expect(confirmTravelAsset.id).toEqual("ConfirmTravel");
    expect(confirmTravelAsset.assetName).toEqual("ConfirmTravel.tsx");
    expect(confirmTravelAsset.content).toContain("const Form__ConfirmTravel");
    expect(storeFormAssetsMock.mock.calls[1][1]).toEqual(sourcePath);
    expect(storeFormAssetsMock.mock.calls[1][2]).toBeTruthy();
  });

  it("Generate forms project with schemas and one failure", () => {
    const ERROR_MESSAGE = "Unexpected Error!";

    const tool: FormGenerationTool = {
      type: "cool tool",
      generate: (schema) => {
        if (schema.name === "ApplyForVisa") {
          throw new Error(ERROR_MESSAGE);
        }

        return {
          id: schema.name,
          content: schema.name,
          type: "txt",
          assetName: `${schema.name}.txt`,
        };
      },
    };

    registerFormGenerationTool(tool);

    const schemas: FormSchema[] = [
      {
        name: "ApplyForVisa",
        schema: ApplyForVisaSchema,
      },
      {
        name: "ConfirmTravel",
        schema: ConfirmTravelSchema,
      },
    ];

    loadProjectSchemasMock.mockReturnValueOnce(schemas);

    generateForms({
      path: sourcePath,
      type: tool.type,
      overwrite: true,
    });

    expect(loadProjectSchemasMock).toBeCalledTimes(1);
    expect(storeFormAssetsMock).toBeCalledTimes(1);
  });
});
