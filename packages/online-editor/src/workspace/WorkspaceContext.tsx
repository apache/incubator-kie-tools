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

export interface WorkspaceContextType {
  file?: File;
  active?: ActiveWorkspace;
  setActive: React.Dispatch<React.SetStateAction<ActiveWorkspace> | undefined>;

  resourceContentGet: (path: string, opts?: ResourceContentOptions) => Promise<ResourceContent | undefined>;
  resourceContentList: (globPattern: string, opts?: ResourceListOptions) => Promise<ResourcesList>;

  openWorkspaceByPath: (path: string) => Promise<void>;
  openWorkspaceByFile: (file: File) => Promise<void>;

  onFileChanged: (file: File) => void;
  onFileNameChanged: (newFileName: string) => Promise<void>;

  createWorkspaceFromLocal: (files: File[], preferredName?: string) => Promise<void>;
  createWorkspaceFromGitHubRepository: (
    repositoryUrl: URL,
    sourceBranch: string,
    preferredName?: string
  ) => Promise<void>;

  addEmptyFile: (fileExtension: string) => Promise<void>;
  updateCurrentFile: (getFileContents: () => Promise<string | undefined>) => Promise<void>;

  prepareZip: () => Promise<Blob>;

  syncWorkspace: () => Promise<void>;
}

export const WorkspaceContext = createContext<WorkspaceContextType>({} as any);

export function useWorkspaces(): WorkspaceContextType {
  return useContext(WorkspaceContext);
}
