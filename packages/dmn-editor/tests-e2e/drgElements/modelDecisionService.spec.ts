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
import { test, expect } from "../__fixtures__/base";
import { EdgeType } from "../__fixtures__/edges";
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model Decision Service", () => {
  test.describe("Model Decision Service - Signature", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 300, y: 100 } });
      await diagram.resetFocus();
    });

    test("Decision Service Output Decisions Signature should be not empty", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/663",
      });

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 180 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual(["New Decision"]);
    });

    test("Decision Service Encapsulated Decisions Signature should be not empty", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/663",
      });

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 350 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual(["New Decision"]);
    });

    test("Decision Service Input Data Signature should be not empty", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/663",
      });

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 350 } });

      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 100, y: 100 } });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: DefaultNodeName.INPUT_DATA,
        to: DefaultNodeName.DECISION,
      });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service(New Input Data)"
      );
      expect(await decisionServicePropertiesPanel.getInputData()).toEqual(["New Input Data"]);
    });

    test("Decision Service Input Decisions Signature should be not empty", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/663",
      });

      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Decision One",
      });
      await diagram.resetFocus();
      await nodes.move({ name: "Decision One", targetPosition: { x: 400, y: 350 } });

      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Decision Two",
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Decision Two",
        to: "Decision One",
      });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service(Decision Two)"
      );
      expect(await decisionServicePropertiesPanel.getInputDecisions()).toEqual(["Decision Two"]);
    });

    test.describe("Model Decision Service - Signature - Inputs Order", () => {
      test.beforeEach("Initialize nodes and connections", async ({ diagram, nodes, palette }) => {
        await palette.dragNewNode({
          type: NodeType.DECISION,
          targetPosition: { x: 100, y: 100 },
        });
        await diagram.resetFocus();
        await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 350 } });

        await palette.dragNewNode({
          type: NodeType.DECISION,
          targetPosition: { x: 100, y: 100 },
          thenRenameTo: "B",
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "B",
          to: DefaultNodeName.DECISION,
        });

        await palette.dragNewNode({
          type: NodeType.DECISION,
          targetPosition: { x: 100, y: 200 },
          thenRenameTo: "A",
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "A",
          to: DefaultNodeName.DECISION,
        });

        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 100, y: 300 },
          thenRenameTo: "BB",
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "BB",
          to: DefaultNodeName.DECISION,
        });

        await palette.dragNewNode({
          type: NodeType.INPUT_DATA,
          targetPosition: { x: 100, y: 400 },
          thenRenameTo: "AA",
        });
        await nodes.dragNewConnectedEdge({
          type: EdgeType.INFORMATION_REQUIREMENT,
          from: "AA",
          to: DefaultNodeName.DECISION,
        });
      });

      test("Decision Service Inputs Signature should have a default order", async ({
        decisionServicePropertiesPanel,
        nodes,
      }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/664",
        });

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();
        expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
          "New Decision Service(B, A, BB, AA)"
        );
        expect(await decisionServicePropertiesPanel.getInputDecisions()).toEqual(["B", "A"]);
        expect(await decisionServicePropertiesPanel.getInputData()).toEqual(["BB", "AA"]);
      });

      test("Decision Service Inputs Signature should be reordered", async ({
        decisionServicePropertiesPanel,
        nodes,
      }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/664",
        });

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();
        await decisionServicePropertiesPanel.moveInputData({ fromIndex: 0, toIndex: 1 });
        await decisionServicePropertiesPanel.moveInputDecision({ fromIndex: 0, toIndex: 1 });
        expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
          "New Decision Service(A, B, AA, BB)"
        );
        expect(await decisionServicePropertiesPanel.getInputDecisions()).toEqual(["A", "B"]);
        expect(await decisionServicePropertiesPanel.getInputData()).toEqual(["AA", "BB"]);
      });
    });

    test.describe("Model Decision Service - Signature - Decisions", () => {
      test.beforeEach(async ({ decisionServicePropertiesPanel, diagram, nodes, palette }) => {
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 }, thenRenameTo: "A" });
        await diagram.resetFocus();
        await nodes.move({ name: "A", targetPosition: { x: 400, y: 180 } });

        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 }, thenRenameTo: "B" });
        await diagram.resetFocus();
        await nodes.move({ name: "B", targetPosition: { x: 400, y: 350 } });

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();
        expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual(["A"]);
        expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual(["B"]);
      });

      test("Decision Service Decision Signature should not contain deleted Output Decision", async ({
        decisionServicePropertiesPanel,
        nodes,
      }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/879",
        });
        await nodes.delete({ name: "A" });
        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual(["(Empty)"]);
      });

      test("Decision Service Decision Signature should not contain deleted Encapsulated Decision", async ({
        decisionServicePropertiesPanel,
        nodes,
      }) => {
        test.info().annotations.push({
          type: TestAnnotations.REGRESSION,
          description: "https://github.com/apache/incubator-kie-issues/issues/879",
        });
        await nodes.delete({ name: "B" });
        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual(["(Empty)"]);
      });
    });

    test.describe("Model Decision Service - Add Content", () => {
      test("Decision Service should allow to drag Decision into it from palette", async ({
        decisionServicePropertiesPanel,
        diagram,
        nodes,
        palette,
      }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/896");
        test.info().annotations.push({
          type: TestAnnotations.AFFECTED_BY,
          description: "https://github.com/apache/incubator-kie-issues/issues/896",
        });

        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 400, y: 300 } });

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();
        expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual([DefaultNodeName.DECISION]);
      });

      test("Decision Service should allow to add connected Decision from a contained Decision", async ({
        decisionServicePropertiesPanel,
        diagram,
        nodes,
        palette,
      }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/897");
        test.info().annotations.push({
          type: TestAnnotations.AFFECTED_BY,
          description: "https://github.com/apache/incubator-kie-issues/issues/897",
        });

        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 }, thenRenameTo: "A" });
        await diagram.resetFocus();
        await nodes.move({ name: "A", targetPosition: { x: 400, y: 180 } });

        await nodes.resize({
          nodeName: DefaultNodeName.DECISION_SERVICE,
          position: NodePosition.TOP,
          xOffset: 350,
          yOffset: 0,
        });

        await nodes.dragNewConnectedNode({ type: NodeType.DECISION, from: "A", targetPosition: { x: 500, y: 160 } });

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();

        expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual(["A, New Decision"]);
      });

      test("Decision Service should allow to move contained Decision by a keyboard without crossing sections", async ({
        decisionServicePropertiesPanel,
        diagram,
        nodes,
        page,
        palette,
      }) => {
        test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/876");
        test.info().annotations.push({
          type: TestAnnotations.AFFECTED_BY,
          description: "https://github.com/apache/incubator-kie-issues/issues/876",
        });
        await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
        await diagram.resetFocus();
        await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 180 } });

        for (let index = 0; index < 10; index++) {
          await page.keyboard.press("ArrowDown");
        }

        await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
        await decisionServicePropertiesPanel.open();
        expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual(["New Decision"]);

        await expect(diagram.get()).toHaveScreenshot("move-decision-in-decision-service-by-keyboard.png");
      });
    });
  });
});
