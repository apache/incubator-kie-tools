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

export class ShapeProperties {
  constructor(public panel: Locator) {}

  public async setShape(args: { width: string; height: string }) {
    await this.panel.getByTitle("Expand / collapse Shape").click();

    await this.panel.getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-width-input").fill(args.width);
    await this.panel.getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-height-input").fill(args.height);

    await this.panel.getByTitle("Expand / collapse Shape").click();
  }

  public async setPosition(args: { x: string; y: string }) {
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
    await this.panel.getByPlaceholder("Enter X value...").fill(args.x);
    await this.panel.getByPlaceholder("Enter Y value...").fill(args.y);
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
  }

  public async getShape() {
    await this.panel.getByTitle("Expand / collapse Shape").click();

    const width = await this.panel
      .getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-width-input")
      .inputValue();
    const height = await this.panel
      .getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-height-input")
      .inputValue();
    const x = await this.panel
      .getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-x-input")
      .locator("input")
      .inputValue();
    const y = await this.panel
      .getByTestId("kie-tools--dmn-editor--properties-panel-node-shape-y-input")
      .locator("input")
      .inputValue();

    await this.panel.getByTitle("Expand / collapse Shape").click();

    return { width: width, height: height, x: x, y: y };
  }

  public async resetShape() {
    await this.panel.getByTitle("Reset shape").click();
  }

  public async setFillColor(args: { color: string }) {
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
    await this.panel.getByTestId("kie-tools--dmn-editor--color-picker-shape-fill").fill(args.color);
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
  }

  public async setStrokeColor(args: { color: string }) {
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
    await this.panel.getByTestId("kie-tools--dmn-editor--color-picker-shape-stroke").fill(args.color);
    await this.panel.getByRole("button", { name: "Expand / collapse Shape" }).click();
  }
}
