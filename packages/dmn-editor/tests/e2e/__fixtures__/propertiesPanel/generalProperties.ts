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

import { DataType } from "../jsonModel";
import { PropertiesPanelBase } from "./propertiesPanelBase";

export class GeneralProperties extends PropertiesPanelBase {
  public async changeNodeName(args: { from: string; to: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.from });
    await this.panel().getByPlaceholder("Enter a name...").fill(args.to);
    await this.page.keyboard.press("Enter");
  }

  public async changeNodeDataType(args: { nodeName: string; newDataType: DataType }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Select a data type...").click();
    await this.page.getByRole("option").getByText(args.newDataType, { exact: true }).click();
  }

  public async changeNodeDescription(args: { nodeName: string; newDescription: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Enter a description...").fill(args.newDescription);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeDescription(args: { nodeName: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    return await this.panel().getByPlaceholder("Enter a description...").inputValue();
  }

  public async addDocumentationLink(args: { nodeName: string; linkText: string; linkHref: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
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
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    return await this.panel().locator(".kie-dmn-editor--documentation-link--row-title").locator("a").all();
  }

  public async changeNodeFont(args: { nodeName: string; newFont: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByTitle("Expand / collapse Font").click();

    await this.panel().locator("[data-ouia-component-id='node-font-style-selector']").click();
    await this.panel().getByText(args.newFont).click();

    await this.diagram.resetFocus();
  }

  public async getNodeFont(args: { nodeName: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByTitle("Expand / collapse Font").click();

    return await this.panel().locator("[data-ouia-component-id='node-font-style-selector']").textContent();
  }
}
