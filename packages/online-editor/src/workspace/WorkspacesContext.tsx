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
import { basename, dirname, extname } from "path";
import { removeFileExtension } from "../common/utils";

export class WorkspaceFile {
  constructor(private readonly args: { path: string; getFileContents: () => Promise<string> }) {}

  get path() {
    return this.args.path;
  }

  get folderPath() {
    return dirname(this.args.path);
  }

  get getFileContents() {
    return this.args.getFileContents;
  }

  get pathRelativeToWorkspaceRoot() {
    return this.args.path.replace(`/${this.workspaceId}/`, "");
  }

  get pathRelativeToWorkspaceRootWithoutExtension() {
    return removeFileExtension(this.pathRelativeToWorkspaceRoot);
  }

  get extension() {
    return extname(this.args.path).replace(".", "");
  }

  get nameWithoutExtension() {
    return basename(this.args.path, `.${this.extension}`);
  }

  get nameWithExtension() {
    return basename(this.args.path);
  }

  get workspaceId() {
    return this.args.path
      .split("/")
      .reverse()
      .filter((a) => a)
      .pop()!;
  }
}

export interface LocalFile {
  path: string;
  getFileContents: () => Promise<string>;
}

export interface WorkspacesContextType {
  workspaceService: WorkspaceService;

  // create
  createWorkspaceFromLocal: (
    files: LocalFile[]
  ) => Promise<{ descriptor: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;
  createWorkspaceFromGitHubRepository: (repositoryUrl: URL, sourceBranch: string) => Promise<WorkspaceDescriptor>;

  // edit workspace
  addEmptyFile: (destinationFolder: string, fileExtension: string) => Promise<WorkspaceFile>;
  prepareZip: (workspaceId: string) => Promise<Blob>;
  resourceContentList: (workspaceId: string, globPattern: string, opts?: ResourceListOptions) => Promise<ResourcesList>;

  // edit files
  renameFile: (file: WorkspaceFile, newFileName: string) => Promise<WorkspaceFile>;
  updateFile: (file: WorkspaceFile, getNewContents: () => Promise<string | undefined>) => Promise<void>;
  resourceContentGet: (path: string, opts?: ResourceContentOptions) => Promise<ResourceContent | undefined>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
