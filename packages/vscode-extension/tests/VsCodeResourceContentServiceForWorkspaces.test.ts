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
import { VsCodeResourceContentServiceForWorkspaces } from "@kie-tools-core/vscode-extension/dist/workspace/VsCodeResourceContentServiceForWorkspaces";
import { TextDocument, Uri } from "vscode";
import * as vscode from "./__mocks__/vscode";
import { SearchType } from "@kie-tools-core/workspace/dist/api";

describe("VsCodeResourceContentServiceForWorkspaces.list", () => {
  const defaultExcludes = "**/{target,dist,node_modules}";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("SearchType.Traversal from workspace root", () => {
    const wsRoot = __path.resolve(__dirname, "test-workspace");
    const docUri = Uri.file(__path.join(wsRoot, "resource1.txt"));
    const listOptions = { type: SearchType.TRAVERSAL };

    const resourceContentService = new VsCodeResourceContentServiceForWorkspaces({
      workspaceRootAbsoluteFsPath: wsRoot,
      document: { uri: docUri } as unknown as TextDocument,
    });

    test("Simple pattern", async () => {
      const pattern = "*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "*.txt" }, defaultExcludes);
    });

    test("Anywhere pattern", async () => {
      const pattern = "**/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "**/*.txt" }, defaultExcludes);
    });

    test("Specific folder pattern", async () => {
      const pattern = "submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "*.txt",
        },
        defaultExcludes
      );
    });

    test("Nested specific folder pattern", async () => {
      const pattern = "**/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        { base: wsRoot, pattern: "**/submodule/*.txt" },
        defaultExcludes
      );
    });

    test("Absolute pattern", async () => {
      const pattern = "/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "*.txt",
        },
        defaultExcludes
      );
    });
  });

  describe("SearchType.Traversal from workspace submodule", () => {
    const wsRoot = __path.resolve(__dirname, "test-workspace");
    const docUri = Uri.file(__path.join(wsRoot, "submodule", "resource1.txt"));
    const listOptions = { type: SearchType.TRAVERSAL };

    const resourceContentService = new VsCodeResourceContentServiceForWorkspaces({
      workspaceRootAbsoluteFsPath: wsRoot,
      document: { uri: docUri } as unknown as TextDocument,
    });

    test("Simple pattern", async () => {
      const pattern = "*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "*.txt" }, defaultExcludes);
    });

    test("Anywhere pattern", async () => {
      const pattern = "**/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "**/*.txt" }, defaultExcludes);
    });

    test("Specific folder pattern", async () => {
      const pattern = "submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "*.txt",
        },
        defaultExcludes
      );
    });

    test("Nested specific folder pattern", async () => {
      const pattern = "**/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        { base: wsRoot, pattern: "**/submodule/*.txt" },
        defaultExcludes
      );
    });

    test("Absolute pattern", async () => {
      const pattern = "/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "*.txt",
        },
        defaultExcludes
      );
    });
  });

  describe("SearchType.AssetFolder from workspace root", () => {
    const wsRoot = __path.resolve(__dirname, "test-workspace");
    const docUri = Uri.file(__path.join(wsRoot, "resource1.txt"));
    const listOptions = { type: SearchType.ASSET_FOLDER };

    const resourceContentService = new VsCodeResourceContentServiceForWorkspaces({
      workspaceRootAbsoluteFsPath: wsRoot,
      document: { uri: docUri } as unknown as TextDocument,
    });

    test("Simple pattern", async () => {
      const pattern = "*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "*.txt" }, defaultExcludes);
    });

    test("Anywhere pattern", async () => {
      const pattern = "**/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith({ base: wsRoot, pattern: "**/*.txt" }, defaultExcludes);
    });

    test("Specific folder pattern", async () => {
      const pattern = "submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        { base: wsRoot, pattern: "submodule/*.txt" },
        defaultExcludes
      );
    });

    test("Nested specific folder pattern", async () => {
      const pattern = "**/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        { base: wsRoot, pattern: "**/submodule/*.txt" },
        defaultExcludes
      );
    });

    test("Absolute pattern", async () => {
      const pattern = "/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        { base: wsRoot, pattern: "/submodule/*.txt" },
        defaultExcludes
      );
    });
  });

  describe("SearchType.AssetFolder from workspace submodule", () => {
    const wsRoot = __path.resolve(__dirname, "test-workspace");
    const docUri = Uri.file(__path.join(wsRoot, "submodule", "resource1.txt"));
    const listOptions = { type: SearchType.ASSET_FOLDER };

    const resourceContentService = new VsCodeResourceContentServiceForWorkspaces({
      workspaceRootAbsoluteFsPath: wsRoot,
      document: { uri: docUri } as unknown as TextDocument,
    });

    test("Simple pattern", async () => {
      const pattern = "*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "*.txt",
        },
        defaultExcludes
      );
    });

    test("Anywhere pattern", async () => {
      const pattern = "**/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "**/*.txt",
        },
        defaultExcludes
      );
    });

    test("Specific folder pattern", async () => {
      const pattern = "submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "submodule/*.txt",
        },
        defaultExcludes
      );
    });

    test("Nested specific folder pattern", async () => {
      const pattern = "**/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "**/submodule/*.txt",
        },
        defaultExcludes
      );
    });

    test("Absolute pattern", async () => {
      const pattern = "/submodule/*.txt";
      await resourceContentService.list(pattern, listOptions);

      expect(vscode.workspace.findFiles).toHaveBeenCalledWith(
        {
          base: __path.join(wsRoot, "submodule"),
          pattern: "/submodule/*.txt",
        },
        defaultExcludes
      );
    });
  });
});
