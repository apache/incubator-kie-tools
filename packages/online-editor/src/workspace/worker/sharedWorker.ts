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

import { GitService } from "../services/GitService";
import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { StorageFile, StorageService } from "../services/StorageService";
import { WorkspaceDescriptorService } from "../services/WorkspaceDescriptorService";
import { WorkspaceService } from "../services/WorkspaceService";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { WorkspaceWorkerFileDescriptor } from "./api/WorkspaceWorkerFileDescriptor";
import { WorkspaceWorkerFile } from "./api/WorkspaceWorkerFile";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "../model/WorkspaceOrigin";
import { decoder, encoder, LocalFile, WorkspaceFile } from "../WorkspacesContext";
import { WorkspacesWorkerApi } from "./api/WorkspacesWorkerApi";
import {
  ContentType,
  ResourceContent,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { join } from "path";
import { Buffer } from "buffer";
import { GIT_DEFAULT_BRANCH } from "../constants/GitConstants";
import { ENV_FILE_PATH } from "../../env/EnvConstants";
import { EditorEnvelopeLocatorFactory } from "../../envelopeLocator/EditorEnvelopeLocatorFactory";
import { KieSandboxWorkspacesFs } from "../services/KieSandboxWorkspaceFs";
import { WorkspaceDescriptorFsService } from "../services/WorkspaceDescriptorFsService";
import { WorkspaceFsService } from "../services/WorkspaceFsService";

declare let importScripts: any;
declare let onconnect: any;

importScripts("fsMain.js");

const MAX_NEW_FILE_INDEX_ATTEMPTS = 10;
const NEW_FILE_DEFAULT_NAME = "Untitled";

const GIT_USER_DEFAULT = {
  name: "KIE Sandbox",
  email: "",
};

async function corsProxyUrl() {
  const envFilePath = `../../${ENV_FILE_PATH}`; // Needs to go back two dirs, since this file is at `workspaces/worker`.
  const env = await (await fetch(envFilePath)).json();
  return env.WEBPACK_REPLACE__corsProxyUrl ?? process.env.WEBPACK_REPLACE__corsProxyUrl ?? "";
}

const implPromise = new Promise<WorkspacesWorkerApi>((resImpl) => {
  const storageService = new StorageService();
  const fsService = new WorkspaceFsService();
  const descriptorsFsService = new WorkspaceDescriptorFsService();
  const descriptorService = new WorkspaceDescriptorService(descriptorsFsService, storageService);
  const service = new WorkspaceService(storageService, descriptorsFsService, descriptorService, fsService);
  const gitService = new GitService(corsProxyUrl());
  // const svgService = new WorkspaceSvgService(storageService);
  const editorEnvelopeLocator = new EditorEnvelopeLocatorFactory().create({ targetOrigin: "" });

  const createWorkspace = async (args: {
    storeFiles: (fs: KieSandboxWorkspacesFs, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    origin: WorkspaceOrigin;
    preferredName?: string;
  }) => {
    const { workspace, files } = await service.create({
      storeFiles: args.storeFiles,
      origin: args.origin,
      preferredName: args.preferredName,
    });

    if (files.length <= 0) {
      return { workspace, suggestedFirstFile: undefined };
    }

    const suggestedFirstFile = files
      .filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath))
      .sort((a, b) => a.relativePath.localeCompare(b.relativePath))[0];

    return {
      workspace,
      suggestedFirstFile: {
        workspaceId: suggestedFirstFile.workspaceId,
        relativePath: suggestedFirstFile.relativePath,
      },
    };
  };

  const impl: WorkspacesWorkerApi = {
    async kieSandboxWorkspacesGit_initGistOnExistingWorkspace(args: {
      workspaceId: string; //
      remoteUrl: string;
    }): Promise<void> {
      return descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
        return descriptorService.turnIntoGist(fs, args.workspaceId, new URL(args.remoteUrl));
      });
    },
    async kieSandboxWorkspacesGit_initGitOnExistingWorkspace(args: {
      workspaceId: string; //
      remoteUrl: string;
    }): Promise<void> {
      return descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
        return descriptorService.turnIntoGit(fs, args.workspaceId, new URL(args.remoteUrl));
      });
    },
    async kieSandboxWorkspacesStorage_getWorkspace(args: {
      workspaceId: string; //
    }): Promise<WorkspaceDescriptor> {
      return descriptorsFsService.withReadonlyInMemoryFs(({ fs }) => {
        return descriptorService.get(fs, args.workspaceId);
      });
    },
    async kieSandboxWorkspacesStorage_listAllWorkspaces(): Promise<WorkspaceDescriptor[]> {
      return descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
        return descriptorService.listAll(fs);
      });
    },
    async kieSandboxWorkspacesStorage_resourceContentGet(args: {
      workspaceId: string;
      relativePath: string;
      opts?: ResourceContentOptions;
    }): Promise<ResourceContent | undefined> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        const file = await service.getFile({
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
    },
    async kieSandboxWorkspacesStorage_resourceContentList(args: {
      workspaceId: string;
      globPattern: string;
      opts?: ResourceListOptions;
    }): Promise<ResourcesList> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        const files = await service.getFilesWithLazyContent(fs, args.workspaceId, args.globPattern);
        const matchingPaths = files.map((file) => file.relativePath);
        return new ResourcesList(args.globPattern, matchingPaths);
      });
    },
    async kieSandboxWorkspacesStorage_addEmptyFile(args: {
      workspaceId: string;
      destinationDirRelativePath: string;
      extension: string;
    }): Promise<WorkspaceWorkerFileDescriptor> {
      return this.kieSandboxWorkspacesStorage_addFile({ ...args, name: NEW_FILE_DEFAULT_NAME, content: "" });
    },
    async kieSandboxWorkspacesStorage_addFile(args: {
      workspaceId: string;
      name: string;
      destinationDirRelativePath: string;
      content: string;
      extension: string;
    }): Promise<WorkspaceWorkerFile> {
      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        for (let i = 0; i < MAX_NEW_FILE_INDEX_ATTEMPTS; i++) {
          const index = i === 0 ? "" : `-${i}`;
          const fileName = `${args.name}${index}.${args.extension}`;
          const relativePath = join(args.destinationDirRelativePath, fileName);
          if (await service.existsFile({ fs, workspaceId: args.workspaceId, relativePath })) {
            continue;
          }

          const newFile: WorkspaceWorkerFile = {
            workspaceId: args.workspaceId,
            content: encoder.encode(args.content),
            relativePath,
          };
          await service.createOrOverwriteFile(fs, newFile, broadcaster);
          return newFile;
        }

        throw new Error("Max attempts of new empty file exceeded.");
      });
    },
    async kieSandboxWorkspacesStorage_deleteFile(args: { wwfd: WorkspaceWorkerFileDescriptor }): Promise<void> {
      return fsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
        await service.deleteFile(fs, args.wwfd, broadcaster);
        // await svgService.deleteSvg(args.wwfd);
      });
    },
    async kieSandboxWorkspacesStorage_deleteWorkspace(args: { workspaceId: string }): Promise<void> {
      await service.delete(args.workspaceId);
      // await svgService.delete(args.workspaceId);
    },
    async kieSandboxWorkspacesStorage_existsFile(args: {
      workspaceId: string;
      relativePath: string;
    }): Promise<boolean> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return await service.existsFile({
          fs: fs,
          workspaceId: args.workspaceId,
          relativePath: args.relativePath,
        });
      });
    },
    async kieSandboxWorkspacesStorage_getFile(args: {
      workspaceId: string;
      relativePath: string;
    }): Promise<WorkspaceWorkerFileDescriptor | undefined> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        const file = await service.getFile({
          fs: fs,
          workspaceId: args.workspaceId,
          relativePath: args.relativePath,
        });

        return (
          file && {
            workspaceId: file.workspaceId,
            relativePath: file.relativePath,
          }
        );
      });
    },
    async kieSandboxWorkspacesStorage_getFileContent(args: {
      workspaceId: string;
      relativePath: string;
    }): Promise<Uint8Array> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return storageService.getFileContent(fs, service.getAbsolutePath(args));
      });
    },
    async kieSandboxWorkspacesStorage_getFiles(args: {
      workspaceId: string;
    }): Promise<WorkspaceWorkerFileDescriptor[]> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return (await service.getFilesWithLazyContent(fs, args.workspaceId)).map((file) => ({
          workspaceId: file.workspaceId,
          relativePath: file.relativePath,
        }));
      });
    },
    async kieSandboxWorkspacesStorage_getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
      return service.getUniqueFileIdentifier(args);
    },
    async kieSandboxWorkspacesStorage_prepareZip(args: {
      workspaceId: string;
      onlyExtensions?: string[];
    }): Promise<Blob> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return service.prepareZip(fs, args.workspaceId, args.onlyExtensions);
      });
    },

    async kieSandboxWorkspacesStorage_renameFile(args: {
      wwfd: WorkspaceWorkerFileDescriptor;
      newFileNameWithoutExtension: string;
    }): Promise<WorkspaceWorkerFileDescriptor> {
      return fsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
        const newFile = service.renameFile({
          fs: fs,
          wwfd: args.wwfd,
          newFileNameWithoutExtension: args.newFileNameWithoutExtension,
          broadcaster,
        });
        // await svgService.renameSvg(args.wwfd, args.newFileNameWithoutExtension);
        return newFile;
      });
    },
    async kieSandboxWorkspacesStorage_renameWorkspace(args: { workspaceId: string; newName: string }): Promise<void> {
      await service.rename(args.workspaceId, args.newName);
    },
    async kieSandboxWorkspacesStorage_updateFile(args: {
      wwfd: WorkspaceWorkerFileDescriptor;
      newContent: string;
    }): Promise<void> {
      return fsService.withReadWriteInMemoryFs(args.wwfd.workspaceId, async ({ fs, broadcaster }) => {
        return service.updateFile(fs, args.wwfd, async () => args.newContent, broadcaster);
      });
    },

    //git

    async kieSandboxWorkspacesGit_addRemote(args: {
      workspaceId: string;
      name: string;
      url: string;
      force: boolean;
    }): Promise<void> {
      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        return gitService.addRemote({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      });
    },
    async kieSandboxWorkspacesGit_branch(args: {
      workspaceId: string;
      name: string;
      checkout: boolean;
    }): Promise<void> {
      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        return gitService.branch({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      });
    },
    async kieSandboxWorkspacesGit_clone(args: {
      origin: GistOrigin | GitHubOrigin;
      gitConfig?: { email: string; name: string };
      authInfo?: { username: string; password: string };
    }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
      return await createWorkspace({
        preferredName: new URL(args.origin.url).pathname.substring(1), // Remove slash
        origin: args.origin,
        storeFiles: async (fs, workspace) => {
          await gitService.clone({
            fs,
            dir: service.getAbsolutePath({ workspaceId: workspace.workspaceId }),
            repositoryUrl: new URL(args.origin.url),
            gitConfig: args.gitConfig,
            authInfo: args.authInfo,
            sourceBranch: args.origin.branch,
          });
          return service.getFilesWithLazyContent(fs, workspace.workspaceId);
        },
      });
    },
    async kieSandboxWorkspacesGit_commit(args: {
      workspaceId: string;
      gitConfig?: { email: string; name: string };
    }): Promise<void> {
      const descriptor = await descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
        return descriptorService.get(fs, args.workspaceId);
      });

      const workspaceRootDirPath = service.getAbsolutePath({ workspaceId: args.workspaceId });

      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        const fileRelativePaths = await gitService.unstagedModifiedFileRelativePaths({
          fs,
          dir: workspaceRootDirPath,
        });

        if (fileRelativePaths.length === 0) {
          console.debug("Nothing to commit.");
          return;
        }

        await Promise.all(
          fileRelativePaths.map(async (relativePath) => {
            if (
              await service.existsFile({
                fs,
                workspaceId: args.workspaceId,
                relativePath,
              })
            ) {
              await gitService.add({
                fs,
                dir: workspaceRootDirPath,
                relativePath,
              });
            } else {
              await gitService.rm({
                fs,
                dir: workspaceRootDirPath,
                relativePath,
              });
            }
          })
        );

        await gitService.commit({
          fs,
          dir: workspaceRootDirPath,
          targetBranch: descriptor.origin.branch,
          message: "Changes from KIE Sandbox",
          author: {
            name: args.gitConfig?.name ?? GIT_USER_DEFAULT.name,
            email: args.gitConfig?.email ?? GIT_USER_DEFAULT.email,
          },
        });

        broadcaster.broadcast({
          channel: args.workspaceId,
          message: async () => ({
            type: "CREATE_SAVE_POINT",
            workspaceId: args.workspaceId,
          }),
        });
      });
    },
    async kieSandboxWorkspacesGit_init(args: {
      localFiles: LocalFile[];
      preferredName?: string;
      gitConfig?: { email: string; name: string };
    }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
      return await createWorkspace({
        preferredName: args.preferredName,
        origin: { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
        storeFiles: async (fs, workspace: WorkspaceDescriptor) => {
          const files = args.localFiles
            .filter((file) => !file.path.startsWith(".git/"))
            .map(
              (localFile) =>
                new StorageFile({
                  path: service.getAbsolutePath({
                    workspaceId: workspace.workspaceId,
                    relativePath: localFile.path,
                  }),
                  getFileContents: async () => localFile.fileContents,
                })
            );

          await Promise.all(
            files.map(async (f) => {
              await storageService.createOrOverwriteFile(fs, f);
            })
          );

          const workspaceRootDirPath = service.getAbsolutePath({
            workspaceId: workspace.workspaceId,
          });

          const ignoredPaths = await storageService.walk({
            fs,
            shouldExcludeDir: () => false,
            startFromDirPath: workspaceRootDirPath,
            onVisit: async ({ absolutePath, relativePath }) => {
              const isIgnored = await gitService.isIgnored({
                fs,
                dir: workspaceRootDirPath,
                filepath: relativePath,
              });
              return isIgnored ? absolutePath : undefined;
            },
          });

          await Promise.all(
            ignoredPaths.map(async (path) => {
              await storageService.deleteFile(fs, path);
            })
          );

          await gitService.init({
            fs,
            dir: workspaceRootDirPath,
          });

          await gitService.add({
            fs,
            dir: workspaceRootDirPath,
            relativePath: ".",
          });

          await gitService.commit({
            fs,
            dir: workspaceRootDirPath,
            message: "Initial commit from KIE Sandbox",
            targetBranch: GIT_DEFAULT_BRANCH,
            author: {
              name: args.gitConfig?.name ?? GIT_USER_DEFAULT.name,
              email: args.gitConfig?.email ?? GIT_USER_DEFAULT.email,
            },
          });

          return service.getFilesWithLazyContent(fs, workspace.workspaceId);
        },
      });
    },
    async kieSandboxWorkspacesGit_pull(args: {
      workspaceId: string;
      gitConfig?: { email: string; name: string };
      authInfo?: { username: string; password: string };
    }): Promise<void> {
      const workspace = await descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
        return descriptorService.get(fs, args.workspaceId);
      });

      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        await gitService.pull({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
          ref: workspace.origin.branch,
          author: {
            name: args.gitConfig?.name ?? GIT_USER_DEFAULT.name,
            email: args.gitConfig?.email ?? GIT_USER_DEFAULT.email,
          },
          authInfo: args.authInfo,
        });

        broadcaster.broadcast({
          channel: args.workspaceId,
          message: async () => ({
            type: "PULL",
            workspaceId: args.workspaceId,
          }),
        });
      });
    },
    async kieSandboxWorkspacesGit_push(args: {
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
      return fsService.withReadWriteInMemoryFs(args.workspaceId, async ({ fs, broadcaster }) => {
        return gitService.push({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
          ...args,
        });
      });
    },
    async kieSandboxWorkspacesGit_resolveRef(args: { workspaceId: string; ref: string }): Promise<string> {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return gitService.resolveRef({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
          ref: args.ref,
        });
      });
    },
    async kieSandboxWorkspacesGit_hasLocalChanges(args: { workspaceId: string }) {
      return fsService.withReadonlyInMemoryFs(args.workspaceId, async ({ fs }) => {
        return gitService.hasLocalChanges({
          fs: fs,
          dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
        });
      });
    },
  };
  resImpl(impl);
});

onconnect = async (e: any) => {
  const port = e.ports[0];

  port.postMessage(`Connected to 'workspaces-shared-worker'`);

  const bus = new EnvelopeBusMessageManager<WorkspacesWorkerApi, { kieToolsWorkspacesWorker_ready: () => void }>((m) =>
    port.postMessage(m)
  );

  const impl = await implPromise;

  port.addEventListener("message", async (m: MessageEvent) => {
    bus.server.receive(m.data, impl);
  });

  port.start(); // Required when using addEventListener. Otherwise, called implicitly by onmessage setter.

  bus.clientApi.notifications.kieToolsWorkspacesWorker_ready.send();
};
