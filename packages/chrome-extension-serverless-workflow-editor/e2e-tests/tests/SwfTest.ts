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

import Tools from "@kie-tools/chrome-extension-test-helper/dist/utils/Tools";
import GitHubListItem from "@kie-tools/chrome-extension-test-helper/dist/framework/github-file-list/GitHubListItem";
import GitHubListPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-file-list/GitHubListPage";
import GitHubEditorPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-editor/GitHubEditorPage";
import GitHubRepoPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-repo/GitHubRepoPage";
import SwfEditor from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/swf/SwfEditor";

// @ts-ignore
import { env } from "../../env";

const TEST_NAME = "SwfTest";

let tools: Tools;

const buildEnv: any = env;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);

  if (buildEnv.swfChromeExtension.e2eTestingToken !== "") {
    const gitHubRepoPage: GitHubRepoPage = await tools.openPage(
      GitHubRepoPage,
      "https://github.com/apache/incubator-kie-tools"
    );
    await gitHubRepoPage.addToken(buildEnv.swfChromeExtension.e2eTestingToken);
  }
});

test(TEST_NAME, async () => {
  const gitHubListPage: GitHubListPage = await tools.openPage(
    GitHubListPage,
    "https://github.com/apache/incubator-kie-tools/tree/main/packages/chrome-extension-serverless-workflow-editor/e2e-tests/samples"
  );
  const gitHubFile: GitHubListItem = await gitHubListPage.getFile("chrome_sample.sw.json");
  const editorPage: GitHubEditorPage = await gitHubFile.open();

  const swfEditor: SwfEditor = await editorPage.getSwfEditor();

  await swfEditor.enter();

  expect(await swfEditor.isTextEditorPresent()).toBe(true);
  expect(await swfEditor.isDiagramEditorPresent()).toBe(true);

  expect(await swfEditor.isTextEditorKeyboardShortcutsIconPresent()).toBe(true);
  expect(await swfEditor.isDiagramEditorKeyboardShortcutsIconPresent()).toBe(true);

  const editorContent: string = await swfEditor.getTextEditorContent();
  expect(editorContent).toContain('"id": "chrome_extension_sample_json",');
  expect(editorContent).toContain('"name": "Chrome Extension Sample JSON",');
  expect(editorContent).toContain('"description": "This JSON sample is created for testing purposes.",');

  await swfEditor.leave();

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
