/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as __path from "path";
import { VsCodeNodeResourceContentService } from "../VsCodeNodeResourceContentService";
import { ContentType } from "@kogito-tooling/editor-envelope-protocol";

const testWorkspace = __path.resolve(__dirname, "test-workspace") + __path.sep;

let resourceContentServie: VsCodeNodeResourceContentService;

describe("VsCodeNodeResourceContentService", () => {

  beforeEach(() => {
    resourceContentServie = new VsCodeNodeResourceContentService(testWorkspace);
  });

  test("Test list", async () => {
    const txtPattern = "*.txt";

    const resourcesListWithAssets = await resourceContentServie.list(txtPattern);

    expect(resourcesListWithAssets).not.toBeNull();
    expect(resourcesListWithAssets.pattern).toBe(txtPattern);
    expect(resourcesListWithAssets.paths).toHaveLength(2);
    expect(resourcesListWithAssets.paths).toContain(testWorkspace + "resource1.txt");
    expect(resourcesListWithAssets.paths).toContain(testWorkspace + "resource2.txt");

    const pdfPattern = "*.pdf";
    const resourcesListEmpty = await resourceContentServie.list(pdfPattern);
    expect(resourcesListEmpty).not.toBeNull();
    expect(resourcesListEmpty.pattern).toBe(pdfPattern);
    expect(resourcesListEmpty.paths).toHaveLength(0);
  });

  test("Test list with errors", async () => {
    resourceContentServie = new VsCodeNodeResourceContentService("/probably/an/unexisting/path/");

    const pattern = "*.txt";
    const resourcesList = await resourceContentServie.list(pattern);

    expect(resourcesList).not.toBeNull();
    expect(resourcesList.pattern).toBe(pattern);
    expect(resourcesList.paths).toHaveLength(0);
  });

  test("Test get", async () => {
    const resource1Path = "resource1.txt";
    const resource1Content = await resourceContentServie.get(resource1Path);

    expect(resource1Content).not.toBeNull();
    expect(resource1Content?.path).toBe(resource1Path);
    expect(resource1Content?.type).toBe(ContentType.TEXT);
    expect(resource1Content?.content).toBe("content for resource 1");

    const resource2Path = "resource2.txt";
    const resource2Content = await resourceContentServie.get(resource2Path);

    expect(resource2Content).not.toBeNull();
    expect(resource2Content?.path).toBe(resource2Path);
    expect(resource2Content?.type).toBe(ContentType.TEXT);
    expect(resource2Content?.content).toBe("content for resource 2");

    const iconPath = "icon.png";
    const iconContent = await resourceContentServie.get(iconPath, { type: ContentType.BINARY });

    expect(iconContent).not.toBeNull();
    expect(iconContent?.path).toBe(iconPath);
    expect(iconContent?.type).toBe(ContentType.BINARY);
    expect(iconContent?.content).not.toBeNull();
  });

  test("Test get with errors", async () => {
    resourceContentServie = new VsCodeNodeResourceContentService("/probably/an/unexisting/path/");

    const txtResourcePath = "resource1.txt";
    const txtResourceContent = await resourceContentServie.get(txtResourcePath);

    expect(txtResourceContent).not.toBeNull();
    expect(txtResourceContent?.path).toBe(txtResourcePath);
    expect(txtResourceContent?.type).toBe(ContentType.TEXT);
    expect(txtResourceContent?.content).toBe(undefined);

    const binaryPath = "icon.png";
    const binaryContent = await resourceContentServie.get(binaryPath, { type: ContentType.BINARY });

    expect(binaryContent).not.toBeNull();
    expect(binaryContent?.path).toBe(binaryPath);
    expect(binaryContent?.type).toBe(ContentType.BINARY);
    expect(binaryContent?.content).toBe(undefined);
  });
});