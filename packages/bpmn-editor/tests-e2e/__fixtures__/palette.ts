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
import { Diagram } from "./diagram";
import { DefaultNodeName, Nodes, NodeType } from "./nodes";

export class Palette {
  constructor(
    public page: Page,
    public diagram: Diagram,
    public nodes: Nodes
  ) {}

  public async dragNewNode(args: { type: NodeType; targetPosition: { x: number; y: number }; thenRenameTo?: string }) {
    const { title, nodeName } = this.getNewNodeProperties(args.type);

    await this.page.getByTitle(title).first().dragTo(this.diagram.get(), { targetPosition: args.targetPosition });

    if (args.thenRenameTo && nodeName) {
      await this.nodes.rename({ current: nodeName, new: args.thenRenameTo });
    }
  }

  public async openProcessVariables() {
    const variablesPanel = this.page.getByTestId("kie-tools--bpmn-editor--variables-popover");
    const panelCount = await variablesPanel.count();
    const isPanelVisible = panelCount > 0 ? await variablesPanel.isVisible() : false;

    if (isPanelVisible) {
      return;
    }

    const toggle = this.page.getByTitle("Process Variables");
    await toggle.click();
  }

  public async closeProcessVariables() {
    const variablesPanel = this.page.getByTestId("kie-tools--bpmn-editor--variables-popover");
    const panelCount = await variablesPanel.count();
    const isPanelVisible = panelCount > 0 ? await variablesPanel.isVisible() : false;

    if (!isPanelVisible) {
      return;
    }

    const toggle = this.page.getByTitle("Process Variables");
    await toggle.click();
  }

  public async addProcessVariable(args: { name: string; dataType?: string }) {
    await this.openProcessVariables();

    const variablesPanel = this.page.getByTestId("kie-tools--bpmn-editor--variables-popover");
    const emptyStateAddButton = this.page.getByRole("button", { name: "Add Variable", exact: true });
    const buttonCount = await emptyStateAddButton.count();
    const isEmptyState = buttonCount > 0 ? await emptyStateAddButton.isVisible() : false;

    if (isEmptyState) {
      await emptyStateAddButton.click();
    } else {
      const addButton = variablesPanel
        .getByRole("button")
        .filter({ has: this.page.getByRole("img", { includeHidden: true }) });
      await addButton.first().click();
    }

    const variableNameInput = variablesPanel.getByPlaceholder("Name...").last();
    await variableNameInput.fill(args.name);

    if (args.dataType) {
      const variableEntries = variablesPanel.getByTestId("kie-tools--bpmn-editor--variable-entry");
      const lastEntry = variableEntries.last();
      const dataTypeInput = lastEntry.getByRole("combobox").first();

      await dataTypeInput.click();
      await this.page.keyboard.type(args.dataType);

      const dataTypeOption = this.page.getByRole("option", { name: args.dataType, exact: true });
      await dataTypeOption.click();

      const selectedValue = await dataTypeInput.inputValue();
      if (!selectedValue || selectedValue === "<Undefined>") {
        await dataTypeOption.click();
      }

      return;
    }

    await variableNameInput.press("Tab");
  }

  private getNewNodeProperties(type: NodeType) {
    switch (type) {
      case NodeType.START_EVENT:
        return { title: "Start Events", nodeName: DefaultNodeName.START_EVENT };
      case NodeType.INTERMEDIATE_CATCH_EVENT:
        return { title: "Intermediate Catch Events", nodeName: DefaultNodeName.INTERMEDIATE_CATCH_EVENT };
      case NodeType.INTERMEDIATE_THROW_EVENT:
        return { title: "Intermediate Throw Events", nodeName: DefaultNodeName.INTERMEDIATE_THROW_EVENT };
      case NodeType.END_EVENT:
        return { title: "End Events", nodeName: DefaultNodeName.END_EVENT };
      case NodeType.TASK:
        return { title: "Tasks", nodeName: DefaultNodeName.TASK };
      case NodeType.CALL_ACTIVITY:
        return { title: "Call Activity", nodeName: DefaultNodeName.CALL_ACTIVITY };
      case NodeType.SUB_PROCESS:
        return { title: "Sub-processes", nodeName: DefaultNodeName.SUB_PROCESS };
      case NodeType.GATEWAY:
        return { title: "Gateways", nodeName: DefaultNodeName.GATEWAY };
      case NodeType.DATA_OBJECT:
        return { title: "Data Object", nodeName: DefaultNodeName.DATA_OBJECT };
      case NodeType.TEXT_ANNOTATION:
        return { title: "Text Annotation", nodeName: DefaultNodeName.TEXT_ANNOTATION };
      case NodeType.GROUP:
        return { title: "Group", nodeName: DefaultNodeName.GROUP };
      case NodeType.LANE:
        return { title: "Lanes", nodeName: DefaultNodeName.LANE };
    }
  }
}
