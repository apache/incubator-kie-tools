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
import { DefaultNodeName, NodeType, NodePosition, EventNodeType } from "../__fixtures__/nodes";
import { EdgeType } from "../__fixtures__/edges";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add node - End Event", () => {
  test.describe("Add from palette", () => {
    test("should add End Event node from palette", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 100, y: 100 } });

      const endEvent = await jsonModel.getFlowElement({ elementIndex: 0 });
      expect(endEvent.__$$element).toBe("endEvent");

      const endEventNode = nodes.getByType(NodeType.END_EVENT);
      await expect(endEventNode).toBeAttached();
    });

    test("should add two End Event nodes from palette in a row", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 300 } });

      await diagram.resetFocus();

      const firstEndEvent = nodes.getByType(NodeType.END_EVENT).first();
      const secondEndEvent = nodes.getByType(NodeType.END_EVENT).nth(1);
      await expect(firstEndEvent).toBeAttached();
      await expect(secondEndEvent).toBeAttached();
    });
  });

  // BPMN 2.0 End Event Types:
  // All types available in any context: None, Message, Error, Escalation, Signal, Compensation, Terminate

  test.describe("End event type morphing", () => {
    const morphTestCases = [
      { morphType: EventNodeType.MESSAGE, eventDefinition: "messageEventDefinition" },
      { morphType: EventNodeType.ERROR, eventDefinition: "errorEventDefinition" },
      { morphType: EventNodeType.ESCALATION, eventDefinition: "escalationEventDefinition" },
      { morphType: EventNodeType.SIGNAL, eventDefinition: "signalEventDefinition" },
      { morphType: EventNodeType.COMPENSATION, eventDefinition: "compensateEventDefinition" },
      { morphType: EventNodeType.TERMINATE, eventDefinition: "terminateEventDefinition" },
    ];

    for (const { morphType, eventDefinition } of morphTestCases) {
      test(`should morph None End Event to ${morphType} End Event`, async ({ jsonModel, palette, diagram, nodes }) => {
        await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 300 } });

        const endEvent = nodes.getByType(NodeType.END_EVENT);
        await expect(endEvent).toBeVisible();

        await nodes.morph({ node: endEvent, to: morphType });

        await expect
          .poll(async () => {
            return await jsonModel.getFlowElement({ elementIndex: 0 });
          })
          .toMatchObject({
            __$$element: "endEvent",
            eventDefinition: [{ __$$element: eventDefinition }],
          });

        await expect(diagram.get()).toHaveScreenshot(`morph-end-event-to-${morphType.toLowerCase()}.png`);
      });
    }
  });

  test.describe("Add connected End Event node", () => {
    test("should create sequence flow from Task to End Event", async ({ diagram, palette, nodes, edges }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 100 } });

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();
      const endEventId = (await endEvent.getAttribute("data-nodehref")) ?? "";

      await nodes.showNodeHandles({ name: DefaultNodeName.TASK });

      const addSequenceFlowHandle = task.getByTitle("Add Sequence Flow");
      await expect(addSequenceFlowHandle).toBeVisible();

      const endEventBox = await nodes.getNodeBounds({ id: endEventId });

      await addSequenceFlowHandle.dragTo(diagram.get(), {
        targetPosition: { x: endEventBox.x + endEventBox.width / 2, y: endEventBox.y + endEventBox.height / 2 },
      });

      const edge = await edges.get({ from: DefaultNodeName.TASK, to: endEventId });
      await expect(edge).toBeAttached();
    });

    test("should add connected End Event from Gateway node", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();

      await nodes.showNodeHandles({ id: await nodes.getIdByType(NodeType.GATEWAY), position: NodePosition.TOP });

      const addEndEventHandle = gateway.getByTitle("Add End Event");
      await expect(addEndEventHandle).toBeVisible();

      await addEndEventHandle.dragTo(diagram.get(), { targetPosition: { x: 300, y: 100 } });

      await expect(nodes.getByType(NodeType.END_EVENT)).toBeAttached();
    });

    test("should add connected End Event from Sub-process node", async ({ diagram, palette, page, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 50, y: 100 } });

      const subProcess = nodes.get({ name: "New Sub-process" });
      await expect(subProcess).toBeAttached();

      await nodes.showNodeHandles({ name: "New Sub-process" });

      const addEndEventHandle = subProcess.getByTitle("Add End Event");
      await expect(addEndEventHandle).toBeVisible();

      await addEndEventHandle.dragTo(diagram.get(), { targetPosition: { x: 600, y: 100 } });

      await diagram.zoomOut({ clicks: 1 });

      await expect(nodes.getByType(NodeType.END_EVENT)).toBeAttached();
    });
  });

  test.describe("End Event operations", () => {
    test("should delete end event", async ({ palette, jsonModel, nodes, page }) => {
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 300 } });

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeVisible();
      await endEvent.click();
      await page.keyboard.press("Delete");

      await expect(endEvent).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process.flowElement?.length).toBe(0);
    });

    test("should move end event to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 300, y: 300 } });

      const endEvent = nodes.getByType(NodeType.END_EVENT);
      await expect(endEvent).toBeAttached();
      await endEvent.scrollIntoViewIfNeeded();

      const endEventBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });

      await endEvent.dragTo(diagram.get(), {
        sourcePosition: { x: endEventBox.width / 2, y: endEventBox.height / 2 },
        targetPosition: { x: 500, y: 400 },
        force: true,
      });

      const boxAfter = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.END_EVENT) });
      expect(boxAfter.x).not.toBe(endEventBox.x);
      expect(boxAfter.y).not.toBe(endEventBox.y);
    });
  });
});
