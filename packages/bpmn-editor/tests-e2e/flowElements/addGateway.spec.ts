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
import { NodeType, GatewayNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Gateway", () => {
  test.describe("Add from palette", () => {
    test("should add Gateway node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = await jsonModel.getFlowElement({ elementIndex: 0 });
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
    const morphTestCases = [
      { morphType: GatewayNodeType.PARALLEL, expectedElement: "parallelGateway" },
      { morphType: GatewayNodeType.INCLUSIVE, expectedElement: "inclusiveGateway" },
      { morphType: GatewayNodeType.EVENT_BASED, expectedElement: "eventBasedGateway" },
      { morphType: GatewayNodeType.COMPLEX, expectedElement: "complexGateway" },
    ];

    for (const { morphType, expectedElement } of morphTestCases) {
      test(`should morph Exclusive Gateway to ${morphType} Gateway`, async ({ jsonModel, palette, diagram, nodes }) => {
        await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

        const gateway = nodes.getByType(NodeType.GATEWAY);
        await expect(gateway).toBeVisible();

        await nodes.morph({ node: gateway, to: morphType });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({ __$$element: expectedElement });

        await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${morphType.toLowerCase()}.png`);
      });
    }

    test("should morph Parallel Gateway back to Exclusive Gateway", async ({ jsonModel, palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.morph({ node: gateway, to: GatewayNodeType.PARALLEL });

      const parallelGateway = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(parallelGateway.__$$element).toBe("parallelGateway");

      await nodes.hideNodeHandles();

      await nodes.morph({ node: gateway, to: GatewayNodeType.EXCLUSIVE });

      await expect
        .poll(async () => {
          return await jsonModel.getFlowElement({ elementIndex: 0 });
        })
        .toMatchObject({ __$$element: "exclusiveGateway" });

      await expect(diagram.get()).toHaveScreenshot("morph-gateway-parallel-to-exclusive.png");
    });
  });

  test.describe("Add connected Gateway node", () => {
    test("should add connected Task node from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      const addTaskHandle = gateway.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should add connected Gateway node from Gateway", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY) });

      const addGatewayHandle = gateway.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const secondGateway = nodes.getByType(NodeType.GATEWAY).nth(1);
      await expect(secondGateway).toBeAttached();
    });

    test("should create sequence flow from Gateway to Task", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 350, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

      const task = nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      await nodes.showNodeHandles({ id: gatewayId });

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const taskBox = await nodes.getNodeBounds({ name: "New Task" });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: taskBox.x + taskBox.width / 2, y: taskBox.y + taskBox.height / 2 },
      });

      const edge = await edges.get({ from: gatewayId, to: "New Task" });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to End Event", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY).first();
      await expect(gateway).toBeVisible();
      const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

      const endEvent = nodes.getByType(NodeType.END_EVENT).first();
      await expect(endEvent).toBeVisible();
      const endEventId = (await endEvent.getAttribute("data-nodehref")) ?? "";

      await nodes.showNodeHandles({ id: gatewayId });

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endEventBox = await nodes.getNodeBounds({ id: endEventId });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endEventBox.x + endEventBox.width / 2, y: endEventBox.y + endEventBox.height / 2 },
      });

      const edge = await edges.get({ from: gatewayId, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to another Gateway", async ({ diagram, palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 350, y: 100 } });

      const firstGateway = nodes.getByType(NodeType.GATEWAY).first();
      await expect(firstGateway).toBeVisible();
      const firstGatewayId = (await firstGateway.getAttribute("data-nodehref")) ?? "";

      const secondGateway = nodes.getByType(NodeType.GATEWAY).nth(1);
      await expect(secondGateway).toBeAttached();
      const secondGatewayId = (await secondGateway.getAttribute("data-nodehref")) ?? "";

      await nodes.showNodeHandles({ id: firstGatewayId });

      const addSequenceFlowHandle = firstGateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const secondGatewayBox = await nodes.getNodeBounds({ id: secondGatewayId });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: secondGatewayBox.x + secondGatewayBox.width / 2,
          y: secondGatewayBox.y + secondGatewayBox.height / 2,
        },
      });

      const edge = await edges.get({ from: firstGatewayId, to: secondGatewayId });
      await expect(edge).toBeAttached();
    });

    test("should add connected Gateway from Start Event", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });

      const addGatewayHandle = startEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should add connected Gateway from Task node", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      const task = nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      await nodes.showNodeHandles({ name: "New Task" });

      const addGatewayHandle = task.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

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
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move gateway to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeAttached();
      await gateway.scrollIntoViewIfNeeded();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      const gatewayBox = await nodes.getNodeBounds({ id: gatewayId });

      await gateway.dragTo(diagram.get(), {
        sourcePosition: { x: gatewayBox.width / 2, y: gatewayBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ id: gatewayId });
      expect(boxAfter.x).not.toBe(gatewayBox.x);
      expect(boxAfter.y).not.toBe(gatewayBox.y);
    });
  });
});
