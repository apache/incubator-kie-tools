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

export enum TabName {
  EDITOR = "Editor",
  DATA_TYPES = "Data types",
  INCLUDED_MODELS = "Included models",
}

export class Editor {
  constructor(
    public page: Page,
    public baseURL?: string
  ) {}

  public async open() {
    await this.page.goto(`${this.baseURL}/iframe.html?args=&id=misc-empty--empty&viewMode=story`);
  }

  public async changeTab(args: { tab: TabName }) {
    await this.page.getByRole("tab", { name: args.tab }).click();
  }
}
