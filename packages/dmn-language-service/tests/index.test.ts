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

import { DmnDocumentData, DmnLanguageService, DmnLanguageServiceImportedModelResource, ImportIndex } from "../src";
import { readFileSync } from "fs";
import * as __path from "path";
import { DmnDecision } from "../src/DmnDecision";
import {
  decisions,
  deepNested,
  deepRecursive,
  doubleImport,
  example1,
  example2,
  example3,
  example4,
  example5,
  simple15,
  simple152,
  singleImport,
} from "./fixtures/fileContents";
import { getMarshaller } from "@kie-tools/dmn-marshaller";

class NoErrorThrownError extends Error {}

const getError = async <TError>(call: () => unknown): Promise<TError> => {
  try {
    await call();
    throw new NoErrorThrownError();
  } catch (error: unknown) {
    return error as TError;
  }
};

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
  describe("recursivelyGetImportedModels", () => {
    it("empty string", async () => {
      const dmnLs = new DmnLanguageService({
        getModelContent: getModelContent,
      });

      const emptyResource = [
        {
          content: "",
          normalizedPosixPathRelativeToWorkspaceRoot: "",
        },
      ];

      const error: Error = await getError(async () => await dmnLs.recursivelyGetImportedModels(emptyResource));

      expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - recursivelyGetImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(emptyResource)}
Error details: SyntaxError: about:blank:1:0: document must contain a root element.`);
    });

    it("invalid dmn", async () => {
      const dmnLs = new DmnLanguageService({
        getModelContent: getModelContent,
      });

      const invalidResource = [
        {
          content: `<?xml version="1.0" encoding="UTF-8"?>
<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_FF389036-1B31-408E-9721-3704AE54EBF6" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:included1="https://kiegroup.org/dmn/_2EFA2297-868F-4243-96C8-DCCD82F25F30" id="_4C90F594-F3D5-44BA-966F-AA104304690E" name="example2" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_FF389036-1B31-408E-9721-3704AE54EBF6">
<<invalid/>/>
</dmn:definitions>
`,
          normalizedPosixPathRelativeToWorkspaceRoot: "",
        },
      ];

      const error: Error = await getError(async () => await dmnLs.recursivelyGetImportedModels(invalidResource));

      expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - recursivelyGetImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(invalidResource)}
Error details: SyntaxError: about:blank:3:2: disallowed character in tag name`);
    });

    describe("single file", () => {
      it("fixtures/doubleImport.dmn", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(
          await dmnLs.recursivelyGetImportedModels([
            {
              content: doubleImport(),
              normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/doubleImport.dmn",
            },
          ])
        ).toEqual({
          hierarchy: new Map([
            [
              "fixtures/doubleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/recursive.dmn",
              {
                immediate: new Set(["fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/nested.dmn",
              {
                immediate: new Set([]),
                deep: new Set([]),
              },
            ],
          ]),
          models: new Map([
            [
              "fixtures/doubleImport.dmn",
              getMarshaller(doubleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/recursive.dmn",
              getMarshaller(deepRecursive(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/nested.dmn",
              getMarshaller(deepNested(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
          ]),
        });
      });

      it("fixtures/simple-1.5.dmn", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        expect(
          await dmnLs.recursivelyGetImportedModels([
            {
              content: simple15(),
              normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/simple-1.5.dmn",
            },
          ])
        ).toEqual({
          hierarchy: new Map([
            [
              "fixtures/simple-1.5.dmn",
              {
                immediate: new Set(["fixtures/doubleImport.dmn"]),
                deep: new Set(["fixtures/doubleImport.dmn", "fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/doubleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/recursive.dmn",
              {
                immediate: new Set(["fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/nested.dmn",
              {
                immediate: new Set([]),
                deep: new Set([]),
              },
            ],
          ]),
          models: new Map([
            ["fixtures/simple-1.5.dmn", getMarshaller(simple15(), { upgradeTo: "latest" }).parser.parse().definitions],
            [
              "fixtures/doubleImport.dmn",
              getMarshaller(doubleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/recursive.dmn",
              getMarshaller(deepRecursive(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/nested.dmn",
              getMarshaller(deepNested(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
          ]),
        });
      });

      it("immediate circular dependency", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: example1(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example1.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/example1.dmn",
              {
                immediate: new Set(["fixtures/example2.dmn"]),
                deep: new Set(["fixtures/example2.dmn", "fixtures/example1.dmn"]),
              },
            ],
            [
              "fixtures/example2.dmn",
              {
                immediate: new Set(["fixtures/example1.dmn"]),
                deep: new Set(["fixtures/example1.dmn", "fixtures/example2.dmn"]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            ["fixtures/example1.dmn", getMarshaller(example1(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example2.dmn", getMarshaller(example2(), { upgradeTo: "latest" }).parser.parse().definitions],
          ])
        );
      });

      it("circular dependency with 3 levels", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: example3(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example3.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/example3.dmn",
              {
                immediate: new Set(["fixtures/example4.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
            [
              "fixtures/example4.dmn",
              {
                immediate: new Set(["fixtures/example5.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
            [
              "fixtures/example5.dmn",
              {
                immediate: new Set(["fixtures/example3.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            ["fixtures/example3.dmn", getMarshaller(example3(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example4.dmn", getMarshaller(example4(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example5.dmn", getMarshaller(example5(), { upgradeTo: "latest" }).parser.parse().definitions],
          ])
        );
      });
    });

    describe("multi file", () => {
      it("singleImport and simple-1.5", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: singleImport(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/singleImport.dmn",
          },
          {
            content: simple15(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/simple-1.5.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/singleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/simple-1.5.dmn",
              {
                immediate: new Set(["fixtures/doubleImport.dmn"]),
                deep: new Set(["fixtures/doubleImport.dmn", "fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/doubleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/recursive.dmn",
              {
                immediate: new Set(["fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/nested.dmn",
              {
                immediate: new Set([]),
                deep: new Set([]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            [
              "fixtures/doubleImport.dmn",
              getMarshaller(doubleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/singleImport.dmn",
              getMarshaller(singleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/recursive.dmn",
              getMarshaller(deepRecursive(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/nested.dmn",
              getMarshaller(deepNested(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            ["fixtures/simple-1.5.dmn", getMarshaller(simple15(), { upgradeTo: "latest" }).parser.parse().definitions],
          ])
        );
      });

      it("singleImport and simple-1.5-2", async () => {
        const dmnLs = new DmnLanguageService({ getModelContent });
        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: singleImport(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/singleImport.dmn",
          },
          {
            content: simple152(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/simple-1.5-2.dmn",
          },
        ]);

        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/singleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/simple-1.5-2.dmn",
              {
                immediate: new Set(["fixtures/singleImport.dmn"]),
                deep: new Set(["fixtures/singleImport.dmn", "fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/recursive.dmn",
              {
                immediate: new Set(["fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/nested.dmn",
              {
                immediate: new Set([]),
                deep: new Set([]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            [
              "fixtures/simple-1.5-2.dmn",
              getMarshaller(simple152(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/recursive.dmn",
              getMarshaller(deepRecursive(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/nested.dmn",
              getMarshaller(deepNested(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/singleImport.dmn",
              getMarshaller(singleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
          ])
        );
      });

      it("two dmns - doubleImport and simple-1.5", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: doubleImport(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/doubleImport.dmn",
          },
          {
            content: simple15(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/simple-1.5.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/doubleImport.dmn",
              {
                immediate: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/simple-1.5.dmn",
              {
                immediate: new Set(["fixtures/doubleImport.dmn"]),
                deep: new Set(["fixtures/doubleImport.dmn", "fixtures/deep/recursive.dmn", "fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/recursive.dmn",
              {
                immediate: new Set(["fixtures/deep/nested.dmn"]),
                deep: new Set(["fixtures/deep/nested.dmn"]),
              },
            ],
            [
              "fixtures/deep/nested.dmn",
              {
                immediate: new Set([]),
                deep: new Set([]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            [
              "fixtures/doubleImport.dmn",
              getMarshaller(doubleImport(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            ["fixtures/simple-1.5.dmn", getMarshaller(simple15(), { upgradeTo: "latest" }).parser.parse().definitions],
            [
              "fixtures/deep/recursive.dmn",
              getMarshaller(deepRecursive(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
            [
              "fixtures/deep/nested.dmn",
              getMarshaller(deepNested(), { upgradeTo: "latest" }).parser.parse().definitions,
            ],
          ])
        );
      });

      it("immediate circular dependency", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: example1(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example1.dmn",
          },
          {
            content: example2(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example2.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/example1.dmn",
              {
                immediate: new Set(["fixtures/example2.dmn"]),
                deep: new Set(["fixtures/example2.dmn", "fixtures/example1.dmn"]),
              },
            ],
            [
              "fixtures/example2.dmn",
              {
                immediate: new Set(["fixtures/example1.dmn"]),
                deep: new Set(["fixtures/example1.dmn", "fixtures/example2.dmn"]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            ["fixtures/example1.dmn", getMarshaller(example1(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example2.dmn", getMarshaller(example2(), { upgradeTo: "latest" }).parser.parse().definitions],
          ])
        );
      });

      it("circular dependency with 3 levels", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const importIndex = await dmnLs.recursivelyGetImportedModels([
          {
            content: example3(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example3.dmn",
          },
          {
            content: example4(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example4.dmn",
          },
          {
            content: example5(),
            normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/example5.dmn",
          },
        ]);
        expect(importIndex.hierarchy).toEqual(
          new Map([
            [
              "fixtures/example3.dmn",
              {
                immediate: new Set(["fixtures/example4.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
            [
              "fixtures/example4.dmn",
              {
                immediate: new Set(["fixtures/example5.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
            [
              "fixtures/example5.dmn",
              {
                immediate: new Set(["fixtures/example3.dmn"]),
                deep: new Set(["fixtures/example4.dmn", "fixtures/example5.dmn", "fixtures/example3.dmn"]),
              },
            ],
          ])
        );
        expect(importIndex.models).toEqual(
          new Map([
            ["fixtures/example3.dmn", getMarshaller(example3(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example4.dmn", getMarshaller(example4(), { upgradeTo: "latest" }).parser.parse().definitions],
            ["fixtures/example5.dmn", getMarshaller(example5(), { upgradeTo: "latest" }).parser.parse().definitions],
          ])
        );
      });
    });

    describe("invelid model resources", () => {
      it("content", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const dmnModelResources = [
          { content: "aaa", normalizedPosixPathRelativeToWorkspaceRoot: "fixtures/model.dmn" },
        ];

        const error: Error = await getError(async () => await dmnLs.recursivelyGetImportedModels(dmnModelResources));

        expect(error).not.toBeInstanceOf(NoErrorThrownError);
        expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - recursivelyGetImportedModels: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(dmnModelResources)}
Error details: SyntaxError: about:blank:1:3: text data outside of root node.`);
      });

      it("normalizedPosixPathRelativeToWorkspaceRoot", async () => {
        const dmnLs = new DmnLanguageService({
          getModelContent,
        });

        const dmnModelResources = [{ content: doubleImport(), normalizedPosixPathRelativeToWorkspaceRoot: "aaa" }];
        const fsAbsoluteModelPath = __path.resolve(__dirname, "deep/recursive.dmn".split("/").join(__path.sep));

        const error: Error = await getError(async () => await dmnLs.recursivelyGetImportedModels(dmnModelResources));
        expect(error).not.toBeInstanceOf(NoErrorThrownError);
        expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - recursivelyGetImportedModels: Error while getting imported models from model resources.
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

      expect(dmnLs.getDmnSpecVersion(doubleImport())).toEqual("1.2");
    });

    it("1.5", () => {
      const dmnLs = new DmnLanguageService({
        getModelContent,
      });

      expect(dmnLs.getDmnSpecVersion(simple15())).toEqual("1.5");
    });
  });
});
