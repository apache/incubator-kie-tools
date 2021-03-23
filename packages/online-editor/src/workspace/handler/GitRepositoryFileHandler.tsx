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

import { File } from "@kie-tooling-core/editor/dist/channel";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { GitHubRepositoryOrigin } from "../model/WorkspaceOrigin";
import { AuthInfo, GitService } from "../services/GitService";
import { SUPPORTED_FILES_EDITABLE_PATTERN } from "../SupportedFiles";
import { FileHandler, FileHandlerCommonArgs } from "./FileHandler";

export interface GitRepositoryFileHandlerArgs extends FileHandlerCommonArgs {
  authInfo: AuthInfo;
  repositoryUrl: URL;
  sourceBranch: string;
  gitService: GitService;
}

export class GitRepositoryFileHandler extends FileHandler {
  private readonly COMMIT_MESSAGE = "Update from the Online Editor";

  public constructor(private readonly args: GitRepositoryFileHandlerArgs) {
    super(args.workspaceService, args.storageService);
  }

  public async store(descriptor: WorkspaceDescriptor): Promise<File[]> {
    const contextPath = await this.workspaceService.resolveContextPath(descriptor);
    await this.args.gitService.clone({
      contextPath: contextPath,
      authInfo: this.args.authInfo,
      repositoryUrl: this.args.repositoryUrl,
      sourceBranch: this.args.sourceBranch,
    });
    return await this.storageService.getFiles(contextPath);
  }

  public async sync(descriptor: WorkspaceDescriptor): Promise<void> {
    const contextPath = await this.workspaceService.resolveContextPath(descriptor);
    const files = await this.storageService.getFiles(contextPath, SUPPORTED_FILES_EDITABLE_PATTERN);
    const targetBranch = (descriptor.origin as GitHubRepositoryOrigin).branch;
    await this.args.gitService.gitCommit({
      contextPath: contextPath,
      message: this.COMMIT_MESSAGE,
      authInfo: this.args.authInfo,
      files: files,
      targetBranch: targetBranch,
    });
    await this.args.gitService.gitPush({
      contextPath: contextPath,
      authInfo: this.args.authInfo,
      targetBranch: targetBranch,
    });
  }
}
