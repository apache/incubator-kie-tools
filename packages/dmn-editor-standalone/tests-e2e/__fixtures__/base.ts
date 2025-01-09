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
import { Editor } from "./editor";
import { Files } from "./files";
import { Palette } from "./palette";
import { Diagram } from "./diagram";
import { Nodes } from "./nodes";

type DmnEditorFixtures = {
  editor: Editor;
  files: Files;
  diagram: Diagram;
  nodes: Nodes;
  palette: Palette;
};

export const test = base.extend<DmnEditorFixtures>({
  editor: async ({ page }, use) => {
    await use(new Editor(page));
  },
  files: async ({ page }, use) => {
    await use(new Files(page));
  },
  diagram: async ({ page, editor }, use) => {
    await use(new Diagram(page, editor));
  },
  nodes: async ({ page, editor, diagram }, use) => {
    await use(new Nodes(page, editor, diagram));
  },
  palette: async ({ page, editor, diagram, nodes }, use) => {
    await use(new Palette(page, editor, diagram, nodes));
  },
});

export { expect } from "@playwright/test";
