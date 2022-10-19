/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  ResourceListOptions,
  ResourcesList,
  ResourceContentOptions,
  ResourceContent,
  ContentType,
} from "@kie-tools-core/workspace/dist/api";
import { join } from "path";
import { GIT_DEFAULT_BRANCH } from "../constants/GitConstants";
import { decoder, encoder } from "../encoderdecoder/EncoderDecoder";
import { FsSchema } from "../services/FsCache";
import { FsFlushManager } from "../services/FsFlushManager";
import { GitService } from "../services/GitService";
import { KieSandboxWorkspacesFs } from "../services/KieSandboxWorkspaceFs";
import { StorageFile, StorageService } from "../services/StorageService";
import { WorkspaceDescriptorFsService } from "../services/WorkspaceDescriptorFsService";
import { WorkspaceDescriptorService } from "../services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "../services/WorkspaceFsService";
import { WorkspaceService } from "../services/WorkspaceService";
import { LocalFile } from "./api/LocalFile";
import { WorkspaceDescriptor } from "./api/WorkspaceDescriptor";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "./api/WorkspaceOrigin";
import { WorkspacesWorkerApi } from "./api/WorkspacesWorkerApi";
import { WorkspaceWorkerFile } from "./api/WorkspaceWorkerFile";
import { WorkspaceWorkerFileDescriptor } from "./api/WorkspaceWorkerFileDescriptor";

export interface GitUser {
  name: string;
  email: string;
}

export class WorkspacesWorkerApiImpl implements WorkspacesWorkerApi {
  private readonly MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
  private readonly NEW_FILE_DEFAULT_NAME = "Untitled";

  private readonly fsFlushManager = new FsFlushManager();
  private readonly storageService = new StorageService();
  private readonly workspaceFsService = new WorkspaceFsService(this.fsFlushManager);
  private readonly descriptorsFsService = new WorkspaceDescriptorFsService(this.fsFlushManager);
  private readonly descriptorService = new WorkspaceDescriptorService(this.descriptorsFsService, this.storageService);
  private readonly workspaceService = new WorkspaceService(
    this.storageService,
    this.descriptorsFsService,
    this.descriptorService,
    this.workspaceFsService
  );
  private readonly gitService;

  constructor(
    private readonly args: {
      corsProxyUrl: string;
      gitDefaultUser: GitUser;
      isEditableFn: (path: string) => boolean;
      isModelFn: (path: string) => boolean;
    }
  ) {
    this.gitService = new GitService(args.corsProxyUrl);
  }

  public get flushManager() {
    return this.fsFlushManager;
  }

  public async kieSandboxWorkspacesGit_initGistOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
  }): Promise<void> {
    return this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.descriptorService.turnIntoGist(fs, args.workspaceId, new URL(args.remoteUrl));
    });
  }

  public async kieSandboxWorkspacesGit_initGitOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
  }): Promise<void> {
    return this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.descriptorService.turnIntoGit(fs, args.workspaceId, new URL(args.remoteUrl));
    });
  }

  public async kieSandboxWorkspacesGit_initLocalOnExistingWorkspace(args: { workspaceId: string }): Promise<void> {
    return this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.descriptorService.turnIntoLocal(fs, args.workspaceId);
    });
  }

  public async kieSandboxWorkspacesStorage_getWorkspace(args: { workspaceId: string }): Promise<WorkspaceDescriptor> {
    return this.descriptorsFsService.withReadonlyInMemoryFs(({ fs }) => {
      return this.descriptorService.get(fs, args.workspaceId);
    });
  }

  public async kieSandboxWorkspacesStorage_listAllWorkspaces(): Promise<WorkspaceDescriptor[]> {
    return this.descriptorsFsService.withReadonlyInMemoryFs(({ fs, schema }) => {
      return this.descriptorService.listAll(fs, schema);
    });
  }

  public async kieSandboxWorkspacesStorage_resourceContentGet(args: {
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }): Promise<ResourceContent | undefined> {
    return this.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      const file = await this.workspaceService.getFile({
        fs: fs,
        workspaceId: args.workspaceId,
        relativePath: args.relativePath,
      });

      if (!file) {
        throw new Error(`File '${args.relativePath}' not found in Workspace ${args.workspaceId}`);
      }

      try {
        if (args.opts?.type === "binary") {
          return new ResourceContent(
            args.relativePath,
            file.content ? Buffer.from(file.content).toString("base64") : "",
            ContentType.BINARY
          );
        }

        // "text" is the default
        return new ResourceContent(args.relativePath, decoder.decode(file.content), ContentType.TEXT);
      } catch (e) {
        console.error(e);
        throw e;
      }
    });
  }

  public async kieSandboxWorkspacesStorage_resourceContentList(args: {
    workspaceId: string;
    globPattern: string;
    opts?: ResourceListOptions;
  }): Promise<ResourcesList> {
    return this.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      const files = await this.workspaceService.getFilteredWorkspaceFileDescriptors(
        schema,
        args.workspaceId,
        args.globPattern
      );
      return new ResourcesList(
        args.globPattern,
        files.map((file) => file.relativePath)
      );
    });
  }

  public async kieSandboxWorkspacesStorage_addEmptyFile(args: {
    workspaceId: string;
    destinationDirRelativePath: string;
    extension: string;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    return this.kieSandboxWorkspacesStorage_addFile({ ...args, name: this.NEW_FILE_DEFAULT_NAME, content: "" });
  }

  public async kieSandboxWorkspacesStorage_addFile(args: {
    workspaceId: string;
    name: string;
    destinationDirRelativePath: string;
    content: string;
    extension: string;
  }): Promise<WorkspaceWorkerFile> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      for (let i = 0; i < this.MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
        const index = i === 0 ? "" : `-${i}`;
        const fileName = `${args.name}${index}.${args.extension}`;
        const relativePath = join(args.destinationDirRelativePath, fileName);
        if (await this.workspaceService.existsFile({ fs, workspaceId: args.workspaceId, relativePath })) {
          continue;
        }

        const newFile: WorkspaceWorkerFile = {
          workspaceId: args.workspaceId,
          content: encoder.encode(args.content),
          relativePath,
        };
        await this.workspaceService.createOrOverwriteFile(fs, newFile, broadcaster);
        return newFile;
      }

      throw new Error("Max attempts of new empty file exceeded.");
    });
  }

  public async kieSandboxWorkspacesStorage_deleteFile(args: { wwfd: WorkspaceWorkerFileDescriptor }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
      await this.workspaceService.deleteFile(fs, args.wwfd, broadcaster);
    });
  }

  public async kieSandboxWorkspacesStorage_moveFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newDirPath: string;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
      return this.workspaceService.moveFile({ fs, wwfd: args.wwfd, newDirPath: args.newDirPath, broadcaster });
    });
  }

  public async kieSandboxWorkspacesStorage_deleteWorkspace(args: { workspaceId: string }): Promise<void> {
    await this.workspaceService.delete(args.workspaceId);
  }

  public async kieSandboxWorkspacesStorage_existsFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<boolean> {
    return this.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return !!schema.get(this.workspaceService.getAbsolutePath(args));
    });
  }

  public kieSandboxWorkspacesStorage_getFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceWorkerFileDescriptor | undefined> {
    return this.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return (
        schema.get(this.workspaceService.getAbsolutePath(args)) && {
          workspaceId: args.workspaceId,
          relativePath: args.relativePath,
        }
      );
    });
  }

  public async kieSandboxWorkspacesStorage_getFileContent(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<Uint8Array> {
    return this.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.storageService.getFileContent(fs, this.workspaceService.getAbsolutePath(args));
    });
  }

  public kieSandboxWorkspacesStorage_getFiles(args: {
    workspaceId: string;
    globPattern?: string;
  }): Promise<WorkspaceWorkerFileDescriptor[]> {
    return this.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return this.workspaceService.getFilteredWorkspaceFileDescriptors(schema, args.workspaceId, args.globPattern);
    });
  }

  public async kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args: {
    workspaceId: string;
    relativePath: string;
  }) {
    return this.workspaceService.getUniqueFileIdentifier(args);
  }

  public async kieSandboxWorkspacesStorage_prepareZip(args: {
    workspaceId: string;
    onlyExtensions?: string[];
  }): Promise<Blob> {
    return this.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs, schema }) => {
      return this.workspaceService.prepareZip(fs, schema, args.workspaceId, args.onlyExtensions);
    });
  }

  public async kieSandboxWorkspacesStorage_renameFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
      return this.workspaceService.renameFile({
        fs: fs,
        wwfd: args.wwfd,
        newFileNameWithoutExtension: args.newFileNameWithoutExtension,
        broadcaster,
      });
    });
  }
  public async kieSandboxWorkspacesStorage_renameWorkspace(args: {
    workspaceId: string;
    newName: string;
  }): Promise<void> {
    await this.workspaceService.rename(args.workspaceId, args.newName);
  }

  public async kieSandboxWorkspacesStorage_updateFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newContent: string;
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
      return this.workspaceService.updateFile(fs, args.wwfd, async () => args.newContent, broadcaster);
    });
  }

  //git

  public async kieSandboxWorkspacesGit_addRemote(args: {
    workspaceId: string;
    name: string;
    url: string;
    force: boolean;
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.addRemote({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_deleteRemote(args: { workspaceId: string; name: string }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.deleteRemote({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_branch(args: {
    workspaceId: string;
    name: string;
    checkout: boolean;
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.branch({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_checkout(args: {
    workspaceId: string;
    ref: string;
    remote: string;
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.checkout({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_clone(args: {
    origin: GistOrigin | GitHubOrigin;
    gitConfig?: { email: string; name: string };
    authInfo?: { username: string; password: string };
  }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
    return await this.createWorkspace({
      preferredName: new URL(args.origin.url).pathname.substring(1), // Remove slash
      origin: args.origin,
      storeFiles: async (fs, schema, workspace) => {
        await this.gitService.clone({
          fs,
          dir: this.workspaceService.getAbsolutePath({ workspaceId: workspace.workspaceId }),
          repositoryUrl: new URL(args.origin.url),
          gitConfig: args.gitConfig,
          authInfo: args.authInfo,
          sourceBranch: args.origin.branch,
        });
        return this.workspaceService.getFilteredWorkspaceFileDescriptors(schema, workspace.workspaceId);
      },
    });
  }

  public async kieSandboxWorkspacesGit_commit(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
  }): Promise<void> {
    const descriptor = await this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.descriptorService.get(fs, args.workspaceId);
    });

    const workspaceRootDirPath = this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId });

    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      const fileRelativePaths = await this.gitService.unstagedModifiedFileRelativePaths({
        fs,
        dir: workspaceRootDirPath,
        exclude: (filepath) => !this.args.isEditableFn(filepath),
      });

      if (fileRelativePaths.length === 0) {
        console.debug("Nothing to commit.");
        return;
      }

      await Promise.all(
        fileRelativePaths.map(async (relativePath) => {
          if (
            await this.workspaceService.existsFile({
              fs,
              workspaceId: args.workspaceId,
              relativePath,
            })
          ) {
            await this.gitService.add({
              fs,
              dir: workspaceRootDirPath,
              relativePath,
            });
          } else {
            await this.gitService.rm({
              fs,
              dir: workspaceRootDirPath,
              relativePath,
            });
          }
        })
      );

      await this.gitService.commit({
        fs,
        dir: workspaceRootDirPath,
        targetBranch: descriptor.origin.branch,
        message: "Changes from KIE Sandbox",
        author: {
          name: args.gitConfig?.name ?? this.args.gitDefaultUser.name,
          email: args.gitConfig?.email ?? this.args.gitDefaultUser.email,
        },
      });

      broadcaster.broadcast({
        channel: args.workspaceId,
        message: async () => ({
          type: "WS_CREATE_SAVE_POINT",
          workspaceId: args.workspaceId,
        }),
      });
    });
  }

  public async kieSandboxWorkspacesGit_fetch(args: {
    workspaceId: string;
    remote: string;
    ref: string;
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.fetch({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_init(args: {
    localFiles: LocalFile[];
    preferredName?: string;
    gitConfig?: { email: string; name: string };
  }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
    return await this.createWorkspace({
      preferredName: args.preferredName,
      origin: { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
      storeFiles: async (fs, schema, workspace) => {
        const files = args.localFiles
          .filter((file) => !file.path.startsWith(".git/"))
          .map(
            (localFile) =>
              new StorageFile({
                path: this.workspaceService.getAbsolutePath({
                  workspaceId: workspace.workspaceId,
                  relativePath: localFile.path,
                }),
                getFileContents: async () => localFile.fileContents,
              })
          );

        await Promise.all(
          files.map(async (f) => {
            await this.storageService.createOrOverwriteFile(fs, f);
          })
        );

        const workspaceRootDirAbsolutePath = this.workspaceService.getAbsolutePath({
          workspaceId: workspace.workspaceId,
        });

        const ignoredPaths = await this.storageService.walk({
          schema,
          shouldExcludeAbsolutePath: () => false,
          baseAbsolutePath: workspaceRootDirAbsolutePath,
          onVisit: async ({ absolutePath, relativePath }) => {
            const isIgnored = await this.gitService.isIgnored({
              fs,
              dir: workspaceRootDirAbsolutePath,
              filepath: relativePath,
            });
            return isIgnored ? absolutePath : undefined;
          },
        });

        await Promise.all(
          ignoredPaths.map(async (path) => {
            await this.storageService.deleteFile(fs, path);
          })
        );

        await this.gitService.init({
          fs,
          dir: workspaceRootDirAbsolutePath,
        });

        await this.gitService.add({
          fs,
          dir: workspaceRootDirAbsolutePath,
          relativePath: ".",
        });

        await this.gitService.commit({
          fs,
          dir: workspaceRootDirAbsolutePath,
          message: "Initial commit from KIE Sandbox",
          targetBranch: GIT_DEFAULT_BRANCH,
          author: {
            name: args.gitConfig?.name ?? this.args.gitDefaultUser.name,
            email: args.gitConfig?.email ?? this.args.gitDefaultUser.name,
          },
        });

        return this.workspaceService.getFilteredWorkspaceFileDescriptors(schema, workspace.workspaceId);
      },
    });
  }

  public async kieSandboxWorkspacesGit_pull(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    authInfo?: { username: string; password: string };
  }): Promise<void> {
    const workspace = await this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.descriptorService.get(fs, args.workspaceId);
    });

    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      await this.gitService.pull({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ref: workspace.origin.branch,
        author: {
          name: args.gitConfig?.name ?? this.args.gitDefaultUser.name,
          email: args.gitConfig?.email ?? this.args.gitDefaultUser.name,
        },
        authInfo: args.authInfo,
      });

      broadcaster.broadcast({
        channel: args.workspaceId,
        message: async () => ({
          type: "WS_PULL",
          workspaceId: args.workspaceId,
        }),
      });
    });
  }

  public async kieSandboxWorkspacesGit_push(args: {
    workspaceId: string;
    ref: string;
    remoteRef?: string;
    remote: string;
    force: boolean;
    authInfo: {
      username: string;
      password: string;
    };
  }): Promise<void> {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
      return this.gitService.push({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ...args,
      });
    });
  }

  public async kieSandboxWorkspacesGit_resolveRef(args: { workspaceId: string; ref: string }): Promise<string> {
    return this.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.gitService.resolveRef({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ref: args.ref,
      });
    });
  }

  public async kieSandboxWorkspacesGit_isModified(args: { workspaceId: string; relativePath: string }) {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.gitService.isModified({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        relativePath: args.relativePath,
      });
    });
  }

  public async kieSandboxWorkspacesGit_hasLocalChanges(args: { workspaceId: string }) {
    return this.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.gitService.hasLocalChanges({
        fs: fs,
        dir: this.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        exclude: (filepath) => !this.args.isEditableFn(filepath),
      });
    });
  }

  public kieSandboxWorkspacesStorage_flushes() {
    return { defaultValue: [] };
  }

  private async createWorkspace(args: {
    storeFiles: (
      fs: KieSandboxWorkspacesFs,
      schema: FsSchema,
      workspace: WorkspaceDescriptor
    ) => Promise<WorkspaceWorkerFileDescriptor[]>;
    origin: WorkspaceOrigin;
    preferredName?: string;
  }) {
    const { workspace, files } = await this.workspaceService.create({
      storeFiles: args.storeFiles,
      origin: args.origin,
      preferredName: args.preferredName,
    });

    if (files.length <= 0) {
      return { workspace, suggestedFirstFile: undefined };
    }

    const suggestedFirstFile = files
      .filter((file) => this.args.isModelFn(file.relativePath))
      .sort((a, b) => a.relativePath.localeCompare(b.relativePath))[0];

    return {
      workspace,
      suggestedFirstFile: {
        workspaceId: suggestedFirstFile.workspaceId,
        relativePath: suggestedFirstFile.relativePath,
      },
    };
  }
}
