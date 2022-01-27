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
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import GitHubListItem from "../framework/github-file-list/GitHubListItem";
import GitHubListPage from "../framework/github-file-list/GitHubListPage";
import Properties from "../framework/editor/Properties";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const WEB_PAGE =
    "https://github.com/kiegroup/kie-tools/tree/main/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples";
  const PROCESS_NAME = "myProcess";
  const FILE_NAME = "test.bpmn";
  const TASK_NODE_NAME = "MyTask";

  const gitHubListPage: GitHubListPage = await tools.openPage(GitHubListPage, WEB_PAGE);
  const gitHubFile: GitHubListItem = await gitHubListPage.getFile(FILE_NAME);

  // open BPMN editor
  const editorPage: GitHubEditorPage = await gitHubFile.open();
  const bpmnEditor: BpmnEditor = await editorPage.getBpmnEditor();

  // check process properties
  await bpmnEditor.enter();
  const sideBar: SideBar = await bpmnEditor.getSideBar();
  const processProps: Properties = await sideBar.openProperties();
  expect(await processProps.getProcessNameFromInput()).toEqual(PROCESS_NAME);

  // check process nodes in explorer
  const explorer: Explorer = await sideBar.openExplorer();
  expect((await explorer.getNodeNames()).sort()).toEqual(["MyStart", "MyTask", "MyEnd"].sort());
  expect(await explorer.getProcessName()).toEqual(PROCESS_NAME);

  // check task properties
  await explorer.selectNode(TASK_NODE_NAME);
  const nodeProps: Properties = await sideBar.openProperties();
  expect(await nodeProps.getNameFromTextArea()).toEqual(TASK_NODE_NAME);

  // check pallete is not visible
  expect(await bpmnEditor.isPalettePresent()).toEqual(false);

  await bpmnEditor.leave();

  // open and check source/editor
  expect(await editorPage.isSourceVisible()).toBe(false);
  expect(await editorPage.isEditorVisible()).toBe(true);
  await editorPage.seeAsSource();
  expect(await editorPage.isSourceVisible()).toBe(true);
  expect(await editorPage.isEditorVisible()).toBe(false);
  await editorPage.seeAsDiagram();
  expect(await editorPage.isSourceVisible()).toBe(false);
  expect(await editorPage.isEditorVisible()).toBe(true);
});

afterEach(async () => {
  await tools.finishTest();
});
