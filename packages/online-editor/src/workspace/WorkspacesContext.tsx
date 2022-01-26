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
import { WorkspaceService } from "./services/WorkspaceService";
import { basename, extname, parse } from "path";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import LightningFS from "@isomorphic-git/lightning-fs";
import { GitService } from "./services/GitService";
import { GistOrigin, GitHubOrigin } from "./model/WorkspaceOrigin";
import { WorkspaceSvgService } from "./services/WorkspaceSvgService";

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
  getFileContents: () => Promise<Uint8Array>;
}

export interface WorkspacesContextType {
  service: WorkspaceService;
  gitService: GitService;
  svgService: WorkspaceSvgService;
  descriptorService: WorkspaceDescriptorService;
  fsService: WorkspaceFsService;

  // create
  createWorkspaceFromLocal: (args: {
    useInMemoryFs: boolean;
    localFiles: LocalFile[];
    preferredName?: string;
  }) => Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;

  createWorkspaceFromGitRepository: (args: {
    origin: GistOrigin | GitHubOrigin;
    gitConfig?: { email: string; name: string };
    authInfo?: {
      username: string;
      password: string;
    };
  }) => Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;

  pull(args: {
    fs: LightningFS;
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    authInfo?: {
      username: string;
      password: string;
    };
  }): Promise<void>;

  // edit workspace
  addEmptyFile(args: {
    fs: LightningFS;
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceFile>;
  prepareZip(args: { fs: LightningFS; workspaceId: string; onlyExtensions?: string[] }): Promise<Blob>;
  getFiles(args: { fs: LightningFS; workspaceId: string }): Promise<WorkspaceFile[]>;
  hasLocalChanges(args: { fs: LightningFS; workspaceId: string }): Promise<boolean>;
  createSavePoint(args: {
    fs: LightningFS;
    workspaceId: string;
    gitConfig?: { email: string; name: string };
  }): Promise<void>;
  getAbsolutePath(args: { workspaceId: string; relativePath?: string }): string;
  getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }): string;
  deleteWorkspace(args: { workspaceId: string }): Promise<void>;
  renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void>;

  resourceContentList: (args: {
    fs: LightningFS;
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }) => Promise<ResourcesList>;

  resourceContentGet: (args: {
    fs: LightningFS;
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }) => Promise<ResourceContent | undefined>;

  //

  getFile(args: { fs: LightningFS; workspaceId: string; relativePath: string }): Promise<WorkspaceFile | undefined>;

  renameFile(args: {
    fs: LightningFS;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceFile>;

  updateFile(args: {
    fs: LightningFS;
    file: WorkspaceFile;
    getNewContents: () => Promise<string | undefined>;
  }): Promise<void>;

  deleteFile(args: { fs: LightningFS; file: WorkspaceFile }): Promise<void>;

  addFile(args: {
    fs: LightningFS;
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  existsFile(args: { fs: LightningFS; workspaceId: string; relativePath: string }): Promise<boolean>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
