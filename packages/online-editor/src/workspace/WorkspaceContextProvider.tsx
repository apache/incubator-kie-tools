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

import { File, newFile } from "@kie-tooling-core/editor/dist/channel";
import {
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
} from "@kie-tooling-core/workspace/dist/api";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { GithubService } from "../common/GithubService";
import { FileHandler } from "./handler/FileHandler";
import { GitRepositoryFileHandler } from "./handler/GitRepositoryFileHandler";
import { LocalFileHandler } from "./handler/LocalFileHandler";
import { ActiveWorkspace } from "./model/ActiveWorkspace";
import { AddFileEvent, ChannelKind, MoveFileEvent, UpdateFileEvent } from "./model/Event";
import { WorkspaceDescriptor } from "./model/WorkspaceDescriptor";
import { GitHubRepositoryOrigin, LocalOrigin, resolveKind, WorkspaceKind } from "./model/WorkspaceOrigin";
import { BroadcastService } from "./services/BroadcastService";
import { GitService } from "./services/GitService";
import { StorageService } from "./services/StorageService";
import { WorkspaceService } from "./services/WorkspaceService";
import { SUPPORTED_FILES, SUPPORTED_FILES_EDITABLE_PATTERN } from "./SupportedFiles";
import { WorkspaceContext } from "./WorkspaceContext";

const INDEXED_DB_NAME = "kogito-online";
const GIT_CORS_PROXY = "https://cors.isomorphic-git.org"; // TODO CAPONETTO: Deploy our own proxy (https://github.com/isomorphic-git/cors-proxy)

// TODO CAPONETTO: fullname and email to be set via settings? Use octokit?
const GIT_USER_FULLNAME = "Kogito Tooling Bot (kiegroup)";
const GIT_USER_EMAIL = "kietooling@gmail.com";

interface Props {
  children: React.ReactNode;
  file?: File;
  githubService: GithubService;
}

export function WorkspaceContextProvider(props: Props) {
  const [file, setFile] = useState<File>();
  const [active, setActive] = useState<ActiveWorkspace>();

  const broadcastService = useMemo(() => {
    const service = new BroadcastService();
    service.register(Object.values(ChannelKind));
    return service;
  }, []);

  const storageService = useMemo(() => new StorageService(INDEXED_DB_NAME, broadcastService), [broadcastService]);
  const workspaceService = useMemo(
    () => new WorkspaceService(storageService, broadcastService),
    [storageService, broadcastService]
  );
  const gitService = useMemo(() => new GitService(GIT_CORS_PROXY, storageService), [storageService]);
  const singleFileMode = useMemo(() => props.file, [props.file]);

  const authInfo = useMemo(
    () => ({
      name: GIT_USER_FULLNAME,
      email: GIT_USER_EMAIL,
      onAuth: () => ({
        username: props.githubService.getLogin(),
        password: props.githubService.resolveToken(),
      }),
    }),
    [props.githubService]
  );

  const reloadWithPath = useCallback((file: File) => {
    if (!file.path) {
      throw new Error("File path is not defined");
    }
    // TODO CAPONETTO: Replace this code after merge with Tiago's branch
    setTimeout(() => {
      // Check: errors occur without this timeout
      window.location.href = `?path=${file.path}#/editor/${file.fileExtension}`;
    }, 500);
  }, []);

  const registerBroadcastHandlers = useCallback(() => {
    const updateFiles = async (targetFilePath: string) => {
      if (!active) {
        return;
      }

      const descriptor = await workspaceService.getByFilePath(targetFilePath);
      if (descriptor.context !== active.descriptor.context) {
        return;
      }

      const updatedFiles = await workspaceService.listFiles(descriptor, SUPPORTED_FILES_EDITABLE_PATTERN);
      setActive({ ...active, files: updatedFiles });
    };

    broadcastService.onEvent<AddFileEvent>(ChannelKind.ADD_FILE, async (event: AddFileEvent) => {
      if (!active || !file) {
        return;
      }
      await updateFiles(event.path);
    });

    broadcastService.onEvent<UpdateFileEvent>(ChannelKind.UPDATE_FILE, async (event: UpdateFileEvent) => {
      if (!active || !file) {
        return;
      }
      if (event.path === file.path) {
        const file = await storageService.getFile(event.path);
        if (!file) {
          throw new Error(`File ${event.path} not found`);
        }
        setFile({ ...file, getFileContents: file.getFileContents });
      }
    });

    broadcastService.onEvent<MoveFileEvent>(ChannelKind.MOVE_FILE, async (event: MoveFileEvent) => {
      if (!active || !file) {
        return;
      }
      if (event.path === file.path) {
        const file = await storageService.getFile(event.newPath);
        if (!file) {
          throw new Error(`File ${event.path} not found`);
        }
        reloadWithPath(file);
        return;
      }
      await updateFiles(event.path);
    });
  }, [active, broadcastService, file, reloadWithPath, storageService, workspaceService]);

  const onFileChanged = useCallback(
    (file: File) => {
      if (singleFileMode) {
        setFile(file);
        return;
      }

      reloadWithPath(file);
    },
    [singleFileMode, reloadWithPath]
  );

  const createWorkspace = useCallback(
    async (descriptor: WorkspaceDescriptor, fileHandler: FileHandler) => {
      const files = await workspaceService.create(descriptor, fileHandler, true);

      setActive({ descriptor: descriptor, files: files, kind: resolveKind(descriptor.origin) });

      if (files.length > 0) {
        const firstFile = files.sort((a: File, b: File) => a.path!.localeCompare(b.path!))[0];
        reloadWithPath(firstFile);
      }
    },
    [reloadWithPath, workspaceService]
  );

  const createWorkspaceFromLocal = useCallback(
    async (files: File[], preferredName?: string) => {
      const descriptor: WorkspaceDescriptor = {
        context: await workspaceService.newContext(),
        name: await workspaceService.newName(preferredName),
        origin: {} as LocalOrigin,
      };

      const supportedFiles = files.filter((file: File) => SUPPORTED_FILES.includes(file.fileExtension));
      const fileHandler = new LocalFileHandler({
        files: supportedFiles,
        workspaceService: workspaceService,
        storageService: storageService,
      });
      await createWorkspace(descriptor, fileHandler);
    },
    [createWorkspace, storageService, workspaceService]
  );

  const createWorkspaceFromGitHubRepository = useCallback(
    async (repositoryUrl: URL, sourceBranch: string, preferredName?: string) => {
      const descriptor: WorkspaceDescriptor = {
        context: await workspaceService.newContext(),
        name: await workspaceService.newName(preferredName),
        origin: { url: repositoryUrl, branch: sourceBranch } as GitHubRepositoryOrigin,
      };

      await props.githubService.authenticate();
      const fileHandler = new GitRepositoryFileHandler({
        authInfo: authInfo,
        repositoryUrl: repositoryUrl,
        sourceBranch: sourceBranch,
        gitService: gitService,
        workspaceService: workspaceService,
        storageService: storageService,
      });
      await createWorkspace(descriptor, fileHandler);
    },
    [workspaceService, props.githubService, authInfo, gitService, storageService, createWorkspace]
  );

  const openWorkspaceByFile = useCallback(
    async (file: File) => {
      const descriptor = await workspaceService.getByFile(file);
      const files = await workspaceService.listFiles(descriptor, SUPPORTED_FILES_EDITABLE_PATTERN);

      setFile(file);
      setActive({ descriptor: descriptor, files: files, kind: resolveKind(descriptor.origin) });
    },
    [workspaceService]
  );

  const openWorkspaceByPath = useCallback(
    async (path: string) => {
      const file = await storageService.getFile(path);

      if (!file) {
        throw new Error(`File ${path} not found`);
      }

      await openWorkspaceByFile(file);
    },
    [openWorkspaceByFile, storageService]
  );

  const onFileNameChanged = useCallback(
    async (newFileName: string) => {
      if (!file) {
        throw new Error("No active file");
      }

      if (singleFileMode) {
        setFile({ ...file, fileName: newFileName });
        return;
      }

      if (!active) {
        throw new Error("No active workspace");
      }

      const renamedFile = await storageService.renameFile(file, newFileName, true);
      setFile(renamedFile);

      const fileIndex = active.files.findIndex((f: File) => f.path === file.path);
      const updatedFiles = [...active.files];
      updatedFiles[fileIndex] = renamedFile;
      setActive({ ...active, files: updatedFiles });

      window.history.pushState("", "", `?path=${renamedFile.path}#/editor/${renamedFile.fileExtension}`);
    },
    [singleFileMode, storageService, file, active]
  );

  const updateCurrentFile = useCallback(
    async (getFileContents: () => Promise<string | undefined>) => {
      if (singleFileMode) {
        return;
      }

      if (!file) {
        throw new Error("No active file");
      }

      const updatedFile = { ...file, getFileContents };
      await storageService.updateFile(updatedFile, true);
    },
    [file, singleFileMode, storageService]
  );

  const addEmptyFile = useCallback(
    async (fileExtension) => {
      if (!active) {
        throw new Error("No active workspace");
      }

      const contextPath = await workspaceService.resolveContextPath(active.descriptor);
      const newEmptyFile = newFile(fileExtension, contextPath);
      await storageService.createFile(newEmptyFile, true);

      reloadWithPath(newEmptyFile);
    },
    [active, workspaceService, storageService, reloadWithPath]
  );

  const prepareZip = useCallback(async () => {
    if (!active) {
      throw new Error("No active workspace");
    }

    return await workspaceService.prepareZip(active.descriptor);
  }, [active, workspaceService]);

  const syncWorkspace = useCallback(async () => {
    if (!active) {
      throw new Error("No active workspace");
    }

    if (active.kind === WorkspaceKind.GITHUB_REPOSITORY) {
      await props.githubService.authenticate();
      const origin = active.descriptor.origin as GitHubRepositoryOrigin;
      const fileHandler = new GitRepositoryFileHandler({
        authInfo: authInfo,
        repositoryUrl: origin.url,
        sourceBranch: origin.branch,
        gitService: gitService,
        workspaceService: workspaceService,
        storageService: storageService,
      });
      await fileHandler.sync(active.descriptor);
    }
  }, [active, authInfo, gitService, props.githubService, storageService, workspaceService]);

  const resourceContentGet = useCallback(
    async (path: string, opts?: ResourceContentOptions) => {
      if (!active) {
        return;
      }

      const file = await storageService.getFile(path);

      if (!file) {
        throw new Error(`File ${path} not found`);
      }

      const content = await file.getFileContents();
      return new ResourceContent(path, content, ContentType.TEXT);
    },
    [active, storageService]
  );

  const resourceContentList = useCallback(
    async (globPattern: string, opts?: ResourceListOptions) => {
      if (!active) {
        return new ResourcesList(globPattern, []);
      }

      const files = await workspaceService.listFiles(active.descriptor, globPattern);
      const matchingPaths = files.map((file: File) => file.path!);
      return new ResourcesList(globPattern, matchingPaths);
    },
    [active, workspaceService]
  );

  useEffect(() => {
    if (singleFileMode) {
      return;
    }

    registerBroadcastHandlers();
  }, [registerBroadcastHandlers, singleFileMode]);

  useEffect(() => {
    workspaceService.init().then(async () => {
      if (singleFileMode) {
        setFile(props.file);
        return;
      }

      const urlParams = new URLSearchParams(window.location.search);
      if (!urlParams.has("path")) {
        return;
      }

      await openWorkspaceByPath(urlParams.get("path")!);
    });
  }, []); //once

  return (
    <WorkspaceContext.Provider
      value={{
        file,
        active,
        resourceContentGet,
        resourceContentList,
        openWorkspaceByPath,
        openWorkspaceByFile,
        onFileChanged,
        onFileNameChanged,
        createWorkspaceFromLocal,
        createWorkspaceFromGitHubRepository,
        addEmptyFile,
        updateCurrentFile,
        prepareZip,
        syncWorkspace,
      }}
    >
      {props.children}
    </WorkspaceContext.Provider>
  );
}
