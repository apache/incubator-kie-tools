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

import { File } from "@kie-tooling-core/editor/dist/channel";
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

// TODO CAPONETTO: review and refactor this context
export interface WorkspaceContextType {
  file?: File;
  active?: ActiveWorkspace;
  setActive: React.Dispatch<React.SetStateAction<ActiveWorkspace> | undefined>;

  workspaceService: WorkspaceService;

  resourceContentGet: (path: string, opts?: ResourceContentOptions) => Promise<ResourceContent | undefined>;
  resourceContentList: (globPattern: string, opts?: ResourceListOptions) => Promise<ResourcesList>;

  openWorkspaceByPath: (path: string) => Promise<File>;
  openWorkspaceByFile: (file: File) => Promise<void>;
  openWorkspaceFile: (workspaceId: string, relativeFilePath: string) => Promise<File>;
  openWorkspaceById: (workspaceId: string) => Promise<void>;
  onFileChanged: (file: File) => void;
  onFileNameChanged: (newFileName: string) => Promise<File>;
  goToFileInNewWindow: (file: File) => Promise<void>;
  goToFile: (descriptor: WorkspaceDescriptor, file: File, replaceArgs: { replace: boolean }) => Promise<void>;

  createWorkspaceFromLocal: (
    files: File[],
    replaceUrl: boolean,
    preferredName?: string
  ) => Promise<WorkspaceDescriptor>;
  createWorkspaceFromGitHubRepository: (
    repositoryUrl: URL,
    sourceBranch: string,
    preferredName?: string
  ) => Promise<WorkspaceDescriptor>;

  addEmptyFile: (fileExtension: string) => Promise<void>;
  updateCurrentFile: (getFileContents: () => Promise<string | undefined>) => Promise<void>;

  prepareZip: () => Promise<Blob>;

  syncWorkspace: () => Promise<void>;

  listWorkspaceOverviews: () => Promise<WorkspaceOverview[]>;
}

export const WorkspaceContext = createContext<WorkspaceContextType>({} as any);

export function useWorkspaces(): WorkspaceContextType {
  return useContext(WorkspaceContext);
}
