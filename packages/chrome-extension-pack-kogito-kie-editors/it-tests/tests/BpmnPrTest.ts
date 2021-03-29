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

const TEST_NAME = "BpmnPrTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

afterEach(async () => {
  await tools.finishTest();
});

test(TEST_NAME, async () => {
  // open bpmn pr
  await tools.open("https://github.com/tomasdavidorg/chrome-extension-pr-test/pull/2/files");

  // check page
  await tools.command().checkSourceVisible(true);

  // open diagram view
  const seeAsDiagramButton = await tools.find(By.xpath("//button[text()='See as diagram']")).getElement();
  await seeAsDiagramButton.click();

  // check page
  await tools.command().checkSourceVisible(false);

  // wait and get kogito iframe
  const iframe = await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();
  await tools.window().leaveFrame();

  // scroll to pr header
  const header = await tools.find(By.className("gh-header-meta")).getElement();
  await header.scroll();

  // click original button
  const originalButton = await tools.find(By.xpath("//button[text()='Original']")).getElement();
  await originalButton.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // scroll to pr header
  await tools.window().leaveFrame();
  await header.scroll();
  await iframe.enterFrame();

  // open explorer panel
  const explorerDiagramButton = await tools
    .find(By.css("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']"))
    .getElement();
  await explorerDiagramButton.click();

  // check node names
  const originalNodes = await tools.find(By.css("[data-ouia-component-type='tree-item'] a")).getElements();
  expect(await Promise.all(originalNodes.map(async n => await n.getText()))).toEqual([
    "new-file",
    "Start",
    "Task",
    "End"
  ]);

  // click changes button
  await tools.window().leaveFrame();
  const changesButton = await tools.find(By.xpath("//button[text()='Changes']")).getElement();
  await changesButton.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // check node names
  const nodes = await tools.find(By.css("[data-ouia-component-type='tree-item'] a")).getElements();
  expect(await Promise.all(nodes.map(async n => await n.getText()))).toEqual([
    "bpmn-file",
    "Start",
    "Task",
    "End",
    "Intermediate Timer"
  ]);

  // scroll to pr header
  await tools.window().leaveFrame();
  await header.scroll();

  // click close diagram button
  const closeDiagramButton = await tools.find(By.xpath("//button[text()='Close diagram']")).getElement();
  await closeDiagramButton.click();

  // check page
  await tools.command().checkSourceVisible(true);
});
