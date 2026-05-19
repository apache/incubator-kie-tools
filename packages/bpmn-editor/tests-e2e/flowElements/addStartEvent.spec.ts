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
import type { Palette } from "../__fixtures__/palette";
import type { Nodes } from "../__fixtures__/nodes";
import type { Page } from "@playwright/test";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

async function setupEventSubProcess(palette: Palette, nodes: Nodes, page: Page) {
  await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 200 } });

  const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
  await expect(subProcess).toBeAttached();

  await nodes.morphNode({ nodeLocator: subProcess, targetMorphType: "Event" });

  const box = await subProcess.boundingBox();
  expect(box).not.toBeNull();

  await palette.dragNewNode({
    type: NodeType.START_EVENT,
    targetPosition: { x: box!.x + box!.width / 2 - 50, y: box!.y + box!.height / 2 + 50 },
  });

  const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
  await expect(startEvent).toBeVisible();

  return startEvent;
}

async function setupRegularSubProcess(palette: Palette, nodes: Nodes, page: Page) {
  await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 200 } });

  const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
  await expect(subProcess).toBeAttached();

  const box = await subProcess.boundingBox();
  expect(box).not.toBeNull();

  await palette.dragNewNode({
    type: NodeType.START_EVENT,
    targetPosition: { x: box!.x + box!.width / 2 - 50, y: box!.y + box!.height / 2 + 50 },
  });

  const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
  await expect(startEvent).toBeVisible();

  return startEvent;
}

test.describe("Add node - Start Event", () => {
  test.describe("Add from palette", () => {
    test("should add Start Event node from palette", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(startEvent.__$$element).toBe("startEvent");

      const startEventNode = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEventNode).toBeAttached();
    });

    test("should add two Start Event nodes from palette in a row", async ({ palette, diagram, page }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Second Start",
      });

      await diagram.resetFocus();

      const firstStartEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      const secondStartEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).nth(1);
      await expect(firstStartEvent).toBeAttached();
      await expect(secondStartEvent).toBeAttached();
    });
  });

  // BPMN 2.0 Start Event Morphing Rules:
  // - Top-Level Process: None, Message, Timer, Conditional, Signal
  // - Event Sub-Process (triggeredByEvent=true): Message, Timer, Error, Escalation, Compensation, Conditional, Signal
  // - Regular Sub-Process (triggeredByEvent=false): None only (no morphing)

  test.describe("Top-level process start event morphing", () => {
    const morphTestCases = [
      { morphType: "Message", eventDefinition: "messageEventDefinition" },
      { morphType: "Timer", eventDefinition: "timerEventDefinition" },
      { morphType: "Conditional", eventDefinition: "conditionalEventDefinition" },
      { morphType: "Signal", eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph None Start Event to ${morphType} Start Event`, async ({
        jsonModel,
        palette,
        diagram,
        page,
        nodes,
      }) => {
        await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

        const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
        await expect(startEvent).toBeVisible();

        await nodes.morphNode({ nodeLocator: startEvent, targetMorphType: morphType });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({
            __$$element: "startEvent",
            eventDefinition: [{ __$$element: eventDefinition }],
          });

        await expect(diagram.get()).toHaveScreenshot(`morph-start-event-to-${morphType.toLowerCase()}.png`);
      });
    }

    test("should NOT show Error/Escalation/Compensation options for Top-Level Start Event", async ({
      palette,
      diagram,
      page,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
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
    const eventSubProcessMorphCases = [
      { morphType: "Message", eventDefinition: "messageEventDefinition" },
      { morphType: "Timer", eventDefinition: "timerEventDefinition" },
      { morphType: "Error", eventDefinition: "errorEventDefinition" },
      { morphType: "Escalation", eventDefinition: "escalationEventDefinition" },
      { morphType: "Compensation", eventDefinition: "compensateEventDefinition" },
      { morphType: "Conditional", eventDefinition: "conditionalEventDefinition" },
      { morphType: "Signal", eventDefinition: "signalEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of eventSubProcessMorphCases) {
      test(`should morph None Start Event to ${morphType} Start Event in Event Sub-Process`, async ({
        jsonModel,
        palette,
        diagram,
        page,
        nodes,
      }) => {
        const startEvent = await setupEventSubProcess(palette, nodes, page);

        await nodes.morphNode({ nodeLocator: startEvent, targetMorphType: morphType });

        await expect
          .poll(async () => {
            const subProcessElement = await jsonModel.getFlowElement({ elementIndex: 0 });
            return subProcessElement.flowElement?.find(
              (el: { __$$element: string }) => el.__$$element === "startEvent"
            );
          })
          .toMatchObject({
            __$$element: "startEvent",
            eventDefinition: [{ __$$element: eventDefinition }],
          });

        await expect(diagram.get()).toHaveScreenshot(
          `morph-event-subprocess-start-event-to-${morphType.toLowerCase()}.png`
        );
      });
    }
  });

  test.describe("Regular embedded sub-process start events", () => {
    test("should add None Start Event inside regular Sub-Process and verify JSON", async ({
      jsonModel,
      palette,
      diagram,
      page,
      nodes,
    }) => {
      await setupRegularSubProcess(palette, nodes, page);

      await expect
        .poll(async () => {
          const subProcessElement = await jsonModel.getFlowElement({ elementIndex: 0 });
          return subProcessElement.flowElement?.find((el: { __$$element: string }) => el.__$$element === "startEvent");
        })
        .toMatchObject({ __$$element: "startEvent" });

      await expect(diagram.get()).toHaveScreenshot("regular-subprocess-start-event-none.png");
    });

    test("should NOT show morphing options for Start Event inside regular Sub-Process", async ({
      palette,
      diagram,
      page,
      nodes,
    }) => {
      const startEvent = await setupRegularSubProcess(palette, nodes, page);

      await nodes.openMorphingPanel({ nodeLocator: startEvent });

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

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeVisible();

      const box = await startEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addTaskHandle = startEvent.getByTitle("Add Task");
      await expect(addTaskHandle).toBeVisible();

      await addTaskHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });
      await diagram.resetFocus();

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should add connected Gateway node from Start Event", async ({ diagram, palette, page }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeVisible();

      const box = await startEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addGatewayHandle = startEvent.getByTitle("Add Gateway");
      await expect(addGatewayHandle).toBeVisible();

      await addGatewayHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      const gateway = page.getByTestId(/^kie-tools--bpmn-editor--node-gateway-/).first();
      await expect(gateway).toBeAttached();
    });

    test("should create sequence flow from Start Event to Sub-process", async ({
      diagram,
      palette,
      page,
      edges,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 350, y: 100 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeVisible();
      const startEventId = (await startEvent.getAttribute("data-nodehref")) ?? "";

      const subProcess = await nodes.get({ name: "New Sub-process" });
      await expect(subProcess).toBeAttached();

      const box = await startEvent.boundingBox();
      expect(box).not.toBeNull();

      await page.mouse.move(box!.x + box!.width - 10, box!.y + box!.height / 2);

      const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const subProcessBox = await subProcess.boundingBox();
      expect(subProcessBox).not.toBeNull();

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: {
          x: subProcessBox!.x + subProcessBox!.width / 2,
          y: subProcessBox!.y + subProcessBox!.height / 2,
        },
      });

      const edge = await edges.get({ from: startEventId, to: "New Sub-process" });
      await expect(edge).toBeAttached();
    });
  });

  test.describe("Start Event operations", () => {
    test("should delete start event", async ({ palette, jsonModel, page }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeVisible();
      await startEvent.click();
      await page.keyboard.press("Delete");

      await expect(startEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move start event to new position", async ({ palette, page, diagram }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 300, y: 300 } });

      const startEvent = page.getByTestId(/^kie-tools--bpmn-editor--node-start-event-/).first();
      await expect(startEvent).toBeAttached();
      await startEvent.scrollIntoViewIfNeeded();

      const startEventBox = await startEvent.boundingBox();
      expect(startEventBox).not.toBeNull();

      await startEvent.dragTo(diagram.get(), {
        sourcePosition: { x: startEventBox!.width / 2, y: startEventBox!.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await startEvent.boundingBox();
      expect(boxAfter).not.toBeNull();
      expect(boxAfter!.x).not.toBe(startEventBox!.x);
      expect(boxAfter!.y).not.toBe(startEventBox!.y);
    });
  });
});
