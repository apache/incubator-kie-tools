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

import { PropertiesPanelBase } from "./propertiesPanelBase";

export class DiagramPropertiesPanel extends PropertiesPanelBase {
  public async setDiagramName(args: { newName: string }) {
    await this.panel().getByPlaceholder("Enter a name...").fill(args.newName);
    await this.page.keyboard.press("Enter");
  }

  public async getDiagramName() {
    return await this.panel().getByPlaceholder("Enter a name...").inputValue();
  }

  public async setDiagramDescription(args: { newDescription: string }) {
    await this.panel().getByPlaceholder("Enter a description...").fill(args.newDescription);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getDiagramDescription() {
    return await this.panel().getByPlaceholder("Enter a description...").inputValue();
  }

  public async setExpressionLanguage(args: { expressionlangugae: string }) {
    await this.panel().getByPlaceholder("Enter an expression language...").fill(args.expressionlangugae);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getExpressionLanguage() {
    return await this.panel().getByPlaceholder("Enter an expression language...").inputValue();
  }

  public async setId(args: { id: string }) {
    await this.panel().getByPlaceholder("Enter a diagram ID...").locator("input").fill(args.id);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getId() {
    return await this.panel().getByPlaceholder("Enter a diagram ID...").locator("input").inputValue();
  }

  public async setNamespace(args: { namespace: string }) {
    await this.panel().getByPlaceholder("Enter a diagram Namespace...").locator("input").fill(args.namespace);

    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNamespace() {
    return await this.panel().getByPlaceholder("Enter a diagram Namespace...").locator("input").inputValue();
  }
}
