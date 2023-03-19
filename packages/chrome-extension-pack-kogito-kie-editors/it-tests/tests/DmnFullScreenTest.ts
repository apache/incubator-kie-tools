/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import DecisionNavigator from "../framework/editor/dmn/DecisionNavigator";
import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import FullScreenPage from "../framework/fullscreen-editor/FullScreenPage";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const dmnUrl: string =
    "https://github.com/kiegroup/kie-tools/" +
    "blob/main/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.dmn";
  let dmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, dmnUrl);
  // open and check full screen editor
  const fullScreenPage: FullScreenPage = await dmnPage.fullScreen();
  const fullScreenEditor: DmnEditor = await fullScreenPage.getDmnEditor();
  await fullScreenEditor.enter();
  const fullScreenSideBar: DmnSideBar = await fullScreenEditor.getSideBar();
  const fullScreenExplorer: DecisionNavigator = await fullScreenSideBar.openDecisionNavigator();
  expect((await fullScreenExplorer.getNodeNames()).sort()).toEqual(
    ["MyDecision", "MyInputData", "MyModel", "Function"].sort()
  );
  await fullScreenEditor.leave();

  expect(await fullScreenPage.getExitFullScreenUrl()).toBe(dmnUrl + "#");

  await fullScreenPage.scrollToTop();
  dmnPage = await fullScreenPage.exitFullScreen();
  expect(await dmnPage.isEditorVisible()).toBe(true);
  expect(await dmnPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
