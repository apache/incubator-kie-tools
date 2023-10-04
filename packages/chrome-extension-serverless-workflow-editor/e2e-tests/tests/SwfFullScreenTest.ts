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

import SwfEditor from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/swf/SwfEditor";
import FullScreenPage from "@kie-tools/chrome-extension-test-helper/dist/framework/fullscreen-editor/FullScreenPage";
import GitHubEditorPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-editor/GitHubEditorPage";
import Tools from "@kie-tools/chrome-extension-test-helper/dist/utils/Tools";

const TEST_NAME = "SwfFullScreenTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const workflowUrl: string =
    "https://github.com/kiegroup/kie-tools/blob/main/packages/chrome-extension-serverless-workflow-editor/e2e-tests/samples/chrome_sample.sw.yaml";
  let swfPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, workflowUrl);
  const fullScreenPage: FullScreenPage = await swfPage.fullScreen();
  const fullScreenEditor: SwfEditor = await fullScreenPage.getSwfEditor();
  await fullScreenEditor.enter();

  expect(await fullScreenEditor.isTextEditorPresent()).toBe(true);
  expect(await fullScreenEditor.isDiagramEditorPresent()).toBe(true);

  expect(await fullScreenEditor.isTextEditorKeyboardShortcutsIconPresent()).toBe(true);
  expect(await fullScreenEditor.isDiagramEditorKeyboardShortcutsIconPresent()).toBe(true);

  const editorContent: string = await fullScreenEditor.getTextEditorContent();
  expect(editorContent).toContain('id: "chrome_extension_sample_yaml"');
  expect(editorContent).toContain('name: "Chrome Extension Sample YAML"');
  expect(editorContent).toContain('description: "This YAML sample is created for testing purposes."');

  await fullScreenEditor.leave();

  expect(await fullScreenPage.getExitFullScreenUrl()).toBe(workflowUrl + "#");

  await fullScreenPage.scrollToTop();
  swfPage = await fullScreenPage.exitFullScreen();
  expect(await swfPage.isEditorVisible()).toBe(true);
  expect(await swfPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
