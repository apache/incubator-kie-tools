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
import { DefaultNodeName, NodePosition, NodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Model Decision Services - DRD", () => {
  //TODO Extract scenarios from https://github.com/apache/incubator-kie-issues/issues/870

  test("888 add to DRD, all DRDs depiction same", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // create non empty DS
    // add the ^ DS into non default DRD
    // all the DS content needs to be added into DRD also
  });

  test("888 add to DRD, automatically layout", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // add decision service, non empty, that is currently in no DRD
    // its content should be automatically layout-ed
  });

  test("A Decision Service with conflicting contained Decisions should be added to the DRD in a collapsed state", async ({
    drds,
    palette,
    nodes,
    diagram,
    drgNodes,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();

    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS2",
    });
    await expect(nodes.get({ name: "DS2" })).toBeAttached();

    await drgNodes.toggle();
    await drgNodes.dragNode({ name: "D1", targetPosition: { x: 200, y: 200 } });
    await drgNodes.toggle();
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drgNodes.toggle();
    await drgNodes.dragNode({ name: "DS2", targetPosition: { x: 100, y: 100 } });
    await drgNodes.toggle();
    await expect(diagram.get()).toHaveScreenshot("add-conflicting-decision-service.png");
  });

  test("888 add to DRD, collapsed", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/888");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/888",
    });
    // prepare on empty DS
    // add some of its content to different DRD
    // collapse the DS
    // add the DS into DRD where already some its content is
    // the DS should be added as collapsed and its content removed from the DRD
  });

  test("894 do not allow add node that is already in DS and in DRD expanded", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/894");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/894",
    });
    // On the DMN Editor, do not allow adding a Decision to a DRD
    // if this Decision is contained by one or more Decision Services already present
    // on that DRD, even if in collapsed state.
  });

  test("894 do not allow add node that is already in DS and in DRD collapsed", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/894");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/894",
    });
    // On the DMN Editor, do not allow adding a Decision to a DRD
    // if this Decision is contained by one or more Decision Services already present
    // on that DRD, even if in collapsed state.
  });

  test("972 autolayout", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/972");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/972",
    });
    // TODO, question, not understand
  });

  test("Collapsing a Decision Service hides all of its contained Decisions from the DRD", async ({
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    await palette.dragNewNode({ type: NodeType.DECISION_SERVICE, targetPosition: { x: 400, y: 200 } });
    await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toBeAttached();

    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "User Decision",
    });
    await expect(nodes.get({ name: "User Decision" })).toBeAttached();

    await nodes.move({ name: "User Decision", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "User Decision" })).toBeAttached();

    await nodes.selectAndCollapseDecisionService({ name: DefaultNodeName.DECISION_SERVICE });

    await expect(nodes.get({ name: "User Decision" })).not.toBeAttached();
    await expect(diagram.get()).toHaveScreenshot("collapse-decision-service.png");
  });

  test("892 collapse - allow only if no other DS is expanded in the DRD", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("Expanding a Decision Service shows the same depiction as in other DRD", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();

    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await nodes.selectAndCollapseDecisionService({ name: "DS1" });

    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });
    await drgNodes.toggle();

    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();

    await nodes.selectAndExpandDecisionService({ name: "DS1" });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot("decision-service-from-other-drd.png");
  });

  test("Resizing decision contained by decision service should resize it in all DRDs", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/889",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();

    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });

    await drgNodes.toggle();
    await nodes.resize({ nodeName: "D1", xOffset: 50, yOffset: 0 });
    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("resized-decision.png");
  });

  test("Moving decision contained by decision service should move it in all DRDs", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/889",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();

    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });

    await drgNodes.toggle();
    await nodes.move({ name: "D1", targetPosition: { x: 600, y: 300 } });
    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("repositioned-decision.png");
  });

  test("892 expand - allow only if no other DS is collpased in the DRD", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/892");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/892",
    });
    // TODO
  });

  test("889 move DS content and replicate in DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/889");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/889",
    });
    // TODO
  });

  test("Resizing a Decision Service in a DRD should replicate it in all DRDs", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/898",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });
    await drgNodes.toggle();
    await nodes.resize({ nodeName: "DS1", xOffset: 100, yOffset: 100 });
    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("resized-decision-service.png");
  });

  test("Moving decision service divider line in one DRD replicates it in all DRDs", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/898",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });

    await drgNodes.toggle();
    await nodes.moveDividerLine({ nodeName: "DS1" });
    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("moving-divider-line.png");
  });

  test("Adding a node to Decision service adds it to all DRDs where Decision Service is in expanded form", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/890",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await drgNodes.dragNode({ name: "D1", targetPosition: { x: 200, y: 200 } });
    await drgNodes.toggle();
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("decision_added_to_decision_service.png");
  });

  test("Moving a node to Decision service , check it is removed from DRDs where DS in collapsed form", async ({
    drds,
    drgNodes,
    palette,
    nodes,
    diagram,
  }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/890",
    });
    await drds.toggle();
    await drds.create({ name: "First DRD" });
    await drgNodes.toggle();

    await palette.dragNewNode({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 400, y: 200 },
      thenRenameTo: "DS1",
    });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await nodes.selectAndCollapseDecisionService({ name: "DS1" });
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 200, y: 200 },
      thenRenameTo: "D1",
    });
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await drds.toggle();
    await drds.create({ name: "Second DRD" });
    await drgNodes.dragNode({ name: "DS1", targetPosition: { x: 400, y: 200 } });
    await expect(nodes.get({ name: "DS1" })).toBeAttached();
    await drgNodes.dragNode({ name: "D1", targetPosition: { x: 200, y: 200 } });
    await drgNodes.toggle();
    await expect(nodes.get({ name: "D1" })).toBeAttached();
    await nodes.move({ name: "D1", targetPosition: { x: 500, y: 300 } });
    await expect(nodes.get({ name: "D1" })).toBeAttached();

    await drds.toggle();
    await drds.navigateTo({ name: "First DRD" });
    await drds.toggle();
    await expect(diagram.get()).toHaveScreenshot("decision_added_to_decision_service_collapsedDS.png");
  });

  test("891 remove node from DS, check its removed form DS in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/891");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/891",
    });
    // TODO, question, isn't this the same as 889 - syncing depiction of DS in all DRDs?
  });

  test("893 collapsed external DS, synced in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("893 expanded external DS, synced in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("893 remove node from DS, check its removed form DS in all DRDs", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/893");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/893",
    });
    // TODO
  });

  test("895 ", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/895");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/895",
    });
    // TODO , Question, isn't that in conflict with other DRD tickets?
    // is that even possible to be in state DS_One is collapsed
    // DS_Two is expanded and both of them contain the same decision?
  });

  test("1298 ", async ({ drds }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1298");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1298",
    });
    // TODO
  });
});
