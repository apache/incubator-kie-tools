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
import { NodeType, EventNodeType, DefaultNodeName } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Add node - Intermediate Catch Event", () => {
  test.describe("Add from palette", () => {
    test("should add Intermediate Catch Event node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeAttached();

      const catchEvent = (await jsonModel.getIntermediateCatchEvents())[0];
      expect(catchEvent.__$$element).toBe("intermediateCatchEvent");
    });

    test("should add two Intermediate Catch Event nodes from palette in a row", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.INTERMEDIATE_CATCH_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Intermediate Catch",
      });

      await diagram.resetFocus();

      const firstCatchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).first();
      const secondCatchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).nth(1);
      await expect(firstCatchEvent).toBeAttached();
      await expect(secondCatchEvent).toBeAttached();
    });
  });

  // BPMN 2.0 Intermediate Catch Event Types:
  // Allowed: Message, Timer, Error, Escalation, Compensation, Conditional, Link, Signal
  // Not allowed: None, Terminate

  test.describe("Intermediate catch event type morphing", () => {
    const morphTestCases = [
      { morphType: EventNodeType.MESSAGE, eventDefinition: "messageEventDefinition" },
      { morphType: EventNodeType.TIMER, eventDefinition: "timerEventDefinition" },
      { morphType: EventNodeType.ERROR, eventDefinition: "errorEventDefinition" },
      { morphType: EventNodeType.ESCALATION, eventDefinition: "escalationEventDefinition" },
      { morphType: EventNodeType.COMPENSATION, eventDefinition: "compensateEventDefinition" },
      { morphType: EventNodeType.CONDITIONAL, eventDefinition: "conditionalEventDefinition" },
      { morphType: EventNodeType.LINK, eventDefinition: "linkEventDefinition" },
      { morphType: EventNodeType.SIGNAL, eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph Intermediate Catch Event to ${morphType}`, async ({ jsonModel, palette, diagram, nodes }) => {
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });
        await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeVisible();

        await nodes.morph({ node: nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT), to: morphType });
        await expect(diagram.get()).toHaveScreenshot(
          `morph-intermediate-catch-event-to-${morphType.toLowerCase()}.png`
        );

        const morphedEvent = (await jsonModel.getIntermediateCatchEvents())[0];
        expect(morphedEvent.__$$element).toBe("intermediateCatchEvent");
        expect(morphedEvent.eventDefinition?.[0].__$$element).toBe(eventDefinition);
      });
    }
  });

  test.describe("Add connected Intermediate Catch Event node", () => {
    test("should add connected Task node from Intermediate Catch Event", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeVisible();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: catchEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-task-node-from-intermediate-catch-event.png");
    });

    test("should add connected Gateway node from Intermediate Catch Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeVisible();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: catchEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-node-from-intermediate-catch-event.png");
    });

    test("should create sequence flow from Intermediate Catch Event to End Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeVisible();
      await expect(nodes.getByType(NodeType.END_EVENT)).toBeVisible();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({ from: catchEventId, to: endEventId });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-intermediate-catch-event-to-end-event.png");
    });

    test("should create sequence flow from Start Event to Intermediate Catch Event", async ({
      diagram,
      palette,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeVisible();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await nodes.createSequenceFlow({ from: startEventId, to: catchEventId });

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

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeVisible();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await nodes.createSequenceFlow({ from: DefaultNodeName.TASK, to: catchEventId });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-task-to-intermediate-catch-event.png");
    });
  });

  test.describe("Intermediate Catch Event operations", () => {
    test("should delete intermediate catch event", async ({ palette, jsonModel, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });
      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeVisible();

      await catchEvent.click();
      await page.keyboard.press("Delete");
      await expect(catchEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move intermediate catch event to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 300 } });

      const catchEvent = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(catchEvent).toBeAttached();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      const boxBefore = await nodes.getNodeBounds({ id: catchEventId });

      await nodes.dragNodeToPosition({ id: catchEventId, toPosition: { x: 500, y: 400 } });

      const boxAfter = await nodes.getNodeBounds({ id: catchEventId });
      expect(boxAfter.x).not.toBe(boxBefore.x);
      expect(boxAfter.y).not.toBe(boxBefore.y);
    });
  });
});
