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

import Explorer from "../framework/editor/Explorer";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";

const TEST_NAME = "BpmnCopyLinkTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

afterEach(async () => {
  await tools.finishTest();
});

test(TEST_NAME, async () => {
  const bpmnPage: GitHubEditorPage = await tools.openPage(
    GitHubEditorPage,
    "https://github.com/kiegroup/kogito-tooling/blob/master/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.bpmn"
  );
  await bpmnPage.copyLinkToOnlineEditor();
  const linkToOnlineEditor: string = await tools.clipboard().getContent();

  const onlineEditorPage = await tools.openPage(OnlineEditorPage, linkToOnlineEditor);
  expect(await onlineEditorPage.getFileName()).toEqual("test");
  const onlineEditor = await onlineEditorPage.getBpmnEditor();
  await onlineEditor.enter();
  const onlineEditorSideBar: SideBar = await onlineEditor.getSideBar();
  const onlineEditorExplorer: Explorer = await onlineEditorSideBar.openExplorer();
  expect(await onlineEditorExplorer.getNodeNames()).toEqual(["MyStart", "MyTask", "MyEnd"]);
});
