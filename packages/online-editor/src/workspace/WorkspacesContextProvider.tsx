/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
} from "@kie-tooling-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { CloneArgs, GIT_DEFAULT_BRANCH, GitService } from "./services/GitService";
import { StorageFile, StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { decoder, encoder, LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions, useGlobals } from "../common/GlobalContext";
import { join } from "path";
import { WorkspaceEvents } from "./hooks/WorkspaceHooks";
import { Buffer } from "buffer";
import LightningFS from "@isomorphic-git/lightning-fs";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "./model/WorkspaceOrigin";
import { WorkspaceSvgService } from "./services/WorkspaceSvgService";

const GIT_CORS_PROXY = "https://cors.isomorphic-git.org"; // TODO CAPONETTO: Deploy our own proxy (https://github.com/isomorphic-git/cors-proxy)

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_FILE_DEFAULT_NAME = "Untitled";

interface Props {
  children: React.ReactNode;
}

export function WorkspacesContextProvider(props: Props) {
  const globals = useGlobals();
  const storageService = useMemo(() => new StorageService(), []);
  const descriptorService = useMemo(() => new WorkspaceDescriptorService(storageService), [storageService]);
  const fsService = useMemo(() => new WorkspaceFsService(descriptorService), [descriptorService]);
  const service = useMemo(
    () => new WorkspaceService(storageService, descriptorService, fsService),
    [storageService, descriptorService, fsService]
  );
  const svgService = useMemo(() => new WorkspaceSvgService(service), [service]);

  const gitService = useMemo(() => new GitService(GIT_CORS_PROXY), [service]);

  const getAbsolutePath = useCallback(
    (args: { workspaceId: string; relativePath?: string }) => service.getAbsolutePath(args),
    [service]
  );

  const getUniqueFileIdentifier = useCallback(
    (args: { workspaceId: string; relativePath: string }) => service.getUniqueFileIdentifier(args),
    [service]
  );

  const createWorkspace = useCallback(
    async (args: {
      useInMemoryFs: boolean;
      storeFiles: (fs: LightningFS, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
      origin: WorkspaceOrigin;
      preferredName?: string;
    }) => {
      const { workspace, files } = await service.create({
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: args.storeFiles,
        broadcastArgs: { broadcast: true },
        origin: args.origin,
        preferredName: args.preferredName,
      });

      if (files.length <= 0) {
        return { workspace, suggestedFirstFile: undefined };
      }

      const suggestedFirstFile = files
        .filter((file) => [...globals.editorEnvelopeLocator.mapping.keys()].includes(file.extension))
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))[0];

      return { workspace, suggestedFirstFile };
    },
    [service]
  );

  const isModified = useCallback(
    async (args: { fs: LightningFS; workspaceId: string }) => {
      return await gitService.isModified({
        fs: args.fs,
        dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      });
    },
    [gitService, service]
  );

  const createSavePoint = useCallback(
    async (args: { fs: LightningFS; workspaceId: string; gitSettings?: { email: string; name: string } }) => {
      const descriptor = await descriptorService.get(args.workspaceId);

      const workspaceRootDirPath = service.getAbsolutePath({ workspaceId: args.workspaceId });

      const fileRelativePaths = await gitService.unstagedModifiedFileRelativePaths({
        fs: args.fs,
        dir: workspaceRootDirPath,
      });

      await Promise.all(
        fileRelativePaths.map(async (relativePath) => {
          if (await service.existsFile({ fs: args.fs, workspaceId: args.workspaceId, relativePath })) {
            await gitService.add({
              fs: args.fs,
              dir: workspaceRootDirPath,
              relativePath,
            });
          } else {
            await gitService.rm({
              fs: args.fs,
              dir: workspaceRootDirPath,
              relativePath,
            });
          }
        })
      );

      await gitService.commit({
        fs: args.fs,
        dir: workspaceRootDirPath,
        targetBranch: descriptor.origin.branch,
        message: "Save point",
        authInfo: {
          name: args.gitSettings?.name ?? "Unknown",
          email: args.gitSettings?.email ?? "unknown@email.com",
        },
      });
      const broadcastChannel = new BroadcastChannel(args.workspaceId);
      const workspaceEvent: WorkspaceEvents = { type: "CREATE_SAVE_POINT", workspaceId: args.workspaceId };
      broadcastChannel.postMessage(workspaceEvent);
    },
    [descriptorService, gitService, service]
  );

  const createWorkspaceFromLocal = useCallback(
    async (args: {
      useInMemoryFs: boolean;
      localFiles: LocalFile[];
      preferredName?: string;
      gitSettings?: { email: string; name: string };
    }) => {
      return await createWorkspace({
        preferredName: args.preferredName,
        origin: { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: async (fs: LightningFS, workspace: WorkspaceDescriptor) => {
          const files = args.localFiles
            .filter((file) => !file.path.startsWith(".git/"))
            .map(
              (localFile) =>
                new StorageFile({
                  path: service.getAbsolutePath({ workspaceId: workspace.workspaceId, relativePath: localFile.path }),
                  getFileContents: localFile.getFileContents,
                })
            );

          await storageService.createFiles(fs, files);

          const workspaceRootDirPath = await service.getAbsolutePath({ workspaceId: workspace.workspaceId });

          const ignoredPaths = await storageService.walk({
            fs,
            shouldExcludeDir: () => false,
            startFromDirPath: workspaceRootDirPath,
            onVisit: async ({ absolutePath, relativePath }) => {
              const isIgnored = await gitService.isIgnored({ fs, dir: workspaceRootDirPath, filepath: relativePath });
              return isIgnored ? absolutePath : undefined;
            },
          });

          await storageService.deleteFiles(fs, ignoredPaths);

          await gitService.init({
            fs: fs,
            dir: workspaceRootDirPath,
          });

          await gitService.add({
            fs: fs,
            dir: workspaceRootDirPath,
            relativePath: ".",
          });

          await gitService.commit({
            fs: fs,
            dir: workspaceRootDirPath,
            message: "Initial",
            targetBranch: GIT_DEFAULT_BRANCH,
            authInfo: {
              name: args.gitSettings?.name ?? "Unknown",
              email: args.gitSettings?.email ?? "unknown@email.com",
            },
          });

          return service.getFilesWithLazyContent(fs, workspace.workspaceId);
        },
      });
    },
    [createWorkspace, gitService, storageService, service]
  );

  const createWorkspaceFromGitRepository = useCallback(
    async (args: {
      origin: GistOrigin | GitHubOrigin;
      gitSettings?: { user: { login: string; email: string; name: string }; token: string };
    }) => {
      let authInfo: CloneArgs["authInfo"];
      if (args.gitSettings) {
        const username = args.gitSettings.user.login;
        const password = args.gitSettings.token;
        authInfo = {
          name: args.gitSettings.user.name,
          email: args.gitSettings.user.email,
          onAuth: () => ({ username, password }),
        };
      }

      return await createWorkspace({
        origin: args.origin,
        useInMemoryFs: true,
        storeFiles: async (fs, workspace) => {
          await gitService.clone({
            fs,
            dir: service.getAbsolutePath({ workspaceId: workspace.workspaceId }),
            repositoryUrl: args.origin.url,
            authInfo,
            sourceBranch: args.origin.branch,
          });
          return service.getFilesWithLazyContent(fs, workspace.workspaceId);
        },
      });
    },
    [createWorkspace, gitService, service]
  );

  const renameFile = useCallback(
    async (args: { fs: LightningFS; file: WorkspaceFile; newFileName: string }) => {
      return service.renameFile(args.fs, args.file, args.newFileName, { broadcast: true });
    },
    [service]
  );

  const getFiles = useCallback(
    async (args: { fs: LightningFS; workspaceId: string }) => {
      return service.getFilesWithLazyContent(args.fs, args.workspaceId);
    },
    [service]
  );

  const getFile = useCallback(
    async (args: { fs: LightningFS; workspaceId: string; relativePath: string }) => {
      return service.getFile(args);
    },
    [service]
  );

  const deleteFile = useCallback(
    async (args: { fs: LightningFS; file: WorkspaceFile }) => {
      await service.deleteFile(args.fs, args.file, { broadcast: true });
    },
    [service]
  );

  const updateFile = useCallback(
    async (args: { fs: LightningFS; file: WorkspaceFile; getNewContents: () => Promise<string> }) => {
      await service.updateFile(args.fs, args.file, args.getNewContents, { broadcast: true });
    },
    [service]
  );

  const addFile = useCallback(
    async (args: {
      fs: LightningFS;
      workspaceId: string;
      name: string;
      destinationDirRelativePath: string;
      content: string;
      extension: SupportedFileExtensions;
    }) => {
      for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        const index = i === 0 ? "" : `-${i}`;
        const fileName = `${args.name}${index}.${args.extension}`;
        const relativePath = join(args.destinationDirRelativePath, fileName);

        if (await service.existsFile({ fs: args.fs, workspaceId: args.workspaceId, relativePath })) {
          continue;
        }

        const newFile = new WorkspaceFile({
          workspaceId: args.workspaceId,
          getFileContents: () => Promise.resolve(encoder.encode(args.content)),
          relativePath,
        });
        await service.createOrOverwriteFile(args.fs, newFile, { broadcast: true });
        return newFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    },
    [service]
  );

  const addEmptyFile = useCallback(
    async (args: {
      fs: LightningFS;
      workspaceId: string;
      destinationDirRelativePath: string;
      extension: SupportedFileExtensions;
    }) => addFile({ ...args, name: NEW_FILE_DEFAULT_NAME, content: "" }),
    [addFile]
  );

  const prepareZip = useCallback(
    (args: { fs: LightningFS; workspaceId: string }) => service.prepareZip(args.fs, args.workspaceId),
    [service]
  );

  // const syncWorkspace = useCallback(async () => {
  //   if (!active) {
  //     throw new Error("No active workspace");
  //   }
  //
  //   if (active.descriptor.origin.kind === WorkspaceKind.GITHUB_REPOSITORY) {
  //     const origin = active.descriptor.origin as GitHubRepositoryOrigin;
  //     const fileHandler = new GitRepositoryFileHandler({
  //       authInfo: authInfo,
  //       repositoryUrl: origin.url,
  //       sourceBranch: origin.branch,
  //       gitService: gitService,
  //       workspaceService: workspaceService,
  //       storageService: storageService,
  //     });
  //     await fileHandler.sync(active.descriptor);
  //   }
  // }, [active, authInfo, gitService, storageService, workspaceService]);

  const resourceContentGet = useCallback(
    async (args: { fs: LightningFS; workspaceId: string; relativePath: string; opts?: ResourceContentOptions }) => {
      const file = await service.getFile(args);
      if (!file) {
        throw new Error(`File '${args.relativePath}' not found in Workspace ${args.workspaceId}`);
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
    [service]
  );

  const resourceContentList = useCallback(
    async (args: { fs: LightningFS; workspaceId: string; globPattern: string }) => {
      const files = await service.getFilesWithLazyContent(args.fs, args.workspaceId, args.globPattern);
      const matchingPaths = files.map((file) => file.relativePath);
      return new ResourcesList(args.globPattern, matchingPaths);
    },
    [service]
  );

  const deleteWorkspace = useCallback(
    async (args: { workspaceId: string }) => {
      await service.delete(args.workspaceId, { broadcast: true });
    },
    [service]
  );

  const renameWorkspace = useCallback(
    async (args: { workspaceId: string; newName: string }) => {
      await service.rename(args.workspaceId, args.newName, { broadcast: true });
    },
    [service]
  );

  return (
    <WorkspacesContext.Provider
      value={{
        service,
        gitService,
        fsService,
        svgService,
        descriptorService,
        //
        resourceContentGet,
        resourceContentList,
        //
        createWorkspaceFromLocal,
        createWorkspaceFromGitRepository,
        renameWorkspace,
        deleteWorkspace,
        prepareZip,
        getAbsolutePath,
        getUniqueFileIdentifier,
        createSavePoint,
        getFiles,
        isModified,
        //
        addEmptyFile,
        addFile,
        renameFile,
        updateFile,
        deleteFile,
        getFile,
      }}
    >
      {props.children}
    </WorkspacesContext.Provider>
  );
}
