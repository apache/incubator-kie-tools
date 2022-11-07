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
import { KieSandboxWorkspacesFs } from "../services/KieSandboxWorkspaceFs";
import { StorageFile } from "../services/StorageService";
import { GitServerRef } from "./api/GitServerRef";
import { LocalFile } from "./api/LocalFile";
import { WorkspaceDescriptor } from "./api/WorkspaceDescriptor";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "./api/WorkspaceOrigin";
import { WorkspacesWorkerApi } from "./api/WorkspacesWorkerApi";
import { WorkspaceWorkerFile } from "./api/WorkspaceWorkerFile";
import { WorkspaceWorkerFileDescriptor } from "./api/WorkspaceWorkerFileDescriptor";
import { WorkspaceServices } from "./createWorkspaceServices";

export interface FileFilter {
  // Files with the highest priority
  isModel: (path: string) => boolean;
  // Any supported file that is editable
  isEditable: (path: string) => boolean;
  // Any supported file including editable and readonly ones
  isSupported: (path: string) => boolean;
}

export class WorkspacesWorkerApiImpl implements WorkspacesWorkerApi {
  private readonly MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
  private readonly NEW_FILE_DEFAULT_NAME = "Untitled";
  private readonly GIT_DEFAULT_USER = {
    name: this.args.appName,
    email: "",
  };

  constructor(
    private readonly args: {
      appName: string;
      fileFilter: FileFilter;
      services: WorkspaceServices;
    }
  ) {}

  public async kieSandboxWorkspacesGit_changeGitAuthSessionId(args: {
    workspaceId: string;
    gitAuthSessionId: string | undefined;
  }): Promise<void> {
    return this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.changeGitAuthSessionId(fs, args.workspaceId, args.gitAuthSessionId);
    });
  }

  public async kieSandboxWorkspacesGit_initGistOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
    branch: string;
  }): Promise<void> {
    return this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.turnIntoGist(
        fs,
        args.workspaceId,
        new URL(args.remoteUrl),
        args.branch
      );
    });
  }

  public async kieSandboxWorkspacesGit_initGitOnExistingWorkspace(args: {
    workspaceId: string;
    remoteUrl: string;
  }): Promise<void> {
    return this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.turnIntoGit(fs, args.workspaceId, new URL(args.remoteUrl));
    });
  }

  public async kieSandboxWorkspacesGit_initLocalOnExistingWorkspace(args: { workspaceId: string }): Promise<void> {
    return this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.turnIntoLocal(fs, args.workspaceId);
    });
  }

  public async kieSandboxWorkspacesStorage_getWorkspace(args: { workspaceId: string }): Promise<WorkspaceDescriptor> {
    return this.args.services.descriptorsFsService.withReadonlyInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.get(fs, args.workspaceId);
    });
  }

  public async kieSandboxWorkspacesStorage_listAllWorkspaces(): Promise<WorkspaceDescriptor[]> {
    return this.args.services.descriptorsFsService.withReadonlyInMemoryFs(({ fs, schema }) => {
      return this.args.services.descriptorService.listAll(fs, schema);
    });
  }

  public async kieSandboxWorkspacesStorage_resourceContentGet(args: {
    workspaceId: string;
    relativePath: string;
    opts?: ResourceContentOptions;
  }): Promise<ResourceContent | undefined> {
    return this.args.services.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      const file = await this.args.services.workspaceService.getFile({
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
    return this.args.services.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      const files = await this.args.services.workspaceService.getFilteredWorkspaceFileDescriptors(
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
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        for (let i = 0; i < this.MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
          const index = i === 0 ? "" : `-${i}`;
          const fileName = `${args.name}${index}.${args.extension}`;
          const relativePath = join(args.destinationDirRelativePath, fileName);
          if (
            await this.args.services.workspaceService.existsFile({ fs, workspaceId: args.workspaceId, relativePath })
          ) {
            continue;
          }

          const newFile: WorkspaceWorkerFile = {
            workspaceId: args.workspaceId,
            content: encoder.encode(args.content),
            relativePath,
          };
          await this.args.services.workspaceService.createOrOverwriteFile(fs, newFile, broadcaster);
          return newFile;
        }

        throw new Error("Max attempts of new empty file exceeded.");
      }
    );
  }

  public async kieSandboxWorkspacesStorage_deleteFile(args: { wwfd: WorkspaceWorkerFileDescriptor }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.wwfd.workspaceId,
      async ({ fs, broadcaster }) => {
        await this.args.services.workspaceService.deleteFile(fs, args.wwfd, broadcaster);
      }
    );
  }

  public async kieSandboxWorkspacesStorage_moveFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newDirPath: string;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.wwfd.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.workspaceService.moveFile({
          fs,
          wwfd: args.wwfd,
          newDirPath: args.newDirPath,
          broadcaster,
        });
      }
    );
  }

  public async kieSandboxWorkspacesStorage_deleteWorkspace(args: { workspaceId: string }): Promise<void> {
    await this.args.services.workspaceService.delete(args.workspaceId);
  }

  public async kieSandboxWorkspacesStorage_existsFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<boolean> {
    return this.args.services.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return !!schema.get(this.args.services.workspaceService.getAbsolutePath(args));
    });
  }

  public kieSandboxWorkspacesStorage_getFile(args: {
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceWorkerFileDescriptor | undefined> {
    return this.args.services.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return (
        schema.get(this.args.services.workspaceService.getAbsolutePath(args)) && {
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
    return this.args.services.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.args.services.storageService.getFileContent(
        fs,
        this.args.services.workspaceService.getAbsolutePath(args)
      );
    });
  }

  public kieSandboxWorkspacesStorage_getFiles(args: {
    workspaceId: string;
    globPattern?: string;
  }): Promise<WorkspaceWorkerFileDescriptor[]> {
    return this.args.services.workspaceFsService.withReadonlyFsSchema(args.workspaceId, async ({ schema }) => {
      return this.args.services.workspaceService.getFilteredWorkspaceFileDescriptors(
        schema,
        args.workspaceId,
        args.globPattern
      );
    });
  }

  public async kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args: {
    workspaceId: string;
    relativePath: string;
  }) {
    return this.args.services.workspaceService.getUniqueFileIdentifier(args);
  }

  public async kieSandboxWorkspacesStorage_prepareZip(args: {
    workspaceId: string;
    onlyExtensions?: string[];
  }): Promise<Blob> {
    return this.args.services.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs, schema }) => {
      return this.args.services.workspaceService.prepareZip(fs, schema, args.workspaceId, args.onlyExtensions);
    });
  }

  public async kieSandboxWorkspacesStorage_renameFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.wwfd.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.workspaceService.renameFile({
          fs: fs,
          wwfd: args.wwfd,
          newFileNameWithoutExtension: args.newFileNameWithoutExtension,
          broadcaster,
        });
      }
    );
  }
  public async kieSandboxWorkspacesStorage_renameWorkspace(args: {
    workspaceId: string;
    newName: string;
  }): Promise<void> {
    await this.args.services.workspaceService.rename(args.workspaceId, args.newName);
  }

  public async kieSandboxWorkspacesStorage_updateFile(args: {
    wwfd: WorkspaceWorkerFileDescriptor;
    newContent: string;
  }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.wwfd.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.workspaceService.updateFile(fs, args.wwfd, async () => args.newContent, broadcaster);
      }
    );
  }

  //git

  public async kieSandboxWorkspacesGit_addRemote(args: {
    workspaceId: string;
    name: string;
    url: string;
    force: boolean;
  }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.addRemote({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_deleteRemote(args: { workspaceId: string; name: string }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.deleteRemote({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_branch(args: {
    workspaceId: string;
    name: string;
    checkout: boolean;
  }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.branch({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_checkout(args: {
    workspaceId: string;
    ref: string;
    remote: string;
  }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.checkout({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_getGitServerRefs(args: {
    url: string;
    authInfo?: {
      username: string;
      password: string;
    };
  }): Promise<GitServerRef[]> {
    return this.args.services.gitService.listServerRefs(args);
  }

  public async kieSandboxWorkspacesGit_clone(args: {
    origin: GistOrigin | GitHubOrigin;
    gitConfig?: { email: string; name: string };
    authInfo?: { username: string; password: string };
    gitAuthSessionId: string | undefined;
  }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
    return this.createWorkspace({
      preferredName: new URL(args.origin.url).pathname.substring(1), // Remove slash
      origin: args.origin,
      gitAuthSessionId: args.gitAuthSessionId,
      storeFiles: async (fs, schema, workspace) => {
        await this.args.services.gitService.clone({
          fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: workspace.workspaceId }),
          repositoryUrl: new URL(args.origin.url),
          gitConfig: args.gitConfig,
          authInfo: args.authInfo,
          sourceBranch: args.origin.branch,
        });
        return this.args.services.workspaceService.getFilteredWorkspaceFileDescriptors(schema, workspace.workspaceId);
      },
    });
  }

  public async kieSandboxWorkspacesGit_commit(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
  }): Promise<void> {
    const descriptor = await this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.get(fs, args.workspaceId);
    });

    const workspaceRootDirPath = this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId });

    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        const fileRelativePaths = await this.args.services.gitService.unstagedModifiedFileRelativePaths({
          fs,
          dir: workspaceRootDirPath,
          exclude: (filepath) => !this.args.fileFilter.isEditable(filepath),
        });

        if (fileRelativePaths.length === 0) {
          console.debug("Nothing to commit.");
          return;
        }

        await Promise.all(
          fileRelativePaths.map(async (relativePath) => {
            if (
              await this.args.services.workspaceService.existsFile({
                fs,
                workspaceId: args.workspaceId,
                relativePath,
              })
            ) {
              await this.args.services.gitService.add({
                fs,
                dir: workspaceRootDirPath,
                relativePath,
              });
            } else {
              await this.args.services.gitService.rm({
                fs,
                dir: workspaceRootDirPath,
                relativePath,
              });
            }
          })
        );

        await this.args.services.gitService.commit({
          fs,
          dir: workspaceRootDirPath,
          targetBranch: descriptor.origin.branch,
          message: `Changes from ${this.args.appName}`,
          author: {
            name: args.gitConfig?.name ?? this.GIT_DEFAULT_USER.name,
            email: args.gitConfig?.email ?? this.GIT_DEFAULT_USER.email,
          },
        });

        broadcaster.broadcast({
          channel: args.workspaceId,
          message: async () => ({
            type: "WS_CREATE_SAVE_POINT",
            workspaceId: args.workspaceId,
          }),
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_fetch(args: {
    workspaceId: string;
    remote: string;
    ref: string;
  }): Promise<void> {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.fetch({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_init(args: {
    localFiles: LocalFile[];
    preferredName?: string;
    gitAuthSessionId: string | undefined;
    gitConfig?: { email: string; name: string };
  }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
    return this.createWorkspace({
      preferredName: args.preferredName,
      origin: { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
      gitAuthSessionId: args.gitAuthSessionId,
      storeFiles: async (fs, schema, workspace) => {
        const files = args.localFiles
          .filter((file) => !file.path.startsWith(".git/"))
          .map(
            (localFile) =>
              new StorageFile({
                path: this.args.services.workspaceService.getAbsolutePath({
                  workspaceId: workspace.workspaceId,
                  relativePath: localFile.path,
                }),
                getFileContents: async () => localFile.fileContents,
              })
          );

        await Promise.all(
          files.map(async (f) => {
            await this.args.services.storageService.createOrOverwriteFile(fs, f);
          })
        );

        const workspaceRootDirAbsolutePath = this.args.services.workspaceService.getAbsolutePath({
          workspaceId: workspace.workspaceId,
        });

        const ignoredPaths = await this.args.services.storageService.walk({
          schema,
          shouldExcludeAbsolutePath: () => false,
          baseAbsolutePath: workspaceRootDirAbsolutePath,
          onVisit: async ({ absolutePath, relativePath }) => {
            const isIgnored = await this.args.services.gitService.isIgnored({
              fs,
              dir: workspaceRootDirAbsolutePath,
              filepath: relativePath,
            });
            return isIgnored ? absolutePath : undefined;
          },
        });

        await Promise.all(
          ignoredPaths.map(async (path) => {
            await this.args.services.storageService.deleteFile(fs, path);
          })
        );

        await this.args.services.gitService.init({
          fs,
          dir: workspaceRootDirAbsolutePath,
        });

        await this.args.services.gitService.add({
          fs,
          dir: workspaceRootDirAbsolutePath,
          relativePath: ".",
        });

        await this.args.services.gitService.commit({
          fs,
          dir: workspaceRootDirAbsolutePath,
          message: `Initial commit from ${this.args.appName}`,
          targetBranch: GIT_DEFAULT_BRANCH,
          author: {
            name: args.gitConfig?.name ?? this.GIT_DEFAULT_USER.name,
            email: args.gitConfig?.email ?? this.GIT_DEFAULT_USER.email,
          },
        });

        return this.args.services.workspaceService.getFilteredWorkspaceFileDescriptors(schema, workspace.workspaceId);
      },
    });
  }

  public async kieSandboxWorkspacesGit_pull(args: {
    workspaceId: string;
    gitConfig?: { email: string; name: string };
    authInfo?: { username: string; password: string };
  }): Promise<void> {
    const workspace = await this.args.services.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.args.services.descriptorService.get(fs, args.workspaceId);
    });

    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        await this.args.services.gitService.pull({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ref: workspace.origin.branch,
          author: {
            name: args.gitConfig?.name ?? this.GIT_DEFAULT_USER.name,
            email: args.gitConfig?.email ?? this.GIT_DEFAULT_USER.email,
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
      }
    );
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
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(
      args.workspaceId,
      async ({ fs, broadcaster }) => {
        return this.args.services.gitService.push({
          fs: fs,
          dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      }
    );
  }

  public async kieSandboxWorkspacesGit_resolveRef(args: { workspaceId: string; ref: string }): Promise<string> {
    return this.args.services.workspaceFsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.args.services.gitService.resolveRef({
        fs: fs,
        dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ref: args.ref,
      });
    });
  }

  public async kieSandboxWorkspacesGit_isModified(args: { workspaceId: string; relativePath: string }) {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.args.services.gitService.isModified({
        fs: fs,
        dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        relativePath: args.relativePath,
      });
    });
  }

  public async kieSandboxWorkspacesGit_hasLocalChanges(args: { workspaceId: string }) {
    return this.args.services.workspaceFsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs }) => {
      return this.args.services.gitService.hasLocalChanges({
        fs: fs,
        dir: this.args.services.workspaceService.getAbsolutePath({ workspaceId: args.workspaceId }),
        exclude: (filepath) => !this.args.fileFilter.isEditable(filepath),
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
    gitAuthSessionId: string | undefined;
  }) {
    const { workspace, files } = await this.args.services.workspaceService.create({
      storeFiles: args.storeFiles,
      origin: args.origin,
      preferredName: args.preferredName,
      gitAuthSessionId: args.gitAuthSessionId,
    });

    if (files.length <= 0) {
      return { workspace, suggestedFirstFile: undefined };
    }

    let filteredFiles = files.filter((file) => this.args.fileFilter.isModel(file.relativePath));
    if (!filteredFiles.length) {
      filteredFiles = files.filter((file) => this.args.fileFilter.isEditable(file.relativePath));
    }
    if (!filteredFiles.length) {
      filteredFiles = files.filter((file) => this.args.fileFilter.isSupported(file.relativePath));
    }

    filteredFiles.sort((a, b) => a.relativePath.localeCompare(b.relativePath));

    const suggestedFirstFile = filteredFiles.length ? filteredFiles[0] : undefined;

    return {
      workspace,
      suggestedFirstFile,
    };
  }
}
