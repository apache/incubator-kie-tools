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

import { DmnDocumentData, DmnLanguageService, DmnLanguageServiceImportedModelResources } from "../src";
import { readFileSync } from "fs";
import { resolve } from "path";
import { DmnDecision } from "../src/DmnDecision";

const tests = [
  { pathRelativeToWorkspaceRoot: "./fixtures/model.dmn", expected: ["fixtures/recursive.dmn", "fixtures/nested.dmn"] },
  { pathRelativeToWorkspaceRoot: "./fixtures/recursive.dmn", expected: ["fixtures/nested.dmn"] },
  { pathRelativeToWorkspaceRoot: "./fixtures/nested.dmn", expected: [] },
];

const getModelContentFromPathRelativeToWorkspaceRoot = (
  pathRelativeToWorkspaceRoot: string
): Promise<DmnLanguageServiceImportedModelResources> => {
  const path = resolve(__dirname, pathRelativeToWorkspaceRoot);
  return new Promise((res, rej) => {
    return res({
      pathRelativeToWorkspaceRoot: path,
      content: readFileSync(path, "utf8"),
    });
  });
};

describe("DmnLanguageService", () => {
  it("getAllImportedModelsByModelResource - empty string", async () => {
    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: "",
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(await dmnLs.getAllImportedModelsByModelResource([{ content: "", pathRelativeToWorkspaceRoot: "" }])).toEqual(
      []
    );
  });

  it("getAllImportedModelsByModelResource - single file", async () => {
    const testResources = await Promise.all(
      tests.map(({ pathRelativeToWorkspaceRoot, expected }) => {
        const dmnLs = new DmnLanguageService({
          modelContent: "",
          pathRelativeToWorkspaceRoot: resolve(__dirname, pathRelativeToWorkspaceRoot),
          getModelContentFromPathRelativeToWorkspaceRoot,
        });

        const content = readFileSync(resolve(__dirname, pathRelativeToWorkspaceRoot), "utf8");
        return dmnLs.getAllImportedModelsByModelResource([
          { content, pathRelativeToWorkspaceRoot: resolve(__dirname, pathRelativeToWorkspaceRoot) },
        ]);
      })
    );

    const expectResources = tests.map(({ expected }) => {
      return expected.map((pathRelativeToWorkspaceRoot) => {
        const path = resolve(__dirname, pathRelativeToWorkspaceRoot);
        return {
          content: readFileSync(path, "utf8"),
          pathRelativeToWorkspaceRoot: path,
        };
      });
    });

    expect(testResources).toEqual(expectResources);
  });

  it("getAllImportedModelsByModelResource - multiple files", async () => {
    const importedModelResources = tests.map(({ pathRelativeToWorkspaceRoot }) => {
      const path = resolve(__dirname, pathRelativeToWorkspaceRoot);
      return {
        content: readFileSync(path, "utf8"),
        pathRelativeToWorkspaceRoot: path,
      };
    });

    const pathSet = new Set();
    const expectResources = tests.flatMap(({ expected }) => {
      return expected.flatMap((pathRelativeToWorkspaceRoot) => {
        const path = resolve(__dirname, pathRelativeToWorkspaceRoot);
        if (pathSet.has(path)) {
          return [];
        }
        pathSet.add(path);
        return {
          content: readFileSync(path, "utf8"),
          pathRelativeToWorkspaceRoot: path,
        };
      });
    });

    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: "",
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(await dmnLs.getAllImportedModelsByModelResource(importedModelResources)).toEqual(expectResources);
  });

  it("getAllImportedModelsByPathRelativeToWorkspaceRoot - empty", async () => {
    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: "",
      getModelContentFromPathRelativeToWorkspaceRoot: (path) => {
        return new Promise((res, rej) => {
          res({
            content: "",
            pathRelativeToWorkspaceRoot: "",
          });
        });
      },
    });

    expect(await dmnLs.getAllImportedModelsByPathRelativeToWorkspaceRoot("")).toEqual([]);
  });

  it("getAllImportedModelsByPathRelativeToWorkspaceRoot - get resources", async () => {
    const absolutePathOfModelWithNestedImportedModel = "./fixtures/recursive.dmn";
    const absolutePathOfImportedModel = resolve(__dirname, "./fixtures/nested.dmn");
    const importedModelContent = readFileSync(absolutePathOfImportedModel, "utf8");

    const expected = [
      {
        pathRelativeToWorkspaceRoot: absolutePathOfImportedModel,
        content: importedModelContent,
      },
    ];

    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: resolve(__dirname, absolutePathOfModelWithNestedImportedModel),
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(
      await dmnLs.getAllImportedModelsByPathRelativeToWorkspaceRoot(absolutePathOfModelWithNestedImportedModel)
    ).toEqual(expected);
  });

  it("getAllImportedModelsByPathRelativeToWorkspaceRoot - should return multiple imported models", async () => {
    const absolutePathOfModelWithMultipleImportedModel = "./fixtures/model.dmn";
    const recursiveAbsolutePathOfImportedModel = resolve(__dirname, "./fixtures/recursive.dmn");
    const nestedAbsolutePathOfImportedModel = resolve(__dirname, "./fixtures/nested.dmn");
    const recursiveImportedModelContent = readFileSync(recursiveAbsolutePathOfImportedModel, "utf8");
    const nestedImportedModelContent = readFileSync(nestedAbsolutePathOfImportedModel, "utf8");

    const expected = [
      {
        pathRelativeToWorkspaceRoot: recursiveAbsolutePathOfImportedModel,
        content: recursiveImportedModelContent,
      },
      {
        pathRelativeToWorkspaceRoot: nestedAbsolutePathOfImportedModel,
        content: nestedImportedModelContent,
      },
    ];

    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: resolve(__dirname, absolutePathOfModelWithMultipleImportedModel),
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(
      await dmnLs.getAllImportedModelsByPathRelativeToWorkspaceRoot(absolutePathOfModelWithMultipleImportedModel)
    ).toEqual(expected);
  });

  it("getAllImportedModelsByPathRelativeToWorkspaceRoot - recursive import", async () => {
    const absolutePathOfExample1ImportedModel = "./fixtures/example1.dmn";
    const example2AbsolutePathOfImportedModel = resolve(__dirname, "./fixtures/example2.dmn");
    const example2ImportedModelContent = readFileSync(example2AbsolutePathOfImportedModel, "utf8");

    const expected = [
      {
        pathRelativeToWorkspaceRoot: example2AbsolutePathOfImportedModel,
        content: example2ImportedModelContent,
      },
    ];

    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: resolve(__dirname, absolutePathOfExample1ImportedModel),
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(await dmnLs.getAllImportedModelsByPathRelativeToWorkspaceRoot(absolutePathOfExample1ImportedModel)).toEqual(
      expected
    );
  });

  it("getDmnDocumentData - get decisions", async () => {
    const absolutePathOfModelWithNestedImportedModel = resolve(__dirname, "./fixtures/decisions.dmn");
    const modelContent = readFileSync(absolutePathOfModelWithNestedImportedModel, "utf8");

    const absolutePathOfImportedModel = resolve(__dirname, "./fixtures/nested.dmn");

    const expected = {
      pathRelativeToWorkspaceRoot: absolutePathOfImportedModel,
      content: readFileSync(absolutePathOfImportedModel, "utf8"),
    };

    const dmnDocumentData: DmnDocumentData = new DmnDocumentData(
      "https://kiegroup.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E",
      "My Model Name",
      [new DmnDecision("Decision-1"), new DmnDecision("Decision-2"), new DmnDecision("Decision-3")]
    );
    const dmnLs = new DmnLanguageService({
      modelContent: "",
      pathRelativeToWorkspaceRoot: absolutePathOfModelWithNestedImportedModel,
      getModelContentFromPathRelativeToWorkspaceRoot,
    });

    expect(dmnLs.getDmnDocumentData(modelContent)).toEqual(dmnDocumentData);
  });
});
