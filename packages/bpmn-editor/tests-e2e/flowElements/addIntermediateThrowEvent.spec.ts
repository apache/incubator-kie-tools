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
import { NodeType, EventNodeType, NodePosition, DefaultNodeName } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Add node - Intermediate Throw Event", () => {
  test.describe("Add from palette", () => {
    test("should add Intermediate Throw Event node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT)).toBeAttached();

      const throwEvent = (await jsonModel.getIntermediateThrowEvents())[0];
      expect(throwEvent.__$$element).toBe("intermediateThrowEvent");
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
      { morphType: EventNodeType.MESSAGE, eventDefinition: "messageEventDefinition" },
      { morphType: EventNodeType.ESCALATION, eventDefinition: "escalationEventDefinition" },
      { morphType: EventNodeType.COMPENSATION, eventDefinition: "compensateEventDefinition" },
      { morphType: EventNodeType.LINK, eventDefinition: "linkEventDefinition" },
      { morphType: EventNodeType.SIGNAL, eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph Intermediate Throw Event to ${morphType}`, async ({ jsonModel, palette, diagram, nodes }) => {
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });
        await expect(nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT)).toBeVisible();

        await nodes.morph({ node: nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT), to: morphType });
        await expect(diagram.get()).toHaveScreenshot(
          `morph-intermediate-throw-event-to-${morphType.toLowerCase()}.png`
        );

        const morphedEvent = (await jsonModel.getIntermediateThrowEvents())[0];
        expect(morphedEvent.__$$element).toBe("intermediateThrowEvent");
        expect(morphedEvent.eventDefinition?.[0].__$$element).toBe(eventDefinition);
      });
    }
  });

  test.describe("Add connected Intermediate Throw Event node", () => {
    test("should add connected Task node from Intermediate Throw Event", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeVisible();

      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: throwEventId,
        targetPosition: { x: 300, y: 100 },
      });

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

      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: throwEventId,
        targetPosition: { x: 300, y: 100 },
      });

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
      await expect(nodes.getByType(NodeType.END_EVENT)).toBeVisible();

      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({ from: throwEventId, to: endEventId });

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

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await nodes.createSequenceFlow({ from: startEventId, to: throwEventId });

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

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();
      await expect(nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT)).toBeVisible();

      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await nodes.createSequenceFlow({ from: DefaultNodeName.TASK, to: throwEventId });

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
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move intermediate throw event to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const throwEvent = nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT);
      await expect(throwEvent).toBeAttached();
      await throwEvent.scrollIntoViewIfNeeded();

      const throwEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_THROW_EVENT);
      const throwEventBox = await nodes.getNodeBounds({ id: throwEventId });

      await nodes.dragNodeToPosition({
        id: throwEventId,
        fromPosition: NodePosition.CENTER,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ id: throwEventId });
      expect(boxAfter.x).not.toBe(throwEventBox.x);
      expect(boxAfter.y).not.toBe(throwEventBox.y);
    });
  });

  test.describe("Default values", () => {
    test("should have default values - signal", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });

      const signalThrowEvent = (await jsonModel.getIntermediateThrowEvents())[0];
      expect(signalThrowEvent.__$$element).toBe("intermediateThrowEvent");
      expect(signalThrowEvent.eventDefinition?.[0].__$$element).toBe("signalEventDefinition");
      expect(signalThrowEvent.extensionElements?.["drools:metaData"]?.length).toBe(1);
      expect(signalThrowEvent.extensionElements?.["drools:metaData"]?.[0]?.["@_name"]).toBe("customScope");
      expect(signalThrowEvent.extensionElements?.["drools:metaData"]?.[0]?.["drools:metaValue"].__$$text).toBe(
        "default"
      );
    });

    test("should remove default values after morphing away - signal", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 300, y: 300 } });
      await nodes.morph({ node: nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT), to: EventNodeType.MESSAGE });

      const messageThrowEvent = (await jsonModel.getIntermediateThrowEvents())[0];
      expect(messageThrowEvent.__$$element).toBe("intermediateThrowEvent");
      expect(messageThrowEvent.eventDefinition?.[0].__$$element).toBe("messageEventDefinition");
      expect(messageThrowEvent.extensionElements?.["drools:metaData"]?.length).toBe(undefined);
    });
  });
});
