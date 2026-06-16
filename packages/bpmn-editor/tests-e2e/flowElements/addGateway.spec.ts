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
import { NodeType, GatewayNodeType, NodePosition, DefaultNodeName } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Add node - Gateway", () => {
  test.describe("Add from palette", () => {
    test("should add Gateway node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = (await jsonModel.getExclusiveGateways())[0];
      expect(gateway.__$$element).toBe("exclusiveGateway");

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should add two Gateway nodes from palette in a row", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Gateway",
      });

      await diagram.resetFocus();

      const firstGateway = nodes.getByType(NodeType.GATEWAY).first();
      const secondGateway = nodes.getByType(NodeType.GATEWAY).nth(1);
      await expect(firstGateway).toBeAttached();
      await expect(secondGateway).toBeAttached();
    });
  });

  // BPMN 2.0 Gateway Types:
  // Exclusive (default), Parallel, Inclusive, Event-Based, Complex

  test.describe("Gateway type morphing", () => {
    test(`should morph Exclusive Gateway to Parallel Gateway`, async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
      await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.PARALLEL });
      await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${GatewayNodeType.PARALLEL.toLowerCase()}.png`);

      const parallelGateway = (await jsonModel.getParallelGateways())[0];
      expect(parallelGateway?.__$$element).toBe("parallelGateway");
    });

    test(`should morph Exclusive Gateway to Inclusive Gateway`, async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
      await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.INCLUSIVE });
      await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${GatewayNodeType.INCLUSIVE.toLowerCase()}.png`);

      const inclusiveGateway = (await jsonModel.getInclusiveGateways())[0];
      expect(inclusiveGateway?.__$$element).toBe("inclusiveGateway");
    });

    test(`should morph Exclusive Gateway to Event Based Gateway`, async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
      await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.EVENT_BASED });
      await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${GatewayNodeType.EVENT_BASED.toLowerCase()}.png`);

      const eventBasedGateway = (await jsonModel.getEventBasedGateways())[0];
      expect(eventBasedGateway?.__$$element).toBe("eventBasedGateway");
    });

    test(`should morph Exclusive Gateway to Complex Gateway`, async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      await expect(nodes.getByType(NodeType.GATEWAY)).toBeVisible();
      await nodes.morph({ node: nodes.getByType(NodeType.GATEWAY), to: GatewayNodeType.COMPLEX });
      await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${GatewayNodeType.COMPLEX.toLowerCase()}.png`);

      const complexGateway = (await jsonModel.getComplexGateways())[0];
      expect(complexGateway?.__$$element).toBe("complexGateway");
    });

    test("should morph Parallel Gateway back to Exclusive Gateway", async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      await nodes.morph({ node: gateway, to: GatewayNodeType.PARALLEL });
      await nodes.hideNodeHandles();
      await nodes.morph({ node: gateway, to: GatewayNodeType.EXCLUSIVE });
      await expect(diagram.get()).toHaveScreenshot("morph-gateway-parallel-to-exclusive.png");

      const exclusiveGateway = (await jsonModel.getExclusiveGateways())[0];
      expect(exclusiveGateway.__$$element).toBe("exclusiveGateway");
    });
  });

  test.describe("Add connected Gateway node", () => {
    test("should add connected Task node from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: gatewayId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should add connected Gateway node from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: gatewayId,
        targetPosition: { x: 300, y: 100 },
      });

      const secondGateway = nodes.getByType(NodeType.GATEWAY).nth(1);
      await expect(secondGateway).toBeAttached();
    });

    test("should create sequence flow from Gateway to Task", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 350, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      expect(gatewayId).not.toBe("");

      const task = nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      await nodes.createSequenceFlow({
        from: gatewayId,
        to: DefaultNodeName.TASK,
      });

      const edge = await edges.get({ from: gatewayId, to: DefaultNodeName.TASK });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to End Event", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY).first();
      await expect(gateway).toBeVisible();
      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      expect(gatewayId).not.toBe("");

      const endEvent = nodes.getByType(NodeType.END_EVENT).first();
      await expect(endEvent).toBeVisible();
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      expect(endEventId).not.toBe("");

      await nodes.createSequenceFlow({
        from: gatewayId,
        to: endEventId,
      });

      const edge = await edges.get({ from: gatewayId, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to another Gateway", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 350, y: 100 } });

      const firstGateway = nodes.getByType(NodeType.GATEWAY).first();
      await expect(firstGateway).toBeVisible();
      const firstGatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      expect(firstGatewayId).not.toBe("");

      const secondGateway = nodes.getByType(NodeType.GATEWAY).nth(1);
      await expect(secondGateway).toBeAttached();
      const secondGatewayId = await nodes.getIdByType(NodeType.GATEWAY, 1);
      expect(secondGatewayId).not.toBe("");

      await nodes.createSequenceFlow({
        from: firstGatewayId,
        to: secondGatewayId,
      });

      const edge = await edges.get({ from: firstGatewayId, to: secondGatewayId });
      await expect(edge).toBeAttached();
    });

    test("should add connected Gateway from Start Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: startEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should add connected Gateway from Task node", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      const task = nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: DefaultNodeName.TASK,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });
  });

  test.describe("Gateway operations", () => {
    test("should delete gateway", async ({ palette, jsonModel, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });
      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      await gateway.click();
      await page.keyboard.press("Delete");
      await expect(gateway).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move gateway to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeAttached();
      await gateway.scrollIntoViewIfNeeded();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      const gatewayBox = await nodes.getNodeBounds({ id: gatewayId });

      await nodes.dragNodeToPosition({
        id: gatewayId,
        fromPosition: NodePosition.CENTER,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ id: gatewayId });
      expect(boxAfter.x).not.toBe(gatewayBox.x);
      expect(boxAfter.y).not.toBe(gatewayBox.y);
    });
  });
});
