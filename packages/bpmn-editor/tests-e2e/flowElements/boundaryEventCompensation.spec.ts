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

    await nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).click();
    await intermediateEventPropertiesPanel.setCompensationDefinition({});
    await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });
    const cancelActivity = await intermediateEventPropertiesPanel.getCancelActivity();
    expect(cancelActivity).toBe(false);

    const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
    expect(boundaryEvent.__$$element).toBe("boundaryEvent");
    expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    expect(boundaryEvent["@_cancelActivity"]).toBeFalsy();
    expect(boundaryEvent.eventDefinition?.[0].__$$element).toBe("compensateEventDefinition");
  });

  test("should allow compensation boundary event on subprocess", async ({
    palette,
    jsonModel,
    intermediateEventPropertiesPanel,
    nodes,
  }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 550, y: 350 } });
    await nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).click();

    await intermediateEventPropertiesPanel.setCompensationDefinition({});
    await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });

    const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
    const subProcess = (await jsonModel.getSubProcesses())[0];
    expect(boundaryEvent.__$$element).toBe("boundaryEvent");
    expect(boundaryEvent["@_attachedToRef"]).toBe(subProcess["@_id"]);
    expect(boundaryEvent["@_cancelActivity"]).toBeFalsy();
    expect(boundaryEvent.eventDefinition?.[0].__$$element).toBe("compensateEventDefinition");
  });
});
