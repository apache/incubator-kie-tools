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
import { DefaultNodeName, NodeType, SubProcessNodeType, EventNodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - Start Event", () => {
  test.describe("Add from palette", () => {
    test("should add Start Event node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.getByType(NodeType.START_EVENT)).toBeAttached();

      const startEvents = await jsonModel.getStartEvents();
      expect(startEvents.length).toBe(1);
      expect(startEvents[0].__$$element).toBe("startEvent");
    });

    test("should add two Start Event nodes from palette in a row", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Start",
      });

      await diagram.resetFocus();

      await expect(nodes.getByType(NodeType.START_EVENT).first()).toBeAttached();
      await expect(nodes.getByType(NodeType.START_EVENT).nth(1)).toBeAttached();
    });
  });

  // BPMN 2.0 Start Event Morphing Rules:
  // - Top-Level Process: None, Message, Timer, Conditional, Signal
  // - Event Sub-Process (triggeredByEvent=true): Message, Timer, Error, Escalation, Compensation, Conditional, Signal
  // - Regular Sub-Process (triggeredByEvent=false): None only (no morphing)

  test.describe("Top-level process start event morphing", () => {
    const morphTestCases = [
      { morphType: EventNodeType.MESSAGE, eventDefinition: "messageEventDefinition" },
      { morphType: EventNodeType.TIMER, eventDefinition: "timerEventDefinition" },
      { morphType: EventNodeType.CONDITIONAL, eventDefinition: "conditionalEventDefinition" },
      { morphType: EventNodeType.SIGNAL, eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph None Start Event to ${morphType} Start Event`, async ({
        jsonModel,
        palette,
        diagram,
        nodes,
      }) => {
        await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });
        await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();

        await nodes.morph({ node: nodes.getByType(NodeType.START_EVENT), to: morphType });
        await expect(diagram.get()).toHaveScreenshot(`morph-start-event-to-${morphType.toLowerCase()}.png`);

        const startEvents = await jsonModel.getStartEvents();
        expect(startEvents.length).toBe(1);
        expect(startEvents[0].__$$element).toBe("startEvent");
        expect(startEvents[0].eventDefinition).toMatchObject([{ __$$element: eventDefinition }]);
      });
    }

    test("should NOT show Error/Escalation/Compensation options for Top-Level Start Event", async ({
      palette,
      diagram,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      await nodes.openMorphingPanel({ nodeLocator: startEvent });

      await expect(page.getByTitle("Error")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Escalation")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Compensation")).toHaveClass(/disabled/);

      await expect(page.getByTitle("Message")).toBeVisible();
      await expect(page.getByTitle("Timer")).toBeVisible();
      await expect(page.getByTitle("Conditional")).toBeVisible();
      await expect(page.getByTitle("Signal")).toBeVisible();

      await expect(diagram.get()).toHaveScreenshot("top-level-start-event-morphing-options.png");
    });
  });

  test.describe("Event sub-process start event morphing", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 200 } });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.EVENT });
      const center = await nodes.getNodeCenterPosition({ name: DefaultNodeName.SUB_PROCESS });
      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: center.x - 50, y: center.y + 50 },
      });
      await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
    });

    const eventSubProcessMorphCases = [
      { morphType: EventNodeType.MESSAGE, eventDefinition: "messageEventDefinition" },
      { morphType: EventNodeType.TIMER, eventDefinition: "timerEventDefinition" },
      { morphType: EventNodeType.ERROR, eventDefinition: "errorEventDefinition" },
      { morphType: EventNodeType.ESCALATION, eventDefinition: "escalationEventDefinition" },
      { morphType: EventNodeType.COMPENSATION, eventDefinition: "compensateEventDefinition" },
      { morphType: EventNodeType.CONDITIONAL, eventDefinition: "conditionalEventDefinition" },
      { morphType: EventNodeType.SIGNAL, eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of eventSubProcessMorphCases) {
      test(`should morph None Start Event to ${morphType} Start Event in Event Sub-Process`, async ({
        jsonModel,
        diagram,
        nodes,
      }) => {
        await nodes.morph({ node: nodes.getByType(NodeType.START_EVENT), to: morphType });
        await expect(diagram.get()).toHaveScreenshot(
          `morph-event-subprocess-start-event-to-${morphType.toLowerCase()}.png`
        );

        const subProcesses = await jsonModel.getSubProcesses();
        expect(subProcesses.length).toBe(1);
        const subProcessStartEvent = (await jsonModel.getStartEvents(subProcesses[0].flowElement))[0];
        expect(subProcessStartEvent?.__$$element).toBe("startEvent");
        expect(subProcessStartEvent?.eventDefinition).toMatchObject([{ __$$element: eventDefinition }]);
      });
    }
  });

  test.describe("Regular embedded sub-process start events", () => {
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 200 } });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const center = await nodes.getNodeCenterPosition({ name: DefaultNodeName.SUB_PROCESS });
      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: center.x - 50, y: center.y + 50 },
      });

      await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
    });

    test("should add None Start Event inside regular Sub-Process and verify JSON", async ({ jsonModel, diagram }) => {
      await expect(diagram.get()).toHaveScreenshot("regular-subprocess-start-event-none.png");

      const subProcesses = await jsonModel.getSubProcesses();
      expect(subProcesses.length).toBe(1);
      const subProcessStartEvent = subProcesses[0].flowElement?.find(
        (el: { __$$element: string }) => el.__$$element === "startEvent"
      );
      expect(subProcessStartEvent?.__$$element).toBe("startEvent");
    });

    test("should NOT show morphing options for Start Event inside regular Sub-Process", async ({
      diagram,
      page,
      nodes,
    }) => {
      await nodes.openMorphingPanel({ nodeLocator: nodes.getByType(NodeType.START_EVENT) });

      await expect(page.getByTitle("Message")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Timer")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Error")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Escalation")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Compensation")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Conditional")).toHaveClass(/disabled/);
      await expect(page.getByTitle("Signal")).toHaveClass(/disabled/);

      await expect(diagram.get()).toHaveScreenshot("regular-subprocess-start-event-no-morphing.png");
    });
  });

  test.describe("Add connected node", () => {
    test("should add connected Task node from Start Event", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: startEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Gateway node from Start Event", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.dragNewConnectedNode({
        type: NodeType.GATEWAY,
        from: startEventId,
        targetPosition: { x: 300, y: 100 },
      });

      await expect(nodes.getByType(NodeType.GATEWAY)).toBeAttached();
    });

    test("should create sequence flow from Start Event to Sub-process", async ({ palette, edges, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 350, y: 100 } });
      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      expect(startEventId).not.toBe("");
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      await nodes.createSequenceFlow({ from: startEventId, to: DefaultNodeName.SUB_PROCESS });
      await expect(await edges.get({ from: startEventId, to: DefaultNodeName.SUB_PROCESS })).toBeAttached();
    });
  });

  test.describe("Start Event operations", () => {
    test("should delete start event", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });
      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      await nodes.deleteByType({ type: NodeType.START_EVENT });
      await expect(startEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move start event to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeAttached();

      await startEvent.scrollIntoViewIfNeeded();
      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      const startEventBox = await nodes.getNodeBounds({ id: startEventId });

      await nodes.dragNodeToPosition({
        id: startEventId,
        fromPosition: NodePosition.CENTER,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ id: startEventId });
      expect(boxAfter.x).not.toBe(startEventBox.x);
      expect(boxAfter.y).not.toBe(startEventBox.y);
    });
  });
});
