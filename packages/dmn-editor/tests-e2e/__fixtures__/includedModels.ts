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

export class IncludedModels {
  constructor(private readonly page: Page) {}

  public get() {
    return this.page.getByTestId("kie-tools--dmn-editor--included-models-container");
  }

  private getModal() {
    return this.page.getByTestId("kie-tools--dmn-editor--included-models-modal");
  }

  public getIncludeModelButton() {
    return this.get().getByRole("button", { name: "Include model" });
  }

  public async fillModelToInclude(args: { modelName: string }) {
    await this.getModal().getByPlaceholder("Select a model to include...").click();
    await this.getModal().getByPlaceholder("Select a model to include...").fill(args.modelName);
  }

  public async selectModel(args: { modelName: string }) {
    await this.page.getByText(args.modelName).click();
  }

  public async fillModelName(args: { modelName: string }) {
    await this.getModal().getByPlaceholder("<Default>").click();
    await this.getModal().getByPlaceholder("<Default>").fill(args.modelName);
  }

  public async includeModel() {
    return await this.page.getByRole("button", { name: "Include model" }).click();
  }
}
