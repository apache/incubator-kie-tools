/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import BpmnEditor from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/bpmn/BpmnEditor";
import Explorer from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/Explorer";
import GitHubPrPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-pr/GitHubPrPage";
import SideBar from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/SideBar";
import Tools from "@kie-tools/chrome-extension-test-helper/dist/utils/Tools";

const TEST_NAME = "BpmnPrTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  // TODO create PR in Apache KIE
  const PR_WEB_PAGE = "https://github.com/tomasdavidorg/chrome-extension-pr-test/pull/2/files";

  // open PR and check that source is opened
  const gitHubPrPage: GitHubPrPage = await tools.openPage(GitHubPrPage, PR_WEB_PAGE);
  expect(await gitHubPrPage.isSourceOpened()).toBe(true);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(false);

  // open diagram and check
  await gitHubPrPage.seeAsDiagram();
  expect(await gitHubPrPage.isSourceOpened()).toBe(false);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(true);

  // check editor with changes
  const changesEditor: BpmnEditor = await gitHubPrPage.getBpmnEditor();
  await gitHubPrPage.scrollToPrHeader();
  await changesEditor.enter();
  const sideBar: SideBar = await changesEditor.getSideBar();
  const exlorer: Explorer = await sideBar.openExplorer();
  expect((await exlorer.getNodeNames()).sort()).toEqual(["Start", "Task", "End", "Intermediate Timer"].sort());
  await changesEditor.leave();

  // check editor with original
  await gitHubPrPage.original();
  const originalEditor: BpmnEditor = await gitHubPrPage.getBpmnEditor();
  await gitHubPrPage.scrollToPrHeader();
  await originalEditor.enter();
  const originalSideBar: SideBar = await originalEditor.getSideBar();
  const originalExlorer: Explorer = await originalSideBar.openExplorer();
  expect((await originalExlorer.getNodeNames()).sort()).toEqual(["Start", "Task", "End"].sort());
  await originalEditor.leave();

  // close diagram and check that source is opened
  await gitHubPrPage.closeDiagram();
  expect(await gitHubPrPage.isSourceOpened()).toBe(true);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
