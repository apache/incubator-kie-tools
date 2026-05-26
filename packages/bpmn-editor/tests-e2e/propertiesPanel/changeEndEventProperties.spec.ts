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

test.beforeEach(async ({ editor, page }) => {
  await editor.open();
  await page.setViewportSize({ width: 1920, height: 1080 });
});

test.describe("Change Properties - End Event", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.END_EVENT, targetPosition: { x: 100, y: 100 } });

    const endEvent = nodes.getByType(NodeType.END_EVENT);
    await expect(endEvent).toBeVisible();

    await endEvent.click();
  });

  test("should change the End Event name", async ({ endEventPropertiesPanel }) => {
    await endEventPropertiesPanel.nameProperties.setName({ newName: "Process Completed" });

    expect(await endEventPropertiesPanel.nameProperties.getName()).toBe("Process Completed");
  });

  test("should change the End Event documentation", async ({ endEventPropertiesPanel }) => {
    await endEventPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This event ends the process successfully",
    });

    expect(await endEventPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This event ends the process successfully"
    );
  });

  test("should configure Terminate event definition", async ({ endEventPropertiesPanel, page, nodes }) => {
    await endEventPropertiesPanel.setTerminateDefinition({
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("end-event-terminate.png");
  });

  test("should configure Message event definition", async ({ endEventPropertiesPanel, page, nodes }) => {
    await endEventPropertiesPanel.setMessageDefinition({
      messageName: "CompletionMessage",
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("end-event-message.png");
  });

  test("should configure Signal event definition", async ({ endEventPropertiesPanel, page, nodes }) => {
    await endEventPropertiesPanel.setSignalDefinition({
      signalName: "CompletionSignal",
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    expect(await endEventPropertiesPanel.getSignalName()).toBe("CompletionSignal");
  });

  test("should configure Error event definition", async ({ endEventPropertiesPanel, page, nodes }) => {
    await endEventPropertiesPanel.setErrorDefinition({
      errorName: "ProcessError",
      errorCode: "ERR500",
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    expect(await endEventPropertiesPanel.getErrorName()).toBe("ProcessError");
  });

  test("should configure Escalation event definition", async ({ endEventPropertiesPanel, page, nodes }) => {
    await endEventPropertiesPanel.setEscalationDefinition({
      escalationName: "ProcessEscalation",
      escalationCode: "ESC100",
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    expect(await endEventPropertiesPanel.getEscalationName()).toBe("ProcessEscalation");
  });

  test("should configure Compensation event definition", async ({ endEventPropertiesPanel, jsonModel, nodes }) => {
    await endEventPropertiesPanel.setCompensationDefinition({
      endEventLocator: nodes.getByType(NodeType.END_EVENT).first(),
    });

    const flowElement = await jsonModel.getFlowElement({ elementIndex: 0 });
    expect(flowElement.__$$element).toBe("endEvent");
    expect(flowElement.eventDefinition[0].__$$element).toBe("compensateEventDefinition");
  });
});
