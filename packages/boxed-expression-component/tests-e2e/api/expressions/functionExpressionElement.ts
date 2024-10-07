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

import { Locator } from "@playwright/test";
import { Monaco } from "../../__fixtures__/monaco";
import { ChildExpression } from "../expressionContainer";
import { FunctionTypeCell } from "../functionType";

export class FunctionExpressionElement {
  constructor(
    private locator: Locator,
    private monaco: Monaco
  ) {}

  public async addEntry() {
    await this.locator.getByRole("cell", { name: "1" }).nth(0).hover();
    await this.locator.getByRole("cell", { name: "1" }).nth(0).locator("svg").click();
  }

  public async addParameter(args: { name?: string; dataType?: string; closeAs?: "Escape" | "Click" }) {
    // open parameters popover
    await this.parameters.click();
    await this.locator.page().getByRole("button", { name: "Add parameter" }).click();
    if (args.name) {
      await this.locator.page().getByPlaceholder("Parameter Name").last().fill(args.name);

      // WORKAROUND_DUE_TO "https://github.com/apache/incubator-kie-issues/issues/540",
      await this.locator.page().keyboard.press("Tab");
    }
    if (args.dataType) {
      await this.locator.page().getByLabel("<Undefined>").last().click();
      await this.locator.page().getByRole("option", { name: args.dataType }).click();
    }

    // close parameters popover
    if (args.closeAs === "Escape") {
      await this.locator.page().keyboard.press("Escape");
    } else {
      await this.parameters.click();
    }
  }

  public async deleteParameter(args: { nth: number }) {
    // open parameters popover
    await this.parameters.click();

    // delete parameter
    await this.locator.page().getByRole("button", { name: "", exact: true }).nth(args.nth).click();

    // close parameters popover
    await this.parameters.click();
  }

  public entry(index: number) {
    return new ChildExpression(this.locator.getByTestId(`kie-tools--bee--expression-row-${index}`).nth(0), this.monaco);
  }

  get functionType() {
    return new FunctionTypeCell(this.locator.getByTestId("kie-tools--bee--selected-function-kind"));
  }

  get parameters() {
    return this.locator.getByTestId("kie-tools--bee--parameters-list");
  }
}
