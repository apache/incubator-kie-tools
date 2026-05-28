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

import { expect, Page } from "@playwright/test";
import { Nodes } from "./nodes";

export class Editor {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {}

  public async open() {
    await this.page.goto(`${this.baseURL}/iframe.html?args=&id=misc-empty--empty&viewMode=story`, {});
    await this.initializeEditor();
  }

  public async openWithLocale(locale: string) {
    await this.page.goto(`${this.baseURL}/iframe.html?args=locale:${locale}&id=misc-empty--empty&viewMode=story`, {});
    await this.initializeEditor();
  }

  private async initializeEditor() {
    await expect(this.page.getByTestId("kie-bpmn-editor--diagram-container")).toBeVisible();

    const processIdInput = this.page.getByPlaceholder("e.g., hiring");
    const inputCount = await processIdInput.count();
    if (inputCount > 0 && (await processIdInput.isVisible())) {
      await processIdInput.fill("test");
      await this.page.getByRole("button", { name: "Start Modeling" }).click();
    }
  }

  public async openCustomTasks({ nodes }: { nodes: Nodes }) {
    await this.page.goto(`${this.baseURL}/iframe.html?args=&id=features-customtasks--custom-tasks&viewMode=story`);
    await nodes.delete({ name: "Rest API call Task" });
    await nodes.delete({ name: "gRPC API call Task" });
  }
}
