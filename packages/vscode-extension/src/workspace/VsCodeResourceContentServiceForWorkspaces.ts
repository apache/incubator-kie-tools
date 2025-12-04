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

import { listFiles } from "isomorphic-git";
import { Minimatch } from "minimatch";
import * as __path from "path";
import * as vscode from "vscode";
import { RelativePattern } from "vscode";
import { toFsPath } from "../paths/paths";
import { KogitoEditorDocument } from "../VsCodeKieEditorController";
import { ReadonlyIsomorphicGitFsForVsCodeWorkspaceFolders } from "./VsCodeResourceContentServiceIsomorphicGitFs";
import { getNormalizedPosixPathRelativeToWorkspaceRoot } from "./workspaceRoot";

/**
 * Implementation of a ResourceContentService using the vscode apis to list/get assets.
 */
export class VsCodeResourceContentServiceForWorkspaces implements ResourceContentService {
  private readonly isomorphicGitFs = new ReadonlyIsomorphicGitFsForVsCodeWorkspaceFolders();

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

    // The vscode API will read all files, including the .gitignore ones,
    // making this action is less performatic than the git ls-files which will
    // automatically exclude the .gitignore files.
    try {
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Trying to use isomorphic-git to read dir.");
      const normalizedPosixPathsRelativeToTheBasePath = await listFiles({
        fs: this.isomorphicGitFs,
        dir: this.args.workspaceRootAbsoluteFsPath,
      });
      console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Success on using isomorphic-git!");

      const minimatch = new Minimatch(pattern);
      const regexp = minimatch.makeRe(); // The regexp is ~50x faster than the direct match using glob.
      // e.g. src/main/resources/com/my/module
      const openFileDirectoryNormalizedPosixPathRelativeToTheWorkspaceRoot = __path.dirname(
        getNormalizedPosixPathRelativeToWorkspaceRoot(this.args.document)
      );

      const matchingNormalizedPosixPathsRelativeToTheBasePath = normalizedPosixPathsRelativeToTheBasePath.filter(
        (p) => {
          // relative to `this.args.document`, e.g. ../../../src/main/java/com/my/module/MyClass.java
          const posixRelative = __path.posix.relative(
            openFileDirectoryNormalizedPosixPathRelativeToTheWorkspaceRoot,
            p
          );

          const matchesPattern =
            // Adding a leading slash here to make the regex have the same behavior as the glob with **/* pattern.
            regexp.test("/" + p) ||
            // check on the asset folder for *.{ext} pattern
            // the regex doesn't support "\" from Windows paths, requiring to test againts POSIX paths
            regexp.test(posixRelative);

          const conformsToSearchType =
            !opts ||
            opts.type === SearchType.TRAVERSAL ||
            (opts.type === SearchType.ASSET_FOLDER &&
              __path
                .join(baseAbsoluteFsPath, toFsPath(p))
                .startsWith(
                  __path.join(
                    baseAbsoluteFsPath,
                    toFsPath(openFileDirectoryNormalizedPosixPathRelativeToTheWorkspaceRoot)
                  )
                ));

          return matchesPattern && conformsToSearchType;
        }
      );

      if (matchingNormalizedPosixPathsRelativeToTheBasePath.length > 0) {
        console.debug(
          "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Git found files [%s]",
          matchingNormalizedPosixPathsRelativeToTheBasePath
        );
        return new ResourcesList(pattern, matchingNormalizedPosixPathsRelativeToTheBasePath);
      } else {
        console.debug(
          "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Git search returned no matches. Falling back to VS Code API."
        );
      }
    } catch (error) {
      console.debug(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: Failed to use isomorphic-git to read dir. Falling back to VS Code API.",
        error
      );
    }

    const baseAbsoluteFsPathPlusPatternFolderPrefix =
      opts?.type === SearchType.ASSET_FOLDER
        ? baseAbsoluteFsPath
        : __path.join(baseAbsoluteFsPath, __path.dirname(pattern));
    const theLastPartOfThePattern = __path.basename(pattern);

    const relativePattern =
      opts?.type === SearchType.ASSET_FOLDER
        ? new RelativePattern(baseAbsoluteFsPath, theLastPartOfThePattern)
        : new RelativePattern(baseAbsoluteFsPathPlusPatternFolderPrefix, theLastPartOfThePattern);

    // Build exclude pattern from .gitignore
    let excludePattern =
      "{**/node_modules/**,**/.git/**,**/dist/**,**/target/**,**/out/**,**/build/**,**/.vscode/**,**/.idea/**}"; // Default excludes
    try {
      const gitignorePath = __path.join(this.args.workspaceRootAbsoluteFsPath, ".gitignore");
      const content = (await vscode.workspace.fs.readFile(vscode.Uri.file(gitignorePath))).toString();
      const lines = content
        .split("\n")
        .map((line) => line.trim())
        .filter((line) => line && !line.startsWith("#"));
      if (lines.length > 0) {
        excludePattern += `,{${lines.join(",")}}`;
      }
    } catch {
      console.debug(
        "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: .gitignore not found or unreadable, using default excludes."
      );
    }

    console.debug(
      "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: vscode.workspace.findFiles excludePattern [%s] ",
      excludePattern
    );
    console.debug(
      "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: vscode.workspace.findFiles relativePattern.baseUri [%s]",
      relativePattern.baseUri
    );
    console.debug(
      "VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: vscode.workspace.findFiles relativePattern.pattern [%s]",
      relativePattern.pattern
    );

    const files = await vscode.workspace.findFiles(relativePattern, excludePattern);
    console.debug("VS CODE RESOURCE CONTENT API IMPL FOR WORKSPACES: found files count [%i]", files.length);

    const normalizedPosixPathsRelativeToTheWorkspaceRoot = files.map((uri) =>
      vscode.workspace.asRelativePath(uri, false)
    );

    return new ResourcesList(pattern, normalizedPosixPathsRelativeToTheWorkspaceRoot);
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
