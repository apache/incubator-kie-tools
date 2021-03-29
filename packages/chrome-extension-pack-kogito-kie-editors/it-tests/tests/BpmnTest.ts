/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { By } from "selenium-webdriver";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

afterEach(async () => {
  await tools.finishTest();
});

test(TEST_NAME, async () => {
  // open github samples list
  await tools.open(
    "https://github.com/kiegroup/kogito-tooling/tree/master/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples"
  );

  // open bpmn sample
  const bpmnSample = await tools.find(By.css("a[title='test.bpmn']")).getElement();
  await bpmnSample.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // test basic bpmn editor functions
  await tools.command().testSampleBpmnInEditor();

  // open move start event to canvas
  const startEvents = await tools.find(By.css("[title='Start Events']")).getElement();
  await startEvents.click();
  const startItem = await tools.find(By.className("kie-palette-item-anchor-spacer")).getElement();
  await startItem.dragAndDrop(200, 0);

  // close start events palette
  const closeButton = await tools.find(By.className("kie-palette-flyout__btn-link--close")).getElement();
  await closeButton.click();

  // open explorer panel
  const explorerDiagramButton = await tools
    .find(By.css("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']"))
    .getElement();
  await explorerDiagramButton.click();

  // check node names
  const nodes = await tools.find(By.css("[data-ouia-component-type='tree-item'] a")).getElements();
  expect(await Promise.all(nodes.map(async n => await n.getText()))).toEqual([
    "myProcess",
    "MyStart",
    "MyTask",
    "MyEnd",
    "Start"
  ]);

  // open source view
  await tools.window().leaveFrame();
  const seeAsSourceButton = await tools.find(By.css("[data-testid='see-as-source-button']")).getElement();
  await seeAsSourceButton.click();

  // check page
  await tools.command().checkSourceVisible(true);

  // open diagram view
  const seeAsDiagramButton = await tools.find(By.css("[data-testid='see-as-diagram-button']")).getElement();
  await seeAsDiagramButton.click();

  // check page
  await tools.command().checkSourceVisible(false);
});
