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
import { JsonModel } from "../__fixtures__/jsonModel";
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
    expect(await getLabelBounds(jsonModel, "Approved")).toBeUndefined();

    const labelBoxBeforeMove = await edges.getLabel({ name: "Approved" }).boundingBox();
    await edges.moveLabel({ name: "Approved", offset: { x: 60, y: 40 } });

    await expect.poll(async () => await getLabelBounds(jsonModel, "Approved")).toBeDefined();
    const labelBoxAfterMove = await edges.getLabel({ name: "Approved" }).boundingBox();
    expect(getBoxCenter(labelBoxAfterMove!).x - getBoxCenter(labelBoxBeforeMove!).x).toBeCloseTo(60, 0);
    expect(getBoxCenter(labelBoxAfterMove!).y - getBoxCenter(labelBoxBeforeMove!).y).toBeCloseTo(40, 0);

    const boundsBeforeSecondMove = (await getLabelBounds(jsonModel, "Approved"))!;
    await edges.moveLabel({ name: "Approved", offset: { x: -30, y: 20 } });

    await expect
      .poll(async () => (await getLabelBounds(jsonModel, "Approved"))?.["@_x"])
      .toBeCloseTo(boundsBeforeSecondMove["@_x"] - 30, 0);
    expect((await getLabelBounds(jsonModel, "Approved"))?.["@_y"]).toBeCloseTo(boundsBeforeSecondMove["@_y"] + 20, 0);
  });

  test("should keep the moved label next to its Sequence Flow when a connected node moves", async ({
    edges,
    nodes,
    jsonModel,
  }) => {
    await edges.moveLabel({ name: "Approved", offset: { x: 40, y: 40 } });
    await expect.poll(async () => await getLabelBounds(jsonModel, "Approved")).toBeDefined();
    const edgeBeforeNodeMove = await getEdge(jsonModel, "Approved");

    const taskBCenter = await nodes.getNodeCenterPosition({ name: "Task B" });
    await nodes.dragNodeToPosition({ name: "Task B", toPosition: { x: taskBCenter.x + 200, y: taskBCenter.y } });

    await expect
      .poll(async () => JSON.stringify((await getEdge(jsonModel, "Approved"))?.["di:waypoint"]))
      .not.toBe(JSON.stringify(edgeBeforeNodeMove?.["di:waypoint"]));
    const edgeAfterNodeMove = await getEdge(jsonModel, "Approved");

    const boundsBeforeNodeMove = edgeBeforeNodeMove!["bpmndi:BPMNLabel"]!["dc:Bounds"]!;
    const boundsAfterNodeMove = edgeAfterNodeMove!["bpmndi:BPMNLabel"]!["dc:Bounds"]!;
    expect(boundsAfterNodeMove).not.toEqual(boundsBeforeNodeMove);
    expect(getDistanceToPath(getBoundsCenter(boundsAfterNodeMove), edgeAfterNodeMove!["di:waypoint"]!)).toBeCloseTo(
      getDistanceToPath(getBoundsCenter(boundsBeforeNodeMove), edgeBeforeNodeMove!["di:waypoint"]!),
      0
    );
  });
});

type Point = { x: number; y: number };
type Bounds = { "@_x": number; "@_y": number; "@_width": number; "@_height": number };
type Waypoint = { "@_x": number; "@_y": number };

async function getEdge(jsonModel: JsonModel, sequenceFlowName: string) {
  const sequenceFlow = (await jsonModel.getSequenceFlows()).find((flow) => flow["@_name"] === sequenceFlowName);
  return jsonModel.getEdge({ bpmnElementId: sequenceFlow!["@_id"]! });
}

async function getLabelBounds(jsonModel: JsonModel, sequenceFlowName: string) {
  return (await getEdge(jsonModel, sequenceFlowName))?.["bpmndi:BPMNLabel"]?.["dc:Bounds"];
}

function getBoxCenter(box: { x: number; y: number; width: number; height: number }): Point {
  return { x: box.x + box.width / 2, y: box.y + box.height / 2 };
}

function getBoundsCenter(bounds: Bounds): Point {
  return { x: bounds["@_x"] + bounds["@_width"] / 2, y: bounds["@_y"] + bounds["@_height"] / 2 };
}

function getDistanceToPath(point: Point, waypoints: Waypoint[]) {
  let distance = Infinity;
  for (let i = 0; i < waypoints.length - 1; i++) {
    const a = { x: waypoints[i]["@_x"], y: waypoints[i]["@_y"] };
    const b = { x: waypoints[i + 1]["@_x"], y: waypoints[i + 1]["@_y"] };
    const dx = b.x - a.x;
    const dy = b.y - a.y;
    const squaredLength = dx * dx + dy * dy;
    const t =
      squaredLength === 0 ? 0 : Math.min(1, Math.max(0, ((point.x - a.x) * dx + (point.y - a.y) * dy) / squaredLength));
    distance = Math.min(distance, Math.hypot(point.x - (a.x + t * dx), point.y - (a.y + t * dy)));
  }
  return distance;
}
