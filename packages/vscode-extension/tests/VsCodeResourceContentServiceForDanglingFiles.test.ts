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

import * as __path from "path";
import { VsCodeResourceContentServiceForDanglingFiles } from "@kie-tools-core/vscode-extension/dist/workspace/VsCodeResourceContentServiceForDanglingFiles";
import { ContentType } from "@kie-tools-core/workspace/dist/api";

const testWorkspaceAbsoluteFsPath = __path.resolve(__dirname, "test-workspace") + __path.sep;

describe("VsCodeResourceContentServiceForDanglingFiles", () => {
  test("Test list", async () => {
    const resourceContentService = new VsCodeResourceContentServiceForDanglingFiles({
      openFileAbsoluteFsPath: __path.join(testWorkspaceAbsoluteFsPath, "myFile.txt"),
    });
    const txtPattern = "*.txt";

    const resourcesListWithAssets = await resourceContentService.list(txtPattern);

    expect(resourcesListWithAssets).not.toBeNull();
    expect(resourcesListWithAssets.pattern).toBe(txtPattern);
    expect(resourcesListWithAssets.normalizedPosixPathsRelativeToTheWorkspaceRoot).toHaveLength(2);
    expect(resourcesListWithAssets.normalizedPosixPathsRelativeToTheWorkspaceRoot).toContain("resource1.txt");
    expect(resourcesListWithAssets.normalizedPosixPathsRelativeToTheWorkspaceRoot).toContain("resource2.txt");

    const pdfPattern = "*.pdf";
    const resourcesListEmpty = await resourceContentService.list(pdfPattern);
    expect(resourcesListEmpty).not.toBeNull();
    expect(resourcesListEmpty.pattern).toBe(pdfPattern);
    expect(resourcesListEmpty.normalizedPosixPathsRelativeToTheWorkspaceRoot).toHaveLength(0);
  });

  test("Test list with errors", async () => {
    const resourceContentService = new VsCodeResourceContentServiceForDanglingFiles({
      openFileAbsoluteFsPath: __path.resolve("/probably/an/unexisting/path/myFile.txt"),
    });

    const pattern = "*.txt";
    const resourcesList = await resourceContentService.list(pattern);

    expect(resourcesList).not.toBeNull();
    expect(resourcesList.pattern).toBe(pattern);
    expect(resourcesList.normalizedPosixPathsRelativeToTheWorkspaceRoot).toHaveLength(0);
  });

  test("Test get", async () => {
    const resourceContentService = new VsCodeResourceContentServiceForDanglingFiles({
      openFileAbsoluteFsPath: __path.join(testWorkspaceAbsoluteFsPath, "myFile.txt"),
    });

    const resource1Path = "resource1.txt";
    const resource1Content = await resourceContentService.get(resource1Path);

    expect(resource1Content).not.toBeNull();
    expect(resource1Content?.normalizedPosixPathRelativeToTheWorkspaceRoot).toBe(resource1Path);
    expect(resource1Content?.type).toBe(ContentType.TEXT);
    expect(resource1Content?.content).toBe("content for resource 1");

    const resource2Path = "resource2.txt";
    const resource2Content = await resourceContentService.get(resource2Path);

    expect(resource2Content).not.toBeNull();
    expect(resource2Content?.normalizedPosixPathRelativeToTheWorkspaceRoot).toBe(resource2Path);
    expect(resource2Content?.type).toBe(ContentType.TEXT);
    expect(resource2Content?.content).toBe("content for resource 2");

    const iconPath = "icon.png";
    const iconContent = await resourceContentService.get(iconPath, { type: ContentType.BINARY });

    expect(iconContent).not.toBeNull();
    expect(iconContent?.normalizedPosixPathRelativeToTheWorkspaceRoot).toBe(iconPath);
    expect(iconContent?.type).toBe(ContentType.BINARY);
    expect(iconContent?.content).not.toBeNull();
  });

  test("Test get with errors", async () => {
    const resourceContentService = new VsCodeResourceContentServiceForDanglingFiles({
      openFileAbsoluteFsPath: __path.resolve("/probably/an/unexisting/path/myFile.txt"),
    });

    const txtResourcePath = "resource1.txt";
    const txtResourceContent = await resourceContentService.get(txtResourcePath);

    expect(txtResourceContent).not.toBeNull();
    expect(txtResourceContent?.normalizedPosixPathRelativeToTheWorkspaceRoot).toBe(txtResourcePath);
    expect(txtResourceContent?.type).toBe(ContentType.TEXT);
    expect(txtResourceContent?.content).toBe(undefined);

    const binaryPath = "icon.png";
    const binaryContent = await resourceContentService.get(binaryPath, { type: ContentType.BINARY });

    expect(binaryContent).not.toBeNull();
    expect(binaryContent?.normalizedPosixPathRelativeToTheWorkspaceRoot).toBe(binaryPath);
    expect(binaryContent?.type).toBe(ContentType.BINARY);
    expect(binaryContent?.content).toBe(undefined);
  });
});
