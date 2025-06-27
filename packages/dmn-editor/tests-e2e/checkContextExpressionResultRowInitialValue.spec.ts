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

import { test, expect } from "./__fixtures__/base";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";
import { DMN15__tContext } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Check Context Result Row Initial Value", () => {
  test.describe("Decision node", () => {
    // Drag Decision node to the diagram and select the context expression menu
    test.beforeEach(async ({ bee, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });

      await nodes.edit({ name: DefaultNodeName.DECISION });
      await bee.selectExpressionMenu.selectContext();
    });

    test("result row of the root context should be absent", async ({ jsonModel }) => {
      // JSON model assertions
      const decision = await jsonModel.drgElements.getDecision({ drgElementIndex: 0, drdIndex: 0 });
      expect((decision.expression as DMN15__tContext).contextEntry?.length).toEqual(1);
    });

    test("result row of the nested context should be absent", async ({ bee, jsonModel }) => {
      await bee.expression.asContext().entry(0).selectExpressionMenu.selectContext();

      // JSON model assertions
      const decision = await jsonModel.drgElements.getDecision({ drgElementIndex: 0, drdIndex: 0 });
      expect(
        ((decision.expression as DMN15__tContext).contextEntry![0].expression as DMN15__tContext).contextEntry?.length
      ).toEqual(1);
    });
  });

  test.describe("BKM node", () => {
    // Drag Decision node to the diagram and select the context expression menu
    test.beforeEach(async ({ bee, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });

      await nodes.edit({ name: DefaultNodeName.BKM });
      await bee.expression.asFunction().entry(0).selectExpressionMenu.selectContext();
    });

    test("result row of the context in function should be absent", async ({ jsonModel }) => {
      // JSON model assertions
      const bkm = await jsonModel.drgElements.getBkm({ drgElementIndex: 0, drdIndex: 0 });
      expect((bkm.encapsulatedLogic?.expression as DMN15__tContext).contextEntry?.length).toEqual(1);
    });
  });
});
