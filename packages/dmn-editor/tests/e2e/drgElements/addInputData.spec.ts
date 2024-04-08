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

import { test, expect } from "../__fixtures__/base";
import { DataType } from "../__fixtures__/jsonModel";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Input Data", () => {
  test.describe("Add to the DRG", () => {
    test.describe("add from the palette", () => {
      test("should add Input Data node from palette", async ({ jsonModel, palette, nodes, diagram }) => {
        await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });

        expect(nodes.get({ name: DefaultNodeName.INPUT_DATA })).toBeAttached();
        await expect(diagram.get()).toHaveScreenshot("add-input-data-node-from-palette.png");

        // JSON model assertions
        const inputData = await jsonModel.drgElements.getInputData({ drgElementIndex: 0, drdIndex: 0 });
        expect(inputData).toEqual({
          __$$element: "inputData",
          "@_id": inputData["@_id"],
          "@_name": DefaultNodeName.INPUT_DATA,
          variable: {
            "@_id": inputData.variable?.["@_id"],
            "@_name": DefaultNodeName.INPUT_DATA,
            "@_typeRef": DataType.Undefined,
          },
        });
        expect(await jsonModel.drd.getDrgElementBoundsOnDrd({ drgElementIndex: 0, drdIndex: 0 })).toEqual({
          "@_x": 0,
          "@_y": 0,
          "@_width": 160,
          "@_height": 80,
        });
      });
    });
  });
});
