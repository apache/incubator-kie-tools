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
import { GitService } from "./services/GitService";
import { StorageFile, StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { SUPPORTED_FILES, SUPPORTED_FILES_EDITABLE } from "./SupportedFiles";
import { decoder, encoder, LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions } from "../common/GlobalContext";
import { extractFileExtension } from "../common/utils";
import { emptyTemplates } from "./FileTemplates";
import { join } from "path";
import git from "isomorphic-git";
import { WorkspaceEvents } from "./hooks/WorkspaceHooks";
import { Buffer } from "buffer";
import LightningFS from "@isomorphic-git/lightning-fs";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";

const GIT_CORS_PROXY = "https://cors.isomorphic-git.org"; // TODO CAPONETTO: Deploy our own proxy (https://github.com/isomorphic-git/cors-proxy)

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_FILE_DEFAULT_NAME = "Untitled";

interface Props {
  children: React.ReactNode;
}

export function WorkspacesContextProvider(props: Props) {
  const storageService = useMemo(() => new StorageService(), []);
  const fsService = useMemo(() => new WorkspaceFsService(), []);
  const descriptorService = useMemo(() => new WorkspaceDescriptorService(storageService), [storageService]);
  const service = useMemo(
    () => new WorkspaceService(storageService, descriptorService, fsService),
    [storageService, descriptorService, fsService]
  );

  const gitService = useMemo(() => {
    const instance = new GitService(GIT_CORS_PROXY);
    // FIXME: easy access to git in the window object.
    (window as any).git = async (workspaceId: string, prop: unknown, args: any) => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      return git[prop]({ fs: await service.getWorkspaceFs(workspaceId), ...args });
    };
    return instance;
  }, [service]);

  const getAbsolutePath = useCallback(
    (args: { workspaceId: string; relativePath: string }) => service.getAbsolutePath(args),
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
    }) => {
      const { workspace, files } = await service.create({
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: args.storeFiles,
        broadcastArgs: { broadcast: true },
      });

      if (files.length <= 0) {
        return { workspace, suggestedFirstFile: undefined };
      }

      const suggestedFirstFile = files
        .filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension))
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
    async (args: { fs: LightningFS; workspaceId: string }) => {
      const workspaceRootDirPath = service.getAbsolutePath({ workspaceId: args.workspaceId });

      const fileRelativePaths = await gitService.unstagedModifiedFileRelativePaths({
        fs: args.fs,
        dir: workspaceRootDirPath,
      });

      await Promise.all(
        fileRelativePaths.map(async (relativePath) => {
          await gitService.add({
            fs: args.fs,
            dir: workspaceRootDirPath,
            relativePath,
          });
        })
      );

      await gitService.commit({
        fs: args.fs,
        dir: workspaceRootDirPath,
        targetBranch: "main", //FIXME: Use current branch whatever it is?
        message: "Save point",
        authInfo: {
          name: "Tiago",
          email: "tfernand+dev@redhat.com", //FIXME: Change this.
        },
      });
      const broadcastChannel = new BroadcastChannel(args.workspaceId);
      const workspaceEvent: WorkspaceEvents = { type: "CREATE_SAVE_POINT", workspaceId: args.workspaceId };
      broadcastChannel.postMessage(workspaceEvent);
    },
    [gitService, service]
  );

  const createWorkspaceFromLocal = useCallback(
    async (args: { useInMemoryFs: boolean; localFiles: LocalFile[] }) => {
      return await createWorkspace({
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: async (fs: LightningFS, workspace: WorkspaceDescriptor) => {
          await storageService.createFiles(
            fs,
            args.localFiles
              .filter((f) => SUPPORTED_FILES.includes(extractFileExtension(f.path)!))
              .map((localFile) => {
                const path = service.getAbsolutePath({
                  workspaceId: workspace.workspaceId,
                  relativePath: localFile.path.substring(localFile.path.indexOf("/") + 1), //FIXME: This doesn't look so good.
                });

                return new StorageFile({ path, getFileContents: localFile.getFileContents });
              })
          );

          const workspaceRootDirPath = await service.getAbsolutePath({ workspaceId: workspace.workspaceId });
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
            targetBranch: "main",
            authInfo: {
              name: "Tiago",
              email: "tfernand+dev@redhat.com", //FIXME: Change this.
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
      repositoryUrl: URL;
      sourceBranch: string;
      githubSettings: { user: { login: string; email: string; name: string }; token: string };
    }) => {
      if (!args.githubSettings.user) {
        throw new Error("User not authenticated on GitHub");
      }

      const authInfo = {
        name: args.githubSettings.user.name,
        email: args.githubSettings.user.email,
        onAuth: () => ({
          username: args.githubSettings.user.login,
          password: args.githubSettings.token,
        }),
      };

      return await createWorkspace({
        useInMemoryFs: true,
        storeFiles: async (fs, workspace) => {
          await gitService.clone({
            fs,
            dir: service.getAbsolutePath({ workspaceId: workspace.workspaceId }),
            repositoryUrl: args.repositoryUrl,
            authInfo,
            sourceBranch: args.sourceBranch,
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

  const addEmptyFile = useCallback(
    async (args: {
      fs: LightningFS;
      workspaceId: string;
      destinationDirRelativePath: string;
      extension: SupportedFileExtensions;
    }) => {
      for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        const index = i === 0 ? "" : `-${i}`;
        const fileName = `${NEW_FILE_DEFAULT_NAME}${index}.${args.extension}`;
        const relativePath = join(args.destinationDirRelativePath, fileName);
        if (
          await service.existsFile({
            fs: args.fs,
            workspaceId: args.workspaceId,
            relativePath,
          })
        ) {
          continue;
        }

        const contents = args.extension in emptyTemplates ? emptyTemplates[args.extension] : emptyTemplates.default;
        const newEmptyFile = new WorkspaceFile({
          workspaceId: args.workspaceId,
          getFileContents: () => Promise.resolve(encoder.encode(contents)),
          relativePath,
        });
        await service.createFile(args.fs, newEmptyFile, { broadcast: true });
        return newEmptyFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    },
    [service]
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

      console.info("Reading " + args.relativePath);

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
        fsService,
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
