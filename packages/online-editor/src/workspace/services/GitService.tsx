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

import { WorkspaceFile } from "../WorkspaceContext";
import git from "isomorphic-git";
import http from "isomorphic-git/http/web";
import { StorageService } from "./StorageService";

export interface AuthInfo {
  name: string;
  email: string;
  onAuth: () => { username: string; password: string };
}

interface CommonArgs {
  contextPath: string;
  authInfo: AuthInfo;
}

export interface CloneArgs extends CommonArgs {
  repositoryUrl: URL;
  sourceBranch: string;
}

export interface CommitArgs extends CommonArgs {
  message: string;
  files: WorkspaceFile[];
  targetBranch: string;
}

export interface PushArgs extends CommonArgs {
  targetBranch: string;
}

export class GitService {
  private readonly GIT_REMOTE_NAME = "origin";

  public constructor(private readonly corsProxy: string, private readonly storageService: StorageService) {}

  public async clone(args: CloneArgs): Promise<void> {
    await git.clone({
      fs: this.storageService.fs,
      http: http,
      corsProxy: this.corsProxy,
      dir: args.contextPath,
      url: args.repositoryUrl.href,
      singleBranch: true,
      noTags: true,
      depth: 1,
      ref: args.sourceBranch,
      onAuth: args.authInfo.onAuth,
    });

    await this.gitConfig(args.contextPath, args.authInfo.name, args.authInfo.email);
  }

  public async gitCommit(args: CommitArgs): Promise<void> {
    for (const file of args.files) {
      await git.add({
        fs: this.storageService.fs,
        dir: args.contextPath,
        filepath: this.storageService.asRelativePath(args.contextPath, file),
      });
    }

    await git.commit({
      fs: this.storageService.fs,
      dir: args.contextPath,
      message: args.message,
      author: {
        name: args.authInfo.name,
        email: args.authInfo.email,
      },
      ref: args.targetBranch,
    });
  }

  public async gitPush(args: PushArgs): Promise<void> {
    const remotes = await git.listRemotes({ fs: this.storageService.fs, dir: args.contextPath });
    if (remotes.length === 0) {
      throw new Error("No remote repository found");
    }

    await git.push({
      fs: this.storageService.fs,
      http: http,
      dir: args.contextPath,
      ref: args.targetBranch,
      remote: this.GIT_REMOTE_NAME,
      onAuth: args.authInfo.onAuth,
      force: true,
    });
  }

  private async gitConfig(contextPath: string, userName: string, userEmail: string): Promise<void> {
    await git.setConfig({
      fs: this.storageService.fs,
      dir: contextPath,
      path: "user.name",
      value: userName,
    });

    await git.setConfig({
      fs: this.storageService.fs,
      dir: contextPath,
      path: "user.email",
      value: userEmail,
    });
  }
}
