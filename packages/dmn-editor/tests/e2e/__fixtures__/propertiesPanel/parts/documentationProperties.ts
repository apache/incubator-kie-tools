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

import { Locator, Page } from "@playwright/test";

export class DocumentationProperties {
  constructor(public panel: Locator, public page: Page) {}

  public async addDocumentationLink(args: { linkText: string; linkHref: string }) {
    await this.panel.getByTitle("Add documentation link").click();
    await this.panel
      .getByTestId("kie-tools--dmn-editor--documentation-link--row")
      .getByPlaceholder("Enter a title...")
      .fill(args.linkText);
    await this.panel
      .getByTestId("kie-tools--dmn-editor--documentation-link--row")
      .getByPlaceholder("https://")
      .fill(args.linkHref);
    await this.page.keyboard.press("Enter");
  }

  public async getDocumentationLinks() {
    return await this.panel.getByTestId("kie-tools--dmn-editor--documentation-link--row-title").all();
  }
}
