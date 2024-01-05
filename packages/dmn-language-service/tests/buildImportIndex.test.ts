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
  dmn12A,
  dmn12B,
  dmn12C,
  dmn12D,
  dmn15ImportsDmn12C,
  dmn15ImportsDmn12D,
  immediateRecursionA,
  immediateRecursionB,
  threeLevelRecursionA,
  threeLevelRecursionB,
  threeLevelRecursionC,
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

describe("retrieve import index from single model resource", () => {
  it("dmn12 - dmn without imports - correctly populate the immediate and deep hierarchy", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12A(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/a.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }]])
    );
  });

  it("dmn12 - correctly populate the immediate and deep hierarchy", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/dImportsAB.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("dmn 1.5 - correctly populate the immediate and deep hierarchy", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
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
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("immediate recursion", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: immediateRecursionA(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/aImportsB.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/immediateRecursion/aImportsB.dmn",
          {
            immediate: new Set(["fixtures/immediateRecursion/bImportsA.dmn"]),
            deep: new Set(["fixtures/immediateRecursion/bImportsA.dmn", "fixtures/immediateRecursion/aImportsB.dmn"]),
          },
        ],
        [
          "fixtures/immediateRecursion/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/immediateRecursion/aImportsB.dmn"]),
            deep: new Set(["fixtures/immediateRecursion/aImportsB.dmn", "fixtures/immediateRecursion/bImportsA.dmn"]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        [
          "fixtures/immediateRecursion/aImportsB.dmn",
          { xml: immediateRecursionA(), definitions: getDefinitions(immediateRecursionA()) },
        ],
        [
          "fixtures/immediateRecursion/bImportsA.dmn",
          { xml: immediateRecursionB(), definitions: getDefinitions(immediateRecursionB()) },
        ],
      ])
    );
  });

  it("three level recursion", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: threeLevelRecursionA(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/aImportsB.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/threeLevelRecursion/aImportsB.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/bImportsC.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
        [
          "fixtures/threeLevelRecursion/bImportsC.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/cImportsA.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
        [
          "fixtures/threeLevelRecursion/cImportsA.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/aImportsB.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        [
          "fixtures/threeLevelRecursion/aImportsB.dmn",
          { xml: threeLevelRecursionA(), definitions: getDefinitions(threeLevelRecursionA()) },
        ],
        [
          "fixtures/threeLevelRecursion/bImportsC.dmn",
          { xml: threeLevelRecursionB(), definitions: getDefinitions(threeLevelRecursionB()) },
        ],
        [
          "fixtures/threeLevelRecursion/cImportsA.dmn",
          { xml: threeLevelRecursionC(), definitions: getDefinitions(threeLevelRecursionC()) },
        ],
      ])
    );
  });
});

describe("retrieve import index from multi model resources", () => {
  it("two dmns with commom deep imports - correctly populate the deep hierarchy", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12C(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/cImportsB.dmn",
      },
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/cImportsB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        ["fixtures/dmn12/cImportsB.dmn", { xml: dmn12C(), definitions: getDefinitions(dmn12C()) }],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
      ])
    );
  });

  it("two dmns with commom deep imports - correctly populate the deep hierarchy - immediate different of deep", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });
    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12C(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/cImportsB.dmn",
      },
      {
        content: dmn15ImportsDmn12C(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12C.dmn",
      },
    ]);

    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/cImportsB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12C.dmn",
          {
            immediate: new Set(["fixtures/dmn12/cImportsB.dmn"]),
            deep: new Set(["fixtures/dmn12/cImportsB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
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
          "fixtures/dmn15/importsDmn12C.dmn",
          { xml: dmn15ImportsDmn12C(), definitions: getDefinitions(dmn15ImportsDmn12C()) },
        ],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
        ["fixtures/dmn12/cImportsB.dmn", { xml: dmn12C(), definitions: getDefinitions(dmn12C()) }],
      ])
    );
  });

  it("two dmns with commom deep imports - correctly populate the deep hierarchy - immediate equal to deep", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/dImportsAB.dmn",
      },
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("two dmns with commom deep imports - correctly populate the deep hierarchy - immediate equal to deep - change order", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
      {
        content: dmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/dImportsAB.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("two equal dmns", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("five dmns - correctly populate the deep hierarchy - immediate equal to deep", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: dmn12B(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/bImportsA.dmn",
      },
      {
        content: dmn15ImportsDmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12D.dmn",
      },
      {
        content: dmn12D(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/dImportsAB.dmn",
      },
      {
        content: dmn15ImportsDmn12C(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn15/importsDmn12C.dmn",
      },
      {
        content: dmn12C(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/dmn12/cImportsB.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/dmn12/dImportsAB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          {
            immediate: new Set(["fixtures/dmn12/dImportsAB.dmn"]),
            deep: new Set(["fixtures/dmn12/dImportsAB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/cImportsB.dmn",
          {
            immediate: new Set(["fixtures/dmn12/bImportsA.dmn"]),
            deep: new Set(["fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn15/importsDmn12C.dmn",
          {
            immediate: new Set(["fixtures/dmn12/cImportsB.dmn"]),
            deep: new Set(["fixtures/dmn12/cImportsB.dmn", "fixtures/dmn12/bImportsA.dmn", "fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/dmn12/a.dmn"]),
            deep: new Set(["fixtures/dmn12/a.dmn"]),
          },
        ],
        [
          "fixtures/dmn12/a.dmn",
          {
            immediate: new Set([]),
            deep: new Set([]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        ["fixtures/dmn12/cImportsB.dmn", { xml: dmn12C(), definitions: getDefinitions(dmn12C()) }],
        ["fixtures/dmn12/dImportsAB.dmn", { xml: dmn12D(), definitions: getDefinitions(dmn12D()) }],
        [
          "fixtures/dmn15/importsDmn12D.dmn",
          { xml: dmn15ImportsDmn12D(), definitions: getDefinitions(dmn15ImportsDmn12D()) },
        ],
        [
          "fixtures/dmn15/importsDmn12C.dmn",
          { xml: dmn15ImportsDmn12C(), definitions: getDefinitions(dmn15ImportsDmn12C()) },
        ],
        ["fixtures/dmn12/bImportsA.dmn", { xml: dmn12B(), definitions: getDefinitions(dmn12B()) }],
        ["fixtures/dmn12/a.dmn", { xml: dmn12A(), definitions: getDefinitions(dmn12A()) }],
      ])
    );
  });

  it("two dmns that import each other - immediate recursion", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: immediateRecursionA(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/aImportsB.dmn",
      },
      {
        content: immediateRecursionB(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/immediateRecursion/bImportsA.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/immediateRecursion/aImportsB.dmn",
          {
            immediate: new Set(["fixtures/immediateRecursion/bImportsA.dmn"]),
            deep: new Set(["fixtures/immediateRecursion/bImportsA.dmn", "fixtures/immediateRecursion/aImportsB.dmn"]),
          },
        ],
        [
          "fixtures/immediateRecursion/bImportsA.dmn",
          {
            immediate: new Set(["fixtures/immediateRecursion/aImportsB.dmn"]),
            deep: new Set(["fixtures/immediateRecursion/aImportsB.dmn", "fixtures/immediateRecursion/bImportsA.dmn"]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        [
          "fixtures/immediateRecursion/aImportsB.dmn",
          { xml: immediateRecursionA(), definitions: getDefinitions(immediateRecursionA()) },
        ],
        [
          "fixtures/immediateRecursion/bImportsA.dmn",
          { xml: immediateRecursionB(), definitions: getDefinitions(immediateRecursionB()) },
        ],
      ])
    );
  });

  it("three dmns that import each other - three level recursion", async () => {
    const dmnLs = new DmnLanguageService({ getModelXml: asyncGetModelXmlForTestFixtures });

    const importIndex = await dmnLs.buildImportIndex([
      {
        content: threeLevelRecursionA(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/aImportsB.dmn",
      },
      {
        content: threeLevelRecursionB(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/bImportsC.dmn",
      },
      {
        content: threeLevelRecursionC(),
        normalizedPosixPathRelativeToTheWorkspaceRoot: "fixtures/threeLevelRecursion/cImportsA.dmn",
      },
    ]);
    expect(importIndex.hierarchy).toEqual(
      new Map([
        [
          "fixtures/threeLevelRecursion/aImportsB.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/bImportsC.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
        [
          "fixtures/threeLevelRecursion/bImportsC.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/cImportsA.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
        [
          "fixtures/threeLevelRecursion/cImportsA.dmn",
          {
            immediate: new Set(["fixtures/threeLevelRecursion/aImportsB.dmn"]),
            deep: new Set([
              "fixtures/threeLevelRecursion/bImportsC.dmn",
              "fixtures/threeLevelRecursion/cImportsA.dmn",
              "fixtures/threeLevelRecursion/aImportsB.dmn",
            ]),
          },
        ],
      ])
    );
    expect(importIndex.models).toEqual(
      new Map([
        [
          "fixtures/threeLevelRecursion/aImportsB.dmn",
          { xml: threeLevelRecursionA(), definitions: getDefinitions(threeLevelRecursionA()) },
        ],
        [
          "fixtures/threeLevelRecursion/bImportsC.dmn",
          { xml: threeLevelRecursionB(), definitions: getDefinitions(threeLevelRecursionB()) },
        ],
        [
          "fixtures/threeLevelRecursion/cImportsA.dmn",
          { xml: threeLevelRecursionC(), definitions: getDefinitions(threeLevelRecursionC()) },
        ],
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

    const dmnModelResources = [{ content: dmn12D(), normalizedPosixPathRelativeToTheWorkspaceRoot: "aaa" }];
    const fsAbsoluteModelPath = __path.resolve(__dirname, "a.dmn".split("/").join(__path.sep));

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
