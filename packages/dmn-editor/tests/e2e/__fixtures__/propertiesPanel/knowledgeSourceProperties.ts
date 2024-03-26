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

export class KnowledgeSourceProperties extends PropertiesPanelBase {
  public async changeNodeSourceType(args: { nodeName: string; newSourceType: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Enter source type...").fill(args.newSourceType);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeSourceType(args: { nodeName: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    return await this.panel().getByPlaceholder("Enter source type...").inputValue();
  }

  public async changeNodeLocationURI(args: { nodeName: string; newLocationURI: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    await this.panel().getByPlaceholder("Enter location URI...").fill(args.newLocationURI);
    // commit changes by click to the diagram
    await this.diagram.resetFocus();
  }

  public async getNodeLocationURI(args: { nodeName: string }) {
    await this.selectNodeToLoadPropertiesPanel({ nodeName: args.nodeName });
    return await this.panel().getByPlaceholder("Enter location URI...").inputValue();
  }
}
