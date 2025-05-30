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

import git, { FetchResult, STAGE, WORKDIR } from "isomorphic-git";
import http from "isomorphic-git/http/web";
import { GIT_DEFAULT_BRANCH } from "../constants/GitConstants";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { CorsProxyHeaderKeys } from "@kie-tools/cors-proxy-api/dist";

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
  insecurelyDisableTlsCertificateValidation?: boolean;
  disableEncoding?: boolean;
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
  insecurelyDisableTlsCertificateValidation?: boolean;
  disableEncoding?: boolean;
}

export interface RemoteRefArgs {
  fs: KieSandboxWorkspacesFs;
  dir: string;
  remoteRef?: string;
  authInfo?: {
    username: string;
    password: string;
  };
  insecurelyDisableTlsCertificateValidation?: boolean;
  disableEncoding?: boolean;
}

export enum FileModificationStatus {
  added = "added",
  modified = "modified",
  deleted = "deleted",
}
export type UnstagedModifiedFilesStatusEntryType = {
  path: string;
  status: FileModificationStatus;
};
export class GitService {
  public constructor(private readonly corsProxy: Promise<string>) {}

  private getRequestHeaders(args: { insecurelyDisableTlsCertificateValidation?: boolean; disableEncoding?: boolean }) {
    if (!args.insecurelyDisableTlsCertificateValidation && !args.disableEncoding) {
      return undefined;
    }
    const headers: Record<string, string> = {
      ...(args.insecurelyDisableTlsCertificateValidation && {
        [CorsProxyHeaderKeys.INSECURELY_DISABLE_TLS_CERTIFICATE_VALIDATION]: Boolean(
          args.insecurelyDisableTlsCertificateValidation
        ).toString(),
      }),
      // If disableEncoding is true, force proxy/server to skip compression
      ...(args.disableEncoding
        ? { [CorsProxyHeaderKeys.DISABLE_ENCODING]: Boolean(args.disableEncoding).toString() }
        : {}),
    };
    return headers;
  }

  public async listServerRefs(args: {
    url: string;
    authInfo?: {
      username: string;
      password: string;
    };
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }) {
    return git.listServerRefs({
      http,
      corsProxy: await this.corsProxy,
      headers: this.getRequestHeaders(args),
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
      headers: this.getRequestHeaders(args),
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

  public async fetch(args: {
    fs: KieSandboxWorkspacesFs;
    dir: string;
    remote: string;
    ref: string;
    authInfo?: {
      username: string;
      password: string;
    };
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<FetchResult> {
    return await git.fetch({
      fs: args.fs,
      http: http,
      corsProxy: await this.corsProxy,
      headers: this.getRequestHeaders(args),
      dir: args.dir,
      remote: args.remote,
      ref: args.ref,
      singleBranch: true,
      depth: 1,
      tags: true,
      onAuth: () => args.authInfo,
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

  public async checkoutFilesFromLocalHead(args: {
    fs: KieSandboxWorkspacesFs;
    dir: string;
    ref?: string;
    filepaths: string[];
  }) {
    await git.checkout({
      fs: args.fs,
      dir: args.dir,
      ref: args.ref,
      filepaths: args.filepaths,
      noUpdateHead: true,
      force: true,
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
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }) {
    await git.pull({
      fs: args.fs,
      http: http,
      corsProxy: await this.corsProxy,
      headers: this.getRequestHeaders(args),
      dir: args.dir,
      ref: args.ref,
      singleBranch: true,
      author: args.author,
      onAuth: () => args.authInfo,
    });
  }

  public async deleteBranch(args: { fs: KieSandboxWorkspacesFs; dir: string; ref: string }) {
    const currentBranch = await git.currentBranch({ fs: args.fs, dir: args.dir });

    if (args.ref === currentBranch) {
      throw new Error("Can't delete current branch.");
    }
    await git.deleteBranch({
      fs: args.fs,
      dir: args.dir,
      ref: args.ref,
    });
  }

  public async getRemoteRef(args: RemoteRefArgs) {
    const url = await git.getConfig({ fs: args.fs, path: "remote.origin.url", dir: args.dir });

    const serverRefs = await git.listServerRefs({
      http: http,
      url,
      corsProxy: await this.corsProxy,
      headers: this.getRequestHeaders(args),
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
      insecurelyDisableTlsCertificateValidation: args.insecurelyDisableTlsCertificateValidation,
      disableEncoding: args.disableEncoding,
    });

    if (serverRemoteRef?.oid && head === serverRemoteRef.oid) return;

    await git.push({
      fs: args.fs,
      http: http,
      corsProxy: await this.corsProxy,
      headers: this.getRequestHeaders(args),
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
    const files = await this.unstagedModifiedFilesStatus(args);
    return files.length > 0;
  }

  public async unstagedModifiedFilesStatus(args: {
    fs: KieSandboxWorkspacesFs;
    dir: string;
    exclude: (filepath: string) => boolean;
  }): Promise<UnstagedModifiedFilesStatusEntryType[]> {
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
    const ret = pseudoStatusMatrix
      .filter((row: any) => row[_WORKDIR] !== row[_STAGE]) // filter differences with staged state
      .map((row: any[]) => {
        // if both _WORKDIR and _STAGE are set, the file is staged and modified (from above filter)
        // if _WORKDIR is set only, it means the file is not yet staged
        // if _STAGE is set only, it means the file has been deleted in workspace
        let status: FileModificationStatus;
        if (row[_WORKDIR] && !row[_STAGE]) {
          status = FileModificationStatus.added;
        } else if (!row[_WORKDIR] && row[_STAGE]) {
          status = FileModificationStatus.deleted;
        } else {
          status = FileModificationStatus.modified;
        }
        const result: UnstagedModifiedFilesStatusEntryType = {
          path: row[_FILE],
          status,
        };
        return result;
      });
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
