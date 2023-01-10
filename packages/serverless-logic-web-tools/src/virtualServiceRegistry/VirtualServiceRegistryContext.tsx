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

import * as React from "react";
import { createContext, useContext } from "react";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

export interface VirtualServiceRegistryContextType {
  listVsrWorkspaces(): Promise<WorkspaceDescriptor[]>;

  addVsrFileForWorkspaceFile(workspaceFile: WorkspaceFile): Promise<WorkspaceFile | undefined>;

  deleteVsrFile(args: { vsrFile: WorkspaceFile }): Promise<void>;

  renameVsrFile(args: { vsrFile: WorkspaceFile; newFileNameWithoutExtension: string }): Promise<WorkspaceFile>;

  updateVsrFile(args: { vsrFile: WorkspaceFile; getNewContents: () => Promise<string> }): Promise<void>;

  getVsrFiles(args: { vsrWorkspaceId: string; globPattern?: string }): Promise<WorkspaceFile[]>;

  getVsrFile(args: { vsrWorkspaceId: string; relativePath: string }): Promise<WorkspaceFile | undefined>;
}

export const VirtualServiceRegistryContext = createContext<VirtualServiceRegistryContextType>({} as any);

export function useVirtualServiceRegistry(): VirtualServiceRegistryContextType {
  return useContext(VirtualServiceRegistryContext);
}
