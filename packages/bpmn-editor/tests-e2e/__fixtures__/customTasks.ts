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
import { Nodes, NodeType } from "./nodes";

export class CustomTasks {
  constructor(
    public page: Page,
    public diagram: Diagram,
    public nodes: Nodes
  ) {}

  public async openPalette() {
    const customTasksButton = this.page.getByTitle("Custom Tasks");
    await customTasksButton.click();
  }

  public async closePalette() {
    const customTasksButton = this.page.getByTitle("Custom Tasks");
    await customTasksButton.click();
  }

  public async dragCustomTask(args: {
    customTaskId: string;
    targetPosition: { x: number; y: number };
    thenRenameTo?: string;
  }) {
    const popover = this.page.getByTestId("kie-tools--bpmn-editor--custom-tasks-popover");
    const popoverCount = await popover.count();
    const isVisible = popoverCount > 0 ? await popover.isVisible() : false;

    if (!isVisible) {
      await this.openPalette();
    }

    const customTaskElement = popover.getByTestId(
      `kie-tools--bpmn-editor--custom-task-palette-item-${args.customTaskId}`
    );

    await customTaskElement.dragTo(this.diagram.get(), { targetPosition: args.targetPosition });

    if (args.thenRenameTo) {
      const newNode = this.nodes.getByType(NodeType.TASK).last();
      await this.nodes.renameByLocator({
        nodeLocator: newNode,
        newName: args.thenRenameTo,
        needsSelection: false,
      });
    }
  }

  public async getAvailableCustomTasks(): Promise<string[]> {
    await this.openPalette();

    const popover = this.page.getByTestId("kie-tools--bpmn-editor--custom-tasks-popover");
    const customTaskElements = popover.getByRole("button");
    const count = await customTaskElements.count();

    const taskNames: string[] = [];
    for (let i = 0; i < count; i++) {
      const text = await customTaskElements.nth(i).textContent();
      if (text) {
        const cleanedText = text.replace(/[^\w\s-]/g, "").trim();
        taskNames.push(cleanedText);
      }
    }

    return taskNames;
  }

  public async isCustomTaskAvailable(customTaskName: string): Promise<boolean> {
    const availableTasks = await this.getAvailableCustomTasks();
    return availableTasks.includes(customTaskName);
  }
}
