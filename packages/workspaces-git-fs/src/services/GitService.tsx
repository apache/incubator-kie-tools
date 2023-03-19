/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import git, { STAGE, WORKDIR } from "isomorphic-git";
import http from "isomorphic-git/http/web";
import { GIT_DEFAULT_BRANCH } from "../constants/GitConstants";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";

export interface CloneArgs {
  fs: KieSandboxWorkspacesFs;
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
  fs: KieSandboxWorkspacesFs;
  message: string;
  targetBranch: string;
  dir: string;
  author: {
    name: string;
    email: string;
  };
}

export interface PushArgs {
  fs: KieSandboxWorkspacesFs;
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
  fs: KieSandboxWorkspacesFs;
  dir: string;
  remoteRef?: string;
  authInfo?: {
    username: string;
    password: string;
  };
}

export class GitService {
  public constructor(private readonly corsProxy: Promise<string>) {}

  public async listServerRefs(args: {
    url: string;
    authInfo?: {
      username: string;
      password: string;
    };
  }) {
    return git.listServerRefs({
      http,
      corsProxy: await this.corsProxy,
      onAuth: () => args.authInfo,
      url: args.url,
      symrefs: true,
      protocolVersion: 1,
    });
  }

  public async clone(args: CloneArgs): Promise<void> {
    console.time("GitService#clone");
    await git.clone({
      fs: args.fs,
      http: http,
      corsProxy: await this.corsProxy,
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
    console.timeEnd("GitService#clone");
  }

  public async branch(args: { fs: KieSandboxWorkspacesFs; dir: string; name: string; checkout: boolean }) {
    await git.branch({
      fs: args.fs,
      dir: args.dir,
      ref: args.name,
      checkout: args.checkout,
    });
  }

  public async addRemote(args: { fs: KieSandboxWorkspacesFs; dir: string; name: string; url: string; force: boolean }) {
    await git.addRemote({
      fs: args.fs,
      dir: args.dir,
      remote: args.name,
      url: args.url,
      force: args.force,
    });
  }

  public async deleteRemote(args: { fs: KieSandboxWorkspacesFs; dir: string; name: string }) {
    await git.deleteRemote({
      fs: args.fs,
      dir: args.dir,
      remote: args.name,
    });
  }

  public async fetch(args: { fs: KieSandboxWorkspacesFs; dir: string; remote: string; ref: string }): Promise<void> {
    await git.fetch({
      fs: args.fs,
      http: http,
      corsProxy: await this.corsProxy,
      dir: args.dir,
      remote: args.remote,
      ref: args.ref,
      singleBranch: true,
      depth: 1,
    });
  }

  public async checkout(args: { fs: KieSandboxWorkspacesFs; dir: string; ref: string; remote: string }) {
    await git.checkout({
      fs: args.fs,
      dir: args.dir,
      ref: args.ref,
      remote: args.remote,
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
    fs: KieSandboxWorkspacesFs;
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
      corsProxy: await this.corsProxy,
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
      corsProxy: await this.corsProxy,
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
      corsProxy: await this.corsProxy,
      dir: args.dir,
      ref: args.ref,
      remoteRef: args.remoteRef,
      remote: args.remote,
      force: args.force,
      onAuth: () => args.authInfo,
    });
  }

  public async add(args: { fs: KieSandboxWorkspacesFs; dir: string; relativePath: string }) {
    await git.add({
      fs: args.fs,
      dir: args.dir,
      filepath: args.relativePath,
    });
  }

  public async setupGitConfig(
    fs: KieSandboxWorkspacesFs,
    dir: string,
    config: { name: string; email: string }
  ): Promise<void> {
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

  async init(args: { fs: KieSandboxWorkspacesFs; dir: string }) {
    await git.init({
      fs: args.fs,
      dir: args.dir,
      bare: false,
      defaultBranch: GIT_DEFAULT_BRANCH,
    });
  }

  async isIgnored(args: { fs: KieSandboxWorkspacesFs; dir: string; filepath: string }) {
    return git.isIgnored({
      fs: args.fs,
      dir: args.dir,
      filepath: args.filepath,
    });
  }

  async rm(args: { fs: KieSandboxWorkspacesFs; dir: string; relativePath: string }) {
    await git.remove({
      fs: args.fs,
      dir: args.dir,
      filepath: args.relativePath,
    });
  }

  async isModified(args: { fs: KieSandboxWorkspacesFs; dir: string; relativePath: string }) {
    const status = await git.status({
      fs: args.fs,
      dir: args.dir,
      filepath: args.relativePath,
    });
    return status !== "unmodified";
  }

  async hasLocalChanges(args: { fs: KieSandboxWorkspacesFs; dir: string; exclude: (filepath: string) => boolean }) {
    const files = await this.unstagedModifiedFileRelativePaths(args);
    return files.length > 0;
  }

  public async unstagedModifiedFileRelativePaths(args: {
    fs: KieSandboxWorkspacesFs;
    dir: string;
    exclude: (filepath: string) => boolean;
  }): Promise<string[]> {
    const now = performance.now();
    console.time(`${now}: hasLocalChanges`);
    const pseudoStatusMatrix = await git.walk({
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

        if (args.exclude(filepath)) return;

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
    const ret = pseudoStatusMatrix.filter((row: any) => row[_WORKDIR] !== row[_STAGE]).map((row: any) => row[_FILE]);
    console.timeEnd(`${now}: hasLocalChanges`);
    return ret;
  }

  public async resolveRef(args: { fs: KieSandboxWorkspacesFs; dir: string; ref: string }) {
    return git.resolveRef({
      fs: args.fs,
      dir: args.dir,
      ref: args.ref,
    });
  }
}
