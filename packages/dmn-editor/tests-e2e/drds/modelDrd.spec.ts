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
import { expect, test } from "../__fixtures__/base";
import { DefaultNodeName, NodeType } from "../__fixtures__/nodes";
import { EdgeType } from "../__fixtures__/edges";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model DRD", () => {
  test.describe("Model DRD - Create", () => {
    test("should create DRD in empty model", async ({ drds, drgNodes }) => {
      await drds.toggle();
      await drds.create({ name: "second drd" });

      await expect(drgNodes.popover()).toBeVisible();
      await expect(drgNodes.popover()).toContainText("No DRG nodes yet");
      expect(await drds.getCurrent()).toEqual("second drd");
    });

    test("should create DRD in non empty model", async ({ drds, drgNodes, palette }) => {
      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await drds.toggle();
      await drds.create({ name: "second drd" });

      await expect(drgNodes.popover()).toBeVisible();
      await expect(drgNodes.popover()).not.toContainText("No DRG nodes yet");
      expect(await drds.getCurrent()).toEqual("second drd");
    });
  });

  test.describe("Model DRD - Multiple DRDs", () => {
    test.beforeEach("Create DRDs", async ({ drds }) => {
      await drds.toggle();
      await drds.create({ name: "First DRD" });

      await drds.toggle();
      await drds.create({ name: "Second DRD" });

      await drds.toggle();
      await drds.create({ name: "Third DRD" });
    });

    test.describe("Model DRD - Rename", () => {
      test("should rename DRD and navigate away", async ({ drds }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.rename({ newName: "SECOND DRD" });
        await drds.navigateTo({ name: "First DRD" });

        expect(await drds.getAll()).toEqual(["1. First DRD", "2. SECOND DRD", "3. Third DRD"]);
      });

      test("should rename DRD using non alphabet character", async ({ drds }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.rename({ newName: "SECOND%20DRD" });

        expect(await drds.getAll()).toEqual(["1. First DRD", "2. SECOND%20DRD", "3. Third DRD"]);
      });
    });

    test.describe("Model DRD - Delete", async () => {
      test("should remove DRD and check the indexes", async ({ drds }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/1174",
        });
        await drds.toggle();
        await drds.remove({ name: "Second DRD" });

        expect(await drds.getAll()).toEqual(["1. First DRD", "2. Third DRD"]);
      });
    });

    test.describe("Model DRD - Navigate", () => {
      test("should navigate to the first DRD", async ({ drds }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });

        expect(await drds.getCurrent()).toEqual("First DRD");
      });

      test("should navigate to multiple DRDs and stay in the last", async ({ drds }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.navigateTo({ name: "Second DRD" });

        expect(await drds.getCurrent()).toEqual("Second DRD");
      });
    });

    test.describe("Model DRD - Add Content", async () => {
      test("should add DRG node to single DRD", async ({ drds, nodes, palette }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "Third DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
      });

      test("should add DRG node to all DRDs", async ({ drds, drgNodes, nodes, palette }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Third DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
      });

      test("should remove DRG node from all DRDs", async ({ drds, drgNodes, nodes, palette }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Third DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await nodes.delete({ name: DefaultNodeName.DECISION });

        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: DefaultNodeName.DECISION })).not.toBeAttached();
      });

      test("should rename DRG node in all DRDs", async ({ drds, drgNodes, nodes, palette }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Third DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await nodes.selectLabel({ name: DefaultNodeName.DECISION });
        await nodes.rename({ current: DefaultNodeName.DECISION, new: "Renamed Decision" });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: "Renamed Decision" })).toBeAttached();

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await expect(nodes.get({ name: "Renamed Decision" })).toBeAttached();
      });

      test("should add connection to all DRDs", async ({ drds, drgNodes, diagram, edges, nodes, palette }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });
        await diagram.resetFocus();
        await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 300, y: 500 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });
        await drgNodes.dragNode({ name: DefaultNodeName.INPUT_DATA, targetPosition: { x: 300, y: 500 } });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: DefaultNodeName.INPUT_DATA,
          to: DefaultNodeName.DECISION,
        });

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await expect(
          await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })
        ).toBeAttached();
      });

      test("should mark node with missing dependency indicator", async ({
        drds,
        drgNodes,
        diagram,
        nodes,
        palette,
      }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });
        await diagram.resetFocus();
        await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 300, y: 500 } });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: DefaultNodeName.INPUT_DATA,
          to: DefaultNodeName.DECISION,
        });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });

        await expect(diagram.get()).toHaveScreenshot("drds-decision-missing-dependency.png");
      });

      test("should remove node missing dependency indicator", async ({
        drds,
        drgNodes,
        diagram,
        edges,
        nodes,
        palette,
      }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });
        await diagram.resetFocus();
        await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 300, y: 500 } });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: DefaultNodeName.INPUT_DATA,
          to: DefaultNodeName.DECISION,
        });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 300, y: 300 } });
        await drgNodes.dragNode({ name: DefaultNodeName.INPUT_DATA, targetPosition: { x: 300, y: 500 } });

        await expect(diagram.get()).toHaveScreenshot("drds-decision-no-missing-dependency.png");

        await expect(
          await edges.get({ from: DefaultNodeName.INPUT_DATA, to: DefaultNodeName.DECISION })
        ).toBeAttached();
      });

      test("should not move the original node depiction", async ({
        decisionPropertiesPanel,
        drds,
        drgNodes,
        nodes,
        palette,
      }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 500, y: 500 } });

        await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 400 } });

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await nodes.select({ name: DefaultNodeName.DECISION });
        await decisionPropertiesPanel.open();
        const { x, y } = await decisionPropertiesPanel.getShape();
        expect(x).toEqual("200");
        expect(y).toEqual("200");
      });

      test("should not resize the original node depiction", async ({
        decisionPropertiesPanel,
        drds,
        drgNodes,
        nodes,
        palette,
      }) => {
        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 300, y: 300 } });

        await drds.toggle();
        await drds.navigateTo({ name: "Second DRD" });
        await drds.toggle();
        await drgNodes.open();
        await drgNodes.dragNode({ name: DefaultNodeName.DECISION, targetPosition: { x: 500, y: 500 } });

        await nodes.resize({ nodeName: DefaultNodeName.DECISION, xOffset: 100, yOffset: 100 });

        await drds.toggle();
        await drds.navigateTo({ name: "First DRD" });
        await drds.toggle();
        await nodes.select({ name: DefaultNodeName.DECISION });
        await decisionPropertiesPanel.open();
        const { width, height } = await decisionPropertiesPanel.getShape();
        expect(width).toEqual("160");
        expect(height).toEqual("80");
      });
    });
  });
});
