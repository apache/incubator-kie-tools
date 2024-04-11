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
import { DataType } from "../jsonModel";
import { ShapeProperties } from "./parts/shapeProperties";
import { PropertiesPanelBase } from "./propertiesPanelBase";
import { FontProperties } from "./parts/fontProperties";
import { DocumentationProperties } from "./parts/documentationProperties";
import { DescriptionProperties } from "./parts/descriptionProperties";
import { NameProperties } from "./parts/nameProperties";

export class KnowledgeSourcePropertiesPanel extends PropertiesPanelBase {
  private nameProperties: NameProperties;
  private descriptionProperties: DescriptionProperties;
  private documentationProperties: DocumentationProperties;
  private fontProperties: FontProperties;
  private shapeProperties: ShapeProperties;

  constructor(public diagram: Diagram, public page: Page) {
    super(diagram, page);
    this.nameProperties = new NameProperties(this.panel(), page);
    this.descriptionProperties = new DescriptionProperties(this.panel(), diagram);
    this.documentationProperties = new DocumentationProperties(this.panel(), page);
    this.fontProperties = new FontProperties(this.panel(), diagram);
    this.shapeProperties = new ShapeProperties(this.panel());
  }

  public async setName(args: { newName: string }) {
    await this.nameProperties.setName({ newName: args.newName });
  }

  public async getName() {
    return await this.nameProperties.getName();
  }

  public async setDescription(args: { newDescription: string }) {
    await this.descriptionProperties.setDescription({ newDescription: args.newDescription });
  }

  public async getDescription() {
    return await this.descriptionProperties.getDescription();
  }

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    await this.documentationProperties.addDocumentationLink({ linkText: args.linkText, linkHref: args.linkHref });
  }

  public async getDocumentationLinks() {
    return await this.documentationProperties.getDocumentationLinks();
  }

  public async setDataType(args: { newDataType: DataType }) {
    throw new Error("Not supported operation for Knowledge Source");
  }

  public async setSourceType(args: { newSourceType: string }) {
    await this.panel().getByPlaceholder("Enter source type...").fill(args.newSourceType);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getSourceType() {
    return await this.panel().getByPlaceholder("Enter source type...").inputValue();
  }

  public async setLocationURI(args: { newLocationURI: string }) {
    await this.panel().getByPlaceholder("Enter location URI...").fill(args.newLocationURI);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getLocationURI() {
    return await this.panel().getByPlaceholder("Enter location URI...").inputValue();
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
    await this.fontProperties.setFont({
      fontSize: args.fontSize,
      bold: args.bold,
      italic: args.italic,
      underline: args.underline,
      striketrough: args.striketrough,
      color: args.color,
      fontFamily: args.fontFamily,
    });
  }

  public async resetFont() {
    await this.fontProperties.resetFont();
  }

  public async getFont() {
    return await this.fontProperties.getFont();
  }

  public async setShape(args: { width: string; height: string }) {
    await this.shapeProperties.setShape({ width: args.width, height: args.height });
  }

  public async setPosition(args: { x: string; y: string }) {
    await this.shapeProperties.setPosition({ x: args.x, y: args.y });
  }

  public async getShape() {
    return await this.shapeProperties.getShape();
  }

  public async resetShape() {
    await this.shapeProperties.resetShape();
  }

  public async setFillColor(args: { color: string }) {
    await this.shapeProperties.setFillColor({ color: args.color });
  }

  public async setStrokeColor(args: { color: string }) {
    await this.shapeProperties.setStrokeColor({ color: args.color });
  }
}
