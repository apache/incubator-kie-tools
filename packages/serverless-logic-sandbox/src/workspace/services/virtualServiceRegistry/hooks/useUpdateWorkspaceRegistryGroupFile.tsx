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

import { useEffect } from "react";
import { isServerlessWorkflow, isSpec, resolveExtension } from "../../../../extension";
import { useWorkspaces, WorkspaceFile } from "../../../WorkspacesContext";
import {
  functionPath,
  functionPathFromWorkspaceFilePath,
  VirtualServiceRegistryFunction,
} from "../models/VirtualServiceRegistry";
import { useVirtualServiceRegistry } from "../VirtualServiceRegistryContext";

export function useUpdateWorkspaceRegistryGroupFile(args: { workspaceFile: WorkspaceFile | undefined }) {
  const { workspaceFile } = args;
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const workspaces = useWorkspaces();

  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    const workspaceId = workspaceFile.workspaceId;
    const broadcastChannel = new BroadcastChannel(workspaceId);
    broadcastChannel.onmessage = async ({ data }) => {
      if (data.type === "DELETE") {
        const vsrGroup = await virtualServiceRegistry.vsrGroupService.get(workspaceId);
        virtualServiceRegistry.deleteFile({
          fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
          path: functionPathFromWorkspaceFilePath(vsrGroup, data.relativePath),
        });
      } else {
        const file = await workspaces.getFile({
          fs: await workspaces.fsService.getFs(workspaceId),
          workspaceId,
          relativePath: data.relativePath || data.newRelativePath,
        });
        if (file) {
          if (data.type === "RENAME_FILE") {
            const vsrGroup = await virtualServiceRegistry.vsrGroupService.get(workspaceId);
            const currentFile = await virtualServiceRegistry.getFile({
              fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
              groupId: workspaceId,
              relativePath: functionPathFromWorkspaceFilePath(vsrGroup, data.oldRelativePath),
            });
            if (currentFile) {
              virtualServiceRegistry.renameFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                file: currentFile,
                newFileNameWithoutExtension: data.newRelativePath.replace(
                  `.${resolveExtension(data.newRelativePath)}`,
                  ""
                ),
              });
            } else {
              const vsrFunction = new VirtualServiceRegistryFunction(file);
              await virtualServiceRegistry.addFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                groupId: workspaceId,
                name: vsrFunction.name,
                destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
                content: await vsrFunction.getOpenApiSpec(),
                extension: vsrFunction.file.extension,
              });
            }
          }
          if (data.type === "UPDATE_FILE") {
            const vsrGroup = await virtualServiceRegistry.vsrGroupService.get(workspaceId);
            const vsrFunction = new VirtualServiceRegistryFunction(file);
            const currentFile = await virtualServiceRegistry.getFile({
              fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
              groupId: workspaceId,
              relativePath: functionPath(vsrGroup, vsrFunction),
            });

            if (currentFile) {
              await virtualServiceRegistry.updateFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                file: currentFile,
                getNewContents: () => vsrFunction.getOpenApiSpec(),
              });
            } else {
              await virtualServiceRegistry.addFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                groupId: workspaceId,
                name: vsrFunction.name,
                destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
                content: await vsrFunction.getOpenApiSpec(),
                extension: vsrFunction.file.extension,
              });
            }
          }
          if (data.type === "ADD_FILE") {
            if (isServerlessWorkflow(data.relativePath) || isSpec(data.relativePath)) {
              const vsrFunction = new VirtualServiceRegistryFunction(file);
              await virtualServiceRegistry.addFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                groupId: workspaceId,
                name: vsrFunction.name,
                destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
                content: await vsrFunction.getOpenApiSpec(),
                extension: vsrFunction.file.extension,
              });
            }
          }
        }
      }
    };

    return () => {
      broadcastChannel.close();
    };
  }, [virtualServiceRegistry, workspaceFile, workspaces]);
}
