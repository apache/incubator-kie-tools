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
import { useCallback } from "react";
import { SwfServiceCatalogStore } from "../../editor/api/SwfServiceCatalogStore";
import { isSupportedByVirtualServiceRegistry, resolveExtension } from "../../extension";
import { useCancelableEffect } from "../../reactExt/Hooks";
import {
  buildUniqueWorkspaceBroadcastChannelName,
  buildWorkspacesBroadcastChannelName,
} from "../../workspace/lfs/LfsWorkspaceEvents";
import { WorkspaceEvents } from "../../workspace/worker/api/WorkspaceEvents";
import { WorkspacesEvents } from "../../workspace/worker/api/WorkspacesEvents";
import { useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";
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

        uniqueWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspaceEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (data.type === "DELETE_FILE") {
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
          } else if (data.type === "RENAME_FILE") {
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
                `.${resolveExtension(data.newRelativePath)}`,
                ""
              ),
            });
          } else if (data.type === "UPDATE_FILE") {
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

            if (!workspaceFile) {
              return;
            }

            const vsrFunction = new VirtualServiceRegistryFunction(workspaceFile);
            const newContents = await vsrFunction.getOpenApiSpec();

            if (newContents) {
              if (vsrFile) {
                await virtualServiceRegistry.updateVsrFile({
                  vsrFile,
                  getNewContents: async () => Promise.resolve(newContents),
                });
              } else {
                await virtualServiceRegistry.addVsrFileForWorkspaceFile(workspaceFile);
              }
            } else if (vsrFile) {
              // Existing vsrFile but invalid new contents -> delete the file
              await virtualServiceRegistry.deleteVsrFile({ vsrFile });
            }
          } else if (data.type === "ADD_FILE") {
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

        uniqueVsrWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspaceEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (["ADD_FILE", "DELETE_FILE", "RENAME_FILE"].includes(data.type)) {
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

        uniqueVsrWorkspaceBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspacesEvents>) => {
          if (canceled.get()) {
            return;
          }

          if (["ADD_WORKSPACE", "DELETE_WORKSPACE"].includes(data.type)) {
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
