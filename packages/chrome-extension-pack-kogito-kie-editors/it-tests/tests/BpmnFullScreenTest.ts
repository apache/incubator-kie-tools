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

import BpmnEditor from "../framework/editor/bpmn/BpmnEditor";
import Explorer from "../framework/editor/Explorer";
import FullScreenPage from "../framework/fullscreen-editor/FullScreenPage";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const processUrl: string =
    "https://github.com/kiegroup/kie-tools/" +
    "blob/main/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.bpmn";
  let bpmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, processUrl);
  const fullScreenPage: FullScreenPage = await bpmnPage.fullScreen();
  const fullScreenEditor: BpmnEditor = await fullScreenPage.getBpmnEditor();
  await fullScreenEditor.enter();
  const fullScreenSideBar: SideBar = await fullScreenEditor.getSideBar();
  const fullScreenExplorer: Explorer = await fullScreenSideBar.openExplorer();
  expect((await fullScreenExplorer.getNodeNames()).sort()).toEqual(["MyStart", "MyTask", "MyEnd"].sort());
  await fullScreenEditor.leave();

  expect(await fullScreenPage.getExitFullScreenUrl()).toBe(processUrl + "#");

  await fullScreenPage.scrollToTop();
  bpmnPage = await fullScreenPage.exitFullScreen();
  expect(await bpmnPage.isEditorVisible()).toBe(true);
  expect(await bpmnPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
