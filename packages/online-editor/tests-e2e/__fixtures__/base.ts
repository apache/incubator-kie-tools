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

import { Page, test as base, expect } from "@playwright/test";
import { Upload } from "./upload";

type BaseFixtures = {
  kieSandbox: KieSandbox;
  upload: Upload;
};

class KieSandbox {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
  }

  public getEditor() {
    return this.page.frameLocator("#kogito-iframe");
  }

  public async isEditorLoaded() {
    await expect(this.getEditor().getByRole("heading", { name: "Loading..." })).toBeAttached();
    await expect(this.getEditor().getByRole("heading", { name: "Loading..." })).not.toBeAttached();
  }
}

export const test = base.extend<BaseFixtures>({
  kieSandbox: async ({ page, baseURL }, use) => {
    await use(new KieSandbox(page, baseURL));
  },
  upload: async ({ page }, use) => {
    await use(new Upload(page));
  },
});

export { expect } from "@playwright/test";
