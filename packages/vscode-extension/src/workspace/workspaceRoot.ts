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

import * as vscode from "vscode";
import * as __path from "path";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";

export function getNormalizedPosixPathRelativeToWorkspaceRoot(document: KogitoEditorDocument["document"]) {
  return vscode.workspace.workspaceFolders
    ? vscode.workspace.asRelativePath(document.uri, false)
    : __path.relative(__path.dirname(document.uri.path), document.uri.path);
}

export function getWorkspaceRoot(document: KogitoEditorDocument["document"]): {
  workspaceRootAbsoluteFsPath: string;
  type: "dangling" | "workspace";
} {
  const workspaceFolder = vscode.workspace.workspaceFolders?.find((wf) =>
    document.uri.fsPath.startsWith(wf.uri.fsPath)
  );

  if (workspaceFolder) {
    return {
      type: "workspace",
      workspaceRootAbsoluteFsPath: workspaceFolder.uri.fsPath,
    };
  } else {
    return {
      type: "dangling",
      workspaceRootAbsoluteFsPath: __path.dirname(document.uri.path),
    };
  }
}

/**
 * The "vscode.workspace.workspaceFolders" returns a POSIX path with a starting "/" on Windows machines
 * This function removes the starting "/" and normalizes the path, returning a compatible absolute FS path.
 */

export function normalizeWindowsWorkspaceRootAbsoluteFsPath(workspaceRootAbsoluteFsPath: string): string {
  // The vscode.env.uiKind returns 1 for desktop applications, enabling the usage of the process.platform
  if (vscode.env.uiKind === 1 && process.platform === "win32" && workspaceRootAbsoluteFsPath.startsWith("/")) {
    return __path.normalize(workspaceRootAbsoluteFsPath.slice(1));
  }
  return workspaceRootAbsoluteFsPath;
}
