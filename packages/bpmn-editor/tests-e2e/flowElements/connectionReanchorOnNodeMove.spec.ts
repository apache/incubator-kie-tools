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

// 0 = node's left border, 0.5 = centre (auto-anchored), 1 = right border.
async function getEndpointRelativeX(args: {
  jsonModel: JsonModel;
  nodeId: string;
  direction: "incoming" | "outgoing";
}): Promise<number> {
  const flows = await args.jsonModel.getSequenceFlows();
  const flow =
    args.direction === "incoming"
      ? flows.find((f) => f["@_targetRef"] === args.nodeId)
      : flows.find((f) => f["@_sourceRef"] === args.nodeId);
  expect(flow).toBeTruthy();

  const elements = (await args.jsonModel.getPlane())?.["di:DiagramElement"] ?? [];
  const shape = elements.find(
    (e: any) => e.__$$element === "bpmndi:BPMNShape" && e["@_bpmnElement"] === args.nodeId
  ) as any;
  const edge = elements.find(
    (e: any) => e.__$$element === "bpmndi:BPMNEdge" && e["@_bpmnElement"] === flow!["@_id"]
  ) as any;

  const bounds = shape["dc:Bounds"];
  const waypoints = edge["di:waypoint"];
  const endpoint = args.direction === "incoming" ? waypoints[waypoints.length - 1] : waypoints[0];

  return (endpoint["@_x"] - bounds["@_x"]) / bounds["@_width"];
}

test.beforeEach(async ({ page, baseURL }) => {
  await page.goto(`${baseURL}/iframe.html?args=&id=misc-connection-reanchor--start-to-end&viewMode=story`);
  await expect(page.getByTestId("kie-bpmn-editor--diagram-container")).toBeVisible();
});

test.describe("Connection - re-anchor on node move", () => {
  test("should re-optimise both incoming and outgoing connections when the task crosses into a different zone", async ({
    nodes,
    jsonModel,
  }) => {
    const taskId = await nodes.getId({ name: "First Function" });

    // Nudge within the same zones - pinned anchors are kept (also commits a change so the model reads).
    const box = await nodes.getNodeBounds({ name: "First Function" });
    await nodes.dragNodeToPosition({
      name: "First Function",
      toPosition: { x: box.x + box.width / 2 + 40, y: box.y + box.height / 2 },
    });
    expect(await getEndpointRelativeX({ jsonModel, nodeId: taskId, direction: "incoming" })).toBeLessThan(0.2);
    expect(await getEndpointRelativeX({ jsonModel, nodeId: taskId, direction: "outgoing" })).toBeGreaterThan(0.8);

    // Move it below both neighbours - both endpoints cross into a new zone and reset to auto.
    const startId = await nodes.getIdByType(NodeType.START_EVENT);
    const startBox = await nodes.getNodeBounds({ id: startId });
    await nodes.dragNodeToPosition({
      name: "First Function",
      toPosition: { x: startBox.x + startBox.width / 2, y: startBox.y + startBox.height + 350 },
    });

    const incomingAfter = await getEndpointRelativeX({ jsonModel, nodeId: taskId, direction: "incoming" });
    expect(incomingAfter).toBeGreaterThan(0.35);
    expect(incomingAfter).toBeLessThan(0.65);

    const outgoingAfter = await getEndpointRelativeX({ jsonModel, nodeId: taskId, direction: "outgoing" });
    expect(outgoingAfter).toBeGreaterThan(0.35);
    expect(outgoingAfter).toBeLessThan(0.65);
  });
});
