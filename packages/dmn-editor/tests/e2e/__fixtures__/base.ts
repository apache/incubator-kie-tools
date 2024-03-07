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
import { Diagram } from "./diagram";
import { Palette } from "./palette";
import { Nodes } from "./nodes";
import { Editor } from "./editor";
import { Edges } from "./edges";
import { JsonModel } from "./jsonModel";

type DmnEditorFixtures = {
  diagram: Diagram;
  edges: Edges;
  editor: Editor;
  jsonModel: JsonModel;
  nodes: Nodes;
  palette: Palette;
};

export const test = base.extend<DmnEditorFixtures>({
  editor: async ({ page, baseURL }, use) => {
    await use(new Editor(page, baseURL));
  },
  jsonModel: async ({ page, baseURL }, use) => {
    await use(new JsonModel(page, baseURL));
  },
  diagram: async ({ page }, use) => {
    await use(new Diagram(page));
  },
  nodes: async ({ page, diagram, browserName }, use) => {
    await use(new Nodes(page, diagram, browserName));
  },
  edges: async ({ page, nodes, diagram }, use) => {
    await use(new Edges(page, nodes, diagram));
  },
  palette: async ({ page, diagram, nodes }, use) => {
    await use(new Palette(page, diagram, nodes));
  },
});

export { expect } from "@playwright/test";
