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
import { StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { decoder, encoder, LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { join } from "path";
import { Buffer } from "buffer";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceDescriptorService } from "./services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "./services/WorkspaceFsService";
import { GistOrigin, GitHubOrigin } from "./model/WorkspaceOrigin";
import { WorkspaceSvgService } from "./services/WorkspaceSvgService";
import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { WorkspacesWorkerGitApi } from "../workspacesWorker/api/WorkspacesWorkerGitApi";

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_FILE_DEFAULT_NAME = "Untitled";

interface Props {
  children: React.ReactNode;
}

const workspacesWorker = new Worker("workspacesWorker/worker.js");

const workspacesWorkerBus = new EnvelopeBusMessageManager<{}, WorkspacesWorkerGitApi>((m) => {
  workspacesWorker.postMessage(m);
});

workspacesWorker.onmessage = (m) => {
  workspacesWorkerBus.server.receive(m.data, {});
};

export function WorkspacesContextProvider(props: Props) {
  const storageService = useMemo(() => new StorageService(), []);
  const descriptorService = useMemo(() => new WorkspaceDescriptorService(storageService), [storageService]);
  const svgService = useMemo(() => new WorkspaceSvgService(storageService), [storageService]);
  const fsService = useMemo(() => new WorkspaceFsService(descriptorService), [descriptorService]);

  const service = useMemo(
    () => new WorkspaceService(storageService, descriptorService, fsService),
    [storageService, descriptorService, fsService]
  );

  const getUniqueFileIdentifier = useCallback(
    (args: { workspaceId: string; relativePath: string }) => service.getUniqueFileIdentifier(args),
    [service]
  );

  const hasLocalChanges = useCallback(async (args: { workspaceId: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_hasLocalChanges(args);
  }, []);

  const pull = useCallback(
    async (args: {
      workspaceId: string;
      gitConfig?: { name: string; email: string };
      authInfo?: { username: string; password: string };
    }) => {
      return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_pull(args);
    },
    []
  );
  const push = useCallback(
    async (args: {
      workspaceId: string;
      ref: string;
      remoteRef?: string;
      remote: string;
      force: boolean;
      authInfo: {
        username: string;
        password: string;
      };
    }) => {
      return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_push(args);
    },
    []
  );

  const addRemote = useCallback(async (args: { workspaceId: string; name: string; url: string; force: boolean }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_addRemote(args);
  }, []);

  const branch = useCallback(async (args: { workspaceId: string; name: string; checkout: boolean }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_branch(args);
  }, []);

  const resolveRef = useCallback(async (args: { workspaceId: string; ref: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_resolveRef(args);
  }, []);

  const createSavePoint = useCallback(
    async (args: { workspaceId: string; gitConfig?: { email: string; name: string } }) => {
      return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_commit(args);
    },
    []
  );

  const createWorkspaceFromLocal = useCallback(
    async (args: {
      useInMemoryFs: boolean;
      localFiles: LocalFile[];
      preferredName?: string;
      gitConfig?: { email: string; name: string };
    }) => {
      const w = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_init(args);

      return {
        workspace: w.workspace,
        suggestedFirstFile: w.suggestedFirstFile
          ? await service.getFile({
              fs: await fsService.getWorkspaceFs(w.workspace.workspaceId),
              workspaceId: w.suggestedFirstFile.workspaceId,
              relativePath: w.suggestedFirstFile.relativePath,
            })
          : undefined,
      };
    },
    [fsService, service]
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
      const w = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_clone(args);
      return {
        workspace: w.workspace,
        suggestedFirstFile: w.suggestedFirstFile
          ? await service.getFile({
              fs: await fsService.getWorkspaceFs(w.workspace.workspaceId),
              workspaceId: w.suggestedFirstFile.workspaceId,
              relativePath: w.suggestedFirstFile.relativePath,
            })
          : undefined,
      };
    },
    [fsService, service]
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
      getUniqueFileIdentifier,
      createSavePoint,
      pull,
      addRemote,
      push,
      branch,
      resolveRef,
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
      getFile,
      getFiles,
      getUniqueFileIdentifier,
      hasLocalChanges,
      prepareZip,
      pull,
      addRemote,
      push,
      branch,
      resolveRef,
      renameFile,
      renameWorkspace,
      resourceContentGet,
      resourceContentList,
      svgService,
      updateFile,
    ]
  );

  return <WorkspacesContext.Provider value={value}>{props.children}</WorkspacesContext.Provider>;
}
