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
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { basename, extname, parse } from "path";
import { GistOrigin, GitHubOrigin } from "./model/WorkspaceOrigin";

export const decoder = new TextDecoder("utf-8");
export const encoder = new TextEncoder();

export class WorkspaceFile {
  constructor(
    private readonly args: {
      workspaceId: string;
      relativePath: string;
      getFileContents: () => Promise<Uint8Array>;
    }
  ) {}

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
    return this.relativePath.split(".").slice(0, -1).join(".");
  }

  get relativeDirPath() {
    return parse(this.relativePath).dir;
  }

  get extension() {
    return extname(this.relativePath).replace(".", "");
  }

  get nameWithoutExtension() {
    return basename(this.relativePath, `.${this.extension}`);
  }

  get name() {
    return basename(this.relativePath);
  }
}

export interface LocalFile {
  path: string;
  fileContents: Uint8Array;
}

export interface WorkspacesContextType {
  // util

  getUniqueFileIdentifier(args: {
    workspaceId: string; //
    relativePath: string;
  }): string;

  // git

  createWorkspaceFromLocal: (args: {
    useInMemoryFs: boolean;
    localFiles: LocalFile[];
    preferredName?: string;
  }) => Promise<{
    workspace: WorkspaceDescriptor;
    suggestedFirstFile?: WorkspaceFile;
  }>;

  createWorkspaceFromGitRepository: (args: {
    origin: GistOrigin | GitHubOrigin;
    gitConfig?: { email: string; name: string };
    authInfo?: {
      username: string;
      password: string;
    };
  }) => Promise<{
    workspace: WorkspaceDescriptor;
    suggestedFirstFile?: WorkspaceFile;
  }>;

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

  branch(args: {
    workspaceId: string; //
    name: string;
    checkout: boolean;
  }): Promise<void>;

  addRemote(args: {
    workspaceId: string; //
    name: string;
    url: string;
    force: boolean;
  }): Promise<void>;

  resolveRef(args: {
    workspaceId: string; //
    ref: string;
  }): Promise<string>;

  hasLocalChanges(args: {
    workspaceId: string; //
  }): Promise<boolean>;

  createSavePoint(args: {
    workspaceId: string; //
    gitConfig?: {
      email: string;
      name: string;
    };
  }): Promise<void>;

  // storage

  addEmptyFile(args: {
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  prepareZip(args: {
    workspaceId: string; //
    onlyExtensions?: string[];
  }): Promise<Blob>;

  getFiles(args: {
    workspaceId: string; //
  }): Promise<WorkspaceFile[]>;

  deleteWorkspace(args: {
    workspaceId: string; //
  }): Promise<void>;

  renameWorkspace(args: {
    workspaceId: string; //
    newName: string;
  }): Promise<void>;

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

  //

  getFile(args: {
    workspaceId: string; //
    relativePath: string;
  }): Promise<WorkspaceFile | undefined>;

  getFileContent(args: {
    workspaceId: string; //
    relativePath: string;
  }): Promise<Uint8Array>;

  renameFile(args: {
    file: WorkspaceFile; //
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceFile>;

  updateFile(args: {
    file: WorkspaceFile; //
    getNewContents: () => Promise<string | undefined>;
  }): Promise<void>;

  deleteFile(args: {
    file: WorkspaceFile; //
  }): Promise<void>;

  addFile(args: {
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  existsFile(args: {
    workspaceId: string; //
    relativePath: string;
  }): Promise<boolean>;

  listAllWorkspaces(): Promise<WorkspaceDescriptor[]>;

  getWorkspace(args: {
    workspaceId: string; //
  }): Promise<WorkspaceDescriptor>;

  initGitOnWorkspace(args: {
    workspaceId: string; //
    remoteUrl: URL;
  }): Promise<void>;

  initGistOnWorkspace(args: {
    workspaceId: string; //
    remoteUrl: URL;
  }): Promise<void>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
