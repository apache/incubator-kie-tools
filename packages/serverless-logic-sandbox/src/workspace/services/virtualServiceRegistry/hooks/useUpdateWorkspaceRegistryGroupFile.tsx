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
import { encoder } from "../../../commonServices/BaseFile";
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
      const file = await workspaces.getFile({
        fs: await workspaces.fsService.getFs(workspaceId),
        workspaceId,
        relativePath: data.relativePath || data.newRelativePath,
      });
      const vsrGroup = await virtualServiceRegistry.vsrGroupService.get(workspaceId);
      const currentFile = await virtualServiceRegistry.getFile({
        fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
        groupId: workspaceId,
        relativePath: functionPathFromWorkspaceFilePath(vsrGroup, data.relativePath || data.oldRelativePath),
      });
      if (currentFile) {
        if (data.type === "DELETE") {
          virtualServiceRegistry.deleteFile({
            fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
            file: currentFile,
          });
        }
        if (data.type === "RENAME_FILE") {
          if (currentFile) {
            virtualServiceRegistry.renameFile({
              fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
              file: currentFile,
              newFileNameWithoutExtension: data.newRelativePath.replace(
                `.${resolveExtension(data.newRelativePath)}`,
                ""
              ),
            });
          } else if (file) {
            const vsrFunction = new VirtualServiceRegistryFunction(file);
            await virtualServiceRegistry.addFile({
              fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
              groupId: workspaceId,
              name: vsrFunction.name,
              destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
              content: await vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content)),
              extension: vsrFunction.file.extension,
            });
          }
        }
        if (data.type === "UPDATE_FILE") {
          if (file) {
            const vsrFunction = new VirtualServiceRegistryFunction(file);
            if (currentFile) {
              await virtualServiceRegistry.updateFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                file: currentFile,
                getNewContents: async () => Promise.resolve(await vsrFunction.getOpenApiSpec()),
              });
            } else {
              await virtualServiceRegistry.addFile({
                fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
                groupId: workspaceId,
                name: vsrFunction.name,
                destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
                content: await vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content)),
                extension: vsrFunction.file.extension,
              });
            }
          }
        }
        if (data.type === "ADD_FILE") {
          if ((isServerlessWorkflow(data.relativePath) || isSpec(data.relativePath)) && file) {
            const vsrFunction = new VirtualServiceRegistryFunction(file);
            await virtualServiceRegistry.addFile({
              fs: await virtualServiceRegistry.vsrFsService.getFs(workspaceId),
              groupId: workspaceId,
              name: vsrFunction.name,
              destinationDirRelativePath: functionPath({ groupId: workspaceId }, vsrFunction),
              content: await vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content)),
              extension: vsrFunction.file.extension,
            });
          }
        }
      }
    };

    return () => {
      broadcastChannel.close();
    };
  }, [virtualServiceRegistry, workspaceFile, workspaces]);
}
