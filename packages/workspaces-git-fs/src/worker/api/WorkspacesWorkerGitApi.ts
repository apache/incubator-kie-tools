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

import { WorkspaceDescriptor } from "./WorkspaceDescriptor";
import {
  BitbucketOrigin,
  BitbucketSnippetOrigin,
  GistOrigin,
  GitHubOrigin,
  GitlabOrigin,
  GitlabSnippetOrigin,
} from "./WorkspaceOrigin";
import { LocalFile } from "./LocalFile";
import { WorkspaceWorkerFileDescriptor } from "./WorkspaceWorkerFileDescriptor";
import { GitServerRef } from "./GitServerRef";
import { FetchResult } from "isomorphic-git";
import { UnstagedModifiedFilesStatusEntryType } from "../../services/GitService";

export interface WorkspacesWorkerGitApi {
  kieSandboxWorkspacesGit_getGitServerRefs(args: {
    url: string;
    authInfo?: {
      username: string;
      password: string;
    };
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<GitServerRef[]>;

  kieSandboxWorkspacesGit_hasLocalChanges(args: { workspaceId: string }): Promise<boolean>;

  kieSandboxWorkspacesGit_isModified(args: { workspaceId: string; relativePath: string }): Promise<boolean>;

  kieSandboxWorkspacesGit_resolveRef(args: { workspaceId: string; ref: string }): Promise<string>;

  kieSandboxWorkspacesGit_init(args: {
    localFiles: LocalFile[];
    preferredName?: string;
    gitAuthSessionId: string | undefined;
    gitConfig?: {
      email: string;
      name: string;
    };
    gitInsecurelyDisableTlsCertificateValidation?: boolean;
  }): Promise<{
    workspace: WorkspaceDescriptor;
    suggestedFirstFile?: WorkspaceWorkerFileDescriptor;
  }>;

  kieSandboxWorkspacesGit_clone(args: {
    origin: GistOrigin | GitHubOrigin | BitbucketOrigin | BitbucketSnippetOrigin | GitlabOrigin | GitlabSnippetOrigin;
    gitAuthSessionId: string | undefined;
    gitConfig?: {
      email: string;
      name: string;
    };
    authInfo?: {
      username: string;
      password: string;
    };
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<{
    workspace: WorkspaceDescriptor;
    suggestedFirstFile?: WorkspaceWorkerFileDescriptor;
  }>;

  kieSandboxWorkspacesGit_pull(args: {
    workspaceId: string;
    gitConfig?: {
      email: string;
      name: string;
    };
    authInfo?: {
      username: string;
      password: string;
    };
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_push(args: {
    workspaceId: string;
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
  }): Promise<void>;

  kieSandboxWorkspacesGit_deleteBranch(args: { workspaceId: string; ref: string }): Promise<void>;

  kieSandboxWorkspacesGit_branch(args: { workspaceId: string; name: string; checkout: boolean }): Promise<void>;

  kieSandboxWorkspacesGit_checkout(args: { workspaceId: string; ref: string; remote: string }): Promise<void>;

  kieSandboxWorkspacesGit_checkoutFilesFromLocalHead(args: {
    workspaceId: string;
    ref?: string;
    filepaths: string[];
  }): Promise<void>;

  kieSandboxWorkspacesGit_addRemote(args: {
    workspaceId: string;
    name: string;
    url: string;
    force: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_deleteRemote(args: { workspaceId: string; name: string }): Promise<void>;

  kieSandboxWorkspacesGit_getUnstagedModifiedFilesStatus(args: {
    workspaceId: string;
  }): Promise<UnstagedModifiedFilesStatusEntryType[]>;

  kieSandboxWorkspacesGit_stageFile(args: { workspaceId: string; relativePath: string }): Promise<void>;

  kieSandboxWorkspacesGit_commit(args: {
    workspaceId: string;
    targetBranch: string;
    gitConfig?: {
      email: string;
      name: string;
    };
    commitMessage?: string;
  }): Promise<void>;

  kieSandboxWorkspacesGit_createSavePoint(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    commitMessage?: string;
  }): Promise<void>;

  kieSandboxWorkspacesGit_fetch(args: {
    workspaceId: string;
    remote: string;
    authInfo?: {
      username: string;
      password: string;
    };
    ref: string;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<FetchResult>;

  kieSandboxWorkspacesGit_initGitOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
    branch?: string;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_initGistOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
    branch: string;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_initSnippetOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
    branch: string;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_initGitlabSnippetOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
    branch: string;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_changeGitAuthSessionId(args: {
    workspaceId: string;
    gitAuthSessionId: string | undefined;
    insecurelyDisableTlsCertificateValidation?: boolean;
    disableEncoding?: boolean;
  }): Promise<void>;

  kieSandboxWorkspacesGit_initLocalOnExistingWorkspace(args: { workspaceId: string }): Promise<void>;
}
