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

import { Normalized } from "@kie-tools/dmn-editor/dist/normalization/normalize";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { KIE__tComponentsWidthsExtension } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { updateExpressionWidths } from "@kie-tools/dmn-editor/dist/mutations/updateExpressionWidths";

describe("updateExpressionWidth", () => {
  const drdIndex = 0;

  test("when a definition is deleted from widthsFromId map, it should also be deleted from definitions", () => {
    const widthDefinitionsForElementA = { dmnElementRef: "a", widths: [10, 150, 110] };
    const widthDefinitionsForElementB = { dmnElementRef: "b", widths: [20, 250, 210] };
    const widthDefinitionsForElementC = { dmnElementRef: "c", widths: [30, 350, 310] };
    const widthDefinitionsForElementD = { dmnElementRef: "d", widths: [40, 450, 410] };

    const definitions = createDefinitionsWithComponentWidths([
      widthDefinitionsForElementA,
      widthDefinitionsForElementB,
      widthDefinitionsForElementC,
      widthDefinitionsForElementD,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [widthDefinitionsForElementA.dmnElementRef, widthDefinitionsForElementA.widths],
      [widthDefinitionsForElementB.dmnElementRef, widthDefinitionsForElementB.widths],
      [widthDefinitionsForElementC.dmnElementRef, widthDefinitionsForElementC.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthDefinition: widthDefinitionsForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: widthDefinitionsForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: widthDefinitionsForElementC,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: widthDefinitionsForElementD,
        definitions: definitions,
      })
    ).toBeFalsy();
  });

  test("when a definition is changed in widthsFromId map, it should be changed in definitions", () => {
    const originalWidthDefinitionsForElementA = { dmnElementRef: "a", widths: [1, 1, 1] };
    const originalWidthDefinitionsForElementB = { dmnElementRef: "b", widths: [2, 2, 2] };
    const originalWidthDefinitionsForElementC = { dmnElementRef: "c", widths: [3, 3, 3] };

    const changedWidthDefinitionsForElementA = { dmnElementRef: "a", widths: [11, 11, 11, 11, 11] };
    const changedWidthDefinitionsForElementB = { dmnElementRef: "b", widths: [20, 20, 20] };
    const changedWidthDefinitionsForElementC = { dmnElementRef: "c", widths: [3, 3] };

    const definitions = createDefinitionsWithComponentWidths([
      originalWidthDefinitionsForElementA,
      originalWidthDefinitionsForElementB,
      originalWidthDefinitionsForElementC,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [changedWidthDefinitionsForElementA.dmnElementRef, changedWidthDefinitionsForElementA.widths],
      [changedWidthDefinitionsForElementB.dmnElementRef, changedWidthDefinitionsForElementB.widths],
      [changedWidthDefinitionsForElementC.dmnElementRef, changedWidthDefinitionsForElementC.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthDefinition: originalWidthDefinitionsForElementA,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: originalWidthDefinitionsForElementB,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: originalWidthDefinitionsForElementC,
        definitions: definitions,
      })
    ).toBeFalsy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: changedWidthDefinitionsForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: changedWidthDefinitionsForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: changedWidthDefinitionsForElementC,
        definitions: definitions,
      })
    ).toBeTruthy();
  });

  test("when a definition is added in widthsFromId map, it should also be added in definitions", () => {
    const originalWidthDefinitionsForElementA = { dmnElementRef: "a", widths: [1, 1, 1] };
    const originalWidthDefinitionsForElementB = { dmnElementRef: "b", widths: [2, 2, 2] };

    const newWidthDefinition = { dmnElementRef: "c", widths: [3, 3] };

    const definitions = createDefinitionsWithComponentWidths([
      originalWidthDefinitionsForElementA,
      originalWidthDefinitionsForElementB,
    ]);

    const widthsById: Map<string, number[]> = new Map([
      [originalWidthDefinitionsForElementA.dmnElementRef, originalWidthDefinitionsForElementA.widths],
      [originalWidthDefinitionsForElementB.dmnElementRef, originalWidthDefinitionsForElementB.widths],
      [newWidthDefinition.dmnElementRef, newWidthDefinition.widths],
    ]);

    updateExpressionWidths({ definitions, drdIndex, widthsById });

    expect(
      definitionContainsWidthExtension({
        widthDefinition: originalWidthDefinitionsForElementA,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: originalWidthDefinitionsForElementB,
        definitions: definitions,
      })
    ).toBeTruthy();

    expect(
      definitionContainsWidthExtension({
        widthDefinition: newWidthDefinition,
        definitions: definitions,
      })
    ).toBeTruthy();
  });
});

function definitionContainsWidthExtension(args: {
  widthDefinition: { dmnElementRef: string; widths: number[] };
  definitions: Normalized<DMN15__tDefinitions>;
}) {
  const componentsWidthExtension =
    args.definitions?.["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]["di:extension"]?.["kie:ComponentsWidthsExtension"];

  const element = {
    "@_dmnElementRef": args.widthDefinition.dmnElementRef,
    "kie:width": args.widthDefinition.widths.map((w) => {
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
  const definitions: Normalized<DMN15__tDefinitions> = {
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
