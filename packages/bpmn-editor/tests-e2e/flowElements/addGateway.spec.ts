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
import { NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Gateway", () => {
  test.describe("Add from palette", () => {
    test("should add Gateway node from palette", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(gateway.__$$element).toBe("exclusiveGateway");

      const gatewayNode = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gatewayNode).toBeAttached();
    });

    test("should add two Gateway nodes from palette in a row", async ({ palette, diagram, page }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Gateway",
      });

      await diagram.resetFocus();

      const firstGateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      const secondGateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).nth(1);
      await expect(firstGateway).toBeAttached();
      await expect(secondGateway).toBeAttached();
    });
  });

  // BPMN 2.0 Gateway Types:
  // Exclusive (default), Parallel, Inclusive, Event-Based, Complex

  test.describe("Gateway type morphing", () => {
    const morphTestCases = [
      { morphType: "Parallel", expectedElement: "parallelGateway", exact: false },
      { morphType: "Inclusive", expectedElement: "inclusiveGateway", exact: false },
      { morphType: "Event", expectedElement: "eventBasedGateway", exact: true },
      { morphType: "Complex", expectedElement: "complexGateway", exact: false },
    ];

    for (const { morphType, expectedElement, exact } of morphTestCases) {
      test(`should morph Exclusive Gateway to ${morphType} Gateway`, async ({
        jsonModel,
        palette,
        diagram,
        page,
        nodes,
      }) => {
        await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

        const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
        await expect(gateway).toBeVisible();

        await nodes.morphNode({ nodeLocator: gateway, targetMorphType: morphType, exact });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({ __$$element: expectedElement });

        await expect(diagram.get()).toHaveScreenshot(`morph-gateway-to-${morphType.toLowerCase()}.png`);
      });
    }

    test("should morph Parallel Gateway back to Exclusive Gateway", async ({
      jsonModel,
      palette,
      diagram,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();

      await nodes.morphNode({ nodeLocator: gateway, targetMorphType: "Parallel" });

      const parallelGateway = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(parallelGateway.__$$element).toBe("parallelGateway");

      await page.mouse.move(0, 0);

      await nodes.morphNode({ nodeLocator: gateway, targetMorphType: "Exclusive" });

      await expect
        .poll(async () => {
          return await jsonModel.getFlowElement({ elementIndex: 0 });
        })
        .toMatchObject({ __$$element: "exclusiveGateway" });

      await expect(diagram.get()).toHaveScreenshot("morph-gateway-parallel-to-exclusive.png");
    });
  });

  test.describe("Add connected Gateway node", () => {
    test("should add connected Task node from Gateway", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();

      const box = await gateway.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addTaskHandle = gateway.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const gatewayNode = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gatewayNode).toBeAttached();
    });

    test("should add connected Gateway node from Gateway", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();

      const box = await gateway.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = gateway.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const secondGateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).nth(1);
      await expect(secondGateway).toBeAttached();
    });

    test("should create sequence flow from Gateway to Task", async ({ diagram, palette, page, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 350, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();
      const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

      const task = nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const box = await gateway.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const taskBox = await task.boundingBox();
      expect(taskBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: taskBox!.x + taskBox!.width / 2, y: taskBox!.y + taskBox!.height / 2 },
      });

      const edge = await edges.get({ from: gatewayId, to: "New Task" });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to End Event", async ({ diagram, palette, page, edges }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();
      const gatewayId = (await gateway.getAttribute("data-nodehref")) ?? "";

      const endEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-end-event-/).first();
      await expect(endEvent).toBeVisible();
      const endEventId = (await endEvent.getAttribute("data-nodehref")) ?? "";

      const box = await gateway.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = gateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endEventBox = await endEvent.boundingBox();
      expect(endEventBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endEventBox!.x + endEventBox!.width / 2, y: endEventBox!.y + endEventBox!.height / 2 },
      });

      const edge = await edges.get({ from: gatewayId, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should create sequence flow from Gateway to another Gateway", async ({ diagram, palette, page, edges }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 350, y: 100 } });

      const firstGateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(firstGateway).toBeVisible();
      const firstGatewayId = (await firstGateway.getAttribute("data-nodehref")) ?? "";

      const secondGateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).nth(1);
      await expect(secondGateway).toBeAttached();
      const secondGatewayId = (await secondGateway.getAttribute("data-nodehref")) ?? "";

      const box = await firstGateway.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = firstGateway.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const secondGatewayBox = await secondGateway.boundingBox();
      expect(secondGatewayBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: secondGatewayBox!.x + secondGatewayBox!.width / 2,
          y: secondGatewayBox!.y + secondGatewayBox!.height / 2,
        },
      });

      const edge = await edges.get({ from: firstGatewayId, to: secondGatewayId });
      await expect(edge).toBeAttached();
    });

    test("should add connected Gateway from Start Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeAttached();

      const box = await startEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = startEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeAttached();
    });

    test("should add connected Gateway from Task node", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });

      const task = nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const box = await task.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = task.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeAttached();
    });
  });

  test.describe("Gateway operations", () => {
    test("should delete gateway", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeVisible();
      await gateway.click();
      await page.keyboard.press("Delete");

      await expect(gateway).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move gateway to new position", async ({ palette, page, diagram }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 300, y: 300 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeAttached();
      await gateway.scrollIntoViewIfNeeded();

      const gatewayBox = await gateway.boundingBox();
      expect(gatewayBox).not.toBeNull();

      await gateway.dragTo(diagram.get(), {
        sourcePosition: { x: gatewayBox!.width / 2, y: gatewayBox!.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await gateway.boundingBox();
      expect(boxAfter).not.toBeNull();
      expect(boxAfter!.x).not.toBe(gatewayBox!.x);
      expect(boxAfter!.y).not.toBe(gatewayBox!.y);
    });
  });
});
