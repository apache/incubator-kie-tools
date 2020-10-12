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
import OnlineEditorPage from "../framework/online-editor/OnlineEditorPage";
import SideBar from "../framework/editor/SideBar";
import Tools from "../utils/Tools";

const TEST_NAME = "BpmnOpenOnlineEditorTest";

let tools: Tools;

beforeEach(async () => {
    tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
    const bpmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, "https://github.com/kiegroup/" +
        "kogito-examples/blob/stable/process-business-rules-quarkus/src/main/resources/org/acme/travels/persons.bpmn");
    const onlineEditorPage: OnlineEditorPage = await bpmnPage.openOnlineEditor();
    expect(await onlineEditorPage.getFileName()).toEqual("persons");
    const onlineEditor: BpmnEditor = await onlineEditorPage.getBpmnEditor();
    await onlineEditor.enter();
    const onlineEditorSideBar: SideBar = await onlineEditor.getSideBar();
    const onlineEditorExplorer: Explorer = await onlineEditorSideBar.openExplorer();
    expect((await onlineEditorExplorer.getNodeNames()).sort())
        .toEqual([
            "StartProcess",
            "End Event 1",
            "End Event 2",
            "Evaluate Person",
            "Exclusive Gateway 1",
            "Special handling for children"
        ].sort());
});

afterEach(async () => {
    await tools.finishTest();
});
