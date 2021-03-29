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

const TEST_NAME = "DmnPrTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

afterEach(async () => {
  await tools.finishTest();
});

test(TEST_NAME, async () => {
  // open dmn pr
  await tools.open("https://github.com/tomasdavidorg/chrome-extension-pr-test/pull/3/files");

  // check page
  await tools.command().checkSourceVisible(true);

  // open diagram view
  const seeAsDiagramButton = await tools.find(By.css(".kogito-toolbar-container-pr > button")).getElement();
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
  await tools.window().leaveFrame();

  // scroll to pr header
  await header.scroll();
  await iframe.enterFrame();

  // open decision navigator
  const decisionNavigatorButton = await tools
    .find(By.css("[data-ouia-component-id='docks-item-org.kie.dmn.decision.navigator']"))
    .getElement();
  await decisionNavigatorButton.click();

  // check dmn nodes
  const originalNodes = await tools
    .find(By.css("[data-i18n-prefix='DecisionNavigatorTreeView.'] > div > span[data-field='text-content']"))
    .getElements();
  expect(await Promise.all(originalNodes.map(async n => n.getText()))).toEqual([
    "new-file",
    "Decision",
    "InputData",
    "Model",
    "Function"
  ]);

  // click changes button
  await tools.window().leaveFrame();
  const changesButton = await tools.find(By.xpath("//button[text()='Changes']")).getElement();
  await changesButton.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // check dmn nodes
  const nodes = await tools
    .find(By.css("[data-i18n-prefix='DecisionNavigatorTreeView.'] > div > span[data-field='text-content']"))
    .getElements();
  expect(await Promise.all(nodes.map(async n => n.getText()))).toEqual([
    "new-file",
    "Annotation",
    "Decision",
    "InputData",
    "Model",
    "Function"
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
