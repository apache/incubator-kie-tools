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
import { NodeType, EventNodeType } from "../__fixtures__/nodes";

test.beforeEach(async ({ editor, page }) => {
  await page.setViewportSize({ width: 1920, height: 1080 });
  await editor.open();
  await editor.setInitialProcessId();
});

test.describe("Change Properties - Intermediate Catch Event", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_CATCH_EVENT, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT)).toBeVisible();

    await nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).click();
  });

  test("should change the Intermediate Catch Event name", async ({ intermediateEventPropertiesPanel }) => {
    await intermediateEventPropertiesPanel.nameProperties.setName({ newName: "Wait for Approval" });

    expect(await intermediateEventPropertiesPanel.nameProperties.getName()).toBe("Wait for Approval");
  });

  test("should change the Intermediate Catch Event documentation", async ({ intermediateEventPropertiesPanel }) => {
    await intermediateEventPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This event waits for an external approval",
    });

    expect(await intermediateEventPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This event waits for an external approval"
    );
  });

  test("should configure Timer definition with duration", async ({ intermediateEventPropertiesPanel, page, nodes }) => {
    await expect(nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).first()).toBeVisible();

    await nodes.morph({ node: nodes.getByType(NodeType.INTERMEDIATE_CATCH_EVENT).first(), to: EventNodeType.TIMER });
    await intermediateEventPropertiesPanel.setTimerDefinition({ type: "duration", value: "PT1H" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "intermediate-catch-event-timer-duration.png"
    );
  });

  test("should configure Message definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setMessageDefinition({ messageName: "ApprovalMessage" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "intermediate-catch-event-message.png"
    );
  });

  test("should configure Conditional expression", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setConditionalExpression({ expression: "${approved == true}" });

    expect(await intermediateEventPropertiesPanel.getConditionalExpression()).toBe("${approved == true}");
  });

  test("should configure Link definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setLinkDefinition({ linkName: "ProcessLink" });

    expect(await intermediateEventPropertiesPanel.getLinkName()).toBe("ProcessLink");
  });

  test("should configure Error definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setErrorDefinition({
      errorName: "ValidationError",
    });

    expect(await intermediateEventPropertiesPanel.getErrorName()).toBe("ValidationError");
  });

  test("should configure Escalation definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setEscalationDefinition({
      escalationName: "ProcessEscalation",
    });

    expect(await intermediateEventPropertiesPanel.getEscalationName()).toBe("ProcessEscalation");
  });
});

test.describe("Change Properties - Intermediate Throw Event", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.INTERMEDIATE_THROW_EVENT, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT)).toBeVisible();

    await nodes.getByType(NodeType.INTERMEDIATE_THROW_EVENT).click();
  });

  test("should change the Intermediate Throw Event name", async ({ intermediateEventPropertiesPanel }) => {
    await intermediateEventPropertiesPanel.nameProperties.setName({ newName: "Send Notification" });

    expect(await intermediateEventPropertiesPanel.nameProperties.getName()).toBe("Send Notification");
  });

  test("should change the Intermediate Throw Event documentation", async ({ intermediateEventPropertiesPanel }) => {
    await intermediateEventPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This event sends a notification to external systems",
    });

    expect(await intermediateEventPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This event sends a notification to external systems"
    );
  });

  test("should configure Message definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setMessageDefinition({ messageName: "NotificationMessage" });

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot(
      "intermediate-throw-event-message.png"
    );
  });

  test("should configure Signal definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setSignalDefinition({
      signalName: "BroadcastSignal",
      scope: "project",
    });

    expect(await intermediateEventPropertiesPanel.getSignalName()).toBe("BroadcastSignal");
  });

  test("should configure Link definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setLinkDefinition({ linkName: "TargetLink" });

    expect(await intermediateEventPropertiesPanel.getLinkName()).toBe("TargetLink");
  });

  test("should configure Escalation definition", async ({ intermediateEventPropertiesPanel, page }) => {
    await intermediateEventPropertiesPanel.setEscalationDefinition({
      escalationName: "ThrowEscalation",
    });

    expect(await intermediateEventPropertiesPanel.getEscalationName()).toBe("ThrowEscalation");
  });

  test("should configure Compensation definition", async ({ intermediateEventPropertiesPanel, jsonModel }) => {
    await intermediateEventPropertiesPanel.setCompensationDefinition({});

    const intermediateCatchEvent = (await jsonModel.getIntermediateCatchEvents())[0];
    expect(intermediateCatchEvent.__$$element).toBe("intermediateThrowEvent");
    expect(intermediateCatchEvent.eventDefinition?.[0].__$$element).toBe("compensateEventDefinition");
  });
});
