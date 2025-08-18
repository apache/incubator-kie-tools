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

import { DMN_LATEST__tDefinitions } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { KIE__tComponentsWidthsExtension } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { updateExpressionWidths } from "@kie-tools/dmn-editor/dist/mutations/updateExpressionWidths";

describe("updateExpressionWidth", () => {
  const drdIndex = 0;

  test("when a definition is deleted from widthsFromId map, it should also be deleted from definitions", () => {
    const widthEntryForElementA = { dmnElementRef: "a", widths: [10, 150, 110] };
    const widthEntryForElementB = { dmnElementRef: "b", widths: [20, 250, 210] };
    const widthEntryForElementC = { dmnElementRef: "c", widths: [30, 350, 310] };
    const widthEntryForElementD = { dmnElementRef: "d", widths: [40, 450, 410] };

    const definitions = createDefinitionsWithComponentWidths([
      widthEntryForElementA,
      widthEntryForElementB,
      widthEntryForElementC,
      widthEntryForElementD,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [widthEntryForElementA.dmnElementRef, widthEntryForElementA.widths],
      [widthEntryForElementB.dmnElementRef, widthEntryForElementB.widths],
      [widthEntryForElementC.dmnElementRef, widthEntryForElementC.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthEntry: widthEntryForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: widthEntryForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: widthEntryForElementC,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: widthEntryForElementD,
        definitions: definitions,
      })
    ).toBeFalsy();
  });

  test("when a definition is changed in widthsFromId map, it should be changed in definitions", () => {
    const originalWidthEntryForElementA = { dmnElementRef: "a", widths: [1, 1, 1] };
    const originalWidthEntryForElementB = { dmnElementRef: "b", widths: [2, 2, 2] };
    const originalWidthEntryForElementC = { dmnElementRef: "c", widths: [3, 3, 3] };

    const changedWidthEntryForElementA = { dmnElementRef: "a", widths: [11, 11, 11, 11, 11] };
    const changedWidthEntryForElementB = { dmnElementRef: "b", widths: [20, 20, 20] };
    const changedWidthEntryForElementC = { dmnElementRef: "c", widths: [3, 3] };

    const definitions = createDefinitionsWithComponentWidths([
      originalWidthEntryForElementA,
      originalWidthEntryForElementB,
      originalWidthEntryForElementC,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [changedWidthEntryForElementA.dmnElementRef, changedWidthEntryForElementA.widths],
      [changedWidthEntryForElementB.dmnElementRef, changedWidthEntryForElementB.widths],
      [changedWidthEntryForElementC.dmnElementRef, changedWidthEntryForElementC.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthEntry: originalWidthEntryForElementA,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: originalWidthEntryForElementB,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: originalWidthEntryForElementC,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: changedWidthEntryForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: changedWidthEntryForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: changedWidthEntryForElementC,
        definitions: definitions,
      })
    ).toBeTruthy();
  });

  test("when a definition is added in widthsFromId map, it should also be added in definitions", () => {
    const originalWidthEntryForElementA = { dmnElementRef: "a", widths: [1, 1, 1] };
    const originalWidthEntryForElementB = { dmnElementRef: "b", widths: [2, 2, 2] };

    const newWidthEntry = { dmnElementRef: "c", widths: [3, 3] };

    const definitions = createDefinitionsWithComponentWidths([
      originalWidthEntryForElementA,
      originalWidthEntryForElementB,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [originalWidthEntryForElementA.dmnElementRef, originalWidthEntryForElementA.widths],
      [originalWidthEntryForElementB.dmnElementRef, originalWidthEntryForElementB.widths],
      [newWidthEntry.dmnElementRef, newWidthEntry.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthEntry: originalWidthEntryForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: originalWidthEntryForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthEntry: newWidthEntry,
        definitions: definitions,
      })
    ).toBeTruthy();
  });
});

function definitionContainsWidthExtension(args: {
  widthEntry: { dmnElementRef: string; widths: number[] };
  definitions: Normalized<DMN_LATEST__tDefinitions>;
}) {
  const componentsWidthExtension =
    args.definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]["di:extension"]?.["kie:ComponentsWidthsExtension"];

  const element = {
    "@_dmnElementRef": args.widthEntry.dmnElementRef,
    "kie:width": args.widthEntry.widths.map((w) => {
      return { __$$text: w };
    }),
  };

  return componentsWidthExtension?.["kie:ComponentWidths"]?.find(
    (e) =>
      e["@_dmnElementRef"] === element["@_dmnElementRef"] &&
      e["kie:width"]?.length === element["kie:width"].length &&
      e["kie:width"]?.every((val, index) => val.__$$text === element["kie:width"]?.[index].__$$text)
  );
}

function createDefinitionsWithComponentWidths(widthDefinition: { dmnElementRef: string; widths: number[] }[]) {
  // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
  const definitions: Normalized<DMN_LATEST__tDefinitions> = {
    "@_namespace": "https://kie.org/dmn/_982CA10C-B1B9-495C-9CFC-98FAA801BB50",
    "@_id": generateUuid(),
    "@_name": "my definitions",
    "dmndi:DMNDI": {
      "dmndi:DMNDiagram": [
        {
          "@_id": generateUuid(),
          "di:extension": createComponentWidthExtension(widthDefinition),
        },
      ],
    },
  };
  return definitions;
}

function createComponentWidthExtension(widthDefinition: { dmnElementRef: string; widths: number[] }[]) {
  // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
  const componentsWidthsExtension: KIE__tComponentsWidthsExtension = {
    ComponentWidths: [
      ...widthDefinition.map((w) => {
        return {
          "@_dmnElementRef": w.dmnElementRef,
          width: w.widths.map((w) => {
            return { __$$text: w };
          }),
        };
      }),
    ],
  };
  return componentsWidthsExtension;
}
