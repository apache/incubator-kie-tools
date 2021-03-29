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

const TEST_NAME = "DmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

afterEach(async () => {
  await tools.finishTest();
});

test(TEST_NAME, async () => {
  // open sample dmn
  await tools.open(
    "https://github.com/kiegroup/kogito-tooling/blob/master/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.dmn"
  );

  // click full screen button
  const fullScreenButton = await tools.find(By.css("[data-testid='go-fullscreen-button']")).getElement();
  await fullScreenButton.click();

  // wait and get kogito iframe
  await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();

  // test basic dmn editor functions
  await tools.command().testSampleDmnInEditor();

  // exit full screen
  await tools.window().leaveFrame();
  const exitButton = await tools.find(By.css("[data-testid='exit-fullscreen-button']")).getElement();
  await exitButton.click();

  // check full screen is closed
  expect(
    await tools
      .find(By.css(".kogito-iframe.not-fullscreen > #kogito-iframe"))
      .wait(1000)
      .isVisible()
  ).toEqual(true);
  expect(
    await tools
      .find(By.css(".kogito-iframe.fullscreen > #kogito-iframe"))
      .wait(1000)
      .isPresent()
  ).toEqual(false);
});
