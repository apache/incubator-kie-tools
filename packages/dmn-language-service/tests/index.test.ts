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

import { DmnDocumentData, DmnLanguageService, DmnLanguageServiceImportedModelResource } from "../src";
import { readFileSync } from "fs";
import * as __path from "path";
import { DmnDecision } from "../src/DmnDecision";
import { decisions, deepNested, deepRecursive, example1, example2, model, simple15 } from "./fixtures/fileContents";

class NoErrorThrownError extends Error {}

const getError = async <TError>(call: () => unknown): Promise<TError> => {
  try {
    await call();
    throw new NoErrorThrownError();
  } catch (error: unknown) {
    return error as TError;
  }
};

const tests = [
  {
    normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/model.dmn",
    expected: new Map([
      [
        "fixtures/model.dmn",
        [
          { content: deepRecursive(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/recursive.dmn" },
          { content: deepNested(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/nested.dmn" },
        ],
      ],
      [
        "fixtures/deep/recursive.dmn",
        [{ content: deepNested(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/nested.dmn" }],
      ],
    ]),
  },
  {
    normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/recursive.dmn",
    expected: new Map([
      [
        "fixtures/deep/recursive.dmn",
        [{ content: deepNested(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/nested.dmn" }],
      ],
    ]),
  },
  {
    normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/simple-1.5.dmn",
    expected: new Map([
      [
        "fixtures/simple-1.5.dmn",
        [{ content: model(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/model.dmn" }],
      ],
      [
        "fixtures/model.dmn",
        [
          { content: deepRecursive(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/recursive.dmn" },
          { content: deepNested(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/nested.dmn" },
        ],
      ],
      [
        "fixtures/deep/recursive.dmn",
        [{ content: deepNested(), normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/deep/nested.dmn" }],
      ],
    ]),
  },
];

const getModelContent = (args: {
  normalizedPosixPathRelativeToWorkspaceRoot: string;
}): Promise<DmnLanguageServiceImportedModelResource> => {
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
    it("empty string", async () => {
      const dmnLs = new DmnLanguageService({
        getModelContent: getModelContent,
      });

      expect(
        await dmnLs.getImportedModels([
          {
            content: "",
            normalizedPosixPathRelativeToWorkspaceRoot: "",
          },
        ])
      ).toEqual(new Map());
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
          return dmnLs.getImportedModels([
            {
              content,
              normalizedPosixPathRelativeToWorkspaceRoot,
            },
          ]);
        })
      );

      expect(testResources).toEqual(tests.map((test) => test.expected));
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

      const expected = tests.reduce((expectedMap, test) => {
        Array.from(test.expected.entries()).forEach(([key, value]) => {
          expectedMap.set(key, value);
        });
        return expectedMap;
      }, new Map());

      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(await dmnLs.getImportedModels(importedModelResources)).toEqual(expected);
    });

    it("recursive import", async () => {
      const normalizedPosixPathRelativeToWorkspaceRoot = "fixtures/example1.dmn";
      const importedModelNormalizedPosixPathRelativeToWorkspaceRoot = "fixtures/example2.dmn";

      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(
        await dmnLs.getImportedModels([{ content: example1(), normalizedPosixPathRelativeToWorkspaceRoot }])
      ).toEqual(
        new Map([
          [
            "fixtures/example1.dmn",
            [
              {
                content: example2(),
                normalizedPosixPathRelativeToWorkspaceRoot: importedModelNormalizedPosixPathRelativeToWorkspaceRoot,
              },
            ],
          ],
          [
            "fixtures/example2.dmn",
            [
              {
                content: example1(),
                normalizedPosixPathRelativeToWorkspaceRoot: normalizedPosixPathRelativeToWorkspaceRoot,
              },
            ],
          ],
        ])
      );
    });

    describe("invelid model resources", () => {
      it("content", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const dmnModelResources = [
          { content: "aaa", normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/model.dmn" },
        ];

        const error: Error = await getError(async () => await dmnLs.getImportedModels(dmnModelResources));

        expect(error).not.toBeInstanceOf(NoErrorThrownError);
        expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - getImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(dmnModelResources)}
Error details: SyntaxError: about:blank:1:3: text data outside of root node.`);
      });

      it("normalizedPosixPathRelativeToWorkspaceRoot", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const dmnModelResources = [{ content: model(), normalizedPosixPathRelativeToWorkspaceRoot: "aaa" }];
        const fsAbsoluteModelPath = __path.resolve(__dirname, "deep/recursive.dmn".split("/").join(__path.sep));

        const error: Error = await getError(async () => await dmnLs.getImportedModels(dmnModelResources));
        expect(error).not.toBeInstanceOf(NoErrorThrownError);
        expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - getImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(dmnModelResources)}
Error details: Error: ENOENT: no such file or directory, open '${fsAbsoluteModelPath}'`);
      });
    });
  });

  describe("getDmnDocumentData", () => {
    it("get decisions", () => {
      const dmnDocumentData: DmnDocumentData = new DmnDocumentData(
        "https://kiegroup.org/dmn/_57B8BED3-0077-4154-8435-30E57EA6F02E",
        "My Model Name",
        [new DmnDecision("Decision-1"), new DmnDecision("Decision-2"), new DmnDecision("Decision-3")]
      );
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnDocumentData(decisions())).toEqual(dmnDocumentData);
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
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion(model())).toEqual("1.2");
    });

    it("1.5", () => {
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion(simple15())).toEqual("1.5");
    });
  });
});
