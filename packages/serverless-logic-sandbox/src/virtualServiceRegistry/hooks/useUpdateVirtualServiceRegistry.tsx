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

import { extractExtension } from "@kie-tools-core/workspaces-git-fs/dist/relativePath/WorkspaceFileRelativePathParser";
import { basename } from "path";
import { useCallback } from "react";
import { SwfServiceCatalogStore } from "../../editor/api/SwfServiceCatalogStore";
import { isSupportedByVirtualServiceRegistry } from "../../extension";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import {
  buildUniqueWorkspaceBroadcastChannelName,
  buildWorkspacesBroadcastChannelName,
} from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsWorkspaceEvents";
import { WorkspaceBroadcastEvents } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceBroadcastEvents";
import {
  WorkspacesBroadcastEvents,
  WorkspacesFilesBroadcastEvents,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspacesBroadcastEvents";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { VirtualServiceRegistryFunction } from "../models/VirtualServiceRegistryFunction";
import { VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX } from "../VirtualServiceRegistryConstants";
import { useVirtualServiceRegistry } from "../VirtualServiceRegistryContext";

export function useUpdateVirtualServiceRegistryOnWorkspaceFileEvents(args: {
  workspaceFile: WorkspaceFile | undefined;
}) {
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const workspaces = useWorkspaces();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!args.workspaceFile) {
          return;
        }

        const vsrWorkspaceId = args.workspaceFile.workspaceId; // both are mapped to the same value
        const uniqueWorkspaceBroadcastChannel = new BroadcastChannel(vsrWorkspaceId);

        uniqueWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspaceBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (data.type === "WS_DELETE_FILE") {
            if (!isSupportedByVirtualServiceRegistry(data.relativePath)) {
              return;
            }

            const vsrFile = await virtualServiceRegistry.getVsrFile({
              vsrWorkspaceId,
              relativePath: data.relativePath,
            });
            if (!vsrFile) {
              return;
            }

            await virtualServiceRegistry.deleteVsrFile({ vsrFile });
          } else if (data.type === "WS_RENAME_FILE") {
            if (!isSupportedByVirtualServiceRegistry(data.oldRelativePath)) {
              return;
            }

            const vsrFile = await virtualServiceRegistry.getVsrFile({
              vsrWorkspaceId,
              relativePath: data.oldRelativePath,
            });
            if (!vsrFile) {
              return;
            }

            await virtualServiceRegistry.renameVsrFile({
              vsrFile,
              newFileNameWithoutExtension: basename(data.newRelativePath).replace(
                `.${extractExtension(data.newRelativePath)}`,
                ""
              ),
            });
          } else if (data.type === "WS_UPDATE_FILE") {
            if (!isSupportedByVirtualServiceRegistry(data.relativePath)) {
              return;
            }

            const workspaceFile = await workspaces.getFile({
              workspaceId: vsrWorkspaceId,
              relativePath: data.relativePath,
            });
            const vsrFile = await virtualServiceRegistry.getVsrFile({
              vsrWorkspaceId,
              relativePath: data.relativePath,
            });

            if (!workspaceFile || !vsrFile) {
              return;
            }

            const vsrFunction = new VirtualServiceRegistryFunction(workspaceFile);
            await virtualServiceRegistry.updateVsrFile({
              vsrFile,
              getNewContents: async () => vsrFunction.getOpenApiSpec(),
            });
          } else if (data.type === "WS_ADD_FILE") {
            if (!isSupportedByVirtualServiceRegistry(data.relativePath)) {
              return;
            }

            const workspaceFile = await workspaces.getFile({
              workspaceId: vsrWorkspaceId,
              relativePath: data.relativePath,
            });
            if (!workspaceFile) {
              return;
            }

            const vsrFile = await virtualServiceRegistry.getVsrFile({
              vsrWorkspaceId,
              relativePath: data.relativePath,
            });

            if (vsrFile) {
              return;
            }

            await virtualServiceRegistry.addVsrFileForWorkspaceFile(workspaceFile);
          }
        };

        return () => {
          uniqueWorkspaceBroadcastChannel.close();
        };
      },
      [args.workspaceFile, virtualServiceRegistry, workspaces]
    )
  );
}

export function useUpdateVirtualServiceRegistryOnVsrFileEvent(args: {
  workspaceId: string;
  catalogStore: SwfServiceCatalogStore;
}) {
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const uniqueVsrWorkspaceBroadcastChannel = new BroadcastChannel(
          buildUniqueWorkspaceBroadcastChannelName({
            workspaceId: args.workspaceId,
            prefix: VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX,
          })
        );

        uniqueVsrWorkspaceBroadcastChannel.onmessage = async ({
          data,
        }: MessageEvent<WorkspacesFilesBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (["WSSFS_ADD", "WSSFS_DELETE", "WSSFS_RENAME"].includes(data.type)) {
            await args.catalogStore.refresh();
          }
        };

        return () => {
          uniqueVsrWorkspaceBroadcastChannel.close();
        };
      },
      [args.catalogStore, args.workspaceId]
    )
  );
}

export function useUpdateVirtualServiceRegistryOnVsrWorkspaceEvent(args: { catalogStore: SwfServiceCatalogStore }) {
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const uniqueVsrWorkspaceBroadcastChannel = new BroadcastChannel(
          buildWorkspacesBroadcastChannelName(VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX)
        );

        uniqueVsrWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspacesBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (["WSS_ADD_WORKSPACE", "WSS_DELETE_WORKSPACE"].includes(data.type)) {
            await args.catalogStore.refresh();
          }
        };

        return () => {
          uniqueVsrWorkspaceBroadcastChannel.close();
        };
      },
      [args.catalogStore]
    )
  );
}
