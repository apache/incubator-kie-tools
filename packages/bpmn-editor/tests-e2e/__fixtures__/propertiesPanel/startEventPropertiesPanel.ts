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

import { Page, Locator } from "@playwright/test";
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { Diagram } from "../diagram";
import { Nodes, EventNodeType } from "../nodes";

export class StartEventPropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page,
    public nodes: Nodes
  ) {
    super(diagram, page);
  }

  private async morphToEventType(args: { startEventLocator: Locator; eventType: EventNodeType }) {
    await this.nodes.morph({
      node: args.startEventLocator,
      to: args.eventType,
    });
  }

  public async setInterrupting(args: { isInterrupting: boolean }) {
    const checkbox = this.panel().getByRole("checkbox", { name: /interrupting/i });
    const isChecked = await checkbox.isChecked();
    if (isChecked !== args.isInterrupting) {
      await checkbox.click();
    }
  }

  public async getInterrupting(): Promise<boolean> {
    const checkbox = this.panel().getByRole("checkbox", { name: /interrupting/i });
    return await checkbox.isChecked();
  }

  public async isInterruptingVisible(): Promise<boolean> {
    const checkbox = this.panel().getByRole("checkbox", { name: /interrupting/i });
    return await checkbox.isVisible();
  }

  public async setTimerDefinition(args: {
    type: "date" | "duration" | "cycle";
    value: string;
    startEventLocator: Locator;
  }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.TIMER });

    const timerTypeMap = {
      date: { label: "Fire at a specific date", placeholder: "date value" },
      duration: { label: "Fire once after duration", placeholder: "duration" },
      cycle: { label: "Fire multiple times", placeholder: "time cycle" },
    };

    const { label, placeholder } = timerTypeMap[args.type];

    const radioButton = this.panel().getByLabel(label);
    await radioButton.click();

    const valueInput = this.panel().getByPlaceholder(new RegExp(placeholder, "i"));
    await valueInput.fill(args.value);
    await valueInput.blur();
  }

  public async getTimerDefinition(): Promise<{ type: string; value: string }> {
    const dateRadio = this.panel().getByLabel("Fire at a specific date");
    const durationRadio = this.panel().getByLabel("Fire once after duration");
    const cycleRadio = this.panel().getByLabel("Fire multiple times");

    let type = "";
    let placeholder = "";

    if (await dateRadio.isChecked()) {
      type = "date";
      placeholder = "date value";
    } else if (await durationRadio.isChecked()) {
      type = "duration";
      placeholder = "duration";
    } else if (await cycleRadio.isChecked()) {
      type = "cycle";
      placeholder = "time cycle";
    }

    const valueInput = this.panel().getByPlaceholder(new RegExp(placeholder, "i"));
    const value = (await valueInput.inputValue()) || "";

    return { type, value };
  }

  public async setMessageDefinition(args: { messageName: string; startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.MESSAGE });

    await this.fillCombobox(args.messageName);

    const createOption = this.page.getByText(`Create Message "${args.messageName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.messageName, exact: true }).click();
    }
    await this.panel().getByRole("combobox").first().blur();
  }

  public async setSignalDefinition(args: { signalName: string; startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.SIGNAL });

    await this.fillCombobox(args.signalName);

    const createOption = this.page.getByText(`Create Signal "${args.signalName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.signalName, exact: true }).click();
    }
    await this.panel().getByRole("combobox").first().blur();
  }

  public async setConditionalExpression(args: { expression: string; startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.CONDITIONAL });

    const expressionInput = this.panel().getByRole("textbox").first();
    await expressionInput.fill(args.expression);
    await expressionInput.blur();
  }

  public async getConditionalExpression(): Promise<string> {
    const expressionInput = this.panel().getByRole("textbox").first();
    return (await expressionInput.inputValue()) || "";
  }

  public async setErrorDefinition(args: { errorName: string; startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.ERROR });

    await this.fillCombobox(args.errorName);

    const createOption = this.page.getByText(`Create Error "${args.errorName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.errorName, exact: true }).click();
    }
    await this.panel().getByRole("combobox").first().blur();
  }

  public async getErrorName(): Promise<string> {
    const errorInput = this.panel().getByRole("combobox").first();
    return (await errorInput.inputValue()) || "";
  }

  public async setEscalationDefinition(args: { escalationName: string; startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.ESCALATION });

    await this.fillCombobox(args.escalationName);

    const createOption = this.page.getByText(`Create Escalation "${args.escalationName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.escalationName, exact: true }).click();
    }
    await this.panel().getByRole("combobox").first().blur();
  }

  public async getEscalationName(): Promise<string> {
    const escalationInput = this.panel().getByRole("combobox").first();
    return (await escalationInput.inputValue()) || "";
  }

  public async setCompensationDefinition(args: { startEventLocator: Locator }) {
    await this.morphToEventType({ startEventLocator: args.startEventLocator, eventType: EventNodeType.COMPENSATION });
  }

  public async isCompensationDefinitionSet(): Promise<boolean> {
    const panel = this.panel();
    const panelCount = await panel.count();
    return panelCount > 0 ? await panel.isVisible() : false;
  }
}
