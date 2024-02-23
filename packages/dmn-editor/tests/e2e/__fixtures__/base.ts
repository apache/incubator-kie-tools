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
import { Pallete } from "./pallete";
import { Node } from "./node";
import { Editor } from "./editor";
import { Edge } from "./edge";

type DmnEditorFixtures = {
  diagram: Diagram;
  edge: Edge;
  editor: Editor;
  node: Node;
  pallete: Pallete;
};

export const test = base.extend<DmnEditorFixtures>({
  diagram: async ({ page }, use) => {
    await use(new Diagram(page));
  },
  editor: async ({ page, baseURL }, use) => {
    await use(new Editor(page, baseURL));
  },
  node: async ({ page, diagram }, use) => {
    await use(new Node(page, diagram));
  },
  edge: async ({ page, node }, use) => {
    await use(new Edge(page, node));
  },
  pallete: async ({ page, diagram, node }, use) => {
    await use(new Pallete(page, diagram, node));
  },
});

export { expect } from "@playwright/test";
