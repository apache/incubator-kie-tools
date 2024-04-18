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
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { DescriptionProperties } from "./parts/descriptionProperties";
import { NameProperties } from "./parts/nameProperties";

export class DiagramPropertiesPanel extends PropertiesPanelBase {
  private nameProperties: NameProperties;
  private descriptionProperties: DescriptionProperties;

  constructor(public diagram: Diagram, public page: Page) {
    super(diagram, page);
    this.nameProperties = new NameProperties(this.panel(), page);
    this.descriptionProperties = new DescriptionProperties(this.panel());
  }

  public async setDescription(args: { newDescription: string }) {
    await this.descriptionProperties.setDescription({ ...args });
  }

  public async getDescription() {
    return await this.descriptionProperties.getDescription();
  }

  public async setName(args: { newName: string }) {
    await this.nameProperties.setName({ ...args });
  }

  public async getName() {
    return await this.nameProperties.getName();
  }

  public async setExpressionLanguage(args: { expressionlangugae: string }) {
    await this.panel().getByPlaceholder("Enter an expression language...").fill(args.expressionlangugae);
    await this.panel().getByPlaceholder("Enter an expression language...").press("Tab");
  }

  public async getExpressionLanguage() {
    return await this.panel().getByPlaceholder("Enter an expression language...").inputValue();
  }

  public async setId(args: { id: string }) {
    await this.panel().getByPlaceholder("Enter a diagram ID...").locator("input").fill(args.id);
    await this.panel().getByPlaceholder("Enter a diagram ID...").locator("input").press("Tab");
  }

  public async getId() {
    return await this.panel().getByPlaceholder("Enter a diagram ID...").locator("input").inputValue();
  }

  public async setNamespace(args: { namespace: string }) {
    await this.panel().getByPlaceholder("Enter a diagram Namespace...").locator("input").fill(args.namespace);
    await this.panel().getByPlaceholder("Enter a diagram Namespace...").locator("input").press("Tab");
  }

  public async getNamespace() {
    return await this.panel().getByPlaceholder("Enter a diagram Namespace...").locator("input").inputValue();
  }

  public async resetIdAndNamespace(args: { cancel: boolean }) {
    await this.panel().getByTitle("Re-generate ID & Namespace").click();
    if (args.cancel) {
      await this.page.locator("footer").getByText("Cancel").click();
    } else {
      await this.page.getByText("Yes, re-generate ID and Namespace").click();
    }
  }
}
