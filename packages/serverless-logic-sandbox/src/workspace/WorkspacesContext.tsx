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

import * as React from "react";
import { createContext, useContext } from "react";
import {
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { WorkspaceService } from "./services/WorkspaceService";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { GitService } from "./commonServices/GitService";
import { GistOrigin, GitHubOrigin } from "./model/WorkspaceOrigin";
import { WorkspaceSvgService } from "./services/WorkspaceSvgService";
import { StorageService } from "./commonServices/StorageService";
import { BaseFile, BaseFileProps } from "./commonServices/BaseFile";

export interface WorkspaceFileProps extends BaseFileProps {
  workspaceId: string;
}

export class WorkspaceFile extends BaseFile {
  constructor(protected readonly args: WorkspaceFileProps) {
    super(args);
  }

  get workspaceId() {
    return this.args.workspaceId;
  }

  get parentId() {
    return this.workspaceId;
  }
}

export interface LocalFile {
  path: string;
  getFileContents: () => Promise<Uint8Array>;
}

export interface WorkspacesContextType {
  storageService: StorageService;
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
    fs: KieSandboxFs;
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    authInfo?: {
      username: string;
      password: string;
    };
  }): Promise<void>;

  // edit workspace
  addEmptyFile(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceFile>;
  prepareZip(args: { fs: KieSandboxFs; workspaceId: string; onlyExtensions?: string[] }): Promise<Blob>;
  prepareZipWithFiles(args: { workspaceId: string; files: WorkspaceFile[] }): Promise<Blob>;
  getFiles(args: { fs: KieSandboxFs; workspaceId: string; globPattern?: string }): Promise<WorkspaceFile[]>;
  hasLocalChanges(args: { fs: KieSandboxFs; workspaceId: string }): Promise<boolean>;
  createSavePoint(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    gitConfig?: { email: string; name: string };
  }): Promise<void>;
  getAbsolutePath(args: { workspaceId: string; relativePath?: string }): string;
  getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }): string;
  deleteWorkspace(args: { workspaceId: string }): Promise<void>;
  renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void>;

  resourceContentList: (args: {
    fs: KieSandboxFs;
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }) => Promise<ResourcesList>;

  resourceContentGet: (args: {
    fs: KieSandboxFs;
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }) => Promise<ResourceContent | undefined>;

  //

  getFile(args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }): Promise<WorkspaceFile | undefined>;

  renameFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceFile>;

  updateFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    getNewContents: () => Promise<string | undefined>;
  }): Promise<void>;

  deleteFile(args: { fs: KieSandboxFs; file: WorkspaceFile }): Promise<void>;

  addFile(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceFile>;

  existsFile(args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }): Promise<boolean>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
