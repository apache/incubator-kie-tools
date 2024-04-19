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

export class FontProperties {
  constructor(public panel: Locator) {}

  public async setFont(args: {
    fontSize?: string;
    bold?: boolean;
    italic?: boolean;
    underline?: boolean;
    striketrough?: boolean;
    color?: string;
    fontFamily?: string;
  }) {
    if (!(await this.panel.getByLabel("Font size").locator("input").isVisible())) {
      await this.panel.getByTitle("Expand / collapse Font").click();
    }

    if (args.fontSize) {
      await this.panel.getByLabel("Font size").locator("input").fill(args.fontSize);
    }
    if (args.bold) {
      await this.panel.getByLabel("Toggle font bold").click();
    }
    if (args.italic) {
      await this.panel.getByLabel("Toggle font italic").click();
    }
    if (args.underline) {
      await this.panel.getByLabel("Toggle font underline").click();
    }
    if (args.striketrough) {
      await this.panel.getByLabel("Toggle font strike through").click();
    }
    if (args.color) {
      await this.panel.getByTestId("kie-tools--dmn-editor--color-picker-font").fill(args.color);
    }
    if (args.fontFamily) {
      await this.panel.getByTestId("kie-tools--dmn-editor--properties-panel-node-font-style").click(); //open
      await this.panel.getByText(args.fontFamily).click();
      await this.panel.getByTestId("kie-tools--dmn-editor--properties-panel-node-font-style").click(); //close
    }
  }

  public async resetFont() {
    await this.panel.getByTitle("Reset font").click();
  }

  public async getFont() {
    await this.panel.getByTitle("Expand / collapse Font").click();

    const font = await this.panel.getByTestId("kie-tools--dmn-editor--properties-panel-node-font-style").textContent();

    await this.panel.getByTitle("Expand / collapse Font").click();

    return font;
  }
}
