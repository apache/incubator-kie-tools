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
import { EdgeType } from "../edges";
import { PropertiesPanelBase } from "./propertiesPanelBase";

export const EDGE_TITLE: Record<EdgeType, string> = {
  [EdgeType.INFORMATION_REQUIREMENT]: "Information Requirement",
  [EdgeType.KNOWLEDGE_REQUIREMENT]: "Knowledge Requirement",
  [EdgeType.AUTHORITY_REQUIREMENT]: "Authority Requirement",
  [EdgeType.ASSOCIATION]: "Association",
};

export const uuidRegExp = /^_[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}$/;

export class EdgePropertiesPanel extends PropertiesPanelBase {
  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    super(diagram, page);
  }

  /** Returns the text content of the panel header title (e.g. "Information Requirement"). */
  public async getTitle() {
    return await this.panel().locator(".kie-dmn-editor--properties-panel-header-title").first().textContent();
  }

  /** Returns the current value of the Description textarea. */
  public async getDescription() {
    return await this.panel().getByLabel("Description").inputValue();
  }

  /** Sets the Description textarea to the given value. */
  public async setDescription(args: { newDescription: string }) {
    await this.panel().getByLabel("Description").fill(args.newDescription);
    await this.panel().getByLabel("Description").press("Tab");
  }

  /** Returns the read-only edge ID shown in the ClipboardCopy field. */
  public async getId() {
    return await this.panel().locator("input[readonly]").first().inputValue();
  }
}
