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
import GitHubPrPage from "../framework/github-pr/GitHubPrPage";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnPrTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  // TODO create PR in kiegroup
  const PR_WEB_PAGE = "https://github.com/tomasdavidorg/chrome-extension-pr-test/pull/3/files";

  // open PR and check that source is opened
  const gitHubPrPage: GitHubPrPage = await tools.openPage(GitHubPrPage, PR_WEB_PAGE);
  expect(await gitHubPrPage.isSourceOpened()).toBe(true);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(false);

  // open diagram and check
  await gitHubPrPage.seeAsDiagram();
  expect(await gitHubPrPage.isSourceOpened()).toBe(false);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(true);

  // check editor with changes
  const changesEditor: DmnEditor = await gitHubPrPage.getDmnEditor();
  await gitHubPrPage.scrollToPrHeader();
  await changesEditor.enter();
  const sideBar: DmnSideBar = await changesEditor.getSideBar();
  const navigator: DecisionNavigator = await sideBar.openDecisionNavigator();
  expect((await navigator.getNodeNames()).sort()).toEqual(
    ["Annotation", "Decision", "InputData", "Model", "Function"].sort()
  );
  await changesEditor.leave();

  // check editor with original
  await gitHubPrPage.original();
  const originalEditor: DmnEditor = await gitHubPrPage.getDmnEditor();
  await gitHubPrPage.scrollToPrHeader();
  await originalEditor.enter();
  const originalSideBar: DmnSideBar = await originalEditor.getSideBar();
  const originalNavigator: DecisionNavigator = await originalSideBar.openDecisionNavigator();
  expect((await originalNavigator.getNodeNames()).sort()).toEqual(
    ["Decision", "InputData", "Model", "Function"].sort()
  );
  await originalEditor.leave();

  // close diagram and check that source is opened
  await gitHubPrPage.closeDiagram();
  expect(await gitHubPrPage.isSourceOpened()).toBe(true);
  expect(await gitHubPrPage.isDiagramOpened()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
