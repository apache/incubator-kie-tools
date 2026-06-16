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
import { NodeType, DefaultNodeName, NodePosition } from "../__fixtures__/nodes";

test.describe("Add Data Object", () => {
  test.beforeEach(async ({ editor }) => {
    await editor.open();
    await editor.setInitialProcessId();
  });

  test("should add data object from palette", async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.DATA_OBJECT, targetPosition: { x: 300, y: 300 } });

    await expect(nodes.get({ name: DefaultNodeName.DATA_OBJECT })).toBeAttached();

    const dataObject = (await jsonModel.getDataObjects())[0];
    expect(dataObject.__$$element).toBe("dataObject");
    expect(dataObject["@_name"]).toBe(DefaultNodeName.DATA_OBJECT);
  });

  test("should rename data object", async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.DATA_OBJECT, targetPosition: { x: 300, y: 300 } });
    await nodes.rename({ current: DefaultNodeName.DATA_OBJECT, new: "Customer Data" });

    await expect(nodes.get({ name: "Customer Data" })).toBeAttached();

    const dataObject = (await jsonModel.getDataObjects())[0];
    expect(dataObject["@_name"]).toBe("Customer Data");
  });

  test("should delete data object", async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.DATA_OBJECT, targetPosition: { x: 300, y: 300 } });
    await nodes.delete({ name: DefaultNodeName.DATA_OBJECT });
    await expect(nodes.get({ name: DefaultNodeName.DATA_OBJECT })).not.toBeAttached();

    const process = await jsonModel.getProcess();
    expect(process?.flowElement?.length).toBe(0);
  });

  test("should move data object to new position", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DATA_OBJECT, targetPosition: { x: 300, y: 300 } });

    await expect(nodes.get({ name: DefaultNodeName.DATA_OBJECT })).toBeAttached();

    await nodes.get({ name: DefaultNodeName.DATA_OBJECT }).scrollIntoViewIfNeeded();

    const dataObjectBox = await nodes.getNodeBounds({ name: DefaultNodeName.DATA_OBJECT });

    await nodes.dragNodeToPosition({
      name: DefaultNodeName.DATA_OBJECT,
      fromPosition: NodePosition.CENTER,
      toPosition: { x: 500, y: 400 },
    });

    const boxAfter = await nodes.getNodeBounds({ name: DefaultNodeName.DATA_OBJECT });
    expect(boxAfter.x).not.toBe(dataObjectBox.x);
    expect(boxAfter.y).not.toBe(dataObjectBox.y);
  });
});
