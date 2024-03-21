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
import { DataType } from "./jsonModel";
import { Diagram } from "./diagram";
import { Nodes } from "./nodes";

export class PropertiesPanel {
  constructor(public diagram: Diagram, public nodes: Nodes, public page: Page) {}

  public async open() {
    await this.page.getByTitle("Properties panel").click();
  }

  public async changeNodeName(args: { newName: string }) {
    await this.page.getByPlaceholder("Enter a name...").fill(args.newName);
    await this.page.keyboard.press("Enter");
  }

  public async changeNodeDataType(args: { newDataType: DataType }) {
    await this.page.getByPlaceholder("Select a data type...").click();
    await this.page.getByRole("option").getByText(args.newDataType, { exact: true }).click();
  }

  public async changeNodeDescription(args: { newDescription: string }) {
    await this.page.getByPlaceholder("Enter a description...").fill(args.newDescription);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeDescription(args: { nodeName: string }) {
    await this.nodes.select({ name: args.nodeName });
    return await this.page.getByPlaceholder("Enter a description...").inputValue();
  }

  public async changeNodeQuestion(args: { newQuestion: string }) {
    await this.page.getByPlaceholder("Enter a question...").fill(args.newQuestion);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeQuestion(args: { nodeName: string }) {
    await this.nodes.select({ name: args.nodeName });
    return await this.page.getByPlaceholder("Enter a question...").inputValue();
  }

  public async changeNodeAllowedAnswers(args: { newAllowedAnswers: string }) {
    await this.page.getByPlaceholder("Enter allowed answers...").fill(args.newAllowedAnswers);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeAllowedAnswers(args: { nodeName: string }) {
    await this.nodes.select({ name: args.nodeName });
    return await this.page.getByPlaceholder("Enter allowed answers...").inputValue();
  }

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    await this.page.getByTitle("Add documentation link").click();
    await this.page
      .locator(".kie-dmn-editor--documentation-link--row")
      .getByPlaceholder("Enter a title...")
      .fill(args.linkText);
    await this.page.locator(".kie-dmn-editor--documentation-link--row").getByPlaceholder("http://").fill(args.linkHref);
    await this.page.keyboard.press("Enter");
  }

  public async getDocumentationLinks(args: { nodeName: string }) {
    await this.nodes.select({ name: args.nodeName });
    return await this.page.locator(".kie-dmn-editor--documentation-link--row-title").locator("a").all();
  }
}
