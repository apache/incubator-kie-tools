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
import { DefaultNodeName, NodeType, SubProcessNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor, page, jsonModel }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Change Properties - Sub-Process", () => {
  test.beforeEach(async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();
  });

  test("should change the Sub-Process name", async ({ subProcessPropertiesPanel, jsonModel }) => {
    await subProcessPropertiesPanel.nameProperties.setName({ newName: "Order Processing" });

    expect(await subProcessPropertiesPanel.nameProperties.getName()).toBe("Order Processing");
  });

  test("should change the Sub-Process documentation", async ({ subProcessPropertiesPanel, jsonModel }) => {
    await subProcessPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This sub-process handles order processing logic",
    });

    expect(await subProcessPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This sub-process handles order processing logic"
    );
  });
});

test.describe("Change Properties - Sub-Process Multi-Instance", () => {
  test.beforeEach(async ({ palette, nodes, jsonModel }) => {
    await palette.addProcessVariable({ name: "orderItems", dataType: "Object" });
    await palette.addProcessVariable({ name: "tasks", dataType: "Object" });

    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
    await expect(subProcess).toBeAttached();

    await nodes.morph({ node: subProcess, to: SubProcessNodeType.MULTI_INSTANCE });
  });

  test("should configure Sub-Process multi-instance parallel", async ({
    subProcessPropertiesPanel,
    page,
    jsonModel,
  }) => {
    await subProcessPropertiesPanel.setMultiInstance({ type: "parallel" });
    await subProcessPropertiesPanel.setCollectionExpression({ expression: "orderItems" });

    await expect(
      subProcessPropertiesPanel.panel().getByRole("button", { name: "Parallel", exact: true }).first()
    ).toHaveAttribute("aria-pressed", "true");

    const loopCharacteristics = (await jsonModel.getSubProcesses())[0].loopCharacteristics;
    expect(loopCharacteristics?.__$$element).toBe("multiInstanceLoopCharacteristics");

    // Narrow the loopCharacteristics union so the multi-instance-only fields are reachable.
    const multiInstanceLoop =
      loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" ? loopCharacteristics : undefined;
    expect(multiInstanceLoop?.["@_isSequential"]).toBeFalsy();
  });

  test("should configure Sub-Process multi-instance sequential", async ({
    subProcessPropertiesPanel,
    page,
    jsonModel,
  }) => {
    await subProcessPropertiesPanel.setMultiInstance({ type: "sequential" });
    await subProcessPropertiesPanel.setCollectionExpression({ expression: "tasks" });

    await expect(
      subProcessPropertiesPanel.panel().getByRole("button", { name: "Sequential", exact: true }).first()
    ).toHaveAttribute("aria-pressed", "true");

    const loopCharacteristics = (await jsonModel.getSubProcesses())[0].loopCharacteristics;
    expect(loopCharacteristics?.__$$element).toBe("multiInstanceLoopCharacteristics");

    // Narrow the loopCharacteristics union so the multi-instance-only fields are reachable.
    const multiInstanceLoop =
      loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" ? loopCharacteristics : undefined;
    expect(multiInstanceLoop?.["@_isSequential"]).toBe(true);
  });
});

test.describe("Change Properties - Ad-Hoc Sub-Process", () => {
  test.beforeEach(async ({ palette, nodes, jsonModel }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    const subProcess = nodes.get({ name: DefaultNodeName.SUB_PROCESS });
    await expect(subProcess).toBeAttached();

    await nodes.morph({ node: subProcess, to: SubProcessNodeType.AD_HOC });
  });

  test("should configure Ad-Hoc Sub-Process", async ({ subProcessPropertiesPanel, page, jsonModel }) => {
    await subProcessPropertiesPanel.setAdHocOrdering({ ordering: "Parallel" });

    await expect(
      subProcessPropertiesPanel.panel().getByRole("button", { name: "Parallel", exact: true }).first()
    ).toHaveAttribute("aria-pressed", "true");

    const adHocSubProcess = (await jsonModel.getAdHocSubProcesses())[0];
    expect(adHocSubProcess["@_ordering"]).toBe("Parallel");
  });

  test("should configure Ad-Hoc Sub-Process with sequential ordering", async ({
    subProcessPropertiesPanel,
    page,
    jsonModel,
  }) => {
    await subProcessPropertiesPanel.setAdHocOrdering({ ordering: "Sequential" });
    await subProcessPropertiesPanel.setAdHocCompletionCondition({ condition: "${allTasksCompleted}" });

    await expect(
      subProcessPropertiesPanel.panel().getByRole("button", { name: "Sequential", exact: true }).first()
    ).toHaveAttribute("aria-pressed", "true");

    await expect(
      subProcessPropertiesPanel
        .panel()
        .getByRole("textbox", { name: /ad-hoc completion condition/i })
        .first()
    ).toHaveValue("${allTasksCompleted}");

    const adHocSubProcess = (await jsonModel.getAdHocSubProcesses())[0];
    expect(adHocSubProcess["@_ordering"]).toBe("Sequential");
    expect(adHocSubProcess.completionCondition?.__$$text).toBe("${allTasksCompleted}");
  });
});
