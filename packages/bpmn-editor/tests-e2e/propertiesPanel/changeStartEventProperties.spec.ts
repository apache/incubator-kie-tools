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

test.beforeEach(async ({ editor, page }) => {
  await editor.open();
  await editor.setInitialProcessId();
  await page.setViewportSize({ width: 1920, height: 1080 });
});

test.describe("Change Properties - Start Event", () => {
  test.beforeEach(async ({ palette, nodes }) => {
    await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });

    await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
  });

  test("should change the Start Event name", async ({ startEventPropertiesPanel, page }) => {
    await startEventPropertiesPanel.nameProperties.setName({ newName: "Process Started" });

    expect(await startEventPropertiesPanel.nameProperties.getName()).toBe("Process Started");
    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("start-event-name-changed.png");
  });

  test("should change the Start Event documentation", async ({ startEventPropertiesPanel }) => {
    await startEventPropertiesPanel.documentationProperties.setDocumentation({
      newDocumentation: "This event starts the process",
    });

    expect(await startEventPropertiesPanel.documentationProperties.getDocumentation()).toBe(
      "This event starts the process"
    );
  });

  test("should configure Timer event definition with date", async ({ startEventPropertiesPanel, nodes, jsonModel }) => {
    await startEventPropertiesPanel.setTimerDefinition({
      type: "date",
      value: "2025-12-31T23:59:59",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    const timerDef = await startEventPropertiesPanel.getTimerDefinition();
    expect(timerDef.type).toBe("date");
    expect(timerDef.value).toBe("2025-12-31T23:59:59");

    const startEvent = (await jsonModel.getStartEvents())[0];
    const eventDefinition = startEvent.eventDefinition?.[0];
    expect(eventDefinition?.__$$element).toBe("timerEventDefinition");

    // Narrow the eventDefinition union so the timerEventDefinition-only fields are reachable.
    const timerEventDefinition = eventDefinition?.__$$element === "timerEventDefinition" ? eventDefinition : undefined;
    expect(timerEventDefinition?.timeDate?.__$$text).toBe("2025-12-31T23:59:59");
  });

  test("should configure Timer event definition with duration", async ({ startEventPropertiesPanel, page, nodes }) => {
    await startEventPropertiesPanel.setTimerDefinition({
      type: "duration",
      value: "PT5M",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    const timerDef = await startEventPropertiesPanel.getTimerDefinition();
    expect(timerDef.type).toBe("duration");
    expect(timerDef.value).toBe("PT5M");
  });

  test("should configure Timer event definition with cycle", async ({ startEventPropertiesPanel, page, nodes }) => {
    await startEventPropertiesPanel.setTimerDefinition({
      type: "cycle",
      value: "R3/PT10M",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    const timerDef = await startEventPropertiesPanel.getTimerDefinition();
    expect(timerDef.type).toBe("cycle");
    expect(timerDef.value).toBe("R3/PT10M");
  });

  test("should configure Message event definition", async ({ startEventPropertiesPanel, nodes, jsonModel }) => {
    await startEventPropertiesPanel.setMessageDefinition({
      messageName: "StartMessage",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    expect(await startEventPropertiesPanel.panel().getByRole("combobox").first().inputValue()).toBe("StartMessage");

    // The named message reaches the model a moment after an auto-named placeholder, so resolving
    // the ref to its name has to be retried rather than read once.
    await expect(async () => {
      const startEvent = (await jsonModel.getStartEvents())[0];
      const eventDefinition = startEvent.eventDefinition?.[0];
      expect(eventDefinition?.__$$element).toBe("messageEventDefinition");

      // Narrow the eventDefinition union so the messageEventDefinition-only fields are reachable.
      const messageEventDefinition =
        eventDefinition?.__$$element === "messageEventDefinition" ? eventDefinition : undefined;
      const messages = (await jsonModel.getDefinitions())?.rootElement?.filter(
        (rootElement) => rootElement.__$$element === "message"
      );
      expect(
        messages?.find((message) => message["@_id"] === messageEventDefinition?.["@_messageRef"])?.["@_name"]
      ).toBe("StartMessage");
    }).toPass();
  });

  test("should configure Signal event definition", async ({ startEventPropertiesPanel, nodes, jsonModel }) => {
    await startEventPropertiesPanel.setSignalDefinition({
      signalName: "StartSignal",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    expect(await startEventPropertiesPanel.panel().getByRole("combobox").first().inputValue()).toBe("StartSignal");

    // The named signal reaches the model a moment after an auto-named placeholder, so resolving
    // the ref to its name has to be retried rather than read once.
    await expect(async () => {
      const startEvent = (await jsonModel.getStartEvents())[0];
      const eventDefinition = startEvent.eventDefinition?.[0];
      expect(eventDefinition?.__$$element).toBe("signalEventDefinition");

      // Narrow the eventDefinition union so the signalEventDefinition-only fields are reachable.
      const signalEventDefinition =
        eventDefinition?.__$$element === "signalEventDefinition" ? eventDefinition : undefined;
      const signals = (await jsonModel.getDefinitions())?.rootElement?.filter(
        (rootElement) => rootElement.__$$element === "signal"
      );
      expect(signals?.find((signal) => signal["@_id"] === signalEventDefinition?.["@_signalRef"])?.["@_name"]).toBe(
        "StartSignal"
      );
    }).toPass();
  });

  test("should configure Conditional event definition", async ({ startEventPropertiesPanel, page, nodes }) => {
    await startEventPropertiesPanel.setConditionalExpression({
      expression: "${amount > 1000}",
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    expect(await startEventPropertiesPanel.getConditionalExpression()).toBe("${amount > 1000}");
  });

  test("should configure Compensation event definition", async ({ startEventPropertiesPanel, nodes }) => {
    await startEventPropertiesPanel.setCompensationDefinition({
      startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
    });

    expect(await startEventPropertiesPanel.isCompensationDefinitionSet()).toBe(true);
  });
});

test.describe("Change Properties - Start Event in Event Sub-Process", () => {
  test.beforeEach(async ({ palette, nodes, page, startEventPropertiesPanel }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.EVENT });

    const box = await nodes.getNodeBounds({ name: DefaultNodeName.SUB_PROCESS });

    const targetPosition = {
      x: box.x + box.width / 2 - 50,
      y: box.y + box.height / 2 + 50,
    };

    await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition });

    await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
    await nodes.getByType(NodeType.START_EVENT).click();

    await startEventPropertiesPanel.setMessageDefinition({
      messageName: "StartMessage",
      startEventLocator: nodes.getByType(NodeType.START_EVENT),
    });

    await nodes.getByType(NodeType.START_EVENT).click();
  });

  test("should display interrupting checkbox for Start Event in Event Sub-Process", async ({
    startEventPropertiesPanel,
  }) => {
    expect(await startEventPropertiesPanel.isInterruptingVisible()).toBe(true);
  });

  test("should toggle interrupting flag for Start Event in Event Sub-Process", async ({
    startEventPropertiesPanel,
    page,
  }) => {
    expect(await startEventPropertiesPanel.getInterrupting()).toBe(true);

    await startEventPropertiesPanel.setInterrupting({ isInterrupting: false });

    expect(await startEventPropertiesPanel.getInterrupting()).toBe(false);

    await expect(page.getByTestId("kie-tools--bpmn-editor--root")).toHaveScreenshot("start-event-non-interrupting.png");
  });
});

test.describe("Change Properties - Error/Escalation Start Events in Event Sub-Process", () => {
  test("should configure Error event definition in Event Sub-Process", async ({
    startEventPropertiesPanel,
    palette,
    nodes,
    page,
  }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.EVENT });

    const center = await nodes.getNodeCenterPosition({ name: DefaultNodeName.SUB_PROCESS });

    await palette.dragNewNode({
      type: NodeType.START_EVENT,
      targetPosition: { x: center.x - 50, y: center.y + 50 },
    });

    await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
    await nodes.getByType(NodeType.START_EVENT).click();

    await startEventPropertiesPanel.setErrorDefinition({
      errorName: "StartError",
      startEventLocator: nodes.getByType(NodeType.START_EVENT),
    });

    expect(await startEventPropertiesPanel.getErrorName()).toBe("StartError");
  });

  test("should configure Escalation event definition in Event Sub-Process", async ({
    startEventPropertiesPanel,
    palette,
    nodes,
    page,
  }) => {
    await palette.dragNewNode({ type: NodeType.SUB_PROCESS, targetPosition: { x: 200, y: 200 } });

    await expect(nodes.get({ name: DefaultNodeName.SUB_PROCESS })).toBeAttached();

    await nodes.morph({ node: nodes.get({ name: DefaultNodeName.SUB_PROCESS }), to: SubProcessNodeType.EVENT });

    const center = await nodes.getNodeCenterPosition({ name: DefaultNodeName.SUB_PROCESS });

    await palette.dragNewNode({
      type: NodeType.START_EVENT,
      targetPosition: { x: center.x - 50, y: center.y + 50 },
    });

    await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();
    await nodes.getByType(NodeType.START_EVENT).click();

    await startEventPropertiesPanel.setEscalationDefinition({
      escalationName: "StartEscalation",
      startEventLocator: nodes.getByType(NodeType.START_EVENT),
    });

    expect(await startEventPropertiesPanel.getEscalationName()).toBe("StartEscalation");
  });

  test.describe("Message Event - ItemDefinition Validation", () => {
    test("should create itemDefinition when message start event is configured", async ({
      palette,
      nodes,
      startEventPropertiesPanel,
      jsonModel,
    }) => {
      await palette.dragNewNode({ type: NodeType.START_EVENT, targetPosition: { x: 100, y: 100 } });
      await expect(nodes.getByType(NodeType.START_EVENT)).toBeVisible();

      await startEventPropertiesPanel.setMessageDefinition({
        messageName: "TestMessage",
        startEventLocator: nodes.getByType(NodeType.START_EVENT).first(),
      });

      // Creating the message lands in the model in two steps: an auto-named placeholder appears
      // immediately and the named message follows ~300ms later. Reading once races that second
      // step — the Chromium engines usually win the race, WebKit usually doesn't.
      await expect(async () => {
        const definitions = await jsonModel.getDefinitions();

        expect(definitions?.rootElement).toEqual(
          expect.arrayContaining([
            expect.objectContaining({
              __$$element: "message",
              "@_name": "TestMessage",
              "@_itemRef": "__messageItemDefinition",
            }),
            expect.objectContaining({
              __$$element: "itemDefinition",
              "@_id": "__messageItemDefinition",
            }),
          ])
        );
      }).toPass();
    });
  });
});
