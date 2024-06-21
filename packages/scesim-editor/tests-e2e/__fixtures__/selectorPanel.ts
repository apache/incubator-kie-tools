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

export class SelectorPanel {
  constructor(public page: Page) {}

  public async open() {
    await this.page.getByRole("button").first().click();
  }

  public async close() {
    await this.page.getByLabel("Close drawer panel").click();
  }
  public getAttribute(args: { name: string }) {
    return this.page.getByRole("button", { name: args.name, exact: true });
  }

  public async expandAttribute(args: { name: string }) {
    await this.page.getByLabel(args.name + args.name).click();
  }

  public async assign(args: { name: string }) {
    await this.page.getByRole("button", { name: args.name, exact: true }).click();
    await this.page.getByRole("button", { name: "Assign" }).click();
  }
}
