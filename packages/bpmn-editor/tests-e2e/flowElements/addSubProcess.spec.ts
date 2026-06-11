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

import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { test, expect } from "../__fixtures__/base";
import { DefaultNodeName, NodeType, SubProcessNodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Add node - Sub-process", () => {
  test.describe("Add from palette", () => {
    test("should add Sub-process node from palette", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 100 } });

      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = (await jsonModel.getSubProcesses())[0];
      expect(subProcess.__$$element).toBe("subProcess");
    });

    test("should add two Sub-process nodes from palette in a row", async ({ palette, nodes, diagram }) => {
      test.info().annotations.push({ type: TestAnnotations.REGRESSION });

      await palette.dragNewNode({
        type: NodeType.SUB_PROCESS,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Sub-process A",
      });

      await palette.dragNewNode({
        type: NodeType.SUB_PROCESS,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Sub-process B",
      });

      await diagram.resetFocus();

      await expect(nodes.get({ name: "Sub-process A" })).toBeAttached();
      await expect(nodes.get({ name: "Sub-process B" })).toBeAttached();
    });
  });

  test.describe("Add connected Sub-process node", () => {
    test("should add connected Task from Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 100 } });

      const subProcess = await nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      await nodes.dragNewConnectedNode({
        type: NodeType.TASK,
        from: DefaultNodeName.SUB_PROCESS,
        targetPosition: { x: 600, y: 100 },
      });

      await diagram.zoomOut({ clicks: 1 });

      await expect(nodes.get({ name: DefaultNodeName.TASK })).toBeAttached();
    });

    test("should create sequence flow from Start Event to Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 100 } });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.createSequenceFlow({ from: startEventId, to: DefaultNodeName.SUB_PROCESS });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-start-event-to-subprocess.png");
    });

    test("should create sequence flow from Gateway to Sub-process", async ({ diagram, palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.GATEWAY, targetPosition: { x: 100, y: 100 } });
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 100 } });

      const gateway = nodes.getByType(NodeType.GATEWAY);
      await expect(gateway).toBeVisible();
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const gatewayId = await nodes.getIdByType(NodeType.GATEWAY);
      await nodes.createSequenceFlow({ from: gatewayId, to: DefaultNodeName.SUB_PROCESS });

      await expect(diagram.get()).toHaveScreenshot("create-sequence-flow-gateway-to-subprocess.png");
    });
  });

  test.describe("Sub-process morphing", () => {
    const morphTestCases = [
      { title: SubProcessNodeType.EVENT, screenshot: "morph-subprocess-to-event.png" },
      { title: SubProcessNodeType.MULTI_INSTANCE, screenshot: "morph-subprocess-to-multi-instance.png" },
      { title: SubProcessNodeType.AD_HOC, screenshot: "morph-subprocess-to-adhoc.png" },
    ];

    for (const { title, screenshot } of morphTestCases) {
      test(`should morph Sub-process to ${title} Sub-process`, async ({ palette, nodes, page, diagram }) => {
        await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

        const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
        await expect(subProcess).toBeAttached();

        await nodes.morph({ node: subProcess, to: title });

        await expect(diagram.get()).toHaveScreenshot(screenshot);
      });
    }
  });

  test.describe("Nested elements inside Sub-process", () => {
    test("should add Start Event inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: box.x + 50, y: box.y + box.height + 50 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-start-event-inside-subprocess.png");
    });

    test("should add Task inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: box.x + 50, y: box.y + 50 } });

      await expect(diagram.get()).toHaveScreenshot("add-task-inside-subprocess.png");
    });

    test("should add Gateway inside Sub-process", async ({ palette, nodes, diagram }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.GATEWAY,
        targetPosition: { x: box.x + box.width / 4, y: box.y + 100 },
      });

      await expect(diagram.get()).toHaveScreenshot("add-gateway-inside-subprocess.png");
    });

    test("should create complete flow inside Sub-process: Start Event -> Task -> End Event", async ({
      palette,
      nodes,
      diagram,
    }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
      await expect(subProcess).toBeAttached();

      const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await palette.dragNewNode({
        type: NodeType.START_EVENT,
        targetPosition: { x: box.x + box.width / 8, y: box.y + 100 },
      });
      await palette.dragNewNode({
        type: NodeType.TASK,
        targetPosition: { x: box.x + box.width / 3, y: box.y + 70 },
      });
      await palette.dragNewNode({
        type: NodeType.END_EVENT,
        targetPosition: { x: box.x + box.width - 100, y: box.y + 100 },
      });

      const startEvent = nodes.getByType(NodeType.START_EVENT);
      await expect(startEvent).toBeVisible();

      const task = await nodes.get({ name: DefaultNodeName.TASK });
      await expect(task).toBeAttached();
      await expect(nodes.getByType(NodeType.END_EVENT)).toBeVisible();

      // Connect Start Event -> Task
      const startEventId = await nodes.getIdByType(NodeType.START_EVENT);
      await nodes.createSequenceFlow({ from: startEventId, to: DefaultNodeName.TASK });

      // Connect Task -> End Event
      const endEventId = await nodes.getIdByType(NodeType.END_EVENT);
      await nodes.createSequenceFlow({ from: DefaultNodeName.TASK, to: endEventId });

      await expect(diagram.get()).toHaveScreenshot("complete-flow-inside-subprocess.png");
    });
  });

  test.describe("Sub-process operations", () => {
    test("should delete sub-process", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.delete({ name: DefaultNodeName.SUB_PROCESS });

      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(process?.flowElement?.length).toBe(0);
    });

    test("should move sub-process to new position", async ({ palette, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });

      const subProcess = nodes.getByType(NodeType.SUB_PROCESS);
      await expect(subProcess).toBeAttached();
      await subProcess.scrollIntoViewIfNeeded();

      const subProcessBox = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

      await nodes.dragNodeToPosition({
        name: DefaultNodeName.SUB_PROCESS,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 500, y: 400 },
      });

      const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });
      expect(boxAfter.x).not.toBe(subProcessBox.x);
      expect(boxAfter.y).not.toBe(subProcessBox.y);
    });

    test("should rename sub-process", async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.rename({ current: DefaultNodeName.SUB_PROCESS, new: "Order Processing" });

      await expect(nodes.get({ name: "Order Processing" })).toBeAttached();

      const subProcess = (await jsonModel.getSubProcesses())[0];
      expect(subProcess?.__$$element).toBe("subProcess");
      expect(subProcess?.["@_name"]).toBe("Order Processing");
    });
  });

  test.describe("Sub-process default properties", () => {
    test(`should check sub-process default properties`, async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = (await jsonModel.getSubProcesses())[0];
      expect(subProcess.__$$element).toBe("subProcess");
      expect(subProcess["@_name"]).toBe(DefaultNodeName.SUB_PROCESS);
      expect(subProcess["@_triggeredByEvent"]).toBe(false);
      expect(subProcess.extensionElements?.["drools:metaData"]?.length).toBe(1);
      expect(subProcess.extensionElements?.["drools:metaData"]?.[0]["@_name"]).toBe("customAsync");
    });

    test(`should check event sub-process default properties`, async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.EVENT });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = (await jsonModel.getSubProcesses())[0];
      expect(subProcess.__$$element).toBe("subProcess");
      expect(subProcess["@_name"]).toBe(DefaultNodeName.SUB_PROCESS);
      expect(subProcess["@_triggeredByEvent"]).toBe(true);
      expect(subProcess.extensionElements?.["drools:metaData"]?.length).toBe(1);
      expect(subProcess.extensionElements?.["drools:metaData"]?.[0]["@_name"]).toBe("customAsync");
    });

    test(`should check multi instance sub-process default properties`, async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.morph({
        node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }),
        to: SubProcessNodeType.MULTI_INSTANCE,
      });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = (await jsonModel.getSubProcesses())[0];
      expect(subProcess.__$$element).toBe("subProcess");
      expect(subProcess["@_name"]).toBe(DefaultNodeName.SUB_PROCESS);
      expect(subProcess["@_triggeredByEvent"]).toBe(false);
      expect(subProcess.loopCharacteristics?.["__$$element"]).toBe("multiInstanceLoopCharacteristics");
      expect(subProcess.extensionElements?.["drools:metaData"]?.length).toBe(1);
      expect(subProcess.extensionElements?.["drools:metaData"]?.[0]["@_name"]).toBe("customAsync");
    });

    test(`should check ad-hoc sub-process default properties`, async ({ palette, nodes, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.AD_HOC });
      await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

      const subProcess = (await jsonModel.getAdHocSubProcesses())[0];
      expect(subProcess.__$$element).toBe("adHocSubProcess");
      expect(subProcess["@_name"]).toBe(DefaultNodeName.SUB_PROCESS);
      expect(subProcess["@_triggeredByEvent"]).toBe(false);
      expect(subProcess["@_ordering"]).toBe("Parallel");
      expect(subProcess.extensionElements?.["drools:metaData"]?.length).toBe(2);
      expect(subProcess.extensionElements?.["drools:metaData"]?.[0]["@_name"]).toBe("customAsync");
      expect(subProcess.extensionElements?.["drools:metaData"]?.[1]["@_name"]).toBe("customAutoStart");
    });
  });
});
