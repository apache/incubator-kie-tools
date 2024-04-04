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

export class MultipleNodesPropertiesPanel extends PropertiesPanelBase {
  public async setName(args: { newName: string }) {
    throw new Error("Not supported operation for multiple selected nodes");
  }

  public async getName() {
    throw new Error("Not supported operation for multiple selected nodes");
    return "";
  }

  public async setDataType(args: { newDataType: DataType }) {
    throw new Error("Not supported operation for multiple selected nodes");
  }

  public async setDescription(args: { newDescription: string }) {
    throw new Error("Not supported operation for multiple selected nodes");
  }

  public async getDescription() {
    throw new Error("Not supported operation for multiple selected nodes");
    return "";
  }

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    throw new Error("Not supported operation for multiple selected nodes");
  }

  public async getDocumentationLinks() {
    throw new Error("Not supported operation for multiple selected nodes");
    return [];
  }

  public async setMultipleNodesFont(args: { newFont: string }) {
    await this.panel().getByTestId("node-font-style-selector").click();
    await this.panel().getByText(args.newFont).click();

    await this.diagram.resetFocus();
  }
}
