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
import { EdgeType } from "../__fixtures__/edges";
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model Decision Service", () => {
  test.describe("Decision Service Signature", () => {
    test.beforeEach(async ({ diagram, palette }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 300, y: 100 } });
      await diagram.resetFocus();
    });

    test("Decision Service Signature - Output Decisions", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 180 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual("New Decision");
    });

    test("Decision Service Signature - Encapsulated Decisions", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
      await diagram.resetFocus();
      await nodes.move({ name: DefaultNodeName.DECISION, targetPosition: { x: 400, y: 350 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual("New Decision");
    });

    test("Decision Service Signature - Input Data", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

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
      expect(await decisionServicePropertiesPanel.getInputData()).toEqual("New Input Data");
    });

    test("Decision Service Signature - Input Decisions", async ({
      decisionServicePropertiesPanel,
      diagram,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

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
      expect(await decisionServicePropertiesPanel.getInputDecisions()).toEqual("Decision Two");
    });

    test.skip("Decision Service Inputs Order", async () => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/664
    });

    test("Delete Decision from the Decision Service upper divider", async () => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/879
    });

    test("Delete Decision from the Decision Service below divider", async () => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/879
    });
  });

  test("Resize non empty decision service", async () => {
    // https://github.com/apache/incubator-kie-issues/issues/881
    // move into `resize` spec file, once is merged https://github.com/ljmotta/kie-tools/pull/27
  });

  test.skip("Drag Decision directly into Decision Service", async () => {
    //TODO https://github.com/apache/incubator-kie-issues/issues/896
  });

  test.skip("Add connected Decision from Decision that is already part of Decision Service", async () => {
    //TODO https://github.com/apache/incubator-kie-issues/issues/897
  });

  test("Move Decision inside Decision Service by a keyboard", async () => {
    //TODO https://github.com/apache/incubator-kie-issues/issues/876
  });

  test.skip("Move Decision inside Decision Service by a keyboard without crossing sections", async () => {
    //TODO https://github.com/apache/incubator-kie-issues/issues/876
  });
});
