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

import { ResourceContentOptions } from "@kie-tools-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { LocalFile } from "./worker/api/LocalFile";
import { GistOrigin, GitHubOrigin } from "./worker/api/WorkspaceOrigin";
import { WorkspaceWorkerFileDescriptor } from "./worker/api/WorkspaceWorkerFileDescriptor";
import { SupportedFileExtensions } from "../extension";
import { WorkspacesSharedWorker } from "./worker/WorkspacesSharedWorker";

interface Props {
  children: React.ReactNode;
}

export function WorkspacesContextProvider(props: Props) {
  const hasLocalChanges = useCallback(
    async (args: { workspaceId: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_hasLocalChanges(args)
      ),
    []
  );

  const pull = useCallback(
    async (args: {
      workspaceId: string;
      gitConfig?: { name: string; email: string };
      authInfo?: { username: string; password: string };
    }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_pull(args)
      ),
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
    }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_push(args)
      ),
    []
  );

  const addRemote = useCallback(
    async (args: { workspaceId: string; name: string; url: string; force: boolean }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_addRemote(args)
      ),
    []
  );

  const deleteRemote = useCallback(
    async (args: { workspaceId: string; name: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_deleteRemote(args)
      ),
    []
  );

  const branch = useCallback(
    async (args: { workspaceId: string; name: string; checkout: boolean }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_branch(args)
      ),
    []
  );

  const checkout = useCallback(
    async (args: { workspaceId: string; ref: string; remote: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_checkout(args)
      ),
    []
  );

  const resolveRef = useCallback(
    async (args: { workspaceId: string; ref: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_resolveRef(args)
      ),
    []
  );

  const createSavePoint = useCallback(
    async (args: { workspaceId: string; gitConfig?: { email: string; name: string } }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_commit(args)
      ),
    []
  );

  const fetch = useCallback(
    async (args: { workspaceId: string; remote: string; ref: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_fetch(args)
      ),
    []
  );

  const getFile = useCallback(async (args: { workspaceId: string; relativePath: string }) => {
    const wwfd = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
      workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFile(args)
    );
    return wwfd ? toWorkspaceFile(wwfd) : undefined;
  }, []);

  const createWorkspaceFromLocal = useCallback(
    async (args: { localFiles: LocalFile[]; preferredName?: string; gitConfig?: { email: string; name: string } }) => {
      const workspaceInit = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_init(args)
      );

      return {
        workspace: workspaceInit.workspace,
        suggestedFirstFile: workspaceInit.suggestedFirstFile
          ? await getFile({
              workspaceId: workspaceInit.suggestedFirstFile.workspaceId,
              relativePath: workspaceInit.suggestedFirstFile.relativePath,
            })
          : undefined,
      };
    },
    [getFile]
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
      const workspaceClone = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_clone(args)
      );
      return {
        workspace: workspaceClone.workspace,
        suggestedFirstFile: workspaceClone.suggestedFirstFile
          ? await getFile({
              workspaceId: workspaceClone.suggestedFirstFile.workspaceId,
              relativePath: workspaceClone.suggestedFirstFile.relativePath,
            })
          : undefined,
      };
    },
    [getFile]
  );

  const isFileModified = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_isModified(args)
      ),
    []
  );

  const getUniqueFileIdentifier = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args)
      ),
    []
  );

  const renameFile = useCallback(async (args: { file: WorkspaceFile; newFileNameWithoutExtension: string }) => {
    const wwfd = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
      workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameFile({
        wwfd: {
          workspaceId: args.file.workspaceId,
          relativePath: args.file.relativePath,
        },
        newFileNameWithoutExtension: args.newFileNameWithoutExtension,
      })
    );
    return toWorkspaceFile(wwfd);
  }, []);

  const getFiles = useCallback(async (args: { workspaceId: string; globPattern?: string }) => {
    const wwfds = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
      workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFiles(args)
    );
    return wwfds.map((wwfd) => toWorkspaceFile(wwfd));
  }, []);

  const getFileContent = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(args)
      ),
    []
  );

  const deleteFile = useCallback(async (args: { file: WorkspaceFile }) => {
    return WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
      workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteFile({
        wwfd: {
          workspaceId: args.file.workspaceId,
          relativePath: args.file.relativePath,
        },
      })
    );
  }, []);

  const moveFile = useCallback(async (args: { file: WorkspaceFile; newDirPath: string }) => {
    const wwfd = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
      workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_moveFile({
        wwfd: {
          workspaceId: args.file.workspaceId,
          relativePath: args.file.relativePath,
        },
        newDirPath: args.newDirPath,
      })
    );
    return toWorkspaceFile(wwfd);
  }, []);

  const updateFile = useCallback(
    async (args: { workspaceId: string; relativePath: string; newContent: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_updateFile({
          wwfd: {
            workspaceId: args.workspaceId,
            relativePath: args.relativePath,
          },
          newContent: args.newContent,
        })
      ),
    []
  );

  const addFile = useCallback(
    async (args: {
      workspaceId: string;
      name: string;
      destinationDirRelativePath: string;
      content: string;
      extension: SupportedFileExtensions;
    }) => {
      const wwfd = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addFile(args)
      );
      return toWorkspaceFile(wwfd);
    },
    []
  );

  const existsFile = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_existsFile(args)
      ),
    []
  );

  const addEmptyFile = useCallback(
    async (args: { workspaceId: string; destinationDirRelativePath: string; extension: SupportedFileExtensions }) => {
      const wwfd = await WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addEmptyFile(args)
      );
      return toWorkspaceFile(wwfd);
    },
    []
  );

  const prepareZip = useCallback(
    async (args: { workspaceId: string; onlyExtensions?: string[] }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_prepareZip(args)
      ),
    []
  );

  const resourceContentGet = useCallback(
    async (args: { workspaceId: string; relativePath: string; opts?: ResourceContentOptions }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentGet(args)
      ),
    []
  );

  const resourceContentList = useCallback(
    async (args: { workspaceId: string; globPattern: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentList(args)
      ),
    []
  );

  const deleteWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteWorkspace(args)
      ),
    []
  );

  const renameWorkspace = useCallback(
    async (args: { workspaceId: string; newName: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameWorkspace(args)
      ),
    []
  );

  const listAllWorkspaces = useCallback(
    async () =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_listAllWorkspaces()
      ),
    []
  );

  const getWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getWorkspace(args)
      ),
    []
  );

  const initGitOnWorkspace = useCallback(
    async (args: { workspaceId: string; remoteUrl: URL }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGitOnExistingWorkspace({
          workspaceId: args.workspaceId,
          remoteUrl: args.remoteUrl.toString(),
        })
      ),
    []
  );

  const initGistOnWorkspace = useCallback(
    async (args: { workspaceId: string; remoteUrl: URL }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGistOnExistingWorkspace({
          workspaceId: args.workspaceId,
          remoteUrl: args.remoteUrl.toString(),
        })
      ),
    []
  );

  const initLocalOnWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initLocalOnExistingWorkspace({
          workspaceId: args.workspaceId,
        })
      ),
    []
  );

  const value = useMemo(
    () => ({
      resourceContentGet,
      resourceContentList,
      createWorkspaceFromLocal,
      createWorkspaceFromGitRepository,
      renameWorkspace,
      deleteWorkspace,
      prepareZip,
      getUniqueFileIdentifier,
      createSavePoint,
      pull,
      addRemote,
      deleteRemote,
      push,
      branch,
      checkout,
      fetch,
      resolveRef,
      getFiles,
      hasLocalChanges,
      moveFile,
      addEmptyFile,
      addFile,
      existsFile,
      renameFile,
      updateFile,
      deleteFile,
      getFile,
      getFileContent,
      listAllWorkspaces,
      getWorkspace,
      initGitOnWorkspace,
      initGistOnWorkspace,
      initLocalOnWorkspace,
      isFileModified,
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
      getFileContent,
      getFile,
      getFiles,
      getUniqueFileIdentifier,
      hasLocalChanges,
      moveFile,
      prepareZip,
      pull,
      addRemote,
      deleteRemote,
      push,
      branch,
      checkout,
      fetch,
      resolveRef,
      renameFile,
      renameWorkspace,
      resourceContentGet,
      resourceContentList,
      updateFile,
      listAllWorkspaces,
      getWorkspace,
      initGitOnWorkspace,
      initGistOnWorkspace,
      initLocalOnWorkspace,
      isFileModified,
    ]
  );

  return <WorkspacesContext.Provider value={value}>{props.children}</WorkspacesContext.Provider>;
}

function toWorkspaceFile(wwfd: WorkspaceWorkerFileDescriptor) {
  return new WorkspaceFile({
    workspaceId: wwfd.workspaceId,
    relativePath: wwfd.relativePath,
    getFileContents: () =>
      WorkspacesSharedWorker.getInstance().withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(wwfd)
      ),
  });
}
