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

import { ContentType, ResourceContent, ResourcesList } from "@kie-tooling-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { FileHandler } from "./handler/FileHandler";
import { GitRepositoryFileHandler } from "./handler/GitRepositoryFileHandler";
import { LocalFileHandler } from "./handler/LocalFileHandler";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { WorkspaceKind } from "./model/WorkspaceOrigin";
import { GitService } from "./services/GitService";
import { StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { SUPPORTED_FILES } from "./SupportedFiles";
import { LocalFile, WorkspaceFile, WorkspacesContext } from "./WorkspacesContext";
import { SupportedFileExtensions } from "../common/GlobalContext";
import { extractFileExtension } from "../common/utils";
import { emptyTemplates } from "./FileTemplates";
import { join } from "path";
import git from "isomorphic-git";
import { WorkspaceEvents } from "./hooks/WorkspaceHooks";

const INDEXED_DB_NAME = "kogito-online";
const GIT_CORS_PROXY = "https://cors.isomorphic-git.org"; // TODO CAPONETTO: Deploy our own proxy (https://github.com/isomorphic-git/cors-proxy)

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

interface Props {
  children: React.ReactNode;
}

export function WorkspacesContextProvider(props: Props) {
  const storageService = useMemo(() => new StorageService(INDEXED_DB_NAME), []);
  const workspaceService = useMemo(() => new WorkspaceService(storageService), [storageService]);
  const gitService = useMemo(() => {
    const instance = new GitService(GIT_CORS_PROXY, storageService);
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    (window as any).git = (prop: unknown, args: any) => git[prop]({ fs: storageService.fs, ...args });
    return instance;
  }, [storageService]);

  const getAbsolutePath = useCallback(
    (args: { workspaceId: string; relativePath: string }) => workspaceService.getAbsolutePath(args),
    [workspaceService]
  );

  const createWorkspace = useCallback(
    async (descriptor: WorkspaceDescriptor, fileHandler: FileHandler) => {
      const files = await workspaceService.create(descriptor, fileHandler, { broadcast: true });
      if (files.length > 0) {
        return { files, suggestedFirstFile: files.sort((a, b) => a.relativePath.localeCompare(b.relativePath))[0] };
      } else {
        return { files, suggestedFirstFile: undefined };
      }
    },
    [workspaceService]
  );

  const isModified = useCallback(
    async (workspaceId: string) => {
      return await gitService.isModified({
        dir: workspaceService.getAbsolutePath({ workspaceId, relativePath: "" }),
      });
    },
    [gitService, workspaceService]
  );

  const createSavePoint = useCallback(
    async (workspaceId: string) => {
      await gitService.commit({
        files: [],
        dir: workspaceService.getAbsolutePath({ workspaceId, relativePath: "" }),
        targetBranch: "main",
        message: "Save point",
        authInfo: {
          name: "Tiago",
          email: "tfernand+dev@redhat.com", //FIXME: Change this.
        },
      });
      const broadcastChannel = new BroadcastChannel(workspaceId);
      const workspaceEvent: WorkspaceEvents = { type: "CREATE_SAVE_POINT", workspaceId };
      broadcastChannel.postMessage(workspaceEvent);
    },
    [gitService, workspaceService]
  );

  const createWorkspaceFromLocal = useCallback(
    async (localFiles: LocalFile[]) => {
      const descriptor: WorkspaceDescriptor = {
        workspaceId: workspaceService.newWorkspaceId(),
        name: NEW_WORKSPACE_DEFAULT_NAME,
        origin: { kind: WorkspaceKind.LOCAL },
        createdDateISO: new Date().toISOString(),
        lastUpdatedDateISO: new Date().toISOString(),
      };

      const supportedFiles = localFiles.filter((localFile) =>
        SUPPORTED_FILES.includes(extractFileExtension(localFile.path)!)
      );

      const fileHandler = new LocalFileHandler({ files: supportedFiles, workspaceService });
      const { files, suggestedFirstFile } = await createWorkspace(descriptor, fileHandler);

      await gitService.init({
        dir: workspaceService.getAbsolutePath({ workspaceId: descriptor.workspaceId, relativePath: "" }),
      });

      for (const file of files) {
        await gitService.add({
          dir: workspaceService.getAbsolutePath({ workspaceId: descriptor.workspaceId, relativePath: "" }),
          relativePath: file.relativePath,
        });
      }

      await gitService.commit({
        files: [],
        dir: workspaceService.getAbsolutePath({ workspaceId: descriptor.workspaceId, relativePath: "" }),
        message: "Initial",
        targetBranch: "main",
        authInfo: {
          name: "Tiago",
          email: "tfernand+dev@redhat.com", //FIXME: Change this.
        },
      });

      return { descriptor, suggestedFirstFile };
    },
    [createWorkspace, gitService, workspaceService]
  );

  const createWorkspaceFromGitHubRepository = useCallback(
    async (
      repositoryUrl: URL,
      sourceBranch: string,
      githubSettings: { user: { login: string; email: string; name: string }; token: string }
    ) => {
      if (!githubSettings.user) {
        throw new Error("User not authenticated on GitHub");
      }

      const descriptor: WorkspaceDescriptor = {
        workspaceId: workspaceService.newWorkspaceId(),
        name: NEW_WORKSPACE_DEFAULT_NAME,
        origin: { url: repositoryUrl, branch: sourceBranch, kind: WorkspaceKind.GITHUB_REPOSITORY },
        createdDateISO: new Date().toISOString(),
        lastUpdatedDateISO: new Date().toISOString(),
      };

      const authInfo = {
        name: githubSettings.user.name,
        email: githubSettings.user.email,
        onAuth: () => ({
          username: githubSettings.user.login,
          password: githubSettings.token,
        }),
      };

      const fileHandler = new GitRepositoryFileHandler({
        authInfo: authInfo,
        repositoryUrl: repositoryUrl,
        sourceBranch: sourceBranch,
        gitService: gitService,
        workspaceService: workspaceService,
      });
      await createWorkspace(descriptor, fileHandler);
      return descriptor;
    },
    [workspaceService, gitService, createWorkspace]
  );

  const renameFile = useCallback(
    async (file: WorkspaceFile, newFileName: string) => {
      const renamedFile = await workspaceService.renameFile(file, newFileName, { broadcast: true });

      await gitService.rm({
        dir: workspaceService.getAbsolutePath({ workspaceId: file.workspaceId, relativePath: "" }),
        relativePath: file.relativePath,
      });
      await gitService.add({
        dir: workspaceService.getAbsolutePath({ workspaceId: renamedFile.workspaceId, relativePath: "" }),
        relativePath: renamedFile.relativePath,
      });
      return renamedFile;
    },
    [workspaceService, gitService]
  );

  const listFiles = useCallback(
    async (workspaceId: string) => {
      return (await workspaceService.listFiles(workspaceId)).filter((f) => !f.relativePath.startsWith(".git"));
    },
    [workspaceService]
  );

  const getFile = useCallback(
    async (args: { workspaceId: string; relativePath: string }) => {
      return await workspaceService.getFile(args);
    },
    [workspaceService]
  );

  const deleteFile = useCallback(
    async (file: WorkspaceFile) => {
      await workspaceService.deleteFile(file, { broadcast: true });
      await gitService.rm({
        dir: workspaceService.getAbsolutePath({ workspaceId: file.workspaceId, relativePath: "" }),
        relativePath: file.relativePath,
      });
    },
    [workspaceService, gitService]
  );

  const updateFile = useCallback(
    async (file: WorkspaceFile, getNewContents: () => Promise<string>) => {
      await workspaceService.updateFile(file, getNewContents, { broadcast: true });
      await gitService.add({
        dir: workspaceService.getAbsolutePath({ workspaceId: file.workspaceId, relativePath: "" }),
        relativePath: file.relativePath,
      });
    },
    [workspaceService, gitService]
  );

  const addEmptyFile = useCallback(
    async (args: { workspaceId: string; destinationDirRelativePath: string; extension: SupportedFileExtensions }) => {
      for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        const index = i === 0 ? "" : `-${i}`;
        const fileName = `Untitled${index}.${args.extension}`;
        const relativePath = join(args.destinationDirRelativePath, fileName);
        if (await workspaceService.existsFile({ workspaceId: args.workspaceId, relativePath })) {
          continue;
        }

        const contents = args.extension in emptyTemplates ? emptyTemplates[args.extension] : emptyTemplates.default;
        const newEmptyFile = new WorkspaceFile({
          workspaceId: args.workspaceId,
          getFileContents: () => Promise.resolve(contents),
          relativePath,
        });
        await workspaceService.createFile(newEmptyFile, { broadcast: true });
        await gitService.add({
          dir: workspaceService.getAbsolutePath({ workspaceId: newEmptyFile.workspaceId, relativePath: "" }),
          relativePath: newEmptyFile.relativePath,
        });
        return newEmptyFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    },
    [workspaceService]
  );

  const prepareZip = useCallback((workspaceId: string) => workspaceService.prepareZip(workspaceId), [workspaceService]);

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
    async (args: { workspaceId: string; relativePath: string }) => {
      const file = await workspaceService.getFile(args);
      if (!file) {
        throw new Error(`File '${args.relativePath}' not found in Workspace ${args.workspaceId}`);
      }

      const content = await file.getFileContents();
      return new ResourceContent(args.relativePath, content, ContentType.TEXT);
    },
    [workspaceService]
  );

  const resourceContentList = useCallback(
    async (workspaceId: string, globPattern: string) => {
      const files = await workspaceService.listFiles(workspaceId, globPattern);
      const matchingPaths = files.map((file) => file.relativePath);
      return new ResourcesList(globPattern, matchingPaths);
    },
    [workspaceService]
  );

  const [ready, setReady] = useState(false);
  useEffect(() => {
    workspaceService.init().then(() => setReady(true));
  }, [workspaceService]);

  return (
    <WorkspacesContext.Provider
      value={{
        workspaceService,
        resourceContentGet,
        resourceContentList,
        //
        createWorkspaceFromLocal,
        createWorkspaceFromGitHubRepository,
        prepareZip,
        getAbsolutePath,
        createSavePoint,
        listFiles,
        isModified,
        //
        addEmptyFile,
        renameFile,
        updateFile,
        deleteFile,
        getFile,
      }}
    >
      {ready && props.children}
    </WorkspacesContext.Provider>
  );
}
