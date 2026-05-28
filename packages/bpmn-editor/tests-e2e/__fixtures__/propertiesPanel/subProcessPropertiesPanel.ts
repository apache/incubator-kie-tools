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

export class SubProcessPropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
  }

  public async setMultiInstance(args: { type: "parallel" | "sequential" }) {
    const executionModeButton = this.panel().getByRole("button", {
      name: args.type === "parallel" ? "Parallel" : "Sequential",
      exact: true,
    });
    await executionModeButton.click();
  }

  public async setCollectionExpression(args: { expression: string }) {
    const collectionInput = this.panel()
      .getByRole("group")
      .filter({ hasText: "Collection input" })
      .getByRole("combobox")
      .first();
    await collectionInput.click();
    await this.page.keyboard.type(args.expression);
    await this.page.keyboard.press("Enter");
  }

  public async setCompletionCondition(args: { condition: string }) {
    const conditionTextarea = this.panel().getByLabel("Completion condition");
    await conditionTextarea.fill(args.condition);
    await conditionTextarea.blur();
  }

  public async setAdHocOrdering(args: { ordering: "Parallel" | "Sequential" }) {
    const orderingSelect = this.panel()
      .getByRole("group")
      .filter({ hasText: "Ad-hoc ordering" })
      .getByRole("combobox")
      .first();
    await orderingSelect.selectOption(args.ordering);
  }

  public async setAdHocCompletionCondition(args: { condition: string }) {
    const conditionTextarea = this.panel()
      .getByRole("textbox", { name: /ad-hoc completion condition/i })
      .first();
    await conditionTextarea.fill(args.condition);
    await conditionTextarea.blur();
  }
}
