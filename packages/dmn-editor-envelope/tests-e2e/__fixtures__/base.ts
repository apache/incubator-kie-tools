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
import { Drds } from "./drds";
import { DrgNodes } from "./drgNodes";
import { DataTypes } from "./dataTypes";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/tests-e2e/__fixtures__/boxedExpression";
import { Monaco } from "@kie-tools/boxed-expression-component/tests-e2e/__fixtures__/monaco";
import { ProjectName } from "@kie-tools/playwright-base/projectNames";
import { Stories } from "./stories";

type DmnEditorFixtures = {
  bee: BoxedExpressionEditor;
  dataTypes: DataTypes;
  diagram: Diagram;
  drds: Drds;
  drgNodes: DrgNodes;
  edges: Edges;
  editor: Editor;
  jsonModel: JsonModel;
  monaco: Monaco;
  nodes: Nodes;
  palette: Palette;
  stories: Stories;
};

export const test = base.extend<DmnEditorFixtures>({
  bee: async ({ page, baseURL, monaco }, use) => {
    await use(new BoxedExpressionEditor(page, monaco, baseURL));
  },
  dataTypes: async ({ page, monaco }, use) => {
    await use(new DataTypes(page, monaco));
  },
  drds: async ({ page }, use) => {
    await use(new Drds(page));
  },
  drgNodes: async ({ diagram, page }, use) => {
    await use(new DrgNodes(diagram, page));
  },
  diagram: async ({ page }, use) => {
    await use(new Diagram(page));
  },
  edges: async ({ page, nodes, diagram }, use) => {
    await use(new Edges(page, nodes, diagram));
  },
  editor: async ({ page, baseURL }, use) => {
    await use(new Editor(page, baseURL));
  },
  jsonModel: async ({ page, baseURL }, use) => {
    await use(new JsonModel(page, baseURL));
  },
  monaco: async ({ page }, use, testInfo) => {
    await use(new Monaco(page, testInfo.project.name as ProjectName));
  },
  nodes: async ({ page, diagram, browserName }, use) => {
    await use(new Nodes(page, diagram, browserName));
  },
  palette: async ({ page, diagram, nodes }, use) => {
    await use(new Palette(page, diagram, nodes));
  },
  stories: async ({ baseURL, page }, use) => {
    await use(new Stories(page, baseURL));
  },
});

export { expect } from "@playwright/test";
