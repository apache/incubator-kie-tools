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
import { Diagram } from "../diagram";
import { NodePosition, Nodes } from "../nodes";
import { DataType } from "../jsonModel";

export abstract class PropertiesPanelBase {
  constructor(public diagram: Diagram, public nodes: Nodes, public page: Page) {}

  public panel() {
    return this.page.getByTestId("properties-panel-container");
  }

  public async selectNodeByClickToAppropriatePosition(args: { nodeName: string; position?: NodePosition }) {
    await this.nodes.select({ name: args.nodeName, position: args.position ?? NodePosition.CENTER });
  }

  public async open() {
    await this.page.getByTitle("Properties panel").click();
  }

  public async setName(args: { from: string; to: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.from });
    await this.panel().getByPlaceholder("Enter a name...").fill(args.to);
    await this.page.keyboard.press("Enter");
  }

  public async getName(args: { nodeName: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    return await this.panel().getByPlaceholder("Enter a name...").inputValue();
  }

  public async setDataType(args: { nodeName: string; newDataType: DataType }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Select a data type...").click();
    await this.page.getByRole("option").getByText(args.newDataType, { exact: true }).click();
  }

  public async setDescription(args: { nodeName: string; newDescription: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Enter a description...").fill(args.newDescription);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeDescription(args: { nodeName: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    return await this.panel().getByPlaceholder("Enter a description...").inputValue();
  }

  public async addDocumentationLink(args: { nodeName: string; linkText: string; linkHref: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByTitle("Add documentation link").click();
    await this.panel()
      .locator(".kie-dmn-editor--documentation-link--row")
      .getByPlaceholder("Enter a title...")
      .fill(args.linkText);
    await this.panel()
      .locator(".kie-dmn-editor--documentation-link--row")
      .getByPlaceholder("http://")
      .fill(args.linkHref);
    await this.page.keyboard.press("Enter");
  }

  public async getDocumentationLinks(args: { nodeName: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    return await this.panel().locator(".kie-dmn-editor--documentation-link--row-title").locator("a").all();
  }

  public async setFont(args: { nodeName: string; newFont: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByTitle("Expand / collapse Font").click();

    await this.panel().getByTestId("node-font-style-selector").click();
    await this.panel().getByText(args.newFont).click();

    await this.diagram.resetFocus();
  }

  public async getNodeFont(args: { nodeName: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByTitle("Expand / collapse Font").click();

    return await this.panel().getByTestId("node-font-style-selector").textContent();
  }

  public async getNodeShape(args: { nodeName: string }) {
    await this.selectNodeByClickToAppropriatePosition({ nodeName: args.nodeName });
    await this.panel().getByTitle("Expand / collapse Shape").click();

    const width = await this.panel().getByTestId("node-shape-width-input-box").inputValue();
    const height = await this.panel().getByTestId("node-shape-height-input-box").inputValue();

    await this.panel().getByTitle("Expand / collapse Shape").click();

    return { width: width, height: height };
  }
}
