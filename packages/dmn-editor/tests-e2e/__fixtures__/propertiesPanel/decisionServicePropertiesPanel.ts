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
import { ShapeProperties } from "./parts/shapeProperties";
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { FontProperties } from "./parts/fontProperties";
import { DocumentationProperties } from "./parts/documentationProperties";
import { DescriptionProperties } from "./parts/descriptionProperties";
import { DataType } from "../dataTypes";
import { DataTypeProperties } from "./parts/dataTypeProperties";
import { NameProperties } from "./parts/nameProperties";

export class DecisionServicePropertiesPanel extends PropertiesPanelBase {
  private nameProperties: NameProperties;
  private dataTypeProperties: DataTypeProperties;
  private descriptionProperties: DescriptionProperties;
  private documentationProperties: DocumentationProperties;
  private fontProperties: FontProperties;
  private shapeProperties: ShapeProperties;

  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
    this.nameProperties = new NameProperties(this.panel(), page);
    this.dataTypeProperties = new DataTypeProperties(this.panel(), page);
    this.descriptionProperties = new DescriptionProperties(this.panel());
    this.documentationProperties = new DocumentationProperties(this.panel(), page);
    this.fontProperties = new FontProperties(this.panel());
    this.shapeProperties = new ShapeProperties(this.panel());
  }

  public async setName(args: { newName: string }) {
    await this.nameProperties.setName({ ...args });
  }

  public async getName() {
    return await this.nameProperties.getName();
  }

  public async setDataType(args: { newDataType: DataType }) {
    await this.dataTypeProperties.setDataType({ ...args });
  }

  public async setDescription(args: { newDescription: string }) {
    await this.descriptionProperties.setDescription({ ...args });
  }

  public async getDescription() {
    return await this.descriptionProperties.getDescription();
  }

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    await this.documentationProperties.addDocumentationLink({ ...args });
  }

  public async getDocumentationLinks() {
    return await this.documentationProperties.getDocumentationLinks();
  }

  public async setFont(args: {
    fontSize?: string;
    bold?: boolean;
    italic?: boolean;
    underline?: boolean;
    striketrough?: boolean;
    color?: string;
    fontFamily?: string;
  }) {
    await this.fontProperties.setFont({ ...args });
  }

  public async resetFont() {
    await this.fontProperties.resetFont();
  }

  public async getFont() {
    return await this.fontProperties.getFont();
  }

  public async setShape(args: { width: string; height: string }) {
    await this.shapeProperties.setShape({ ...args });
  }

  public async setPosition(args: { x: string; y: string }) {
    await this.shapeProperties.setPosition({ ...args });
  }

  public async getShape() {
    return await this.shapeProperties.getShape();
  }

  public async resetShape() {
    await this.shapeProperties.resetShape();
  }

  public async setFillColor(args: { color: string }) {
    await this.shapeProperties.setFillColor({ ...args });
  }

  public async setStrokeColor(args: { color: string }) {
    await this.shapeProperties.setStrokeColor({ ...args });
  }

  public async getOutputDecisions() {
    return (
      await this.panel()
        .getByTestId("kie-tools--dmn-editor--decision-service-output-decisions")
        .getByRole("listitem")
        .allTextContents()
    ).map((content) => content.trim());
  }

  public async getEncapsulatedDecisions() {
    return (
      await this.panel()
        .getByTestId("kie-tools--dmn-editor--decision-service-encapsulated-decisions")
        .getByRole("listitem")
        .allTextContents()
    ).map((content) => content.trim());
  }

  public async getInputDecisions() {
    return (
      await this.panel()
        .getByTestId("kie-tools--dmn-editor--decision-service-input-decisions")
        .getByTestId("kie-tools--dmn-editor--draggable-children")
        .allTextContents()
    ).map((content) => content.trim());
  }

  public async moveInputDecision(args: { fromIndex: number; toIndex: number }) {
    const target = this.panel()
      .getByTestId("kie-tools--dmn-editor--decision-service-input-decisions")
      .getByTestId("kie-tools--dmn-editor--draggable-icon")
      .nth(args.toIndex);

    const source = this.panel()
      .getByTestId("kie-tools--dmn-editor--decision-service-input-decisions")
      .getByTestId("kie-tools--dmn-editor--draggable-icon")
      .nth(args.fromIndex);

    await source.hover();
    await source.dispatchEvent("dragstart");
    await source.dragTo(target);
  }

  public async getInputData() {
    return (
      await this.panel()
        .getByTestId("kie-tools--dmn-editor--decision-service-input-data")
        .getByTestId("kie-tools--dmn-editor--draggable-children")
        .allTextContents()
    ).map((content) => content.trim());
  }

  public async moveInputData(args: { fromIndex: number; toIndex: number }) {
    const target = this.panel()
      .getByTestId("kie-tools--dmn-editor--decision-service-input-data")
      .getByTestId("kie-tools--dmn-editor--draggable-icon")
      .nth(args.toIndex);

    const source = this.panel()
      .getByTestId("kie-tools--dmn-editor--decision-service-input-data")
      .getByTestId("kie-tools--dmn-editor--draggable-icon")
      .nth(args.fromIndex);

    await source.hover();
    await source.dispatchEvent("dragstart");
    await source.dragTo(target);
  }

  public async getInvokingThisDecisionServiceInFeel() {
    return await this.panel().getByTestId("kie-tools--dmn-editor--decision-service-feel").textContent();
  }
}
