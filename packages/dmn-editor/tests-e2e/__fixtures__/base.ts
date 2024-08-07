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
import { DecisionServicePropertiesPanel } from "./propertiesPanel/decisionServicePropertiesPanel";
import { DecisionPropertiesPanel } from "./propertiesPanel/decisionPropertiesPanel";
import { KnowledgeSourcePropertiesPanel } from "./propertiesPanel/knowledgeSourcePropertiesPanel";
import { TextAnnotationProperties as TextAnnotationPropertiesPanel } from "./propertiesPanel/textAnnotationPropertiesPanel";
import { BkmPropertiesPanel } from "./propertiesPanel/bkmPropertiesPanel";
import { InputDataPropertiesPanel } from "./propertiesPanel/inputDataPropertiesPanel";
import { GroupPropertiesPanel } from "./propertiesPanel/groupPropertiesPanel";
import { DiagramPropertiesPanel } from "./propertiesPanel/diagramPropertiesPanel";
import { MultipleNodesPropertiesPanel } from "./propertiesPanel/multipleNodesPropertiesPanel";
import { Overlays } from "./overlays";
import { Drds } from "./drds";
import { DrgNodes } from "./drgNodes";
import { DataTypes } from "./dataTypes";
import { BeePropertiesPanel } from "./propertiesPanel/beePropertiesPanel";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/tests-e2e/__fixtures__/boxedExpression";
import { Monaco } from "@kie-tools/boxed-expression-component/tests-e2e/__fixtures__/monaco";
import { ProjectName } from "@kie-tools/playwright-base/projectNames";

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
  overlays: Overlays;
  beePropertiesPanel: BeePropertiesPanel;
  bkmPropertiesPanel: BkmPropertiesPanel;
  decisionPropertiesPanel: DecisionPropertiesPanel;
  decisionServicePropertiesPanel: DecisionServicePropertiesPanel;
  diagramPropertiesPanel: DiagramPropertiesPanel;
  groupPropertiesPanel: GroupPropertiesPanel;
  inputDataPropertiesPanel: InputDataPropertiesPanel;
  knowledgeSourcePropertiesPanel: KnowledgeSourcePropertiesPanel;
  multipleNodesPropertiesPanel: MultipleNodesPropertiesPanel;
  textAnnotationPropertiesPanel: TextAnnotationPropertiesPanel;
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
  overlays: async ({ page }, use) => {
    await use(new Overlays(page));
  },
  beePropertiesPanel: async ({ diagram, page }, use) => {
    await use(new BeePropertiesPanel(diagram, page));
  },
  bkmPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new BkmPropertiesPanel(diagram, page));
  },
  decisionPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new DecisionPropertiesPanel(diagram, page));
  },
  decisionServicePropertiesPanel: async ({ diagram, page }, use) => {
    await use(new DecisionServicePropertiesPanel(diagram, page));
  },
  diagramPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new DiagramPropertiesPanel(diagram, page));
  },
  groupPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new GroupPropertiesPanel(diagram, page));
  },
  knowledgeSourcePropertiesPanel: async ({ diagram, page }, use) => {
    await use(new KnowledgeSourcePropertiesPanel(diagram, page));
  },
  inputDataPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new InputDataPropertiesPanel(diagram, page));
  },
  multipleNodesPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new MultipleNodesPropertiesPanel(diagram, page));
  },
  textAnnotationPropertiesPanel: async ({ diagram, page }, use) => {
    await use(new TextAnnotationPropertiesPanel(diagram, page));
  },
});

export { expect } from "@playwright/test";
