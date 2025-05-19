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

export class Overlays {
  constructor(public page: Page) {}

  public async enableEvaluationHighlights() {
    await this.page.getByTitle("Overlays").click();
    await this.page.getByTestId("kie-tools--dmn-editor--evaluation-highlights-control").scrollIntoViewIfNeeded();
    await this.page.getByTestId("kie-tools--dmn-editor--evaluation-highlights-control").check({ force: true });
    await this.page.getByTitle("Overlays").click();
  }

  public async turnOffSnapping() {
    await this.page.getByTitle("Overlays").click();
    await this.page.getByLabel("Snapping").uncheck({ force: true });
    await this.page.getByTitle("Overlays").click();
  }

  public async setSnapping(args: { horizontal: string; vertical: string }) {
    await this.page.getByTitle("Overlays").click();
    await this.page
      .getByTestId("kie-tools--dmn-editor--horizontal-snapping-control")
      .locator("input")
      .fill(args.horizontal);
    await this.page
      .getByTestId("kie-tools--dmn-editor--vertical-snapping-control")
      .locator("input")
      .fill(args.vertical);
    await this.page.getByTitle("Overlays").click();
  }
}
