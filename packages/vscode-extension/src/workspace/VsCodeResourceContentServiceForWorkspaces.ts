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

import {
  ResourceContent,
  ResourceContentOptions,
  ResourceContentService,
  ResourceListOptions,
  ResourcesList,
  SearchType,
} from "@kie-tools-core/workspace/dist/api";

import * as __path from "path";
import * as vscode from "vscode";
import { RelativePattern } from "vscode";
import { toFsPath } from "../paths/paths";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";
import { getNormalizedPosixPathRelativeToWorkspaceRoot } from "./workspaceRoot";

/**
 * Implementation of a ResourceContentService using the vscode apis to list/get assets.
 */
export class VsCodeResourceContentServiceForWorkspaces implements ResourceContentService {
  constructor(
    private readonly args: { workspaceRootAbsoluteFsPath: string; document: KogitoEditorDocument["document"] }
  ) {}

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    if (!vscode.workspace.workspaceFolders?.length) {
      throw new Error("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: No workspaces found.");
    }

    const baseAbsoluteFsPath =
      opts?.type === SearchType.ASSET_FOLDER
        ? __path.join(
            this.args.workspaceRootAbsoluteFsPath,
            __path.dirname(getNormalizedPosixPathRelativeToWorkspaceRoot(this.args.document))
          )
        : this.args.workspaceRootAbsoluteFsPath;

    // Normalize incoming pattern to POSIX for VS Code globbing
    const posixPattern = pattern.replace(/\\/g, "/");
    const { leadingFolderOfPattern, trailingPattern } =
      this.splitPosixPatternForLeadingFolderAndTrailingPattern(posixPattern);

    let theMostSpecificFolder = baseAbsoluteFsPath;
    let globRelativeToBase = posixPattern;

    // In case of ASSET_FOLDER we are done, see baseAbsoluteFsPath creation
    if (opts?.type !== SearchType.ASSET_FOLDER) {
      theMostSpecificFolder = leadingFolderOfPattern
        ? __path.join(baseAbsoluteFsPath, leadingFolderOfPattern)
        : baseAbsoluteFsPath;
      globRelativeToBase = trailingPattern || posixPattern;
    }

    const relativePattern = new RelativePattern(theMostSpecificFolder, globRelativeToBase);

    const defaultExcludes = "**/{target,dist,node_modules}";
    const vscodeNormalizedPosixPathsRelativeToTheBasePath = (
      await vscode.workspace.findFiles(relativePattern, defaultExcludes)
    ).map((uri) => vscode.workspace.asRelativePath(uri, false).replace(/\\/g, "/"));

    console.debug(
      "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: VS Code found files %s",
      vscodeNormalizedPosixPathsRelativeToTheBasePath
    );

    return new ResourcesList(pattern, vscodeNormalizedPosixPathsRelativeToTheBasePath);
  }

  /**
   * Detects if a path segment contains glob "magic" characters (e.g., *, ?, [], {}, !, or extglobs like +(…), *(…), ?(…), !(…)).
   * @param pathSegment - The path segment to check.
   * @returns True if the segment contains glob magic, false otherwise.
   */
  private hasGlobMagic(pathSegment: string): boolean {
    // *, ?, [], {}, !, and simple extglobs like +(…), *(…), ?(…), !(…)
    return /[*?[\]{},!]/.test(pathSegment) || /\([^)]+\)/.test(pathSegment);
  }

  /**
   * Splits a POSIX-style path (e.g., "a/b/c") into its non-empty segments ["a", "b", "c"].
   * @param path - The POSIX path string to split.
   * @returns An array of non-empty path segments.
   */
  private splitPosixSegments(path: string): string[] {
    return path.split("/").filter((s) => s.length > 0);
  }

  /**
   * Given a posix pattern (e.g. org/model/*.txt), returns:
   *  - staticPrefix (org/model): leading directory segments with no glob magic, may be empty
   *  - remainingGlob (*.txt): the rest of the pattern (dirs + filename) relative to staticPrefix
   */
  private splitPosixPatternForLeadingFolderAndTrailingPattern(pattern: string): {
    leadingFolderOfPattern: string;
    trailingPattern: string;
  } {
    const segments = this.splitPosixSegments(pattern);

    if (segments.length === 1) {
      return { leadingFolderOfPattern: "", trailingPattern: segments[0] };
    }

    let i = 0;
    while (i < segments.length && !this.hasGlobMagic(segments[i])) {
      i++;
    }

    const leadingFolderOfPattern = segments.slice(0, i).join("/");
    const trailingPattern = segments.slice(i).join("/") || "";

    return { leadingFolderOfPattern, trailingPattern };
  }

  public async get(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    opts?: ResourceContentOptions
  ): Promise<ResourceContent | undefined> {
    if (!vscode.workspace.workspaceFolders?.length) {
      throw new Error("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: No workspaces found.");
    }

    if (__path.isAbsolute(normalizedPosixPathRelativeToTheWorkspaceRoot)) {
      throw new Error(
        `VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Can't work with absolute paths. All paths must be relative to the workspace root.
Normalized POSIX path relative to the workspace root: ${normalizedPosixPathRelativeToTheWorkspaceRoot}`
      );
    }

    const workspaceRootAbsoluteFsPath = vscode.workspace.workspaceFolders[0].uri.fsPath;
    const normalizedFsPathRelativeToTheWorkspaceRoot = toFsPath(normalizedPosixPathRelativeToTheWorkspaceRoot);

    const absoluteFsPath = __path.join(workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot);

    if (__path.resolve(workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot) !== absoluteFsPath) {
      throw new Error(
        `VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Path relative to the workspace root trying to access files outside the workspace.
Absolute FS path: ${absoluteFsPath}
Resolved path: ${__path.resolve(workspaceRootAbsoluteFsPath, normalizedFsPathRelativeToTheWorkspaceRoot)}
`
      );
    }

    try {
      const content = await vscode.workspace.fs.readFile(vscode.Uri.file(absoluteFsPath));

      if (opts?.type === "binary") {
        return new ResourceContent(
          normalizedPosixPathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString("base64"),
          "binary"
        );
      } else {
        return new ResourceContent(
          normalizedPosixPathRelativeToTheWorkspaceRoot, // Always return the relative path.
          Buffer.from(content).toString(),
          "text"
        );
      }
    } catch (e) {
      console.error(
        `VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Error reading file ${normalizedPosixPathRelativeToTheWorkspaceRoot}. Returning undefined.`,
        e
      );
      return new ResourceContent(normalizedPosixPathRelativeToTheWorkspaceRoot, undefined, opts?.type);
    }
  }
}
