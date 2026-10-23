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
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";
import { EdgeType } from "../__fixtures__/edges";
import { EDGE_TITLE, uuidRegExp } from "../__fixtures__/propertiesPanel/edgePropertiesPanel";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Edge Properties Panel", () => {
  test.describe("Information Requirement edge", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
      });
    });

    test("should show 'Information Requirement' as the panel title when the edge is selected", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const title = await edgePropertiesPanel.getTitle();
      expect(title?.trim()).toBe(EDGE_TITLE[EdgeType.INFORMATION_REQUIREMENT]);
    });

    test("should show the edge ID in the properties panel", async ({ edges, edgePropertiesPanel }) => {
      await edges.select({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const id = await edgePropertiesPanel.getId();
      expect(id).toBeTruthy();
      expect(id).toMatch(uuidRegExp);
    });

    test("should set and get the edge description for Information Requirement", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();
      await edgePropertiesPanel.setDescription({ newDescription: "Edge description" });

      expect(await edgePropertiesPanel.getDescription()).toBe("Edge description");
    });
  });

  test.describe("Knowledge Requirement edge", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.BKM, targetPosition: { x: 100, y: 100 }, thenRenameTo: "BKM - A" });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.KNOWLEDGE_REQUIREMENT,
        from: "BKM - A",
        to: DefaultNodeName.DECISION,
      });
    });

    test("should show 'Knowledge Requirement' as the panel title when the edge is selected", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: "BKM - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const title = await edgePropertiesPanel.getTitle();
      expect(title?.trim()).toBe(EDGE_TITLE[EdgeType.KNOWLEDGE_REQUIREMENT]);
    });

    test("should show the edge ID in the properties panel", async ({ edges, edgePropertiesPanel }) => {
      await edges.select({ from: "BKM - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const id = await edgePropertiesPanel.getId();
      expect(id).toBeTruthy();
      expect(id).toMatch(uuidRegExp);
    });

    test("should set and get the edge description for Knowledge Requirement", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: "BKM - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();
      await edgePropertiesPanel.setDescription({ newDescription: "Edge description" });

      expect(await edgePropertiesPanel.getDescription()).toBe("Edge description");
    });
  });

  test.describe("Authority Requirement edge", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.KNOWLEDGE_SOURCE,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Knowledge Source - A",
      });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 300 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.AUTHORITY_REQUIREMENT,
        from: "Knowledge Source - A",
        to: DefaultNodeName.DECISION,
      });
    });

    test("should show 'Authority Requirement' as the panel title when the edge is selected", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: "Knowledge Source - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const title = await edgePropertiesPanel.getTitle();
      expect(title?.trim()).toBe(EDGE_TITLE[EdgeType.AUTHORITY_REQUIREMENT]);
    });

    test("should show the edge ID in the properties panel", async ({ edges, edgePropertiesPanel }) => {
      await edges.select({ from: "Knowledge Source - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();

      const id = await edgePropertiesPanel.getId();
      expect(id).toBeTruthy();
      expect(id).toMatch(uuidRegExp);
    });

    test("should set and get the edge description for Authority Requirement", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: "Knowledge Source - A", to: DefaultNodeName.DECISION });
      await edgePropertiesPanel.open();
      await edgePropertiesPanel.setDescription({ newDescription: "Edge description" });

      expect(await edgePropertiesPanel.getDescription()).toBe("Edge description");
    });
  });

  test.describe("Association edge", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TEXT_ANNOTATION, targetPosition: { x: 400, y: 100 } });
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.ASSOCIATION,
        from: DefaultNodeName.DECISION,
        to: DefaultNodeName.TEXT_ANNOTATION,
      });
    });

    test("should show 'Association' as the panel title when the edge is selected", async ({
      edges,
      edgePropertiesPanel,
    }) => {
      await edges.select({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION });
      await edgePropertiesPanel.open();

      const title = await edgePropertiesPanel.getTitle();
      expect(title?.trim()).toBe(EDGE_TITLE[EdgeType.ASSOCIATION]);
    });

    test("should show the edge ID in the properties panel", async ({ edges, edgePropertiesPanel }) => {
      await edges.select({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION });
      await edgePropertiesPanel.open();

      const id = await edgePropertiesPanel.getId();
      expect(id).toBeTruthy();
      expect(id).toMatch(uuidRegExp);
    });

    test("should set and get the edge description for Association", async ({ edges, edgePropertiesPanel }) => {
      await edges.select({ from: DefaultNodeName.DECISION, to: DefaultNodeName.TEXT_ANNOTATION });
      await edgePropertiesPanel.open();
      await edgePropertiesPanel.setDescription({ newDescription: "Edge description" });

      expect(await edgePropertiesPanel.getDescription()).toBe("Edge description");
    });
  });
});
