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
    test.beforeEach(async ({ palette, nodes }) => {
      await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 100, y: 100 } });
      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
    });

    test.only("Decision Service Signature - Output Decisions", async ({
      decisionServicePropertiesPanel,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 120, y: 120 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getOutputDecisions()).toEqual("New Decision");
    });

    test.only("Decision Service Signature - Encapsulated Decisions", async ({
      decisionServicePropertiesPanel,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 200 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service()"
      );
      expect(await decisionServicePropertiesPanel.getEncapsulatedDecisions()).toEqual("New Decision");
    });

    test.only("Decision Service Signature - Input Data", async ({ decisionServicePropertiesPanel, nodes, palette }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 200 } });
      await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 200, y: 200 } });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service(New Input Data)"
      );
      expect(await decisionServicePropertiesPanel.getInputData()).toEqual("New Input Data");
    });

    test.only("Decision Service Signature - Input Decisions", async ({
      decisionServicePropertiesPanel,
      nodes,
      palette,
    }) => {
      //TODO https://github.com/apache/incubator-kie-issues/issues/663

      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 200, y: 200 },
        thenRenameTo: "First Decision",
      });
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Second Decision",
      });
      await nodes.dragNewConnectedEdge({
        type: EdgeType.INFORMATION_REQUIREMENT,
        from: "Second Decision",
        to: "Second Decision",
      });

      await nodes.select({ name: DefaultNodeName.DECISION_SERVICE, position: NodePosition.TOP });
      await decisionServicePropertiesPanel.open();
      expect(await decisionServicePropertiesPanel.getInvokingThisDecisionServiceInFeel()).toEqual(
        "New Decision Service(New Decision)"
      );
      expect(await decisionServicePropertiesPanel.getInputDecisions()).toEqual("Second Decision");
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
