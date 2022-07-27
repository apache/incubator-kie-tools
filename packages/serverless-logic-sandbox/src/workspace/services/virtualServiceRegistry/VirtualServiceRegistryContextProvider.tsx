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
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useMemo, useEffect } from "react";
import { Buffer } from "buffer";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { VirtualServiceRegistryContext } from "./VirtualServiceRegistryContext";
import { VirtualServiceRegistryGroupService } from "./services/VirtualServiceRegistryGroupService";
import { VirtualServiceRegistryFsService } from "./services/VirtualServiceRegistryFsService";
import { VirtualServiceRegistryService } from "./services/VirtualServiceRegistryService";
import { StorageFile, StorageService } from "../../commonServices/StorageService";
import {
  functionPath,
  VirtualServiceRegistryFunction,
  VirtualServiceRegistryGroup,
} from "./models/VirtualServiceRegistry";
import { ServiceRegistryFile } from "./models/ServiceRegistryFile";
import { WorkspaceDescriptor } from "../../model/WorkspaceDescriptor";
import { useWorkspaces } from "../../WorkspacesContext";
import { isServerlessWorkflow, isSpec } from "../../../extension";
import { decoder, encoder } from "../../commonServices/BaseFile";

type SupportedFileExtensions = ".yaml" | ".json";
const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;

interface Props {
  children: React.ReactNode;
}

export function VirtualServiceRegistryContextProvider(props: Props) {
  const storageService = useMemo(() => new StorageService(), []);
  const vsrGroupService = useMemo(() => new VirtualServiceRegistryGroupService(storageService), [storageService]);
  const vsrFsService = useMemo(() => new VirtualServiceRegistryFsService(vsrGroupService), [vsrGroupService]);
  const vsrService = useMemo(
    () => new VirtualServiceRegistryService(storageService, vsrGroupService, vsrFsService),
    [storageService, vsrGroupService, vsrFsService]
  );
  const workspaces = useWorkspaces();

  const getAbsolutePath = useCallback(
    (args: { groupId: string; relativePath?: string }) =>
      vsrService.getAbsolutePath({ descriptorId: args.groupId, ...args }),
    [vsrService]
  );

  const getUniqueFileIdentifier = useCallback(
    (args: { groupId: string; relativePath: string }) =>
      vsrService.getUniqueFileIdentifier({ descriptorId: args.groupId, ...args }),
    [vsrService]
  );

  const createServiceRegistryGroup = useCallback(
    async (args: {
      useInMemoryFs: boolean;
      storeRegistryFiles: (fs: KieSandboxFs, vsrGroup: VirtualServiceRegistryGroup) => Promise<ServiceRegistryFile[]>;
      workspaceDescriptor: WorkspaceDescriptor;
    }) => {
      const { descriptor: vsrGroup, files } = await vsrService.create({
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: args.storeRegistryFiles,
        workspaceDescriptor: args.workspaceDescriptor,
        broadcastArgs: { broadcast: true },
      });

      return { vsrGroup, files };
    },
    [vsrService]
  );

  const createServiceRegistryGroupFromWorkspace = useCallback(
    async (args: { useInMemoryFs: boolean; workspaceDescriptor: WorkspaceDescriptor }) => {
      return createServiceRegistryGroup({
        useInMemoryFs: args.useInMemoryFs,
        workspaceDescriptor: args.workspaceDescriptor,
        storeRegistryFiles: async (fs: KieSandboxFs, vsrGroup: VirtualServiceRegistryGroup) => {
          const files = await workspaces.getFiles({
            fs: await workspaces.fsService.getFs(vsrGroup.groupId),
            workspaceId: vsrGroup.groupId,
          });

          const workflowFiles = files.filter(
            (file) => isServerlessWorkflow(file.relativePath) || isSpec(file.relativePath)
          );

          const filesSpecs = workflowFiles.map((file) => {
            const vsrFunction = new VirtualServiceRegistryFunction(file);
            return new StorageFile({
              path: `/${functionPath(vsrGroup, vsrFunction)}`,
              getFileContents: () => vsrFunction.getOpenApiSpec().then((content) => encoder.encode(content)),
            });
          });

          await storageService.createFiles(fs, filesSpecs);

          return vsrService.getFilesWithLazyContent(fs, vsrGroup.groupId);
        },
      });
    },
    [createServiceRegistryGroup, workspaces, storageService, vsrService]
  );

  const renameFile = useCallback(
    async (args: { fs: KieSandboxFs; file: ServiceRegistryFile; newFileNameWithoutExtension: string }) => {
      const newFile = await vsrService.renameFile({
        fs: args.fs,
        file: args.file,
        newFileNameWithoutExtension: args.newFileNameWithoutExtension,
        broadcastArgs: { broadcast: true },
      });
      return newFile;
    },
    [vsrService]
  );

  const getFiles = useCallback(
    async (args: { fs: KieSandboxFs; groupId: string; globPattern?: string }) => {
      return vsrService.getFilesWithLazyContent(args.fs, args.groupId, args.globPattern);
    },
    [vsrService]
  );

  const getFile = useCallback(
    async (args: { fs: KieSandboxFs; groupId: string; relativePath: string }) => {
      return vsrService.getFile({
        ...args,
        descriptorId: args.groupId,
      });
    },
    [vsrService]
  );

  const deleteFile = useCallback(
    async (args: { fs: KieSandboxFs; file: ServiceRegistryFile }) => {
      await vsrService.deleteFile(args.fs, args.file, { broadcast: true });
    },
    [vsrService]
  );

  const updateFile = useCallback(
    async (args: { fs: KieSandboxFs; file: ServiceRegistryFile; getNewContents: () => Promise<string> }) => {
      await vsrService.updateFile(args.fs, args.file, args.getNewContents, { broadcast: true });
    },
    [vsrService]
  );

  const addFile = useCallback(
    async (args: {
      fs: KieSandboxFs;
      groupId: string;
      name: string;
      destinationDirRelativePath: string;
      content: Uint8Array;
      extension: SupportedFileExtensions;
    }) => {
      for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        if (
          await vsrService.existsFile({
            fs: args.fs,
            descriptorId: args.groupId,
            relativePath: args.destinationDirRelativePath,
          })
        ) {
          continue;
        }

        const newFile = new ServiceRegistryFile({
          groupId: args.groupId,
          getFileContents: () => Promise.resolve(args.content!),
          relativePath: args.destinationDirRelativePath,
          needsWorkspaceDeploy: true,
        });
        await vsrService.createOrOverwriteFile(args.fs, newFile, { broadcast: true });
        return newFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    },
    [vsrService]
  );

  const existsFile = useCallback(
    async (args: { fs: KieSandboxFs; groupId: string; relativePath: string }) =>
      await vsrService.existsFile({ descriptorId: args.groupId, ...args }),
    [vsrService]
  );

  const resourceContentGet = useCallback(
    async (args: { fs: KieSandboxFs; groupId: string; relativePath: string; opts?: ResourceContentOptions }) => {
      const file = await vsrService.getFile({ descriptorId: args.groupId, ...args });
      if (!file) {
        throw new Error(`File '${args.relativePath}' not found in Workspace ${args.groupId}`);
      }

      try {
        const content = await file.getFileContents();
        if (args.opts?.type === "binary") {
          return new ResourceContent(args.relativePath, Buffer.from(content).toString("base64"), ContentType.BINARY);
        }

        // "text" is the default
        return new ResourceContent(args.relativePath, decoder.decode(content), ContentType.TEXT);
      } catch (e) {
        console.error(e);
        throw e;
      }
    },
    [vsrService]
  );

  const resourceContentList = useCallback(
    async (args: { fs: KieSandboxFs; groupId: string; globPattern: string }) => {
      const files = await vsrService.getFilesWithLazyContent(args.fs, args.groupId, args.globPattern);
      const matchingPaths = files.map((file) => file.relativePath);
      return new ResourcesList(args.globPattern, matchingPaths);
    },
    [vsrService]
  );

  const deleteRegistryGroup = useCallback(
    async (args: { groupId: string }) => {
      await vsrService.delete(args.groupId, { broadcast: true });
    },
    [vsrService]
  );

  const renameRegistryGroup = useCallback(
    async (args: { groupId: string; newName: string }) => {
      await vsrService.rename(args.groupId, args.newName, { broadcast: true });
    },
    [vsrService]
  );

  useEffect(() => {
    const broadcastChannel = new BroadcastChannel("workspaces");
    broadcastChannel.onmessage = async ({ data }) => {
      if (data.type === "ADD_WORKSPACE") {
        const workspaceDescriptor = await workspaces.descriptorService.get(data.workspaceId);
        createServiceRegistryGroupFromWorkspace({ useInMemoryFs: false, workspaceDescriptor });
      }
      if (data.type === "DELETE_WORKSPACE") {
        deleteRegistryGroup({ groupId: data.workspaceId });
      }
      if (data.type === "RENAME_WORKSPACE") {
        const workspaceDescriptor = await workspaces.descriptorService.get(data.workspaceId);
        renameRegistryGroup({ groupId: data.workspaceId, newName: workspaceDescriptor.name });
      }
    };

    return () => {
      broadcastChannel.close();
    };
  }, [createServiceRegistryGroupFromWorkspace, deleteRegistryGroup, renameRegistryGroup, workspaces.descriptorService]);

  const value = useMemo(
    () => ({
      storageService,
      vsrService,
      vsrGroupService,
      vsrFsService,
      //
      resourceContentGet,
      resourceContentList,
      //
      createServiceRegistryGroupFromWorkspace,
      renameRegistryGroup,
      deleteRegistryGroup,
      getAbsolutePath,
      getUniqueFileIdentifier,
      getFiles,
      //
      addFile,
      existsFile,
      renameFile,
      updateFile,
      deleteFile,
      getFile,
    }),
    [
      storageService,
      vsrService,
      vsrGroupService,
      vsrFsService,
      resourceContentGet,
      resourceContentList,
      createServiceRegistryGroupFromWorkspace,
      renameRegistryGroup,
      deleteRegistryGroup,
      getAbsolutePath,
      getUniqueFileIdentifier,
      getFiles,
      addFile,
      existsFile,
      renameFile,
      updateFile,
      deleteFile,
      getFile,
    ]
  );

  return (
    <VirtualServiceRegistryContext.Provider value={value}>{props.children}</VirtualServiceRegistryContext.Provider>
  );
}
