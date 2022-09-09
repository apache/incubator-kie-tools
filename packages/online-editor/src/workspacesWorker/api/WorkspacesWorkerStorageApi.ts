/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import { WorkspaceWorkerFileDescriptor } from "./WorkspaceWorkerFileDescriptor";

export interface WorkspacesWorkerStorageApi {
  // convenience

  kieSandboxWorkspacesStorage_getAbsolutePath(args: { workspaceId: string; relativePath?: string }): string;
  kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }): string;

  // model

  kieSandboxWorkspacesStorage_deleteWorkspace(args: { workspaceId: string }): Promise<void>;
  kieSandboxWorkspacesStorage_renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void>;

  // util

  kieSandboxWorkspacesStorage_prepareZip(args: { workspaceId: string; onlyExtensions?: string[] }): Promise<Blob>;

  resourceContentList: (args: {
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }) => Promise<ResourcesList>;

  resourceContentGet: (args: {
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }) => Promise<ResourceContent | undefined>;

  // core

  kieSandboxWorkspacesStorage_getFiles(args: {
    workspaceId: string; //
  }): Promise<WorkspaceWorkerFileDescriptor[]>;

  kieSandboxWorkspacesStorage_addEmptyFile(args: {
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_getFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceWorkerFileDescriptor | undefined>;

  kieSandboxWorkspacesStorage_renameFile(args: {
    file: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_updateFile(args: {
    file: WorkspaceWorkerFileDescriptor;
    getNewContents: () => Promise<string | undefined>;
  }): Promise<void>;

  kieSandboxWorkspacesStorage_deleteFile(args: {
    workspaceId: string;
    file: WorkspaceWorkerFileDescriptor;
  }): Promise<void>;

  kieSandboxWorkspacesStorage_addFile(args: {
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_existsFile(args: {
    workspaceId: string; //
    relativePath: string;
  }): Promise<boolean>;
}
