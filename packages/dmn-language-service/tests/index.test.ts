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

import { DmnDocumentData, DmnLanguageService } from "../src";
import { readFile } from "fs/promises";
import { readFileSync } from "fs";
import { resolve } from "path";
import { DmnDecision } from "../src/DmnDecision";

const tests = [
  { pathRelativeToWorkspaceRoot: "./fixtures/model.dmn", expected: ["fixtures/recursive.dmn", "fixtures/nested.dmn"] },
  { pathRelativeToWorkspaceRoot: "./fixtures/recursive.dmn", expected: ["fixtures/nested.dmn"] },
  { pathRelativeToWorkspaceRoot: "./fixtures/nested.dmn", expected: [] },
];

describe("DmnLanguageService", () => {
  const service = new DmnLanguageService({
    getDmnImportedModelResource: () =>
      new Promise((res) =>
        res({
          pathRelativeToWorkspaceRoot: "",
          content: "",
        })
      ),
  });

  it("getImportedModelPathsRelativeToWorkspaceRoot - empty string", () => {
    expect(
      service.getImportedModelPathsRelativeToWorkspaceRoot([{ content: "", pathRelativeToWorkspaceRoot: "" }])
    ).toEqual([]);
  });

  it("getImportedModelPathsRelativeToWorkspaceRoot - single file", () => {
    tests.forEach(({ pathRelativeToWorkspaceRoot, expected }) => {
      const content = readFileSync(resolve(__dirname, pathRelativeToWorkspaceRoot), "utf8");
      expect(service.getImportedModelPathsRelativeToWorkspaceRoot({ content, pathRelativeToWorkspaceRoot })).toEqual(
        expected
      );
    });
  });

  it("getImportedModelPathsRelativeToWorkspaceRoot - multiple files", async () => {
    const importedModelResources = (
      await Promise.all(
        tests.map(({ pathRelativeToWorkspaceRoot }) => readFile(resolve(__dirname, pathRelativeToWorkspaceRoot)))
      )
    ).map((e, i) => ({
      content: e.toString("utf8"),
      pathRelativeToWorkspaceRoot: tests[i].pathRelativeToWorkspaceRoot,
    }));

    expect(service.getImportedModelPathsRelativeToWorkspaceRoot(importedModelResources)).toEqual(
      tests.flatMap((e) => e.expected)
    );
  });

  it("getAllImportedModelsResources - empty", async () => {
    const service = new DmnLanguageService({
      getDmnImportedModelResource: () =>
        new Promise((res) =>
          res({
            pathRelativeToWorkspaceRoot: "",
            content: "",
          })
        ),
    });

    expect(await service.getAllImportedModelsResources([])).toEqual([]);
  });

  it("getAllImportedModelsResources - get resources", async () => {
    const absolutePathOfModelWithNestedIncludedModel = resolve(__dirname, "./fixtures/recursive.dmn");
    const modelContent = readFileSync(absolutePathOfModelWithNestedIncludedModel, "utf8");

    const absolutePathOfIncludedModel = resolve(__dirname, "./fixtures/nested.dmn");
    const includedModelContent = readFileSync(absolutePathOfIncludedModel, "utf8");

    const expected = {
      pathRelativeToWorkspaceRoot: absolutePathOfIncludedModel,
      content: includedModelContent,
    };

    const service = new DmnLanguageService({ getDmnImportedModelResource: () => new Promise((res) => res(expected)) });

    expect(
      await service.getAllImportedModelsResources([
        { content: modelContent, pathRelativeToWorkspaceRoot: absolutePathOfModelWithNestedIncludedModel },
      ])
    ).toEqual([expected]);
  });

  it("getDmnDocumentData - get decisions", async () => {
    const absolutePathOfModelWithNestedIncludedModel = resolve(__dirname, "./fixtures/decisions.dmn");
    const modelContent = readFileSync(absolutePathOfModelWithNestedIncludedModel, "utf8");

    const absolutePathOfIncludedModel = resolve(__dirname, "./fixtures/nested.dmn");
    const includedModelContent = readFileSync(absolutePathOfIncludedModel, "utf8");

    const expected = {
      pathRelativeToWorkspaceRoot: absolutePathOfIncludedModel,
      content: includedModelContent,
    };

    const dmnDocumentData: DmnDocumentData = new DmnDocumentData(
      "https://kiegroup.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E",
      "My Model Name",
      [new DmnDecision("Decision-1"), new DmnDecision("Decision-2"), new DmnDecision("Decision-3")]
    );
    const service = new DmnLanguageService({ getDmnImportedModelResource: () => new Promise((res) => res(expected)) });

    expect(service.getDmnDocumentData(modelContent)).toEqual(dmnDocumentData);
  });
});
