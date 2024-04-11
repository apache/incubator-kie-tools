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
import { EdgeType } from "../__fixtures__/edges";
import { DataType } from "../__fixtures__/jsonModel";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - BKM", () => {
  test.describe("Add to the DRG", () => {
    test.describe("add from the palette", () => {
      test("should add new BKM node from palette", async ({ jsonModel, palette, nodes, diagram }) => {
        await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 } });

        expect(nodes.get({ name: DefaultNodeName.BKM })).toBeAttached();
        await expect(diagram.get()).toHaveScreenshot("add-bkm-node-from-palette.png");

        // JSON model assertions
        const bkm = await jsonModel.drgElements.getBkm({ drgElementIndex: 0, drdIndex: 0 });
        expect(bkm).toEqual({
          __$$element: "businessKnowledgeModel",
          "@_id": bkm["@_id"],
          "@_name": DefaultNodeName.BKM,
          variable: {
            "@_id": bkm.variable?.["@_id"],
            "@_name": DefaultNodeName.BKM,
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

    test.describe("add from nodes", () => {
      test("should add connected BKM node from BKM node", async ({ diagram, palette, nodes, edges }) => {
        await palette.dragNewNode({
          type: NodeType.BKM,
          targetPosition: { x: 100, y: 100 },
          thenRenameTo: "BKM - A",
        });
        await nodes.dragNewConnectedNode({
          from: "BKM - A",
          type: NodeType.BKM,
          targetPosition: { x: 100, y: 300 },
        });

        expect(await edges.get({ from: "BKM - A", to: DefaultNodeName.BKM })).toBeAttached();
        expect(await edges.getType({ from: "BKM - A", to: DefaultNodeName.BKM })).toEqual(
          EdgeType.KNOWLEDGE_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-bkm-node-from-bkm-node.png");
      });

      test("should add connected BKM node from Decision Service node", async ({ diagram, palette, nodes, edges }) => {
        await palette.dragNewNode({
          type: NodeType.DECISION_SERVICE,
          targetPosition: { x: 100, y: 100 },
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.DECISION_SERVICE,
          type: NodeType.BKM,
          targetPosition: { x: 500, y: 500 },
        });

        expect(await edges.get({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toBeAttached();
        expect(await edges.getType({ from: DefaultNodeName.DECISION_SERVICE, to: DefaultNodeName.BKM })).toEqual(
          EdgeType.KNOWLEDGE_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-bkm-node-from-decision-service-node.png");
      });

      test("should add connected BKM node from Knowledge Source node", async ({ diagram, palette, nodes, edges }) => {
        await palette.dragNewNode({
          type: NodeType.KNOWLEDGE_SOURCE,
          targetPosition: { x: 100, y: 100 },
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.KNOWLEDGE_SOURCE,
          type: NodeType.BKM,
          targetPosition: { x: 100, y: 300 },
        });

        expect(await edges.get({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toBeAttached();
        expect(await edges.getType({ from: DefaultNodeName.KNOWLEDGE_SOURCE, to: DefaultNodeName.BKM })).toEqual(
          EdgeType.AUTHORITY_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-bkm-node-from-knowledge-source-node.png");
      });
    });
  });
});
