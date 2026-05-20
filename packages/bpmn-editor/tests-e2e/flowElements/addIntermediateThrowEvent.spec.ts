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

test.describe("Add node - Intermediate Throw Event", () => {
  test.describe("Add from palette", () => {
    test("should add Intermediate Throw Event node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(throwEvent.__$$element).toBe("intermediateThrowEvent");

      const throwEventNode = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEventNode).toBeAttached();
    });

    test("should add two Intermediate Throw Event nodes from palette in a row", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.INTERMEDIATE_THROW_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Intermediate Throw",
      });

      await diagram.resetFocus();

      const firstThrowEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT).first();
      const secondThrowEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT).nth(1);
      await expect(firstThrowEvent).toBeAttached();
      await expect(secondThrowEvent).toBeAttached();
    });
  });

  // BPMN 2.0 Intermediate Throw Event Types:
  // Allowed: Message, Escalation, Compensation, Link, Signal
  // Not allowed: None, Timer, Error, Conditional, Terminate

  test.describe("Intermediate throw event type morphing", () => {
    const morphTestCases = [
      { morphType: "Message", eventDefinition: "messageEventDefinition" },
      { morphType: "Escalation", eventDefinition: "escalationEventDefinition" },
      { morphType: "Compensation", eventDefinition: "compensateEventDefinition" },
      { morphType: "Link", eventDefinition: "linkEventDefinition" },
      { morphType: "Signal", eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph Intermediate Throw Event to ${morphType}`, async ({ jsonModel, palette, diagram, nodes }) => {
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

        const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
        await expect(throwEvent).toBeVisible();

        await nodes.morphNode({ nodeLocator: throwEvent, targetMorphType: morphType });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({
            __$$element: "intermediateThrowEvent",
            eventDefinition: [{ __$$element: eventDefinition }],
          });

        await expect(diagram.get()).toHaveScreenshot(
          `morph-intermediate-throw-event-to-${morphType.toLowerCase()}.png`
        );
      });
    }
  });

  test.describe("Add connected Intermediate Throw Event node", () => {
    test("should add connected Task node from Intermediate Throw Event", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });

      const addTaskHandle = throwEvent.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-task-node-from-intermediate-throw-event.png");
    });

    test("should add connected Gateway node from Intermediate Throw Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });

      const addGatewayHandle = throwEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-node-from-intermediate-throw-event.png");
    });

    test("should create sequence flow from Intermediate Throw Event to End Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });

      const addSequenceFlowHandle = throwEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endBox.x + endBox.width / 2, y: endBox.y + endBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-intermediate-throw-event-to-end-event.png");
    });

    test("should create sequence flow from Start Event to Intermediate Throw Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.START_EVENT) });

      const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const throwBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: throwBox.x + throwBox.width / 2, y: throwBox.y + throwBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-start-event-to-intermediate-throw-event.png");
    });

    test("should create sequence flow from Task to Intermediate Throw Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 100 } });

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      await nodes.showNodeHandles({ name: "New Task" });

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const throwBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: throwBox.x + throwBox.width / 2, y: throwBox.y + throwBox.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-task-to-intermediate-throw-event.png");
    });
  });

  test.describe("Intermediate Throw Event operations", () => {
    test("should delete intermediate throw event", async ({ palette, jsonModel, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();
      await throwEvent.click();
      await page.keyboard.press("Delete");

      await expect(throwEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move intermediate throw event to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeAttached();
      await throwEvent.scrollIntoViewIfNeeded();

      const throwEventBox = await nodes.getNodeBounds({
        id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT),
      });

      await throwEvent.dragTo(diagram.get(), {
        sourcePosition: { x: throwEventBox.width / 2, y: throwEventBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT) });
      expect(boxAfter.x).not.toBe(throwEventBox.x);
      expect(boxAfter.y).not.toBe(throwEventBox.y);
    });
  });
});
