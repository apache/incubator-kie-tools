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

import { Locator, Page } from "@playwright/test";
import { ConstraintType, DataType } from "../../dataTypes";

export class DataTypeProperties {
  constructor(
    public panel: Locator,
    public page: Page
  ) {}

  public async setDataType(args: { newDataType: DataType }) {
    await this.panel.getByPlaceholder("Select a data type...").click();
    await this.panel.getByPlaceholder("Select a data type...").press("ControlOrMeta+a");
    await this.panel.getByPlaceholder("Select a data type...").fill(args.newDataType);
    await this.page.getByRole("option").getByText(args.newDataType, { exact: true }).click();
  }

  public async setCustomDataType(args: { newDataType: string }) {
    await this.panel.getByPlaceholder("Select a data type...").click();
    await this.panel.getByPlaceholder("Select a data type...").press("ControlOrMeta+a");
    await this.panel.getByPlaceholder("Select a data type...").fill(args.newDataType);
    await this.page.getByRole("option").getByText(`${args.newDataType} `, { exact: false }).click();
  }

  public getDataType() {
    return this.panel.getByPlaceholder("Select a data type...");
  }

  public getConstraintSection() {
    return this.panel.getByText("Constraint");
  }

  public getNoneConstraintButton() {
    return this.panel.getByRole("button", { name: ConstraintType.NONE, exact: true });
  }

  public getEnumerationConstraintButton() {
    return this.panel.getByRole("button", { name: ConstraintType.ENUMERATION, exact: true });
  }

  public getEnumerationElementAt(element: number) {
    return this.panel.locator(".kie-dmn-editor--draggable-row").nth(element).locator("input");
  }

  public getExpressionConstraintButton() {
    return this.panel.getByRole("button", { name: ConstraintType.EXPRESSION, exact: true });
  }

  public getRangeConstraintButton() {
    return this.panel.getByRole("button", { name: ConstraintType.RANGE, exact: true });
  }
}
