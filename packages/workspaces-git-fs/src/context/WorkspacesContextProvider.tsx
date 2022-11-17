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
import { LocalFile } from "../worker/api/LocalFile";
import { GistOrigin, GitHubOrigin } from "../worker/api/WorkspaceOrigin";
import { WorkspaceWorkerFileDescriptor } from "../worker/api/WorkspaceWorkerFileDescriptor";
import { WorkspacesSharedWorker } from "../worker/WorkspacesSharedWorker";

interface Props {
  children: React.ReactNode;
  workspacesSharedWorkerScriptUrl: string;
}

export function WorkspacesContextProvider(props: Props) {
  const workspacesSharedWorker = useMemo(
    () =>
      new WorkspacesSharedWorker({
        workerName: "workspaces-shared-worker",
        workerScriptUrl: props.workspacesSharedWorkerScriptUrl,
      }),
    [props.workspacesSharedWorkerScriptUrl]
  );

  const toWorkspaceFile = useCallback(
    (wwfd: WorkspaceWorkerFileDescriptor) =>
      new WorkspaceFile({
        workspaceId: wwfd.workspaceId,
        relativePath: wwfd.relativePath,
        getFileContents: () =>
          workspacesSharedWorker.withBus((workspacesWorkerBus) =>
            workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(wwfd)
          ),
      }),
    [workspacesSharedWorker]
  );

  const hasLocalChanges = useCallback(
    async (args: { workspaceId: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_hasLocalChanges(args)
      ),
    [workspacesSharedWorker]
  );

  const pull = useCallback(
    async (args: {
      workspaceId: string;
      gitConfig?: { name: string; email: string };
      authInfo?: { username: string; password: string };
    }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_pull(args)
      ),
    [workspacesSharedWorker]
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
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_push(args)
      ),
    [workspacesSharedWorker]
  );

  const addRemote = useCallback(
    async (args: { workspaceId: string; name: string; url: string; force: boolean }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_addRemote(args)
      ),
    [workspacesSharedWorker]
  );

  const deleteRemote = useCallback(
    async (args: { workspaceId: string; name: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_deleteRemote(args)
      ),
    [workspacesSharedWorker]
  );

  const branch = useCallback(
    async (args: { workspaceId: string; name: string; checkout: boolean }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_branch(args)
      ),
    [workspacesSharedWorker]
  );

  const fetch = useCallback(
    async (args: { workspaceId: string; remote: string; ref: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_fetch(args)
      ),
    [workspacesSharedWorker]
  );

  const checkout = useCallback(
    async (args: { workspaceId: string; ref: string; remote: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_checkout(args)
      ),
    [workspacesSharedWorker]
  );

  const resolveRef = useCallback(
    async (args: { workspaceId: string; ref: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_resolveRef(args)
      ),
    [workspacesSharedWorker]
  );

  const createSavePoint = useCallback(
    async (args: { workspaceId: string; gitConfig?: { email: string; name: string } }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_commit(args)
      ),
    [workspacesSharedWorker]
  );

  const getGitServerRefs = useCallback(
    async (args: {
      url: string;
      authInfo?: {
        username: string;
        password: string;
      };
    }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_getGitServerRefs(args)
      ),
    [workspacesSharedWorker]
  );

  const getFile = useCallback(
    async (args: { workspaceId: string; relativePath: string }) => {
      const wwfd = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFile(args)
      );
      return wwfd ? toWorkspaceFile(wwfd) : undefined;
    },
    [workspacesSharedWorker, toWorkspaceFile]
  );

  const createWorkspaceFromLocal = useCallback(
    async (args: {
      localFiles: LocalFile[];
      preferredName?: string;
      gitAuthSessionId: string | undefined;
      gitConfig?: { email: string; name: string };
    }) => {
      const workspaceInit = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
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
    [getFile, workspacesSharedWorker]
  );

  const createWorkspaceFromGitRepository = useCallback(
    async (args: {
      origin: GistOrigin | GitHubOrigin;
      gitConfig?: { email: string; name: string };
      gitAuthSessionId: string | undefined;
      authInfo?: {
        username: string;
        password: string;
      };
    }) => {
      const workspaceClone = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
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
    [getFile, workspacesSharedWorker]
  );

  const isFileModified = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_isModified(args)
      ),
    [workspacesSharedWorker]
  );

  const getUniqueFileIdentifier = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args)
      ),
    [workspacesSharedWorker]
  );

  const renameFile = useCallback(
    async (args: { file: WorkspaceFile; newFileNameWithoutExtension: string }) => {
      const wwfd = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameFile({
          wwfd: {
            workspaceId: args.file.workspaceId,
            relativePath: args.file.relativePath,
          },
          newFileNameWithoutExtension: args.newFileNameWithoutExtension,
        })
      );
      return toWorkspaceFile(wwfd);
    },
    [workspacesSharedWorker, toWorkspaceFile]
  );

  const getFiles = useCallback(
    async (args: { workspaceId: string; globPattern?: string }) => {
      const wwfds = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFiles(args)
      );
      return wwfds.map((wwfd) => toWorkspaceFile(wwfd));
    },
    [workspacesSharedWorker, toWorkspaceFile]
  );

  const getFileContent = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(args)
      ),
    [workspacesSharedWorker]
  );

  const deleteFile = useCallback(
    async (args: { file: WorkspaceFile }) => {
      return workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteFile({
          wwfd: {
            workspaceId: args.file.workspaceId,
            relativePath: args.file.relativePath,
          },
        })
      );
    },
    [workspacesSharedWorker]
  );

  const moveFile = useCallback(
    async (args: { file: WorkspaceFile; newDirPath: string }) => {
      const wwfd = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_moveFile({
          wwfd: {
            workspaceId: args.file.workspaceId,
            relativePath: args.file.relativePath,
          },
          newDirPath: args.newDirPath,
        })
      );
      return toWorkspaceFile(wwfd);
    },
    [toWorkspaceFile, workspacesSharedWorker]
  );

  const updateFile = useCallback(
    async (args: { workspaceId: string; relativePath: string; newContent: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_updateFile({
          wwfd: {
            workspaceId: args.workspaceId,
            relativePath: args.relativePath,
          },
          newContent: args.newContent,
        })
      ),
    [workspacesSharedWorker]
  );

  const addFile = useCallback(
    async (args: {
      workspaceId: string;
      name: string;
      destinationDirRelativePath: string;
      content: string;
      extension: string;
    }) => {
      const wwfd = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addFile(args)
      );
      return toWorkspaceFile(wwfd);
    },
    [workspacesSharedWorker, toWorkspaceFile]
  );

  const existsFile = useCallback(
    async (args: { workspaceId: string; relativePath: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_existsFile(args)
      ),
    [workspacesSharedWorker]
  );

  const addEmptyFile = useCallback(
    async (args: { workspaceId: string; destinationDirRelativePath: string; extension: string }) => {
      const wwfd = await workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addEmptyFile(args)
      );
      return toWorkspaceFile(wwfd);
    },
    [workspacesSharedWorker, toWorkspaceFile]
  );

  const prepareZip = useCallback(
    async (args: { workspaceId: string; onlyExtensions?: string[] }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_prepareZip(args)
      ),
    [workspacesSharedWorker]
  );

  const resourceContentGet = useCallback(
    async (args: { workspaceId: string; relativePath: string; opts?: ResourceContentOptions }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentGet(args)
      ),
    [workspacesSharedWorker]
  );

  const resourceContentList = useCallback(
    async (args: { workspaceId: string; globPattern: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentList(args)
      ),
    [workspacesSharedWorker]
  );

  const deleteWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteWorkspace(args)
      ),
    [workspacesSharedWorker]
  );

  const renameWorkspace = useCallback(
    async (args: { workspaceId: string; newName: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameWorkspace(args)
      ),
    [workspacesSharedWorker]
  );

  const listAllWorkspaces = useCallback(
    async () =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_listAllWorkspaces()
      ),
    [workspacesSharedWorker]
  );

  const getWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getWorkspace(args)
      ),
    [workspacesSharedWorker]
  );

  const initGitOnWorkspace = useCallback(
    async (args: { workspaceId: string; remoteUrl: URL }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGitOnExistingWorkspace({
          workspaceId: args.workspaceId,
          remoteUrl: args.remoteUrl.toString(),
        })
      ),
    [workspacesSharedWorker]
  );

  const initGistOnWorkspace = useCallback(
    async (args: { workspaceId: string; remoteUrl: URL; branch: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGistOnExistingWorkspace({
          workspaceId: args.workspaceId,
          remoteUrl: args.remoteUrl.toString(),
          branch: args.branch,
        })
      ),
    [workspacesSharedWorker]
  );

  const changeGitAuthSessionId = useCallback(
    async (args: { workspaceId: string; gitAuthSessionId: string | undefined }) => {
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_changeGitAuthSessionId({
          workspaceId: args.workspaceId,
          gitAuthSessionId: args.gitAuthSessionId,
        })
      );
    },
    [workspacesSharedWorker]
  );

  const initLocalOnWorkspace = useCallback(
    async (args: { workspaceId: string }) =>
      workspacesSharedWorker.withBus((workspacesWorkerBus) =>
        workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initLocalOnExistingWorkspace({
          workspaceId: args.workspaceId,
        })
      ),
    [workspacesSharedWorker]
  );

  const value = useMemo(
    () => ({
      workspacesSharedWorker,
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
      changeGitAuthSessionId,
      initLocalOnWorkspace,
      isFileModified,
      getGitServerRefs,
    }),
    [
      workspacesSharedWorker,
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
      changeGitAuthSessionId,
      initLocalOnWorkspace,
      isFileModified,
      getGitServerRefs,
    ]
  );

  return <WorkspacesContext.Provider value={value}>{props.children}</WorkspacesContext.Provider>;
}
