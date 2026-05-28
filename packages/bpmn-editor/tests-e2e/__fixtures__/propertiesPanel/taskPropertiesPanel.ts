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

import { Page } from "@playwright/test";
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { Diagram } from "../diagram";

export class TaskPropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
  }

  public async setActors(args: { actors: string }) {
    const actorsInput = this.panel().getByPlaceholder(/Enter actors/i);
    await actorsInput.fill(args.actors);
    await this.page.keyboard.press("Enter");
  }

  public async getActors(): Promise<string> {
    const actorsInput = this.panel().getByPlaceholder(/Enter actors/i);
    return (await actorsInput.inputValue()) || "";
  }

  public async setGroups(args: { groups: string }) {
    const groupsInput = this.panel().getByPlaceholder(/Enter groups/i);
    await groupsInput.fill(args.groups);
    await this.page.keyboard.press("Enter");
  }

  public async getGroups(): Promise<string> {
    const groupsInput = this.panel().getByPlaceholder(/Enter groups/i);
    return (await groupsInput.inputValue()) || "";
  }

  public async setTaskName(args: { taskName: string }) {
    const taskNameInput = this.panel().getByPlaceholder(/Enter task name/i);
    await taskNameInput.fill(args.taskName);
    await this.page.keyboard.press("Enter");
  }

  public async getTaskName(): Promise<string> {
    const taskNameInput = this.panel().getByPlaceholder(/Enter task name/i);
    return (await taskNameInput.inputValue()) || "";
  }

  public async setImplementation(args: { implementation: string }) {
    const implButton = this.panel().getByRole("button", { name: args.implementation, exact: true });
    await implButton.click();
  }

  public async getImplementation(): Promise<string> {
    const implButtons = this.panel()
      .getByRole("button")
      .filter({ hasText: /Java|WebService|DMN/ });
    const count = await implButtons.count();
    for (let i = 0; i < count; i++) {
      const button = implButtons.nth(i);
      const isPressed = await button.getAttribute("aria-pressed");
      if (isPressed === "true") {
        return (await button.textContent()) || "";
      }
    }
    return "";
  }

  public async setInterface(args: { interfaceName: string }) {
    const interfaceInput = this.panel().getByPlaceholder(/Enter an interface/i);
    await interfaceInput.fill(args.interfaceName);
    await this.page.keyboard.press("Enter");
  }

  public async setOperation(args: { operationName: string }) {
    const operationInput = this.panel().getByPlaceholder(/Enter an operation/i);
    await operationInput.fill(args.operationName);
    await this.page.keyboard.press("Enter");
  }

  public async setScript(args: { script: string }) {
    const scriptTextarea = this.panel().getByPlaceholder(/Enter code/i);
    await scriptTextarea.fill(args.script);
    await scriptTextarea.blur();
  }

  public async setRuleFlowGroup(args: { ruleFlowGroup: string }) {
    const drlButton = this.panel().getByRole("button", { name: "DRL", exact: true });
    await drlButton.click();

    const ruleFlowInput = this.panel().getByPlaceholder(/Enter a Rule flow group/i);
    await ruleFlowInput.fill(args.ruleFlowGroup);
    await ruleFlowInput.blur();
  }

  public async setDmnModel(args: { relativePath: string; namespace: string; modelName: string }) {
    const dmnButton = this.panel().getByRole("button", { name: "DMN", exact: true });
    await dmnButton.click();

    const relativePathInput = this.panel().getByPlaceholder(/Enter a relative path/i);
    await relativePathInput.fill(args.relativePath);
    await relativePathInput.blur();

    const namespaceInput = this.panel().getByLabel("DMN model namespace");
    await namespaceInput.fill(args.namespace);
    await namespaceInput.blur();

    const modelNameInput = this.panel().getByLabel("DMN model name", { exact: true });
    await modelNameInput.fill(args.modelName);
    await modelNameInput.blur();
  }

  public async setMultiInstance(args: { type: "parallel" | "sequential" }) {
    const multiInstanceCheckbox = this.panel().getByRole("checkbox", { name: /multi-instance/i });
    const isChecked = await multiInstanceCheckbox.isChecked();
    if (!isChecked) {
      await multiInstanceCheckbox.click();
    }

    const executionModeButton = this.panel().getByRole("button", {
      name: args.type === "parallel" ? "Parallel" : "Sequential",
      exact: true,
    });
    await executionModeButton.click();
  }

  public async setCollectionExpression(args: { expression: string }) {
    const collectionInput = this.panel().getByRole("combobox").first();
    await collectionInput.click();
    await this.page.keyboard.type(args.expression);
    await this.page.keyboard.press("Enter");
  }

  public async setCompletionCondition(args: { condition: string }) {
    const conditionTextarea = this.panel().getByLabel("Completion condition");
    await conditionTextarea.fill(args.condition);
    await conditionTextarea.blur();
  }

  public async openDataMappingModal() {
    const dataMappingSection = this.panel().getByLabel(/⇆Data mapping/i);
    const dataMappingButton = dataMappingSection.getByRole("button", { name: "Manage" });
    await dataMappingButton.click();
  }

  public async closeDataMappingModal() {
    const saveButton = this.page.getByRole("button", { name: /save/i });
    await saveButton.click();
  }

  public async addDataInputInModal(args: { name: string }) {
    const addInputButton = this.page.getByRole("button", { name: /Add Input data mapping/i });
    const buttonCount = await addInputButton.count();
    const isVisible = buttonCount > 0 ? await addInputButton.isVisible() : false;

    if (isVisible) {
      await addInputButton.click();
    } else {
      const inputsSection = this.page.getByText("Inputs", { exact: true });
      const plusButton = inputsSection
        .locator("..")
        .getByRole("button", { name: "" })
        .filter({ has: this.page.getByRole("img", { includeHidden: true }) })
        .first();
      await plusButton.click();
    }

    const nameInput = this.page.getByPlaceholder(/name/i).last();
    await nameInput.fill(args.name);
  }

  public async addDataOutputInModal(args: { name: string }) {
    const addOutputButton = this.page.getByRole("button", { name: /Add Output data mapping/i });
    const buttonCount = await addOutputButton.count();
    const isVisible = buttonCount > 0 ? await addOutputButton.isVisible() : false;

    if (isVisible) {
      await addOutputButton.click();
    } else {
      const outputsSection = this.page.getByText("Outputs", { exact: true });
      const plusButton = outputsSection
        .locator("..")
        .getByRole("button", { name: "" })
        .filter({ has: this.page.getByRole("img", { includeHidden: true }) })
        .first();
      await plusButton.click();
    }

    const nameInput = this.page.getByPlaceholder(/name/i).last();
    await nameInput.fill(args.name);
  }

  public async addDataInput(args: { name: string }) {
    await this.openDataMappingModal();
    await this.addDataInputInModal(args);
    await this.closeDataMappingModal();
  }

  public async addDataOutput(args: { name: string }) {
    await this.openDataMappingModal();
    await this.addDataOutputInModal(args);
    await this.closeDataMappingModal();
  }

  public async setAsync(args: { isAsync: boolean }) {
    const asyncCheckbox = this.panel().getByRole("checkbox", { name: /async/i });
    const isChecked = await asyncCheckbox.isChecked();

    if (isChecked !== args.isAsync) {
      await asyncCheckbox.click();
    }
  }
}
