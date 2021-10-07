/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { WorkspaceFile } from "../WorkspacesContext";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { GitHubRepositoryOrigin } from "../model/WorkspaceOrigin";
import { GitService } from "../services/GitService";
import { SUPPORTED_FILES_EDITABLE_PATTERN } from "../SupportedFiles";
import { FileHandler, FileHandlerCommonArgs } from "./FileHandler";

export interface GitRepositoryFileHandlerArgs extends FileHandlerCommonArgs {
  authInfo: { name: string; email: string; onAuth: () => { username: string; password: string } };
  repositoryUrl: URL;
  sourceBranch: string;
  gitService: GitService;
}

export class GitRepositoryFileHandler extends FileHandler {
  private readonly COMMIT_MESSAGE = "Update from the Online Editor";

  public constructor(private readonly args: GitRepositoryFileHandlerArgs) {
    super(args.workspaceService);
  }

  public async store(descriptor: WorkspaceDescriptor): Promise<WorkspaceFile[]> {
    const rootPath = await this.workspaceService.resolveRootPath(descriptor);
    await this.args.gitService.clone({
      rootPath: rootPath,
      authInfo: this.args.authInfo,
      repositoryUrl: this.args.repositoryUrl,
      sourceBranch: this.args.sourceBranch,
    });
    return await this.workspaceService.getFiles(rootPath);
  }

  public async sync(descriptor: WorkspaceDescriptor): Promise<void> {
    const rootPath = await this.workspaceService.resolveRootPath(descriptor);
    const files = await this.workspaceService.getFiles(rootPath, SUPPORTED_FILES_EDITABLE_PATTERN);
    const targetBranch = (descriptor.origin as GitHubRepositoryOrigin).branch;
    await this.args.gitService.gitCommit({
      rootPath: rootPath,
      message: this.COMMIT_MESSAGE,
      authInfo: this.args.authInfo,
      files: files,
      targetBranch: targetBranch,
    });
    await this.args.gitService.gitPush({
      rootPath: rootPath,
      authInfo: this.args.authInfo,
      targetBranch: targetBranch,
    });
  }
}
