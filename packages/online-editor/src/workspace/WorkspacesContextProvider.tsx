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
} from "@kie-tools-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { GIT_DEFAULT_BRANCH, GitService } from "./services/GitService";
import { StorageFile, StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { decoder, encoder, LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions, useEditorEnvelopeLocator } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { join } from "path";
import { WorkspaceEvents } from "./hooks/WorkspaceHooks";
import { Buffer } from "buffer";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "./model/WorkspaceOrigin";
import { WorkspaceSvgService } from "./services/WorkspaceSvgService";
import { DEFAULT_CORS_PROXY_URL, useEnv } from "../env/EnvContext";

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_FILE_DEFAULT_NAME = "Untitled";

interface Props {
  children: React.ReactNode;
}

export function WorkspacesContextProvider(props: Props) {
  const env = useEnv();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const storageService = useMemo(() => new StorageService(), []);
  const descriptorService = useMemo(() => new WorkspaceDescriptorService(storageService), [storageService]);
  const svgService = useMemo(() => new WorkspaceSvgService(storageService), [storageService]);
  const fsService = useMemo(() => new WorkspaceFsService(descriptorService), [descriptorService]);
  const service = useMemo(
    () => new WorkspaceService(storageService, descriptorService, fsService),
    [storageService, descriptorService, fsService]
  );

  const gitService = useMemo(() => {
    let envUrl = DEFAULT_CORS_PROXY_URL;
    if (envUrl !== env.vars.CORS_PROXY_URL) {
      try {
        new URL(env.vars.CORS_PROXY_URL);
        envUrl = env.vars.CORS_PROXY_URL;
      } catch (e) {
        console.error(`Invalid CORS_PROXY_URL: ${env.vars.CORS_PROXY_URL}`, e);
      }
    }
    return new GitService(envUrl);
  }, [env.vars.CORS_PROXY_URL]);

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
      storeFiles: (fs: KieSandboxFs, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
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
        .filter((file) => [...editorEnvelopeLocator.mapping.keys()].includes(file.extension))
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))[0];

      return { workspace, suggestedFirstFile };
    },
    [editorEnvelopeLocator, service]
  );

  const hasLocalChanges = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string }) => {
      return await gitService.hasLocalChanges({
        fs: args.fs,
        dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      });
    },
    [gitService, service]
  );

  const pull = useCallback(
    async (args: {
      fs: KieSandboxFs;
      workspaceId: string;
      gitConfig?: { name: string; email: string };
      authInfo?: { username: string; password: string };
    }) => {
      const workspace = await descriptorService.get(args.workspaceId);
      await gitService.pull({
        fs: args.fs,
        dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
        ref: workspace.origin.branch,
        author: {
          name: args.gitConfig?.name ?? "Unknown",
          email: args.gitConfig?.email ?? "unknown@email.com",
        },
        authInfo: args.authInfo,
      });

      const broadcastChannel2 = new BroadcastChannel(args.workspaceId);
      const workspaceEvent: WorkspaceEvents = { type: "PULL", workspaceId: args.workspaceId };
      broadcastChannel2.postMessage(workspaceEvent);
    },
    [descriptorService, gitService, service]
  );

  const createSavePoint = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string; gitConfig?: { email: string; name: string } }) => {
      const descriptor = await descriptorService.get(args.workspaceId);

      const workspaceRootDirPath = service.getAbsolutePath({ workspaceId: args.workspaceId });

      const fileRelativePaths = await gitService.unstagedModifiedFileRelativePaths({
        fs: args.fs,
        dir: workspaceRootDirPath,
      });

      if (fileRelativePaths.length === 0) {
        console.debug("Nothing to commit.");
        return;
      }

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
        message: "Changes from KIE Sandbox",
        author: {
          name: args.gitConfig?.name ?? "Unknown",
          email: args.gitConfig?.email ?? "unknown@email.com",
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
      gitConfig?: { email: string; name: string };
    }) => {
      return await createWorkspace({
        preferredName: args.preferredName,
        origin: { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
        useInMemoryFs: args.useInMemoryFs,
        storeFiles: async (fs: KieSandboxFs, workspace: WorkspaceDescriptor) => {
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
            message: "Initial commit from KIE Sandbox",
            targetBranch: GIT_DEFAULT_BRANCH,
            author: {
              name: args.gitConfig?.name ?? "Unknown",
              email: args.gitConfig?.email ?? "unknown@email.com",
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
      gitConfig?: { email: string; name: string };
      authInfo?: {
        username: string;
        password: string;
      };
    }) => {
      return await createWorkspace({
        preferredName: args.origin.url.pathname.substring(1), // Remove slash
        origin: args.origin,
        useInMemoryFs: true,
        storeFiles: async (fs, workspace) => {
          await gitService.clone({
            fs,
            dir: service.getAbsolutePath({ workspaceId: workspace.workspaceId }),
            repositoryUrl: args.origin.url,
            gitConfig: args.gitConfig,
            authInfo: args.authInfo,
            sourceBranch: args.origin.branch,
          });
          return service.getFilesWithLazyContent(fs, workspace.workspaceId);
        },
      });
    },
    [createWorkspace, gitService, service]
  );

  const renameFile = useCallback(
    async (args: { fs: KieSandboxFs; file: WorkspaceFile; newFileNameWithoutExtension: string }) => {
      const newFile = service.renameFile({
        fs: args.fs,
        file: args.file,
        newFileNameWithoutExtension: args.newFileNameWithoutExtension,
        broadcastArgs: { broadcast: true },
      });
      await svgService.renameSvg(args.file, args.newFileNameWithoutExtension);
      return newFile;
    },
    [service, svgService]
  );

  const getFiles = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string }) => {
      return service.getFilesWithLazyContent(args.fs, args.workspaceId);
    },
    [service]
  );

  const getFile = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }) => {
      return service.getFile(args);
    },
    [service]
  );

  const deleteFile = useCallback(
    async (args: { fs: KieSandboxFs; file: WorkspaceFile }) => {
      await service.deleteFile(args.fs, args.file, { broadcast: true });
      await svgService.deleteSvg(args.file);
    },
    [service, svgService]
  );

  const updateFile = useCallback(
    async (args: { fs: KieSandboxFs; file: WorkspaceFile; getNewContents: () => Promise<string> }) => {
      await service.updateFile(args.fs, args.file, args.getNewContents, { broadcast: true });
    },
    [service]
  );

  const addFile = useCallback(
    async (args: {
      fs: KieSandboxFs;
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

  const existsFile = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }) => await service.existsFile(args),
    [service]
  );

  const addEmptyFile = useCallback(
    async (args: {
      fs: KieSandboxFs;
      workspaceId: string;
      destinationDirRelativePath: string;
      extension: SupportedFileExtensions;
    }) => addFile({ ...args, name: NEW_FILE_DEFAULT_NAME, content: "" }),
    [addFile]
  );

  const prepareZip = useCallback(
    (args: { fs: KieSandboxFs; workspaceId: string; onlyExtensions?: string[] }) =>
      service.prepareZip(args.fs, args.workspaceId, args.onlyExtensions),
    [service]
  );

  const resourceContentGet = useCallback(
    async (args: { fs: KieSandboxFs; workspaceId: string; relativePath: string; opts?: ResourceContentOptions }) => {
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
    async (args: { fs: KieSandboxFs; workspaceId: string; globPattern: string }) => {
      const files = await service.getFilesWithLazyContent(args.fs, args.workspaceId, args.globPattern);
      const matchingPaths = files.map((file) => file.relativePath);
      return new ResourcesList(args.globPattern, matchingPaths);
    },
    [service]
  );

  const deleteWorkspace = useCallback(
    async (args: { workspaceId: string }) => {
      await service.delete(args.workspaceId, { broadcast: true });
      await svgService.delete(args.workspaceId);
    },
    [service, svgService]
  );

  const renameWorkspace = useCallback(
    async (args: { workspaceId: string; newName: string }) => {
      await service.rename(args.workspaceId, args.newName, { broadcast: true });
    },
    [service]
  );

  const value = useMemo(
    () => ({
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
      pull,
      getFiles,
      hasLocalChanges,
      //
      addEmptyFile,
      addFile,
      existsFile,
      renameFile,
      updateFile,
      deleteFile,
      getFile,
    }),
    [
      addEmptyFile,
      addFile,
      existsFile,
      createSavePoint,
      createWorkspaceFromGitRepository,
      createWorkspaceFromLocal,
      deleteFile,
      deleteWorkspace,
      descriptorService,
      fsService,
      getAbsolutePath,
      getFile,
      getFiles,
      getUniqueFileIdentifier,
      gitService,
      hasLocalChanges,
      prepareZip,
      pull,
      renameFile,
      renameWorkspace,
      resourceContentGet,
      resourceContentList,
      service,
      svgService,
      updateFile,
    ]
  );

  return <WorkspacesContext.Provider value={value}>{props.children}</WorkspacesContext.Provider>;
}
