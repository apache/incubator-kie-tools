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
  await editor.setInitialProcessId();
});

test.describe("Move Sequence Flow label", () => {
  test.beforeEach(async ({ palette, nodes, edges, diagram, sequenceFlowPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 100, y: 100 }, thenRenameTo: "Task A" });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 400, y: 400 }, thenRenameTo: "Task B" });
    await diagram.resetFocus();

    await nodes.createSequenceFlow({ from: "Task A", to: "Task B" });

    const edge = await edges.get({ from: "Task A", to: "Task B" });
    await edge.click({ force: true });
    await sequenceFlowPropertiesPanel.open();
    await sequenceFlowPropertiesPanel.nameProperties.setName({ newName: "Approved" });
    await sequenceFlowPropertiesPanel.close();
    await diagram.resetFocus();

    await expect(edges.getLabel({ name: "Approved" })).toBeVisible();
  });

  test("should move the label and save its position", async ({ edges, jsonModel }) => {
    const bpmnElementId = (await jsonModel.getSequenceFlows())[0]["@_id"]!;
    expect(await jsonModel.getEdgeLabelBounds({ bpmnElementId })).toBeUndefined();

    const labelCenterBeforeMove = await edges.getLabelCenterPosition({ name: "Approved" });
    await edges.moveLabel({ name: "Approved", offset: { x: 60, y: 40 } });

    await expect.poll(() => jsonModel.getEdgeLabelBounds({ bpmnElementId })).toBeDefined();
    const labelCenterAfterMove = await edges.getLabelCenterPosition({ name: "Approved" });
    expect(labelCenterAfterMove.x - labelCenterBeforeMove.x).toBeCloseTo(60, 0);
    expect(labelCenterAfterMove.y - labelCenterBeforeMove.y).toBeCloseTo(40, 0);

    const boundsBeforeSecondMove = (await jsonModel.getEdgeLabelBounds({ bpmnElementId }))!;
    await edges.moveLabel({ name: "Approved", offset: { x: -30, y: 20 } });

    await expect
      .poll(async () => (await jsonModel.getEdgeLabelBounds({ bpmnElementId }))?.["@_x"])
      .toBeCloseTo(boundsBeforeSecondMove["@_x"] - 30, 0);
    expect((await jsonModel.getEdgeLabelBounds({ bpmnElementId }))?.["@_y"]).toBeCloseTo(
      boundsBeforeSecondMove["@_y"] + 20,
      0
    );
  });

  test("should keep the moved label next to its Sequence Flow when a connected node moves", async ({
    edges,
    nodes,
    jsonModel,
  }) => {
    const bpmnElementId = (await jsonModel.getSequenceFlows())[0]["@_id"]!;
    await edges.moveLabel({ name: "Approved", offset: { x: 40, y: 40 } });
    await expect.poll(() => jsonModel.getEdgeLabelBounds({ bpmnElementId })).toBeDefined();
    const edgeBeforeNodeMove = (await jsonModel.getEdge({ bpmnElementId }))!;

    const taskBCenter = await nodes.getNodeCenterPosition({ name: "Task B" });
    await nodes.dragNodeToPosition({ name: "Task B", toPosition: { x: taskBCenter.x + 200, y: taskBCenter.y } });

    await expect
      .poll(async () => (await jsonModel.getEdge({ bpmnElementId }))?.["di:waypoint"])
      .not.toEqual(edgeBeforeNodeMove["di:waypoint"]);
    const edgeAfterNodeMove = (await jsonModel.getEdge({ bpmnElementId }))!;

    expect(edgeAfterNodeMove["bpmndi:BPMNLabel"]).not.toEqual(edgeBeforeNodeMove["bpmndi:BPMNLabel"]);
    expect(edges.getLabelDistanceToEdge(edgeAfterNodeMove)).toBeCloseTo(
      edges.getLabelDistanceToEdge(edgeBeforeNodeMove),
      0
    );
  });
});
