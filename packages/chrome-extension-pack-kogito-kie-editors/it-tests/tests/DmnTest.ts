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

const TEST_NAME = "DmnTest";

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

  // open dmn sample
  const dmnFile = await tools.find(By.css("a[title='test.dmn'] ")).getElement();
  await dmnFile.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // test basic bpmn editor functions
  await tools.command().testSampleDmnInEditor();

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
