/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { VirtualServiceRegistryService } from "./services/VirtualServiceRegistryService";
import { VirtualServiceRegistryGroupService } from "./services/VirtualServiceRegistryGroupService";
import { VirtualServiceRegistryFsService } from "./services/VirtualServiceRegistryFsService";
import { StorageService } from "../../commonServices/StorageService";
import { VirtualServiceRegistryGroup } from "./models/VirtualServiceRegistry";
import { WorkspaceDescriptor } from "../../model/WorkspaceDescriptor";
import { ServiceRegistryFile } from "./models/ServiceRegistryFile";

export interface LocalFile {
  path: string;
  getFileContents: () => Promise<Uint8Array>;
}

export interface VirtualServiceRegistryContextType {
  storageService: StorageService;
  vsrService: VirtualServiceRegistryService;
  vsrGroupService: VirtualServiceRegistryGroupService;
  vsrFsService: VirtualServiceRegistryFsService;

  // create
  createServiceRegistryGroupFromWorkspace: (args: {
    useInMemoryFs: boolean;
    workspaceDescriptor: WorkspaceDescriptor;
  }) => Promise<{ vsrGroup: VirtualServiceRegistryGroup; files: ServiceRegistryFile[] }>;

  // edit service registry group
  getFiles(args: { fs: KieSandboxFs; groupId: string; globPattern?: string }): Promise<ServiceRegistryFile[]>;
  getAbsolutePath(args: { groupId: string; relativePath?: string }): string;
  getUniqueFileIdentifier(args: { groupId: string; relativePath: string }): string;
  deleteRegistryGroup(args: { groupId: string }): Promise<void>;
  renameRegistryGroup(args: { groupId: string; newName: string }): Promise<void>;

  resourceContentList: (args: {
    fs: KieSandboxFs;
    groupId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }) => Promise<ResourcesList>;

  resourceContentGet: (args: {
    fs: KieSandboxFs;
    groupId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }) => Promise<ResourceContent | undefined>;

  getFile(args: { fs: KieSandboxFs; groupId: string; relativePath: string }): Promise<ServiceRegistryFile | undefined>;

  renameFile(args: {
    fs: KieSandboxFs;
    file: ServiceRegistryFile;
    newFileNameWithoutExtension: string;
  }): Promise<ServiceRegistryFile>;

  updateFile(args: {
    fs: KieSandboxFs;
    file: ServiceRegistryFile;
    getNewContents: () => Promise<string>;
  }): Promise<void>;

  deleteFile(args: { fs: KieSandboxFs; file: ServiceRegistryFile }): Promise<void>;

  addFile(args: {
    fs: KieSandboxFs;
    groupId: string;
    name: string;
    destinationDirRelativePath: string;
    content: Uint8Array;
    extension: string;
  }): Promise<ServiceRegistryFile>;

  existsFile(args: { fs: KieSandboxFs; groupId: string; relativePath: string }): Promise<boolean>;
}

export const VirtualServiceRegistryContext = createContext<VirtualServiceRegistryContextType>({} as any);

export function useVirtualServiceRegistry(): VirtualServiceRegistryContextType {
  return useContext(VirtualServiceRegistryContext);
}
