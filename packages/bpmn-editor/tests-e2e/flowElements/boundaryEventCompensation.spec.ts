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

test.describe("Compensation Boundary Events", () => {
  test("should create compensation boundary event on task", async ({
    palette,
    jsonModel,
    intermediateEventPropertiesPanel,
    nodes,
  }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

    const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
    expect(boundaryEvent.__$$element).toBe("boundaryEvent");
    expect(boundaryEvent["@_attachedToRef"]).toBeDefined();

    const eventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
    await eventNode.click();

    await intermediateEventPropertiesPanel.setCompensationDefinition({});
    await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });

    await expect
      .poll(async () => {
        return await jsonModel.getFlowElement({ elementIndex: 1 });
      })
      .toMatchObject({
        __$$element: "boundaryEvent",
        "@_attachedToRef": expect.stringMatching(/.+/),
        "@_cancelActivity": false,
        eventDefinition: [{ __$$element: "compensateEventDefinition" }],
      });

    const cancelActivity = await intermediateEventPropertiesPanel.getCancelActivity();
    expect(cancelActivity).toBe(false);
  });

  test.skip("should not allow incoming sequence flows to compensation boundary event", async ({
    // TODO: Enable when compensation boundary event validation is implemented
    palette,
    jsonModel,
    page,
    diagram,
    intermediateEventPropertiesPanel,
    nodes,
  }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

    const eventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
    await expect(eventNode).toBeAttached();
    await eventNode.click();

    await intermediateEventPropertiesPanel.setCompensationDefinition({});

    await expect
      .poll(async () => {
        const process = await jsonModel.getProcess();
        return process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "boundaryEvent");
      })
      .toMatchObject({
        __$$element: "boundaryEvent",
        eventDefinition: [{ __$$element: "compensateEventDefinition" }],
      });

    const process = await jsonModel.getProcess();
    const boundaryEvent = process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "boundaryEvent");

    await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 300 } });

    const startEvent = nodes.getByType(NodeType.START_EVENT).last();
    await expect(startEvent).toBeAttached();

    const startEventId = (await startEvent.getAttribute("data-nodehref")) ?? "";
    await nodes.showNodeHandles({ id: startEventId });

    const addSequenceFlowHandle = startEvent.getByTitle("Add Sequence Flow");
    await expect(addSequenceFlowHandle).toBeVisible();

    const eventBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT) });

    await addSequenceFlowHandle.dragTo(diagram.get(), {
      targetPosition: { x: eventBox.x + eventBox.width / 2, y: eventBox.y + eventBox.height / 2 },
    });

    const updatedProcess = await jsonModel.getProcess();
    const sequenceFlows =
      updatedProcess.flowElement?.filter((e: { __$$element: string }) => e.__$$element === "sequenceFlow") ?? [];
    const incomingFlowToBoundary = sequenceFlows.find(
      (flow: { "@_targetRef"?: string }) => flow["@_targetRef"] === boundaryEvent["@_id"]
    );

    expect(incomingFlowToBoundary).toBeUndefined();
  });

  test("should allow compensation boundary event on subprocess", async ({
    palette,
    jsonModel,
    intermediateEventPropertiesPanel,
    nodes,
  }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 550, y: 350 } });

    const process = await jsonModel.getProcess();
    const boundaryEvent = process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "boundaryEvent");
    const subProcessElement = process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "subProcess");

    expect(boundaryEvent).toBeDefined();
    expect(boundaryEvent["@_attachedToRef"]).toBe(subProcessElement["@_id"]);

    const eventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
    await eventNode.click();

    await intermediateEventPropertiesPanel.setCompensationDefinition({});
    await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });

    await expect
      .poll(async () => {
        const updatedProcess = await jsonModel.getProcess();
        return updatedProcess.flowElement?.find((e: { __$$element: string }) => e.__$$element === "boundaryEvent");
      })
      .toMatchObject({
        __$$element: "boundaryEvent",
        "@_attachedToRef": subProcessElement["@_id"],
        "@_cancelActivity": false,
        eventDefinition: [{ __$$element: "compensateEventDefinition" }],
      });
  });
});
