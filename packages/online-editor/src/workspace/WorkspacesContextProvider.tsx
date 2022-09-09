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
import { LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { GistOrigin, GitHubOrigin } from "./model/WorkspaceOrigin";
import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { WorkspacesWorkerApi } from "../workspacesWorker/api/WorkspacesWorkerApi";
import { WorkspaceWorkerFileDescriptor } from "../workspacesWorker/api/WorkspaceWorkerFileDescriptor";
import * as path from "path";

interface Props {
  children: React.ReactNode;
}

const workspacesWorker = new Worker("workspacesWorker/worker.js");

const workspacesWorkerBus = new EnvelopeBusMessageManager<{}, WorkspacesWorkerApi>((m) => {
  workspacesWorker.postMessage(m);
});

workspacesWorker.onmessage = (m) => {
  workspacesWorkerBus.server.receive(m.data, {});
};

export function WorkspacesContextProvider(props: Props) {
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

  const getFile = useCallback(async (args: { workspaceId: string; relativePath: string }) => {
    const wwfd = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFile(args);
    return wwfd ? toWorkspaceFile(wwfd) : undefined;
  }, []);

  const createWorkspaceFromLocal = useCallback(
    async (args: {
      useInMemoryFs: boolean;
      localFiles: LocalFile[];
      preferredName?: string;
      gitConfig?: { email: string; name: string };
    }) => {
      const workspaceInit = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_init(args);

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
      const workspaceClone = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_clone(args);
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

  const getUniqueFileIdentifier = useCallback((args: { workspaceId: string; relativePath: string }) => {
    return args.workspaceId + "__" + path.join("/", args.relativePath ?? ""); // FIXME: DUPLICATED
  }, []);

  const renameFile = useCallback(async (args: { file: WorkspaceFile; newFileNameWithoutExtension: string }) => {
    const wwfd = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameFile({
      wwfd: {
        workspaceId: args.file.workspaceId,
        relativePath: args.file.relativePath,
      },
      newFileNameWithoutExtension: args.newFileNameWithoutExtension,
    });
    return toWorkspaceFile(wwfd);
  }, []);

  const getFiles = useCallback(async (args: { fs: KieSandboxFs; workspaceId: string }) => {
    const wwfds = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFiles(args);
    return wwfds.map((wwfd) => toWorkspaceFile(wwfd));
  }, []);

  const getFileContent = useCallback(async (args: { workspaceId: string; relativePath: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(args);
  }, []);

  const deleteFile = useCallback(async (args: { file: WorkspaceFile }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteFile({
      wwfd: {
        workspaceId: args.file.workspaceId,
        relativePath: args.file.relativePath,
      },
    });
  }, []);

  const updateFile = useCallback(async (args: { file: WorkspaceFile; getNewContents: () => Promise<string> }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_updateFile({
      wwfd: {
        workspaceId: args.file.workspaceId,
        relativePath: args.file.relativePath,
      },
      newContent: await args.getNewContents(),
    });
  }, []);

  const addFile = useCallback(
    async (args: {
      workspaceId: string;
      name: string;
      destinationDirRelativePath: string;
      content: string;
      extension: SupportedFileExtensions;
    }) => {
      const wwfd = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addFile(args);
      return toWorkspaceFile(wwfd);
    },
    []
  );

  const existsFile = useCallback(async (args: { workspaceId: string; relativePath: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_existsFile(args);
  }, []);

  const addEmptyFile = useCallback(
    async (args: { workspaceId: string; destinationDirRelativePath: string; extension: SupportedFileExtensions }) => {
      const wwfd = await workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_addEmptyFile(args);
      return toWorkspaceFile(wwfd);
    },
    []
  );

  const prepareZip = useCallback((args: { workspaceId: string; onlyExtensions?: string[] }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_prepareZip(args);
  }, []);

  const resourceContentGet = useCallback(
    async (args: { workspaceId: string; relativePath: string; opts?: ResourceContentOptions }) => {
      return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentGet(args);
    },
    []
  );

  const resourceContentList = useCallback(async (args: { workspaceId: string; globPattern: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_resourceContentList(args);
  }, []);

  const deleteWorkspace = useCallback(async (args: { workspaceId: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_deleteWorkspace(args);
  }, []);

  const renameWorkspace = useCallback(async (args: { workspaceId: string; newName: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_renameWorkspace(args);
  }, []);

  const listAllWorkspaces = useCallback(async () => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_listAllWorkspaces();
  }, []);

  const getWorkspace = useCallback(async (args: { workspaceId: string }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getWorkspace(args);
  }, []);

  const initGitOnWorkspace = useCallback(async (args: { workspaceId: string; remoteUrl: URL }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGitOnExistingWorkspace({
      workspaceId: args.workspaceId,
      remoteUrl: args.remoteUrl.toString(),
    });
  }, []);

  const initGistOnWorkspace = useCallback(async (args: { workspaceId: string; remoteUrl: URL }) => {
    return workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesGit_initGistOnExistingWorkspace({
      workspaceId: args.workspaceId,
      remoteUrl: args.remoteUrl.toString(),
    });
  }, []);

  const value = useMemo(
    () => ({
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
      getFileContent,
      //
      listAllWorkspaces,
      getWorkspace,
      initGitOnWorkspace,
      initGistOnWorkspace,
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
      updateFile,
      listAllWorkspaces,
      getWorkspace,
      initGitOnWorkspace,
      initGistOnWorkspace,
    ]
  );

  return <WorkspacesContext.Provider value={value}>{props.children}</WorkspacesContext.Provider>;
}

function toWorkspaceFile(wwfd: WorkspaceWorkerFileDescriptor) {
  return new WorkspaceFile({
    workspaceId: wwfd.workspaceId,
    relativePath: wwfd.relativePath,
    getFileContents: () => workspacesWorkerBus.clientApi.requests.kieSandboxWorkspacesStorage_getFileContent(wwfd),
  });
}
