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

test.describe("Add node - Intermediate Catch Event", () => {
  test.describe("Add from palette", () => {
    test("should add Intermediate Catch Event node from palette", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

      const catchEvent = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(catchEvent.__$$element).toBe("intermediateCatchEvent");

      const catchEventNode = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEventNode).toBeAttached();
    });

    test("should add two Intermediate Catch Event nodes from palette in a row", async ({ palette, diagram, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.INTERMEDIATE_CATCH_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Intermediate Catch",
      });

      await diagram.resetFocus();

      const firstCatchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      const secondCatchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).nth(1);
      await expect(firstCatchEvent).toBeAttached();
      await expect(secondCatchEvent).toBeAttached();
    });
  });

  // BPMN 2.0 Intermediate Catch Event Types:
  // Allowed: Message, Timer, Error, Escalation, Compensation, Conditional, Link, Signal
  // Not allowed: None, Terminate

  test.describe("Intermediate catch event type morphing", () => {
    const morphTestCases = [
      { morphType: "Message", eventDefinition: "messageEventDefinition" },
      { morphType: "Timer", eventDefinition: "timerEventDefinition" },
      { morphType: "Error", eventDefinition: "errorEventDefinition" },
      { morphType: "Escalation", eventDefinition: "escalationEventDefinition" },
      { morphType: "Compensation", eventDefinition: "compensateEventDefinition" },
      { morphType: "Conditional", eventDefinition: "conditionalEventDefinition" },
      { morphType: "Link", eventDefinition: "linkEventDefinition" },
      { morphType: "Signal", eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph Intermediate Catch Event to ${morphType}`, async ({
        jsonModel,
        palette,
        diagram,
        page,
        nodes,
      }) => {
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });

        const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
        await expect(catchEvent).toBeVisible();

        await nodes.morphNode({ nodeLocator: catchEvent, targetMorphType: morphType });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({
            __$$element: "intermediateCatchEvent",
            eventDefinition: [{ __$$element: eventDefinition }],
          });

        await expect(diagram.get()).toHaveScreenshot(
          `morph-intermediate-catch-event-to-${morphType.toLowerCase()}.png`
        );
      });
    }
  });

  test.describe("Add connected Intermediate Catch Event node", () => {
    test("should add connected Task node from Intermediate Catch Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();

      const box = await catchEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addTaskHandle = catchEvent.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-task-node-from-intermediate-catch-event.png");
    });

    test("should add connected Gateway node from Intermediate Catch Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();

      const box = await catchEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = catchEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-node-from-intermediate-catch-event.png");
    });

    test("should create sequence flow from Intermediate Catch Event to End Event", async ({
      diagram,
      palette,
      page,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();

      const endEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-end-event-/).first();
      await expect(endEvent).toBeVisible();

      const box = await catchEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = catchEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endBox = await endEvent.boundingBox();
      expect(endBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endBox!.x + endBox!.width / 2, y: endBox!.y + endBox!.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-intermediate-catch-event-to-end-event.png");
    });

    test("should create sequence flow from Start Event to Intermediate Catch Event", async ({
      diagram,
      palette,
      page,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 100 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeAttached();

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();

      const startBox = await startEvent.boundingBox();
      expect(startBox).not.toBeNull();

      await page.mouse.move(startBox!.x + startBox!.width - 10, startBox!.y + startBox!.height / 2);

      const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const catchBox = await catchEvent.boundingBox();
      expect(catchBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: catchBox!.x + catchBox!.width / 2, y: catchBox!.y + catchBox!.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-start-event-to-intermediate-catch-event.png");
    });

    test("should create sequence flow from Task to Intermediate Catch Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 100 } });

      const task = await nodes.get({ name: "New Task" });
      await expect(task).toBeAttached();

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();

      const taskBox = await task.boundingBox();
      expect(taskBox).not.toBeNull();

      await page.mouse.move(taskBox!.x + taskBox!.width - 10, taskBox!.y + taskBox!.height / 2);

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const catchBox = await catchEvent.boundingBox();
      expect(catchBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: catchBox!.x + catchBox!.width / 2, y: catchBox!.y + catchBox!.height / 2 },
      });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-task-to-intermediate-catch-event.png");
    });
  });

  test.describe("Intermediate Catch Event operations", () => {
    test("should delete intermediate catch event", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeVisible();
      await catchEvent.click();
      await page.keyboard.press("Delete");

      await expect(catchEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move intermediate catch event to new position", async ({ palette, page, diagram }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });

      const catchEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-intermediate-catch-event-/).first();
      await expect(catchEvent).toBeAttached();

      const boxBefore = await catchEvent.boundingBox();
      expect(boxBefore).not.toBeNull();

      await catchEvent.dragTo(diagram.get(), { targetPosition: { x: 500, y: 400 } });

      const boxAfter = await catchEvent.boundingBox();
      expect(boxAfter).not.toBeNull();
      expect(boxAfter!.x).not.toBe(boxBefore!.x);
      expect(boxAfter!.y).not.toBe(boxBefore!.y);
    });
  });
});
