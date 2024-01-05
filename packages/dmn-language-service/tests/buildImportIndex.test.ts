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

import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as __path from "path";
import { DmnLanguageService } from "../src";
import {
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
} from "./fs/fixtures";
import { asyncGetModelXmlForTestFixtures } from "./fs/getModelXml";

class NoErrorThrownError extends Error {}

const getError = async <E>(call: () => unknown): Promise<E> => {
  try {
    await call();
    throw new NoErrorThrownError();
  } catch (error: unknown) {
    return error as E;
  }
};

describe("invalid inputs", () => {
  it("empty string", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const emptyResource = [
      {
        content: "",
        normalizedPosixPathRelativeToTheWorkspaceRoot: "",
      },
    ];

    const error: Error = await getError(async () => await dmnLs.buildImportIndex(emptyResource));

    expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - buildImportIndex: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(emptyResource)}
Error details: SyntaxError: about:blank:1:0: document must contain a root element.`);
  });

  it("invalid dmn", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const invalidResource = [
      {
        content: `<?xml version="1.0" encoding="UTF-8"?>
<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="https://kiegroup.org/dmn/_FF389036-1B31-408E-9721-3704AE54EBF6" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" xmlns:included1="https://kiegroup.org/dmn/_2EFA2297-868F-4243-96C8-DCCD82F25F30" id="_4C90F594-F3D5-44BA-966F-AA104304690E" name="example2" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="https://kiegroup.org/dmn/_FF389036-1B31-408E-9721-3704AE54EBF6">
<<invalid/>/>
</dmn:definitions>
`,
        normalizedPosixPathRelativeToTheWorkspaceRoot: "",
      },
    ];

    const error: Error = await getError(async () => await dmnLs.buildImportIndex(invalidResource));

    expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - buildImportIndex: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(invalidResource)}
Error details: SyntaxError: about:blank:3:2: disallowed character in tag name`);
  });
});

describe("single file", () => {
  it("fixtures/doubleImport.dmn", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    expect(
      await dmnLs.buildImportIndex([
        {
          content: doubleImport(),
          normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/doubleImport.dmn",
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
        ["fixtures/doubleImport.dmn", { xml: doubleImport(), definitions: getDefinitions(doubleImport()) }],
        ["fixtures/deep/recursive.dmn", { xml: deepRecursive(), definitions: getDefinitions(deepRecursive()) }],
        ["fixtures/deep/nested.dmn", { xml: deepNested(), definitions: getDefinitions(deepNested()) }],
      ]),
    });
  });

  it("fixtures/simple-1.5.dmn", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    expect(
      await dmnLs.buildImportIndex([
        {
          content: simple15(),
          normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5.dmn",
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
        ["fixtures/simple-1.5.dmn", { xml: simple15(), definitions: getDefinitions(simple15()) }],
        ["fixtures/doubleImport.dmn", { xml: doubleImport(), definitions: getDefinitions(doubleImport()) }],
        ["fixtures/deep/recursive.dmn", { xml: deepRecursive(), definitions: getDefinitions(deepRecursive()) }],
        ["fixtures/deep/nested.dmn", { xml: deepNested(), definitions: getDefinitions(deepNested()) }],
      ]),
    });
  });

  it("immediate circular dependency", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: example1(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example1.dmn",
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
        ["fixtures/example1.dmn", { xml: example1(), definitions: getDefinitions(example1()) }],
        ["fixtures/example2.dmn", { xml: example2(), definitions: getDefinitions(example2()) }],
      ])
    );
  });

  it("circular dependency with 3 levels", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: example3(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example3.dmn",
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
        ["fixtures/example3.dmn", { xml: example3(), definitions: getDefinitions(example3()) }],
        ["fixtures/example4.dmn", { xml: example4(), definitions: getDefinitions(example4()) }],
        ["fixtures/example5.dmn", { xml: example5(), definitions: getDefinitions(example5()) }],
      ])
    );
  });
});

describe("multi file", () => {
  it("singleImport and simple-1.5", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: singleImport(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/singleImport.dmn",
      },
      {
        content: simple15(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5.dmn",
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
        ["fixtures/doubleImport.dmn", { xml: doubleImport(), definitions: getDefinitions(doubleImport()) }],
        ["fixtures/singleImport.dmn", { xml: singleImport(), definitions: getDefinitions(singleImport()) }],
        ["fixtures/deep/recursive.dmn", { xml: deepRecursive(), definitions: getDefinitions(deepRecursive()) }],
        ["fixtures/deep/nested.dmn", { xml: deepNested(), definitions: getDefinitions(deepNested()) }],
        ["fixtures/simple-1.5.dmn", { xml: simple15(), definitions: getDefinitions(simple15()) }],
      ])
    );
  });

  it("singleImport and simple-1.5-2", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
    const importIndex = await dmnLs.buildImportIndex([
      {
        content: singleImport(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/singleImport.dmn",
      },
      {
        content: simple152(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5-2.dmn",
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
        ["fixtures/simple-1.5-2.dmn", { xml: simple152(), definitions: getDefinitions(simple152()) }],
        ["fixtures/deep/recursive.dmn", { xml: deepRecursive(), definitions: getDefinitions(deepRecursive()) }],
        ["fixtures/deep/nested.dmn", { xml: deepNested(), definitions: getDefinitions(deepNested()) }],
        ["fixtures/singleImport.dmn", { xml: singleImport(), definitions: getDefinitions(singleImport()) }],
      ])
    );
  });

  it("two dmns - doubleImport and simple-1.5", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: doubleImport(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/doubleImport.dmn",
      },
      {
        content: simple15(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/simple-1.5.dmn",
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
        ["fixtures/doubleImport.dmn", { xml: doubleImport(), definitions: getDefinitions(doubleImport()) }],
        ["fixtures/simple-1.5.dmn", { xml: simple15(), definitions: getDefinitions(simple15()) }],
        ["fixtures/deep/recursive.dmn", { xml: deepRecursive(), definitions: getDefinitions(deepRecursive()) }],
        ["fixtures/deep/nested.dmn", { xml: deepNested(), definitions: getDefinitions(deepNested()) }],
      ])
    );
  });

  it("immediate circular dependency", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: example1(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example1.dmn",
      },
      {
        content: example2(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example2.dmn",
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
        ["fixtures/example1.dmn", { xml: example1(), definitions: getDefinitions(example1()) }],
        ["fixtures/example2.dmn", { xml: example2(), definitions: getDefinitions(example2()) }],
      ])
    );
  });

  it("circular dependency with 3 levels", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: example3(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example3.dmn",
      },
      {
        content: example4(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example4.dmn",
      },
      {
        content: example5(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/example5.dmn",
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
        ["fixtures/example3.dmn", { xml: example3(), definitions: getDefinitions(example3()) }],
        ["fixtures/example4.dmn", { xml: example4(), definitions: getDefinitions(example4()) }],
        ["fixtures/example5.dmn", { xml: example5(), definitions: getDefinitions(example5()) }],
      ])
    );
  });
});

describe("invalid model resources", () => {
  it("content", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const dmnModelResources = [{ content: "aaa", normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/model.dmn" }];

    const error: Error = await getError(async () => await dmnLs.buildImportIndex(dmnModelResources));

    expect(error).not.toBeInstanceOf(NoErrorThrownError);
    expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - buildImportIndex: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(dmnModelResources)}
Error details: SyntaxError: about:blank:1:3: text data outside of root node.`);
  });

  it("normalizedPosixPathRelativeToTheWorkspaceRoot", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const dmnModelResources = [{ content: doubleImport(), normalizedPosixPathRelativeToTheWorkspaceRoot: "aaa" }];
    const fsAbsoluteModelPath = __path.resolve(__dirname, "deep/recursive.dmn".split("/").join(__path.sep));

    const error: Error = await getError(async () => await dmnLs.buildImportIndex(dmnModelResources));
    expect(error).not.toBeInstanceOf(NoErrorThrownError);
    expect(error.message).toEqual(`
DMN LANGUAGE SERVICE - buildImportIndex: Error while getting imported models from model resources.
Tried to use the following model resources: ${JSON.stringify(dmnModelResources)}
Error details: Error: ENOENT: no such file or directory, open '${fsAbsoluteModelPath}'`);
  });
});

function getDefinitions(content: string): DMN15__tDefinitions {
  return getMarshaller(content, { upgradeTo: "latest" }).parser.parse().definitions;
}
