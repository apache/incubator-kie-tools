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
import { EdgeType } from "../__fixtures__/edges";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add edge - Information Requirement", () => {
  test("should add an Information Requirement edge from Input Data node to Decision node", async ({
    diagram,
    palette,
    nodes,
    edges,
  }) => {
    await palette.dragNewNode({
      type: NodeType.INPUT_DATA,
      targetPosition: { x: 100, y: 100 },
    });
    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/980",
    });
    await diagram.resetFocus();
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 300 },
    });
    await nodes.dragNewConnectedEdge({
      type: EdgeType.INFORMATION_REQUIREMENT,
      from: DefaultNodeName.INPUT_DATA,
      to: DefaultNodeName.DECISION,
    });

    expect(await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toBeAttached();
    expect(await edges.getType({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })).toEqual(
      EdgeType.INFORMATION_REQUIREMENT
    );
    await expect(diagram.get()).toHaveScreenshot(
      "add-information-requirement-edge-from-input-data-node-to-decision-node.png"
    );
  });

  test("should add an Information Requirement edge from Decision node to Decision node", async ({
    diagram,
    palette,
    nodes,
    edges,
  }) => {
    // Rename to avoid ambuiguity
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 100 },
      thenRenameTo: "Decision - A",
    });
    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/980",
    });
    await diagram.resetFocus();
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 100, y: 300 },
    });
    await nodes.dragNewConnectedEdge({
      type: EdgeType.INFORMATION_REQUIREMENT,
      from: "Decision - A",
      to: DefaultNodeName.DECISION,
    });

    expect(await edges.get({ from: "Decision - A", to: DefaultNodeName.DECISION })).toBeAttached();
    expect(await edges.getType({ from: "Decision - A", to: DefaultNodeName.DECISION })).toEqual(
      EdgeType.INFORMATION_REQUIREMENT
    );
    await expect(diagram.get()).toHaveScreenshot(
      "add-information-requirement-edge-from-decision-node-to-decision-node.png"
    );
  });
});
