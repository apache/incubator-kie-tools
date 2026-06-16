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
import { NodeType, DefaultNodeName, TaskNodeType, NodePosition } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Add Boundary Event", () => {
  test.describe("Basic attachment", () => {
    test("should attach intermediate catch event to task", async ({ palette, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach intermediate catch event to subprocess", async ({ palette, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 550, y: 350 } });

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach multiple boundary events to same task", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 280 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 350, y: 350 } });
      await expect(diagram.get()).toHaveScreenshot("attach-multiple-boundary-events-to-task.png");

      const boundaryEvents = await jsonModel.getBoundaryEvents();
      expect(boundaryEvents?.length).toBe(3);
      expect(boundaryEvents[0]["__$$element"]).toBe("boundaryEvent");
      expect(boundaryEvents[0]["@_attachedToRef"]).toBeDefined();
      expect(boundaryEvents[1]["__$$element"]).toBe("boundaryEvent");
      expect(boundaryEvents[1]["@_attachedToRef"]).toBeDefined();
      expect(boundaryEvents[2]["__$$element"]).toBe("boundaryEvent");
      expect(boundaryEvents[2]["@_attachedToRef"]).toBeDefined();
    });
  });

  test.describe("Detachment", () => {
    test("should detach boundary event back to intermediate catch event", async ({
      palette,
      jsonModel,
      diagram,
      nodes,
    }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeVisible();

      const catchEventId = await nodes.getIdByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await nodes.dragNodeToPosition({ id: catchEventId, toPosition: { x: 500, y: 100 } });
      await expect(diagram.get()).toHaveScreenshot("detach-boundary-event-from-task.png");

      const detachedEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(detachedEvent?.__$$element).toBe(undefined);
      expect(detachedEvent?.["@_attachedToRef"]).toBeUndefined();

      const intermediateCatchEvent = (await jsonModel.getIntermediateCatchEvents())[0];
      expect(intermediateCatchEvent?.__$$element).toBe("intermediateCatchEvent");
    });
  });

  test.describe("Interrupting vs Non-interrupting", () => {
    test("should create interrupting boundary event by default", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(diagram.get()).toHaveScreenshot("interrupting-boundary-event.png");

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
      expect(boundaryEvent["@_cancelActivity"]).not.toBe(false);
    });

    test("should create non-interrupting boundary event", async ({
      palette,
      jsonModel,
      diagram,
      nodes,
      intermediateEventPropertiesPanel,
    }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeVisible();

      await nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).click();
      await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });
      await expect(diagram.get()).toHaveScreenshot("non-interrupting-boundary-event.png");

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
      expect(boundaryEvent["@_cancelActivity"]).toBe(false);
    });
  });

  test.describe("Activity types", () => {
    test("should attach to user task", async ({ palette, nodes, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await nodes.morph({ node: nodes.get({ name: DefaultNodeName.TASK }), to: TaskNodeType.USER });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(diagram.get()).toHaveScreenshot("attach-boundary-event-to-user-task.png");

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach to call activity", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(diagram.get()).toHaveScreenshot("attach-boundary-event-to-call-activity.png");

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });
  });

  test.describe("Operations", () => {
    test("should delete boundary event", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeAttached();

      await nodes.deleteByType({ type: NodeType.INTERMEDIATE_CATCH_EVENT });
      await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).not.toBeAttached();

      const boundaryEvents = await jsonModel.getBoundaryEvents();
      expect(boundaryEvents.length).toBe(0);

      const task = (await jsonModel.getTasks())[0];
      expect(task.__$$element).toBe("task");
    });

    test.skip(
      "should delete task with boundary event",
      {
        annotation: {
          type: "issue",
          description: "https://github.com/apache/incubator-kie-issues/issues/2318",
        },
      },
      async ({ palette, nodes, jsonModel, diagram }) => {
        await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
        await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
        await nodes.delete({ name: DefaultNodeName.TASK });
        await expect(diagram.get()).toHaveScreenshot("delete-task-with-boundary-event.png");

        const process = await jsonModel.getProcess();
        expect(process?.flowElement?.length).toBe(0);
      }
    );

    test("should move task with boundary event", async ({ palette, jsonModel, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await expect(nodes.getByType(NodeType.TASK)).toBeAttached();

      await nodes.getByType(NodeType.TASK).scrollIntoViewIfNeeded();
      const taskId = await nodes.getIdByType(NodeType.TASK);
      expect(taskId).not.toBe("");

      await nodes.dragNodeToPosition({
        id: taskId,
        fromPosition: NodePosition.LEFT,
        toPosition: { x: 400, y: 350 },
      });
      await expect(diagram.get()).toHaveScreenshot("move-task-with-boundary-event.png");

      const boundaryEvent = (await jsonModel.getBoundaryEvents())[0];
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });
  });
});
