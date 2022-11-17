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
import { useCallback, useMemo } from "react";
import { GLOB_PATTERN } from "../extension";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { LfsWorkspaceDescriptorService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsWorkspaceDescriptorService";
import { LfsWorkspaceFsService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsWorkspaceFsService";
import { LfsWorkspaceService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsWorkspaceService";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { VirtualServiceRegistryFunction } from "./models/VirtualServiceRegistryFunction";
import {
  VIRTUAL_SERVICE_REGISTRY_DESCRIPTOR_DATABASE_NAME,
  VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX,
  VIRTUAL_SERVICE_REGISTRY_MOUNT_POINT,
} from "./VirtualServiceRegistryConstants";
import { VirtualServiceRegistryContext } from "./VirtualServiceRegistryContext";
import { toVsrFunctionPathFromWorkspaceFilePath, toVsrMountPoint } from "./VirtualServiceRegistryPathConverter";
import {
  WorkspacesBroadcastEvents,
  WORKSPACES_BROADCAST_CHANNEL,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspacesBroadcastEvents";

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;

interface Props {
  children: React.ReactNode;
}

export function VirtualServiceRegistryContextProvider(props: Props) {
  const workspaces = useWorkspaces();

  const vsrStorageService = useMemo(() => new LfsStorageService(), []);
  const vsrDescriptorService = useMemo(
    () => new LfsWorkspaceDescriptorService(VIRTUAL_SERVICE_REGISTRY_DESCRIPTOR_DATABASE_NAME, vsrStorageService),
    [vsrStorageService]
  );
  const vsrService = useMemo(
    () =>
      new LfsWorkspaceService({
        storageService: vsrStorageService,
        descriptorService: vsrDescriptorService,
        fsMountPoint: VIRTUAL_SERVICE_REGISTRY_MOUNT_POINT,
        eventNamePrefix: VIRTUAL_SERVICE_REGISTRY_EVENT_PREFIX,
      }),
    [vsrStorageService, vsrDescriptorService]
  );
  const vsrFsService = useMemo(
    () => new LfsWorkspaceFsService(vsrDescriptorService, toVsrMountPoint),
    [vsrDescriptorService]
  );

  const listVsrWorkspaces = useCallback(async () => vsrDescriptorService.listAll(), [vsrDescriptorService]);

  const renameVsrFile = useCallback(
    async (args: { vsrFile: WorkspaceFile; newFileNameWithoutExtension: string }) =>
      vsrService.renameFile({
        fs: await vsrFsService.getFs(args.vsrFile.workspaceId),
        file: args.vsrFile,
        broadcastArgs: { broadcast: true },
        ...args,
      }),
    [vsrFsService, vsrService]
  );

  const getVsrFiles = useCallback(
    async (args: { vsrWorkspaceId: string; globPattern?: string }) =>
      vsrService.getFilesWithLazyContent({
        fs: await vsrFsService.getFs(args.vsrWorkspaceId),
        workspaceId: args.vsrWorkspaceId,
        ...args,
      }),
    [vsrService, vsrFsService]
  );

  const getVsrFile = useCallback(
    async (args: { vsrWorkspaceId: string; relativePath: string }) =>
      vsrService.getFile({
        fs: await vsrFsService.getFs(args.vsrWorkspaceId),
        relativePath: toVsrFunctionPathFromWorkspaceFilePath({
          vsrWorkspaceId: args.vsrWorkspaceId,
          relativePath: args.relativePath,
        }),
        workspaceId: args.vsrWorkspaceId,
      }),
    [vsrService, vsrFsService]
  );

  const deleteVsrFile = useCallback(
    async (args: { vsrFile: WorkspaceFile }) =>
      vsrService.deleteFile({
        fs: await vsrFsService.getFs(args.vsrFile.workspaceId),
        file: args.vsrFile,
        broadcastArgs: { broadcast: true },
      }),
    [vsrService, vsrFsService]
  );

  const updateVsrFile = useCallback(
    async (args: { vsrFile: WorkspaceFile; getNewContents: () => Promise<string> }) =>
      vsrService.updateFile({
        fs: await vsrFsService.getFs(args.vsrFile.workspaceId),
        file: args.vsrFile,
        broadcastArgs: { broadcast: false },
        ...args,
      }),
    [vsrService, vsrFsService]
  );

  const addVsrFileForWorkspaceFile = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const workspaceId = workspaceFile.workspaceId;
      const vsrFunction = new VirtualServiceRegistryFunction(workspaceFile);
      const vsrFunctionPath = toVsrFunctionPathFromWorkspaceFilePath({
        vsrWorkspaceId: workspaceId,
        relativePath: vsrFunction.relativePath,
      });
      const vsrFunctionContent = await vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content));

      if (!vsrFunctionContent) {
        return;
      }

      const fs = await vsrFsService.getFs(workspaceId);
      for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        if (
          await vsrService.existsFile({
            fs,
            workspaceId,
            relativePath: vsrFunctionPath,
          })
        ) {
          continue;
        }

        const newFile = new WorkspaceFile({
          workspaceId,
          relativePath: vsrFunctionPath,
          getFileContents: async () => vsrFunctionContent,
        });
        await vsrService.createOrOverwriteFile({ fs, file: newFile, broadcastArgs: { broadcast: true } });
        return newFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    },
    [vsrFsService, vsrService]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const workspacesBroadcastChannel = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);

        workspacesBroadcastChannel.onmessage = async ({ data }: MessageEvent<WorkspacesBroadcastEvents>) => {
          if (canceled.get()) {
            return;
          }
          if (data.type === "WSS_ADD_WORKSPACE") {
            if (await vsrDescriptorService.exists(data.workspaceId)) {
              return;
            }
            const workspaceDescriptor = await workspaces.getWorkspace({ workspaceId: data.workspaceId });
            const storeRegistryFiles = async (vsrDescriptor: WorkspaceDescriptor) => {
              const workflowFiles = await workspaces.getFiles({
                workspaceId: vsrDescriptor.workspaceId,
                globPattern: GLOB_PATTERN.sw_spec,
              });

              const filesSpecs = workflowFiles.map((file) => {
                const vsrFunction = new VirtualServiceRegistryFunction(file);
                return new LfsStorageFile({
                  path: `/${toVsrFunctionPathFromWorkspaceFilePath({
                    vsrWorkspaceId: data.workspaceId,
                    relativePath: vsrFunction.relativePath,
                  })}`,
                  getFileContents: () => vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content)),
                });
              });

              const fs = await vsrFsService.getFs(vsrDescriptor.workspaceId);
              await vsrStorageService.createFiles(fs, filesSpecs);
              return vsrService.getFilesWithLazyContent({ fs, workspaceId: vsrDescriptor.workspaceId });
            };

            await vsrService.create({
              storeFiles: storeRegistryFiles,
              descriptorArgs: workspaceDescriptor,
              broadcastArgs: { broadcast: true },
            });
          } else if (data.type === "WSS_DELETE_WORKSPACE") {
            if (await vsrDescriptorService.exists(data.workspaceId)) {
              await vsrService.delete({ workspaceId: data.workspaceId, broadcastArgs: { broadcast: true } });
            }
          } else if (data.type === "WSS_RENAME_WORKSPACE") {
            const workspaceDescriptor = await workspaces.getWorkspace({ workspaceId: data.workspaceId });
            const vsrDescriptor = await vsrDescriptorService.get(data.workspaceId);
            if (vsrDescriptor.name === workspaceDescriptor.name) {
              return;
            }
            await vsrService.rename({
              workspaceId: data.workspaceId,
              newName: workspaceDescriptor.name,
              broadcastArgs: { broadcast: true },
            });
          }
        };

        return () => {
          workspacesBroadcastChannel.close();
        };
      },
      [vsrDescriptorService, vsrFsService, vsrService, vsrStorageService, workspaces]
    )
  );

  const value = useMemo(
    () => ({
      listVsrWorkspaces,
      addVsrFileForWorkspaceFile,
      deleteVsrFile,
      renameVsrFile,
      updateVsrFile,
      getVsrFiles,
      getVsrFile,
    }),
    [
      listVsrWorkspaces,
      addVsrFileForWorkspaceFile,
      deleteVsrFile,
      renameVsrFile,
      updateVsrFile,
      getVsrFiles,
      getVsrFile,
    ]
  );

  return (
    <VirtualServiceRegistryContext.Provider value={value}>{props.children}</VirtualServiceRegistryContext.Provider>
  );
}
