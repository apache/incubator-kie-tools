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
import { Nodes } from "../nodes";

export class EndEventPropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page,
    public nodes: Nodes
  ) {
    super(diagram, page);
  }

  private async morphToEventType(args: { endEventLocator: Locator; eventType: string }) {
    await this.nodes.morphNode({
      nodeLocator: args.endEventLocator,
      targetMorphType: args.eventType,
    });
  }

  public async setTerminateDefinition(args: { endEventLocator: Locator }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Terminate" });
  }

  public async setMessageDefinition(args: { messageName: string; endEventLocator: Locator }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Message" });

    await this.panel().getByRole("combobox").first().click();
    await this.page.keyboard.type(args.messageName);

    const createOption = this.page.getByText(`Create Message "${args.messageName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.messageName, exact: true }).click();
    }
  }

  public async setSignalDefinition(args: { signalName: string; endEventLocator: Locator }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Signal" });

    await this.panel().getByRole("combobox").first().click();
    await this.page.keyboard.type(args.signalName);

    const createOption = this.page.getByText(`Create Signal "${args.signalName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.signalName, exact: true }).click();
    }
  }

  public async getSignalName(): Promise<string> {
    const signalInput = this.panel().getByRole("combobox").first();
    return (await signalInput.inputValue()) || "";
  }

  public async setErrorDefinition(args: { errorName: string; errorCode?: string; endEventLocator: Locator }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Error" });

    await this.panel().getByRole("combobox").first().click();
    await this.page.keyboard.type(args.errorName);

    const createOption = this.page.getByText(`Create Error "${args.errorName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.errorName, exact: true }).click();
    }

    if (args.errorCode) {
      const errorCodeInput = this.panel().getByPlaceholder("Error code");
      const inputCount = await errorCodeInput.count();
      if (inputCount > 0 && (await errorCodeInput.isVisible())) {
        await errorCodeInput.fill(args.errorCode);
        await errorCodeInput.blur();
      }
    }
  }

  public async getErrorName(): Promise<string> {
    const errorInput = this.panel().getByRole("combobox").first();
    return (await errorInput.inputValue()) || "";
  }

  public async getErrorCode(): Promise<string> {
    const errorCodeInput = this.panel().getByPlaceholder("Error code");
    const inputCount = await errorCodeInput.count();
    if (inputCount === 0 || !(await errorCodeInput.isVisible())) return "";
    return (await errorCodeInput.inputValue()) || "";
  }

  public async setEscalationDefinition(args: {
    escalationName: string;
    escalationCode?: string;
    endEventLocator: Locator;
  }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Escalation" });

    await this.panel().getByRole("combobox").first().click();
    await this.page.keyboard.type(args.escalationName);

    const createOption = this.page.getByText(`Create Escalation "${args.escalationName}"`, { exact: true });
    const optionCount = await createOption.count();
    if (optionCount > 0 && (await createOption.isVisible())) {
      await createOption.click();
    } else {
      await this.page.getByRole("option", { name: args.escalationName, exact: true }).click();
    }

    if (args.escalationCode) {
      const escalationCodeInput = this.panel().getByPlaceholder("Escalation code");
      const inputCount = await escalationCodeInput.count();
      if (inputCount > 0 && (await escalationCodeInput.isVisible())) {
        await escalationCodeInput.fill(args.escalationCode);
        await escalationCodeInput.blur();
      }
    }
  }

  public async getEscalationName(): Promise<string> {
    const escalationInput = this.panel().getByRole("combobox").first();
    return (await escalationInput.inputValue()) || "";
  }

  public async getEscalationCode(): Promise<string> {
    const escalationCodeInput = this.panel().getByPlaceholder("Escalation code");
    const inputCount = await escalationCodeInput.count();
    if (inputCount === 0 || !(await escalationCodeInput.isVisible())) return "";
    return (await escalationCodeInput.inputValue()) || "";
  }

  public async setCompensationDefinition(args: { endEventLocator: Locator }) {
    await this.morphToEventType({ endEventLocator: args.endEventLocator, eventType: "Compensation" });
  }

  public async isCompensationDefinitionSet(): Promise<boolean> {
    const compensationSection = this.panel().getByText("Compensation");
    const sectionCount = await compensationSection.count();
    return sectionCount > 0 ? await compensationSection.isVisible() : false;
  }
}
