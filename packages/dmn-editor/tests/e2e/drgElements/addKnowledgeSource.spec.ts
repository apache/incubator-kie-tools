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
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Knowledge Source", () => {
  test.describe("Add to the DRG", () => {
    test.describe("add from the palette", () => {
      test("should add Knowledge Source node from palette", async ({ jsonModel, palette, nodes, diagram }) => {
        await palette.dragNewNode({ type: NodeType.KNOWLEDGE_SOURCE, targetPosition: { x: 100, y: 100 } });

        expect(nodes.get({ name: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
        await expect(diagram.get()).toHaveScreenshot("add-knowledge-source-node-from-palette.png");

        // JSON model assertions
        const knowledgeSource = await jsonModel.drgElements.getKnowledgeSource({ drgElementIndex: 0, drdIndex: 0 });
        expect(knowledgeSource).toEqual({
          __$$element: "knowledgeSource",
          "@_id": knowledgeSource["@_id"],
          "@_name": DefaultNodeName.KNOWLEDGE_SOURCE,
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
      test("should add connected Knowledge Source node from Input Data node", async ({
        diagram,
        palette,
        nodes,
        edges,
      }) => {
        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 100, y: 100 },
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.INPUT_DATA,
          type: NodeType.KNOWLEDGE_SOURCE,
          targetPosition: { x: 100, y: 300 },
        });

        expect(
          await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })
        ).toBeAttached();
        expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
          EdgeType.AUTHORITY_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-knowledge-source-node-from-input-data-node.png");
      });

      test("should add connected Knowledge Source node from Decision node", async ({
        diagram,
        palette,
        nodes,
        edges,
      }) => {
        await palette.dragNewNode({
          type: NodeType.DECISION,
          targetPosition: { x: 100, y: 100 },
        });
        await nodes.dragNewConnectedNode({
          from: DefaultNodeName.DECISION,
          type: NodeType.KNOWLEDGE_SOURCE,
          targetPosition: { x: 100, y: 300 },
        });

        expect(
          await edges.get({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })
        ).toBeAttached();
        expect(await edges.getType({ from: DefaultNodeName.DECISION, to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
          EdgeType.AUTHORITY_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-knowledge-source-node-from-decision-node.png");
      });

      test("should add connected Knowledge Source node from Knowledge Source node", async ({
        diagram,
        palette,
        nodes,
        edges,
      }) => {
        await palette.dragNewNode({
          type: NodeType.KNOWLEDGE_SOURCE,
          targetPosition: { x: 100, y: 100 },
          thenRenameTo: "Knowledge Source - A",
        });
        await nodes.dragNewConnectedNode({
          from: "Knowledge Source - A",
          type: NodeType.KNOWLEDGE_SOURCE,
          targetPosition: { x: 500, y: 500 },
        });

        expect(await edges.get({ from: "Knowledge Source - A", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toBeAttached();
        expect(await edges.getType({ from: "Knowledge Source - A", to: DefaultNodeName.KNOWLEDGE_SOURCE })).toEqual(
          EdgeType.AUTHORITY_REQUIREMENT
        );
        await expect(diagram.get()).toHaveScreenshot("add-knowledge-source-node-from-knowledge-source-node.png");
      });
    });
  });
});
