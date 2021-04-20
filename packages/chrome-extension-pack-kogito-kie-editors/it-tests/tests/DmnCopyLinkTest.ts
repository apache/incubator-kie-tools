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

import DecisionNavigator from "../framework/editor/dmn/DecisionNavigator";
import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import Properties from "../framework/editor/Properties";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";
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
  const dmnGitHubEditorPage: GitHubEditorPage = await tools.openPage(
    GitHubEditorPage,
    "https://github.com/kiegroup/kogito-tooling/blob/master/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.dmn"
  );
  await dmnGitHubEditorPage.copyLinkToOnlineEditor();
  const clipboardUrl: string = await tools.clipboard().getContent();

  const onlineEditorPage: OnlineEditorPage = await tools.openPage(OnlineEditorPage, clipboardUrl);
  expect(await onlineEditorPage.getFileName()).toEqual("test");
  const onlineEditor: DmnEditor = await onlineEditorPage.getDmnEditor();
  await onlineEditorPage.closeTour();
  await onlineEditor.enter();
  const dmnSideBar: DmnSideBar = await onlineEditor.getSideBar();
  const onlineProperties: Properties = await dmnSideBar.openProperties();
  expect(await onlineProperties.getDmnNameFromInput()).toEqual("myDmn");

  const decisionNavigator: DecisionNavigator = await onlineEditor.openLeftSideBar();
  expect((await decisionNavigator.getNodeNames()).sort()).toEqual(
    ["MyDecision", "MyInputData", "MyModel", "Function"].sort()
  );
  expect(await decisionNavigator.getDmnName()).toEqual("myDmn");
});
