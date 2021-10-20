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
} from "@kie-tooling-core/workspace/dist/api";
import * as React from "react";
import { createContext, useContext } from "react";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { WorkspaceService } from "./services/WorkspaceService";
import { basename, extname, parse } from "path";
import { removeFileExtension } from "../common/utils";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import LightningFS from "@isomorphic-git/lightning-fs";

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
    return removeFileExtension(this.relativePath);
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
  descriptorService: WorkspaceDescriptorService;
  fsService: WorkspaceFsService;

  // create
  createWorkspaceFromLocal: (args: {
    useInMemoryFs: boolean;
    localFiles: LocalFile[];
  }) => Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;

  createWorkspaceFromGitRepository: (args: {
    repositoryUrl: URL;
    sourceBranch: string;
    githubSettings: { user: { login: string; email: string; name: string }; token: string };
  }) => Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;

  // edit workspace
  addEmptyFile(args: {
    fs: LightningFS;
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceFile>;
  prepareZip(args: { fs: LightningFS; workspaceId: string }): Promise<Blob>;
  getFiles(args: { fs: LightningFS; workspaceId: string }): Promise<WorkspaceFile[]>;
  isModified(args: { fs: LightningFS; workspaceId: string }): Promise<boolean>;
  createSavePoint(args: { fs: LightningFS; workspaceId: string }): Promise<void>;
  getAbsolutePath(args: { workspaceId: string; relativePath: string }): string;
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

  renameFile(args: { fs: LightningFS; file: WorkspaceFile; newFileName: string }): Promise<WorkspaceFile>;

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
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
