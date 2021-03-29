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

const TEST_NAME = "DmnCopyLinkTest";

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

  // click copy link to online editor button
  const copyLinkButton = await tools.find(By.css("[data-testid='copy-link-button']")).getElement();
  await copyLinkButton.click();

  // open online editor from clipboard content
  await tools.open(await tools.clipboard().getContent());

  // wait and get kogito iframe
  const iframe = await tools.command().getEditor();

  // wait util loading dialog disappears
  await tools.command().loadEditor();
  await tools.window().leaveFrame();

  // close tour
  const closeTourButton = await tools.find(By.xpath("//button[@data-kgt-close]")).getElement();
  await closeTourButton.click();
  await iframe.enterFrame();

  // test basic dmn editor functions
  await tools.command().testSampleDmnInEditor();

  // check dmn name on the top
  await tools.window().leaveFrame();
  const titleName = await tools.find(By.css("[data-testid='toolbar-title'] > input")).getElement();
  expect(await titleName.getAttribute("value")).toEqual("test");
});
