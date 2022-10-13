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

import { basename } from "path";
import { useEffect } from "react";
import { isServerlessWorkflow, isSpec, resolveExtension } from "../../../../extension";
import { WorkspaceEvents } from "../../../worker/api/WorkspaceEvents";
import { useWorkspaces, WorkspaceFile } from "../../../WorkspacesContext";
import { VirtualServiceRegistryFunction } from "../models/VirtualServiceRegistryFunction";
import { useVirtualServiceRegistry } from "../VirtualServiceRegistryContext";

export function useUpdateVirtualServiceRegistryOnWorkspaceFileEvents(args: {
  workspaceFile: WorkspaceFile | undefined;
}) {
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const workspaces = useWorkspaces();

  useEffect(() => {
    if (!args.workspaceFile) {
      return;
    }

    const workspaceId = args.workspaceFile.workspaceId;
    const uniqueWorkspaceBroadcastChannel = new BroadcastChannel(workspaceId);

    uniqueWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspaceEvents>) => {
      if (data.type === "DELETE_FILE") {
        const vsrFile = await virtualServiceRegistry.getVsrFile({
          vsrWorkspaceId: workspaceId,
          relativePath: data.relativePath,
        });
        if (!vsrFile) {
          return;
        }

        virtualServiceRegistry.deleteVsrFile({ vsrFile });
      } else if (data.type === "RENAME_FILE") {
        const vsrFile = await virtualServiceRegistry.getVsrFile({
          vsrWorkspaceId: workspaceId,
          relativePath: data.oldRelativePath,
        });
        if (!vsrFile) {
          return;
        }

        virtualServiceRegistry.renameVsrFile({
          vsrFile,
          newFileNameWithoutExtension: basename(data.newRelativePath).replace(
            `.${resolveExtension(data.newRelativePath)}`,
            ""
          ),
        });
      } else if (data.type === "UPDATE_FILE") {
        const workspaceFile = await workspaces.getFile({ workspaceId, relativePath: data.relativePath });
        const vsrFile = await virtualServiceRegistry.getVsrFile({
          vsrWorkspaceId: workspaceId,
          relativePath: data.relativePath,
        });
        if (!workspaceFile || !vsrFile) {
          return;
        }

        const vsrFunction = new VirtualServiceRegistryFunction(workspaceFile);
        await virtualServiceRegistry.updateVsrFile({
          vsrFile,
          getNewContents: async () => Promise.resolve(await vsrFunction.getOpenApiSpec()),
        });
      } else if (data.type === "ADD_FILE") {
        if (!isServerlessWorkflow(data.relativePath) && !isSpec(data.relativePath)) {
          return;
        }

        const workspaceFile = await workspaces.getFile({ workspaceId, relativePath: data.relativePath });
        if (!workspaceFile) {
          return;
        }

        await virtualServiceRegistry.addVsrFileForWorkspaceFile(workspaceFile);
      }
    };

    return () => {
      uniqueWorkspaceBroadcastChannel.close();
    };
  }, [virtualServiceRegistry, args.workspaceFile, workspaces]);
}
