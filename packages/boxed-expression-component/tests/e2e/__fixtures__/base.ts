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
import { BoxedExpressionEditor } from "./boxedExpression";
import { Clipboard } from "./clipboard";
import { Stories } from "./stories";
import { Resizing } from "./resizing";
import { UseCases } from "./useCases";
import { Monaco } from "./monaco";
import { ProjectName } from "@kie-tools/playwright-base/projectNames";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  stories: Stories;
  clipboard: Clipboard;
  resizing: Resizing;
  useCases: UseCases;
  monaco: Monaco;
};

export const test = base.extend<BoxedExpressionFixtures>({
  monaco: async ({ page }, use, testInfo) => {
    await use(new Monaco(page, testInfo.project.name as ProjectName));
  },
  boxedExpressionEditor: async ({ page, baseURL, monaco }, use) => {
    await use(new BoxedExpressionEditor(page, monaco, baseURL));
  },
  stories: async ({ page, baseURL }, use) => {
    await use(new Stories(page, baseURL));
  },
  clipboard: async ({ browserName, context, page }, use) => {
    const clipboard = new Clipboard(page);
    clipboard.setup(context, browserName);
    await use(clipboard);
  },
  resizing: async ({ page }, use) => {
    await use(new Resizing(page));
  },
  useCases: async ({ page, baseURL }, use) => {
    await use(new UseCases(page, baseURL));
  },
});

export { expect } from "@playwright/test";
