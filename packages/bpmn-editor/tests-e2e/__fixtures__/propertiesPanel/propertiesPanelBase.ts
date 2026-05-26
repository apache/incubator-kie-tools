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
import { NameProperties } from "./parts/nameProperties";
import { DocumentationProperties } from "./parts/documentationProperties";

export abstract class PropertiesPanelBase {
  public nameProperties: NameProperties;
  public documentationProperties: DocumentationProperties;

  constructor(
    public diagram: Diagram,
    public page: Page
  ) {
    this.nameProperties = new NameProperties(this.panel(), page);
    this.documentationProperties = new DocumentationProperties(this.panel(), page);
  }

  public panel() {
    return this.page.getByTestId("kie-tools--bpmn-editor--properties-panel-container");
  }

  public async open() {
    const isPanelOpen = await this.panel().isVisible();
    if (isPanelOpen) {
      return;
    }

    const propertiesButton = this.page.getByTitle("Properties");
    const isButtonVisible = await propertiesButton.isVisible();
    if (isButtonVisible) {
      await propertiesButton.click();
    }
  }

  public async close() {
    const closeButton = this.panel().getByTitle("Close");
    const isVisible = await closeButton.isVisible();
    if (isVisible) {
      await closeButton.click();
    }
  }

  public async isOpen(): Promise<boolean> {
    return await this.panel().isVisible();
  }
}
