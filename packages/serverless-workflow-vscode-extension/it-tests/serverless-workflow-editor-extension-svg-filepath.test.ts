/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as path from "path";
import * as fs from "fs";
import { expect } from "chai";
import { VSCodeTestHelper, sleep } from "@kie-tools/vscode-extension-common-test-helpers";

describe("Serverless workflow editor - SVG generation with path setting integration tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "svg-filepath");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

  const FILE_NAME_NO_EXTENSION: string = "hello-world";
  const WORKFLOW_NAME: string = `${FILE_NAME_NO_EXTENSION}.sw.json`;
  const RESOURCE_FOLDER: string = path.join("src", "main", "resources");

  const IS_VALID_SVG_REGEX = new RegExp("<svg.*<\\/svg>");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER);
  });

  beforeEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.takeScreenshotOnTestFailure(this, DIST_IT_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it(`Opens ${WORKFLOW_NAME}, saves it, and verifies SVG generation`, async function () {
    this.timeout(30000);

    const svgName = `${FILE_NAME_NO_EXTENSION}.svg`;

    const editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME, RESOURCE_FOLDER);

    await testHelper.saveFileInTextEditor();

    // verify SVG was generated after file save
    const SVG_FILE_PATH: string = path.resolve(TEST_PROJECT_FOLDER, RESOURCE_FOLDER, svgName);
    expect(fs.readFileSync(SVG_FILE_PATH, "utf-8")).to.match(
      IS_VALID_SVG_REGEX,
      `SVG file was not generated correctly at path: ${SVG_FILE_PATH}.`
    );
  });

  it(`Changes settings, opens ${WORKFLOW_NAME}, saves it, and verifies SVG generation`, async function () {
    this.timeout(60000);

    const svgNameAddition = "-changed";
    const svgName = `${FILE_NAME_NO_EXTENSION}${svgNameAddition}.svg`;
    const changedFilename = `\${fileBasenameNoExtension}${svgNameAddition}.svg`;
    const changedDirectory = path.join(RESOURCE_FOLDER, "META-INF", "processSVG");

    // set different filename and file path values in VSCode settings and retrieve default values
    const settingValuesToSet = [
      {
        settingValue: changedFilename,
        settingName: "Svg Filename Template",
        settingCategories: ["Kogito", "Swf"],
      },
      {
        settingValue: path.join("${workspaceFolder}", changedDirectory),
        settingName: "Svg File Path",
        settingCategories: ["Kogito", "Swf"],
      },
    ];
    const [previousSettingFilename, previousSettingFilePath] = (await testHelper.setVSCodeSettings(
      ...settingValuesToSet
    )) as string[];

    await testHelper.openFileFromSidebar(WORKFLOW_NAME, RESOURCE_FOLDER);

    // save file and wait for the SVG generation
    await testHelper.saveFileInTextEditor();
    await sleep(1000);

    // verify SVG was generated after file save
    const SVG_FILE_PATH: string = path.resolve(TEST_PROJECT_FOLDER, changedDirectory, svgName);
    expect(fs.readFileSync(SVG_FILE_PATH, "utf-8")).to.match(
      IS_VALID_SVG_REGEX,
      `SVG file was not generated correctly at path: ${SVG_FILE_PATH}.`
    );

    // set back the previous setting values
    const settingValuesToRestore = [
      {
        settingValue: previousSettingFilename,
        settingName: "Svg Filename Template",
        settingCategories: ["Kogito", "Swf"],
      },
      {
        settingValue: previousSettingFilePath,
        settingName: "Svg File Path",
        settingCategories: ["Kogito", "Swf"],
      },
    ];
    await testHelper.setVSCodeSettings(...settingValuesToRestore);
  });
});
