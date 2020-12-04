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

import DmnEditor from "../framework/editor/dmn/DmnEditor";
import DmnSideBar from "../framework/editor/dmn/DmnSideBar";
import GitHubEditorPage from "../framework/github-editor/GitHubEditorPage";
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";
import Properties from "../framework/editor/Properties";
import Tools from "../utils/Tools";

const TEST_NAME = "DmnOpenOnlineEditorTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const dmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, "https://github.com/kiegroup/" +
        "kogito-tooling/blob/master/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.dmn");
    const onlineEditorPage: OnlineEditorPage = await dmnPage.openOnlineEditor();
    expect(await onlineEditorPage.getFileName()).toEqual("test");
    const onlineEditor: DmnEditor = await onlineEditorPage.getDmnEditor();
    await onlineEditor.enter();
    const onlineEditorSideBar: DmnSideBar = await onlineEditor.getSideBar();
    const onlineProperties: Properties = await onlineEditorSideBar.openProperties();
    expect((await onlineProperties.getDmnNameFromInput())).toEqual("myDmn");
    console.log("Current URL is: " + await tools.getCurrentUrl());
});

afterEach(async () => {
    await tools.finishTest();
});
