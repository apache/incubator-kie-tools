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

export class TextAnnotationProperties extends PropertiesPanelBase {
  public async setName(args: { newName: string }) {
    throw new Error("Not supported operation for Text Annotation");
  }

  public async getName() {
    throw new Error("Not supported operation for Text Annotation");
    return "";
  }

  public async setDataType(args: { newDataType: DataType }) {
    throw new Error("Not supported operation for Text Annotation");
  }

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    throw new Error("Not supported operation for Text Annotation");
  }

  public async getDocumentationLinks() {
    throw new Error("Not supported operation for Text Annotation");
    return [];
  }

  public async setFormat(args: { newFormat: string }) {
    await this.panel().getByPlaceholder("Enter a text format...").fill(args.newFormat);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getFormat() {
    return await this.panel().getByPlaceholder("Enter a text format...").inputValue();
  }

  public async setText(args: { newText: string }) {
    await this.panel().getByPlaceholder("Enter text...").fill(args.newText);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getText() {
    return await this.panel().getByPlaceholder("Enter text...").inputValue();
  }

  public async setFillColor(args: { color: string }) {
    await this.panel().getByRole("button", { name: "Expand / collapse Shape" }).click();
    await this.panel().getByTestId("color-picker-shape-fill").fill(args.color);
    await this.panel().getByRole("button", { name: "Expand / collapse Shape" }).click();
  }

  public async setStrokeColor(args: { color: string }) {
    await this.panel().getByRole("button", { name: "Expand / collapse Shape" }).click();
    await this.panel().getByTestId("color-picker-shape-stroke").fill(args.color);
    await this.panel().getByRole("button", { name: "Expand / collapse Shape" }).click();
  }
}
