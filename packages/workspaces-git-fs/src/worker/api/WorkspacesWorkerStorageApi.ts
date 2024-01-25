/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { WorkspaceWorkerFileDescriptor } from "./WorkspaceWorkerFileDescriptor";
import { WorkspaceDescriptor } from "./WorkspaceDescriptor";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

export interface WorkspacesWorkerStorageApi {
  // model

  kieSandboxWorkspacesStorage_deleteWorkspace(args: { workspaceId: string }): Promise<void>;
  kieSandboxWorkspacesStorage_renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void>;

  kieSandboxWorkspacesStorage_getWorkspace(args: { workspaceId: string }): Promise<WorkspaceDescriptor>;

  kieSandboxWorkspacesStorage_listAllWorkspaces(): Promise<WorkspaceDescriptor[]>;

  // util

  kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<string>;

  kieSandboxWorkspacesStorage_prepareZip(args: {
    workspaceId: string;
    onlyExtensions?: string[];
    globPattern?: string;
  }): Promise<Blob>;

  kieSandboxWorkspacesStorage_resourceContentList(args: {
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }): Promise<ResourcesList>;

  kieSandboxWorkspacesStorage_resourceContentGet(args: {
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }): Promise<ResourceContent | undefined>;

  // core

  kieSandboxWorkspacesStorage_getFiles(args: {
    workspaceId: string;
    globPattern?: string;
  }): Promise<WorkspaceWorkerFileDescriptor[]>;

  kieSandboxWorkspacesStorage_addEmptyFile(args: {
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_getFileContent(args: { workspaceId: string; relativePath: string }): Promise<Uint8Array>;

  kieSandboxWorkspacesStorage_getFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceWorkerFileDescriptor | undefined>;

  kieSandboxWorkspacesStorage_renameFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_updateFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newContent: string;
  }): Promise<void>;

  kieSandboxWorkspacesStorage_deleteFile(args: { wwfd: WorkspaceWorkerFileDescriptor }): Promise<void>;

  kieSandboxWorkspacesStorage_moveFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newDirPath: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_addFile(args: {
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceWorkerFileDescriptor>;

  kieSandboxWorkspacesStorage_existsFile(args: { workspaceId: string; relativePath: string }): Promise<boolean>;

  kieSandboxWorkspacesStorage_flushes(): SharedValueProvider<string[]>;
}
