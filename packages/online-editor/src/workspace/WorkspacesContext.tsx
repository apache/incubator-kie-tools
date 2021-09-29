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
import { ActiveWorkspace } from "./model/ActiveWorkspace";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { WorkspaceOverview } from "./model/WorkspaceOverview";
import { WorkspaceService } from "./services/WorkspaceService";
import { basename, extname } from "path";
import { removeFileExtension } from "../common/utils";

export class WorkspaceFile {
  constructor(private readonly args: { path: string; getFileContents: () => Promise<string | undefined> }) {}

  get path() {
    return this.args.path;
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

// TODO CAPONETTO: review and refactor this context
export interface WorkspacesContextType {
  file?: WorkspaceFile;
  active?: ActiveWorkspace;
  setActive: React.Dispatch<React.SetStateAction<ActiveWorkspace> | undefined>;

  workspaceService: WorkspaceService;

  resourceContentGet: (path: string, opts?: ResourceContentOptions) => Promise<ResourceContent | undefined>;
  resourceContentList: (globPattern: string, opts?: ResourceListOptions) => Promise<ResourcesList>;

  openWorkspaceByPath: (path: string) => Promise<WorkspaceFile>;
  openWorkspaceByFile: (file: WorkspaceFile) => Promise<void>;
  openWorkspaceFile: (workspaceId: string, relativeFilePath: string) => Promise<WorkspaceFile>;
  openWorkspaceById: (workspaceId: string) => Promise<void>;
  onFileNameChanged: (newFileName: string) => Promise<WorkspaceFile>;

  createWorkspaceFromLocal: (
    files: LocalFile[],
    preferredName?: string
  ) => Promise<{ descriptor: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceFile }>;
  createWorkspaceFromGitHubRepository: (
    repositoryUrl: URL,
    sourceBranch: string,
    preferredName?: string
  ) => Promise<WorkspaceDescriptor>;

  addEmptyFile: (fileExtension: string) => Promise<WorkspaceFile>;
  updateCurrentFile: (getFileContents: () => Promise<string | undefined>) => Promise<void>;

  prepareZip: () => Promise<Blob>;

  syncWorkspace: () => Promise<void>;

  listWorkspaceOverviews: () => Promise<WorkspaceOverview[]>;
}

export const WorkspacesContext = createContext<WorkspacesContextType>({} as any);

export function useWorkspaces(): WorkspacesContextType {
  return useContext(WorkspacesContext);
}
