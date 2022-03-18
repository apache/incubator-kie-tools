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

import LightningFS from "@isomorphic-git/lightning-fs";
import git, { STAGE, WORKDIR } from "isomorphic-git";
import http from "isomorphic-git/http/web";

export const GIST_DEFAULT_BRANCH = "master";
export const GIST_ORIGIN_REMOTE_NAME = "origin";
export const GIT_ORIGIN_REMOTE_NAME = "origin";
export const GIT_DEFAULT_BRANCH = "main";

export interface CloneArgs {
  fs: LightningFS;
  repositoryUrl: URL;
  sourceBranch: string;
  dir: string;
  gitConfig?: {
    name: string;
    email: string;
  };
  authInfo?: {
    username: string;
    password: string;
  };
}

export interface CommitArgs {
  fs: LightningFS;
  message: string;
  targetBranch: string;
  dir: string;
  author: {
    name: string;
    email: string;
  };
}

export interface PushArgs {
  fs: LightningFS;
  dir: string;
  ref: string;
  remoteRef?: string;
  remote: string;
  force: boolean;
  authInfo: {
    username: string;
    password: string;
  };
}

export interface RemoteRefArgs {
  fs: LightningFS;
  dir: string;
  remoteRef?: string;
  authInfo?: {
    username: string;
    password: string;
  };
}

export class GitService {
  public constructor(private readonly corsProxy: string) {}

  public async clone(args: CloneArgs): Promise<void> {
    console.debug("GitService#clone--------begin");
    await git.clone({
      fs: args.fs,
      http: http,
      corsProxy: this.corsProxy,
      dir: args.dir,
      url: args.repositoryUrl.href,
      singleBranch: true,
      noTags: true,
      depth: 1,
      ref: args.sourceBranch,
      onAuth: () => args.authInfo,
    });

    if (args.gitConfig) {
      await this.setupGitConfig(args.fs, args.dir, args.gitConfig);
    }
  }

  public async branch(args: { fs: LightningFS; dir: string; name: string; checkout: boolean }) {
    await git.branch({
      fs: args.fs,
      dir: args.dir,
      ref: args.name,
      checkout: args.checkout,
    });
  }

  public async addRemote(args: { fs: LightningFS; dir: string; name: string; url: string; force: boolean }) {
    await git.addRemote({
      fs: args.fs,
      dir: args.dir,
      remote: args.name,
      url: args.url,
      force: args.force,
    });
  }

  public async commit(args: CommitArgs): Promise<void> {
    if (args.author) {
      await this.setupGitConfig(args.fs, args.dir, args.author);
    }

    await git.commit({
      fs: args.fs,
      dir: args.dir,
      message: args.message,
      author: {
        name: args.author.name,
        email: args.author.email,
      },
      ref: args.targetBranch,
    });

    await git.writeRef({
      fs: args.fs,
      dir: args.dir,
      ref: "HEAD",
      force: true,
      value: args.targetBranch,
    });
  }

  public async pull(args: {
    fs: LightningFS;
    dir: string;
    ref: string;
    author: {
      name: string;
      email: string;
    };
    authInfo?: {
      username: string;
      password: string;
    };
  }) {
    await git.pull({
      fs: args.fs,
      http: http,
      corsProxy: this.corsProxy,
      dir: args.dir,
      ref: args.ref,
      singleBranch: true,
      author: args.author,
      onAuth: () => args.authInfo,
    });
  }

  public async getRemoteRef(args: RemoteRefArgs) {
    const url = await git.getConfig({ fs: args.fs, path: "remote.origin.url", dir: args.dir });

    const serverRefs = await git.listServerRefs({
      http: http,
      url,
      corsProxy: this.corsProxy,
      onAuth: () => args.authInfo,
    });

    return serverRefs.find((serverRef) =>
      args.remoteRef ? serverRef.ref === args.remoteRef : serverRef.ref === "HEAD"
    );
  }

  public async push(args: PushArgs): Promise<void> {
    if ((await git.listRemotes(args)).length === 0) {
      throw new Error("No remote repository found");
    }

    const head = await this.resolveRef({
      fs: args.fs,
      dir: args.dir,
      ref: "HEAD",
    });

    const serverRemoteRef = await this.getRemoteRef({
      fs: args.fs,
      dir: args.dir,
      remoteRef: args.remoteRef,
      authInfo: args.authInfo,
    });

    if (serverRemoteRef?.oid && head === serverRemoteRef.oid) return;

    await git.push({
      fs: args.fs,
      http: http,
      corsProxy: this.corsProxy,
      dir: args.dir,
      ref: args.ref,
      remoteRef: args.remoteRef,
      remote: args.remote,
      force: args.force,
      onAuth: () => args.authInfo,
    });
  }

  public async add(args: { fs: LightningFS; dir: string; relativePath: string }) {
    await git.add({
      fs: args.fs,
      dir: args.dir,
      filepath: args.relativePath,
    });
  }

  public async setupGitConfig(fs: LightningFS, dir: string, config: { name: string; email: string }): Promise<void> {
    await git.setConfig({
      fs: fs,
      dir: dir,
      path: "user.name",
      value: config.name,
    });

    await git.setConfig({
      fs: fs,
      dir: dir,
      path: "user.email",
      value: config.email,
    });
  }

  async init(args: { fs: LightningFS; dir: string }) {
    await git.init({
      fs: args.fs,
      dir: args.dir,
      bare: false,
      defaultBranch: GIT_DEFAULT_BRANCH,
    });
  }

  async isIgnored(args: { fs: LightningFS; dir: string; filepath: string }) {
    return await git.isIgnored({
      fs: args.fs,
      dir: args.dir,
      filepath: args.filepath,
    });
  }

  async rm(args: { fs: LightningFS; dir: string; relativePath: string }) {
    await git.remove({
      fs: args.fs,
      dir: args.dir,
      filepath: args.relativePath,
    });
  }

  async hasLocalChanges(args: { fs: LightningFS; dir: string }) {
    const files = await this.unstagedModifiedFileRelativePaths(args);
    return files.length > 0;
  }

  public async unstagedModifiedFileRelativePaths(args: { fs: LightningFS; dir: string }): Promise<string[]> {
    const cache = {};
    const pseudoStatusMatrix = await git.walk({
      cache,
      fs: args.fs,
      dir: args.dir,
      trees: [WORKDIR(), STAGE()],
      map: async (filepath, [workdir, stage]) => {
        if (!stage && workdir && (await git.isIgnored({ fs: args.fs, dir: args.dir, filepath }))) {
          return null;
        }

        // match against base paths
        if (filepath.startsWith(".git")) {
          return null;
        }

        // For now, just bail on directories
        const workdirType = workdir && (await workdir.type());
        if (workdirType === "tree" || workdirType === "special") return;

        const stageType = stage && (await stage.type());
        if (stageType === "commit") return null;
        if (stageType === "tree" || stageType === "special") return;

        // Figure out the oids, using the staged oid for the working dir oid if the stats match.
        const stageOid = stage ? await stage.oid() : undefined;
        let workdirOid;
        if (workdir && !stage) {
          // We don't actually NEED the sha. Any sha will do
          // TODO: update this logic to handle N trees instead of just 3.
          workdirOid = "42";
        } else if (workdir) {
          workdirOid = await workdir.oid();
        }
        const entry = [undefined, undefined, workdirOid, stageOid];
        const result = entry.map((value) => entry.indexOf(value));
        result.shift(); // remove leading undefined entry
        return [filepath, ...result];
      },
    });

    const _WORKDIR = 2;
    const _STAGE = 3;
    const _FILE = 0;
    return pseudoStatusMatrix.filter((row: any) => row[_WORKDIR] !== row[_STAGE]).map((row: any) => row[_FILE]);
  }

  public async resolveRef(args: { fs: LightningFS; dir: string; ref: string }) {
    return git.resolveRef({
      fs: args.fs,
      dir: args.dir,
      ref: args.ref,
    });
  }
}
