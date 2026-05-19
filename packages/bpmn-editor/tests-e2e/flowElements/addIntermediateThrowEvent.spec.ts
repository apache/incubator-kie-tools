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
    test("should add Intermediate Throw Event node from palette", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(throwEvent.__$$element).toBe("intermediateThrowEvent");

      const throwEventNode = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEventNode).toBeAttached();
    });

    test("should add two Intermediate Throw Event nodes from palette in a row", async ({ palette, diagram, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.INTERMEDIATE_THROW_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Intermediate Throw",
      });

      await diagram.resetFocus();

      const firstThrowEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      const secondThrowEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).nth(1);
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
      test(`should morph Intermediate Throw Event to ${morphType}`, async ({
        jsonModel,
        palette,
        diagram,
        page,
        nodes,
      }) => {
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

        const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
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
    test("should add connected Task node from Intermediate Throw Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();

      const box = await throwEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addTaskHandle = throwEvent.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-task-node-from-intermediate-throw-event.png");
    });

    test("should add connected Gateway node from Intermediate Throw Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();

      const box = await throwEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = throwEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-node-from-intermediate-throw-event.png");
    });

    test("should create sequence flow from Intermediate Throw Event to End Event", async ({
      diagram,
      palette,
      page,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();

      const endEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-end-event-/).first();
      await expect(endEvent).toBeVisible();

      const box = await throwEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = throwEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endBox = await endEvent.boundingBox();
      expect(endBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endBox!.x + endBox!.width / 2, y: endBox!.y + endBox!.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-intermediate-throw-event-to-end-event.png");
    });

    test("should create sequence flow from Start Event to Intermediate Throw Event", async ({
      diagram,
      palette,
      page,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 100 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeAttached();

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();

      const startBox = await startEvent.boundingBox();
      expect(startBox).not.toBeNull();

      await page.mouse.move(startBox!.x + startBox!.width - 10, startBox!.y + startBox!.height / 2);

      const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const throwBox = await throwEvent.boundingBox();
      expect(throwBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: throwBox!.x + throwBox!.width / 2, y: throwBox!.y + throwBox!.height / 2 },
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

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();

      const taskBox = await task.boundingBox();
      expect(taskBox).not.toBeNull();

      await page.mouse.move(taskBox!.x + taskBox!.width - 10, taskBox!.y + taskBox!.height / 2);

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const throwBox = await throwEvent.boundingBox();
      expect(throwBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: throwBox!.x + throwBox!.width / 2, y: throwBox!.y + throwBox!.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-task-to-intermediate-throw-event.png");
    });
  });

  test.describe("Intermediate Throw Event operations", () => {
    test("should delete intermediate throw event", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeVisible();
      await throwEvent.click();
      await page.keyboard.press("Delete");

      await expect(throwEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move intermediate throw event to new position", async ({ palette, page, diagram }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const throwEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-throw-event-/).first();
      await expect(throwEvent).toBeAttached();
      await throwEvent.scrollIntoViewIfNeeded();

      const throwEventBox = await throwEvent.boundingBox();
      expect(throwEventBox).not.toBeNull();

      await throwEvent.dragTo(diagram.get(), {
        sourcePosition: { x: throwEventBox!.width / 2, y: throwEventBox!.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await throwEvent.boundingBox();
      expect(boxAfter).not.toBeNull();
      expect(boxAfter!.x).not.toBe(throwEventBox!.x);
      expect(boxAfter!.y).not.toBe(throwEventBox!.y);
    });
  });
});
