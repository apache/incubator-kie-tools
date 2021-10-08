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
import git from "isomorphic-git";
import http from "isomorphic-git/http/web";
import { StorageService } from "./StorageService";

export interface CloneArgs {
  repositoryUrl: URL;
  sourceBranch: string;
  dir: string;
  authInfo: {
    name: string;
    email: string;
    onAuth: () => { username: string; password: string };
  };
}

export interface CommitArgs {
  message: string;
  files: WorkspaceFile[];
  targetBranch: string;
  dir: string;
  authInfo: {
    name: string;
    email: string;
  };
}

export interface PushArgs {
  targetBranch: string;
  dir: string;
  authInfo: {
    name: string;
    email: string;
    onAuth: () => { username: string; password: string };
  };
}

export class GitService {
  private readonly GIT_REMOTE_NAME = "origin";

  public constructor(private readonly corsProxy: string, private readonly storageService: StorageService) {}

  public async clone(args: CloneArgs): Promise<void> {
    await git.clone({
      fs: this.storageService.fs,
      http: http,
      corsProxy: this.corsProxy,
      dir: args.dir,
      url: args.repositoryUrl.href,
      singleBranch: true,
      noTags: true,
      depth: 1,
      ref: args.sourceBranch,
      onAuth: args.authInfo.onAuth,
    });

    await this.gitConfig(args.dir, args.authInfo.name, args.authInfo.email);
  }

  public async gitCommit(args: CommitArgs): Promise<void> {
    for (const file of args.files) {
      await git.add({
        fs: this.storageService.fs,
        dir: args.dir,
        filepath: file.pathRelativeToWorkspaceRoot,
      });
    }

    await git.commit({
      fs: this.storageService.fs,
      dir: args.dir,
      message: args.message,
      author: {
        name: args.authInfo.name,
        email: args.authInfo.email,
      },
      ref: args.targetBranch,
    });
  }

  public async gitPush(args: PushArgs): Promise<void> {
    const remotes = await git.listRemotes({ fs: this.storageService.fs, dir: args.dir });
    if (remotes.length === 0) {
      throw new Error("No remote repository found");
    }

    await git.push({
      fs: this.storageService.fs,
      http: http,
      dir: args.dir,
      ref: args.targetBranch,
      remote: this.GIT_REMOTE_NAME,
      onAuth: args.authInfo.onAuth,
      force: false,
    });
  }

  private async gitConfig(dir: string, userName: string, userEmail: string): Promise<void> {
    await git.setConfig({
      fs: this.storageService.fs,
      dir: dir,
      path: "user.name",
      value: userName,
    });

    await git.setConfig({
      fs: this.storageService.fs,
      dir: dir,
      path: "user.email",
      value: userEmail,
    });
  }
}
