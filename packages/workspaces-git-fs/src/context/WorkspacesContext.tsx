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

import {
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import * as React from "react";
import { createContext, useContext } from "react";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { GistOrigin, GitHubOrigin } from "../worker/api/WorkspaceOrigin";
import { LocalFile } from "../worker/api/LocalFile";
import { decoder } from "../encoderdecoder/EncoderDecoder";
import { parseWorkspaceFileRelativePath } from "../relativePath/WorkspaceFileRelativePathParser";
import { WorkspacesSharedWorker } from "../worker/WorkspacesSharedWorker";
import { GitServerRef } from "../worker/api/GitServerRef";

export class WorkspaceFile {
  private readonly parsedRelativePath;
  constructor(
    private readonly args: {
      workspaceId: string;
      relativePath: string;
      getFileContents: () => Promise<Uint8Array>;
    }
  ) {
    this.parsedRelativePath = parseWorkspaceFileRelativePath(this.relativePath);
  }

  get getFileContentsAsString() {
    return () => this.getFileContents().then((c) => decoder.decode(c));
  }

  get getFileContents() {
    return this.args.getFileContents;
  }

  get workspaceId() {
    return this.args.workspaceId;
  }

  get relativePath() {
    return this.args.relativePath;
  }

  get relativePathWithoutExtension() {
    return this.parsedRelativePath.relativePathWithoutExtension;
  }

  get relativeDirPath() {
    return this.parsedRelativePath.relativeDirPath;
  }

  get extension() {
    return this.parsedRelativePath.extension;
  }

  get nameWithoutExtension() {
    return this.parsedRelativePath.nameWithoutExtension;
  }

  get name() {
    return this.parsedRelativePath.name;
  }
}

export interface WorkspacesContextType {
  workspacesSharedWorker: WorkspacesSharedWorker;

  // util

  getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }): Promise<string>;

  // git

  createWorkspaceFromLocal: (args: {
    localFiles: LocalFile[];
    preferredName?: string;
    gitAuthSessionId?: string;
  }) => Promise<{
    workspace: WorkspaceDescriptor;
    suggestedFirstFile?: WorkspaceFile;
  }>;

  createWorkspaceFromGitRepository: (args: {
    origin: GistOrigin | GitHubOrigin;
    gitConfig?: { email: string; name: string };
    gitAuthSessionId: string | undefined;
    authInfo?: {
      username: string;
      password: string;
    };
  }) => Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;

  pull(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    authInfo?: {
      username: string;
      password: string;
    };
  }): Promise<void>;

  push(args: {
    workspaceId: string;
    ref: string;
    remoteRef?: string;
    remote: string;
    force: boolean;
    authInfo: {
      username: string;
      password: string;
    };
  }): Promise<void>;

  branch(args: { workspaceId: string; name: string; checkout: boolean }): Promise<void>;

  checkout(args: { workspaceId: string; ref: string; remote: string }): Promise<void>;

  addRemote(args: { workspaceId: string; name: string; url: string; force: boolean }): Promise<void>;

  deleteRemote(args: { workspaceId: string; name: string }): Promise<void>;

  resolveRef(args: { workspaceId: string; ref: string }): Promise<string>;

  getGitServerRefs(args: {
    url: string;
    authInfo?: {
      username: string;
      password: string;
    };
  }): Promise<GitServerRef[]>;

  hasLocalChanges(args: { workspaceId: string }): Promise<boolean>;

  isFileModified(args: { workspaceId: string; relativePath: string }): Promise<boolean>;

  createSavePoint(args: {
    workspaceId: string;
    gitConfig?: {
      email: string;
      name: string;
    };
  }): Promise<void>;

  fetch(args: { workspaceId: string; remote: string; ref: string }): Promise<void>;

  // storage

  addEmptyFile(args: {
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  prepareZip(args: { workspaceId: string; onlyExtensions?: string[] }): Promise<Blob>;

  getFiles(args: { workspaceId: string; globPattern?: string }): Promise<WorkspaceFile[]>;

  deleteWorkspace(args: { workspaceId: string }): Promise<void>;

  renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void>;

  resourceContentList(args: {
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }): Promise<ResourcesList>;

  resourceContentGet(args: {
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }): Promise<ResourceContent | undefined>;

  getFile(args: { workspaceId: string; relativePath: string }): Promise<WorkspaceFile | undefined>;

  getFileContent(args: { workspaceId: string; relativePath: string }): Promise<Uint8Array>;

  renameFile(args: { file: WorkspaceFile; newFileNameWithoutExtension: string }): Promise<WorkspaceFile>;

  updateFile(args: { workspaceId: string; relativePath: string; newContent: string }): Promise<void>;

  deleteFile(args: { file: WorkspaceFile }): Promise<void>;

  moveFile(args: { file: WorkspaceFile; newDirPath: string }): Promise<WorkspaceFile>;

  addFile(args: {
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  existsFile(args: { workspaceId: string; relativePath: string }): Promise<boolean>;

  listAllWorkspaces(): Promise<WorkspaceDescriptor[]>;

  getWorkspace(args: { workspaceId: string }): Promise<WorkspaceDescriptor>;

  initGitOnWorkspace(args: { workspaceId: string; remoteUrl: URL }): Promise<void>;

  initGistOnWorkspace(args: { workspaceId: string; remoteUrl: URL; branch: string }): Promise<void>;

  changeGitAuthSessionId(args: { workspaceId: string; gitAuthSessionId: string | undefined }): Promise<void>;

  initLocalOnWorkspace(args: { workspaceId: string }): Promise<void>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
