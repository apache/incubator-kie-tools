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
import * as __path from "path";
import { DmnDecision } from "../src/DmnDecision";

const tests = [
  {
    normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/model.dmn",
    expected: ["fixtures/recursive.dmn", "fixtures/nested.dmn"],
  },
  { normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/recursive.dmn", expected: ["fixtures/nested.dmn"] },
  { normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/nested.dmn", expected: [] },
];

const getModelContent = (args: {
  normalizedPosixPathRelativeToWorkspaceRoot: string;
}): Promise<DmnLanguageServiceImportedModelResources> => {
  const fsAbsolutePath = __path.resolve(
    __dirname,
    args.normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
  );
  return new Promise((res, rej) => {
    return res({
      normalizedPosixPathRelativeToWorkspaceRoot: args.normalizedPosixPathRelativeToWorkspaceRoot,
      content: readFileSync(fsAbsolutePath, "utf8"),
    });
  });
};

describe("DmnLanguageService", () => {
  describe("getImportedModels", () => {
    describe("ModelResource", () => {
      it("empty string", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent: getModelContent,
        });

        expect(
          await dmnLs.getImportedModels({
            modelResources: [
              {
                content: "",
                normalizedPosixPathRelativeToWorkspaceRoot: "",
              },
            ],
          })
        ).toEqual([]);
      });

      it("single file", async () => {
        const testResources = await Promise.all(
          tests.map(({ normalizedPosixPathRelativeToWorkspaceRoot }) => {
            const dmnLs = new DmnLanguageService({
              getModelContent,
            });

            const fsFileAbsolutePath = __path.resolve(
              __dirname,
              normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
            );
            const content = readFileSync(fsFileAbsolutePath, "utf8");
            return dmnLs.getImportedModels({
              modelResources: [
                {
                  content,
                  normalizedPosixPathRelativeToWorkspaceRoot,
                },
              ],
            });
          })
        );

        const expectResources = tests.map(({ expected }) => {
          return expected.map((normalizedPosixPathRelativeToWorkspaceRoot) => {
            const fsFileAbsolutePath = __path.resolve(
              __dirname,
              normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
            );
            return {
              content: readFileSync(fsFileAbsolutePath, "utf8"),
              normalizedPosixPathRelativeToWorkspaceRoot,
            };
          });
        });

        expect(testResources).toEqual(expectResources);
      });

      it("multiple files", async () => {
        const importedModelResources = tests.map(({ normalizedPosixPathRelativeToWorkspaceRoot }) => {
          const fsFileAbsolutePath = __path.resolve(
            __dirname,
            normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
          );
          return {
            content: readFileSync(fsFileAbsolutePath, "utf8"),
            normalizedPosixPathRelativeToWorkspaceRoot,
          };
        });

        const pathSet = new Set();
        const expectResources = tests.flatMap(({ expected }) => {
          return expected.flatMap((normalizedPosixPathRelativeToWorkspaceRoot) => {
            const fsFileAbsolutePath = __path.resolve(
              __dirname,
              normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
            );
            if (pathSet.has(fsFileAbsolutePath)) {
              return [];
            }
            pathSet.add(fsFileAbsolutePath);
            return {
              content: readFileSync(fsFileAbsolutePath, "utf8"),
              normalizedPosixPathRelativeToWorkspaceRoot,
            };
          });
        });

        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(await dmnLs.getImportedModels({ modelResources: importedModelResources })).toEqual(expectResources);
      });
    });

    describe("NormalizedPosixPathRelativeToWorkspaceRoot", () => {
      it("empty", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent: (path) => {
            return new Promise((res, rej) => {
              res({
                content: "",
                normalizedPosixPathRelativeToWorkspaceRoot: "",
              });
            });
          },
        });

        expect(await dmnLs.getImportedModels({ normalizedPosixPathRelativeToWorkspaceRoot: "" })).toEqual([]);
      });

      it("get resources", async () => {
        const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/recursive.dmn";

        const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = "fixtures/nested.dmn";
        const fsImportedModelAbsolutePath = __path.resolve(
          __dirname,
          importedModelNormalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
        );
        const expected = [
          {
            normalizedPosixPathRelativeToWorkspaceRoot: importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
            content: readFileSync(fsImportedModelAbsolutePath, "utf8"),
          },
        ];

        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(await dmnLs.getImportedModels({ normalizedPosixPathRelativeToWorkspaceRoot })).toEqual(expected);
      });

      it("should return multiple imported models", async () => {
        const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/model.dmn";

        const recursiveImportedModelNormalizedPosixPathRelativeToWorkspaceRoot = "fixtures/recursive.dmn";
        const nestedImportedModelNormalizedPosixPathRelativeToWorkspaceRoot = "fixtures/nested.dmn";

        const recursiveImportedModelAbsolutePath = __path.resolve(
          __dirname,
          recursiveImportedModelNormalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
        );
        const nestedImportedModelAbsolutePath = __path.resolve(
          __dirname,
          nestedImportedModelNormalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
        );

        const expected = [
          {
            normalizedPosixPathRelativeToWorkspaceRoot:
              recursiveImportedModelNormalizedPosixPathRelativeToWorkspaceRoot,
            content: readFileSync(recursiveImportedModelAbsolutePath, "utf8"),
          },
          {
            normalizedPosixPathRelativeToWorkspaceRoot: nestedImportedModelNormalizedPosixPathRelativeToWorkspaceRoot,
            content: readFileSync(nestedImportedModelAbsolutePath, "utf8"),
          },
        ];

        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(await dmnLs.getImportedModels({ normalizedPosixPathRelativeToWorkspaceRoot })).toEqual(expected);
      });

      it("recursive import", async () => {
        const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/example1.dmn";
        const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = "fixtures/example2.dmn";
        const fsImportedModelAbsolutePath = __path.resolve(
          __dirname,
          importedModelNormalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
        );
        const expected = [
          {
            normalizedPosixPathRelativeToWorkspaceRoot: importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
            content: readFileSync(fsImportedModelAbsolutePath, "utf8"),
          },
        ];

        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(await dmnLs.getImportedModels({ normalizedPosixPathRelativeToWorkspaceRoot })).toEqual(expected);
      });
    });
  });

  describe("getDmnDocumentData", () => {
    it("get decisions", () => {
      const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/decisions.dmn";
      const fsModelAbsolutePath = __path.resolve(
        __dirname,
        normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
      );
      const modelContent = readFileSync(fsModelAbsolutePath, "utf8");

      const dmnDocumentData: DmnDocumentData = new DmnDocumentData(
        "https://kiegroup.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E",
        "My Model Name",
        [new DmnDecision("Decision-1"), new DmnDecision("Decision-2"), new DmnDecision("Decision-3")]
      );
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnDocumentData(modelContent)).toEqual(dmnDocumentData);
    });
  });

  describe("getDmnSpecVersion", () => {
    it("empty", () => {
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion("")).toEqual(undefined);
    });

    it("invalid", () => {
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion("aa")).toEqual(undefined);
    });

    it("1.2", () => {
      const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/decisions.dmn";
      const fsModelAbsolutePath = __path.resolve(
        __dirname,
        normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
      );

      const modelContent = readFileSync(fsModelAbsolutePath, "utf8");
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion(modelContent)).toEqual("1.2");
    });

    it("1.5", () => {
      const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/simple-1.5.dmn";
      const fsModelAbsolutePath = __path.resolve(
        __dirname,
        normalizedPosixPathRelativeToWorkspaceRoot.split("/").join(__path.sep)
      );
      const modelContent = readFileSync(fsModelAbsolutePath, "utf8");
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion(modelContent)).toEqual("1.5");
    });
  });
});
