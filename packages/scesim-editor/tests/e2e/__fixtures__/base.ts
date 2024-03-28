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

import { test as base } from "@playwright/test";
import { Clipboard } from "./clipboard";
import { Resizing } from "./resizing";
import { UseCases } from "./useCases";
import { ProjectName } from "@kie-tools/playwright-base/projectNames";
import { Editor } from "./editor";
import { TestScenarioTable } from "./testScenarioTable";
import { BackgroundTable } from "./backgroundTable";
import { Table } from "./table";
import { ContextMenu } from "./contextMenu";
import { SelectorPanel } from "./selectorPanel";

type SceSimEditorFixtures = {
  testScenarioTable: TestScenarioTable;
  backgroundTable: BackgroundTable;
  editor: Editor;
  clipboard: Clipboard;
  resizing: Resizing;
  table: Table;
  useCases: UseCases;
  contextMenu: ContextMenu;
  selectorPanel: SelectorPanel;
};

export const test = base.extend<SceSimEditorFixtures>({
  testScenarioTable: async ({ page }, use, testInfo) => {
    await use(new TestScenarioTable(page, testInfo.project.name as ProjectName));
  },
  backgroundTable: async ({ page }, use, testInfo) => {
    await use(new BackgroundTable(page, testInfo.project.name as ProjectName));
  },
  editor: async ({ page, selectorPanel, baseURL }, use) => {
    await use(new Editor(page, selectorPanel, baseURL));
  },
  clipboard: async ({ browserName, context, page }, use) => {
    const clipboard = new Clipboard(page);
    clipboard.setup(context, browserName);
    await use(clipboard);
  },
  resizing: async ({ page }, use) => {
    await use(new Resizing(page));
  },
  table: async ({ page }, use) => {
    await use(new Table(page));
  },
  contextMenu: async ({ page }, use) => {
    await use(new ContextMenu(page));
  },
  selectorPanel: async ({ page }, use) => {
    await use(new SelectorPanel(page));
  },
  useCases: async ({ page, selectorPanel, baseURL }, use) => {
    await use(new UseCases(page, selectorPanel, baseURL));
  },
});

export { expect } from "@playwright/test";
