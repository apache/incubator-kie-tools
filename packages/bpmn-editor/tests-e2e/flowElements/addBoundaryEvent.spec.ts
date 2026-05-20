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
import { NodeType, DefaultNodeName } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Add Boundary Event", () => {
  test.describe("Basic attachment", () => {
    test("should attach intermediate catch event to task", async ({ palette, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach intermediate catch event to subprocess", async ({ palette, jsonModel }) => {
      await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 100, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 550, y: 350 } });

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach multiple boundary events to same task", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 300, y: 280 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 350, y: 350 } });

      await expect(diagram.get()).toHaveScreenshot("attach-multiple-boundary-events-to-task.png");

      const process = await jsonModel.getProcess();
      const boundaryEvents = process.flowElement?.filter(
        (e: { __$$element: string }) => e.__$$element === "boundaryEvent"
      );
      expect(boundaryEvents?.length).toBe(3);
      boundaryEvents?.forEach((event: { __$$element: string; "@_attachedToRef"?: string }) => {
        expect(event.__$$element).toBe("boundaryEvent");
        expect(event["@_attachedToRef"]).toBeDefined();
      });
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

      const boundaryEventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(boundaryEventNode).toBeVisible();
      await boundaryEventNode.dragTo(diagram.get(), { targetPosition: { x: 500, y: 100 } });

      await expect(diagram.get()).toHaveScreenshot("detach-boundary-event-from-task.png");

      const detachedEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(detachedEvent.__$$element).toBe("intermediateCatchEvent");
      expect(detachedEvent["@_attachedToRef"]).toBeUndefined();
    });
  });

  test.describe("Interrupting vs Non-interrupting", () => {
    test("should create interrupting boundary event by default", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      await expect(diagram.get()).toHaveScreenshot("interrupting-boundary-event.png");

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
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

      const boundaryEventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(boundaryEventNode).toBeVisible();
      await boundaryEventNode.click();

      await intermediateEventPropertiesPanel.setCancelActivity({ cancelActivity: false });

      await expect
        .poll(async () => {
          return await jsonModel.getFlowElement({ elementIndex: 1 });
        })
        .toMatchObject({
          __$$element: "boundaryEvent",
          "@_attachedToRef": expect.stringMatching(/.+/),
          "@_cancelActivity": false,
        });

      await expect(diagram.get()).toHaveScreenshot("non-interrupting-boundary-event.png");
    });
  });

  test.describe("Activity types", () => {
    test("should attach to user task", async ({ palette, nodes, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });

      const task = nodes.get({ name: DefaultNodeName.TASK });
      await nodes.morphNode({ nodeLocator: task, targetMorphType: "User task" });

      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      await expect(diagram.get()).toHaveScreenshot("attach-boundary-event-to-user-task.png");

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });

    test("should attach to call activity", async ({ palette, jsonModel, diagram }) => {
      await palette.dragNewNode({ type: NodeType.CALL_ACTIVITY, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      await expect(diagram.get()).toHaveScreenshot("attach-boundary-event-to-call-activity.png");

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });
  });

  test.describe("Operations", () => {
    test("should delete boundary event", async ({ palette, jsonModel, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      const boundaryEventNode = nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT);
      await expect(boundaryEventNode).toBeAttached();

      await boundaryEventNode.click();
      await boundaryEventNode.press("Delete");

      await expect(boundaryEventNode).not.toBeAttached();

      const process = await jsonModel.getProcess();
      expect(
        process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "boundaryEvent")
      ).toBeUndefined();
      expect(process.flowElement?.find((e: { __$$element: string }) => e.__$$element === "task")).toBeDefined();
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

        const process = await jsonModel.getProcess();
        expect(process.flowElement?.filter((e: { __$$element: string }) => e.__$$element === "task").length).toBe(0);
        expect(
          process.flowElement?.filter((e: { __$$element: string }) => e.__$$element === "boundaryEvent").length
        ).toBe(0);

        await expect(diagram.get()).toHaveScreenshot("delete-task-with-boundary-event.png");
      }
    );

    test("should move task with boundary event", async ({ palette, jsonModel, diagram, nodes }) => {
      await palette.dragNewNode({ type: NodeType.TASK, targetPosition: { x: 300, y: 300 } });
      await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 450, y: 300 } });

      const taskNode = nodes.getByType(NodeType.TASK);
      await expect(taskNode).toBeAttached();
      await taskNode.scrollIntoViewIfNeeded();

      const taskBox = await nodes.getNodeBounds({ id: await nodes.getIdByType(NodeType.TASK) });

      await taskNode.dragTo(diagram.get(), {
        sourcePosition: { x: 20, y: taskBox.height / 2 },
        targetPosition: { x: 400, y: 350 },
        force: true,
      });

      await expect(diagram.get()).toHaveScreenshot("move-task-with-boundary-event.png");

      const boundaryEvent = await jsonModel.getFlowElement({ elementIndex: 1 });
      expect(boundaryEvent.__$$element).toBe("boundaryEvent");
      expect(boundaryEvent["@_attachedToRef"]).toBeDefined();
    });
  });
});
