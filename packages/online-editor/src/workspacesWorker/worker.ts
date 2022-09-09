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

import { GIT_DEFAULT_BRANCH, GitService } from "../workspace/services/GitService";
import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { WorkspacesWorkerGitApi } from "./api/WorkspacesWorkerGitApi";
import { StorageFile, StorageService } from "../workspace/services/StorageService";
import { WorkspaceDescriptorService } from "../workspace/services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "../workspace/services/WorkspaceFsService";
import { WorkspaceService } from "../workspace/services/WorkspaceService";
import { WorkspaceDescriptor } from "../workspace/model/WorkspaceDescriptor";
import { WorkspaceWorkerFileDescriptor } from "./api/WorkspaceWorkerFileDescriptor";
import { GistOrigin, GitHubOrigin, WorkspaceKind, WorkspaceOrigin } from "../workspace/model/WorkspaceOrigin";
import { LocalFile, WorkspaceFile } from "../workspace/WorkspacesContext";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceEvents } from "../workspace/hooks/WorkspaceHooks";

console.log("[workspaces-worker] Loaded.");

const bus = new EnvelopeBusMessageManager<WorkspacesWorkerGitApi, {}>((a) => postMessage(a));

const GIT_USER_DEFAULT = {
  name: "KIE Sandbox",
  email: "",
};

/// MOCKS
const editorEnvelopeLocator = {
  hasMappingFor(relativePath: string) {
    return ["bpmn", "bpmn2", "dmn", "pmml"].some((ext) => relativePath.endsWith(ext));
  },
};
const corsProxyUrl =
  "https://cors-proxy-kie-sandbox.rhba-cluster-0ad6762cc85bcef5745bb684498c2436-0000.us-south.containers.appdomain.cloud";
////

const storageService = new StorageService();
const descriptorService = new WorkspaceDescriptorService(storageService);
const fsService = new WorkspaceFsService(descriptorService);
const service = new WorkspaceService(storageService, descriptorService, fsService);
const gitService = new GitService(corsProxyUrl);

const createWorkspace = async (args: {
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

const impl: WorkspacesWorkerGitApi = {
  async kieSandboxWorkspacesGit_addRemote(args: {
    workspaceId: string;
    name: string;
    url: string;
    force: boolean;
  }): Promise<void> {
    return gitService.addRemote({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      ...args,
    });
  },
  async kieSandboxWorkspacesGit_branch(args: { workspaceId: string; name: string; checkout: boolean }): Promise<void> {
    return gitService.branch({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      ...args,
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
      useInMemoryFs: true,
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
    const descriptor = await descriptorService.get(args.workspaceId);

    const workspaceRootDirPath = service.getAbsolutePath({ workspaceId: args.workspaceId });

    const fs = await fsService.getWorkspaceFs(args.workspaceId);

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

    const broadcastChannel = new BroadcastChannel(args.workspaceId);
    const workspaceEvent: WorkspaceEvents = { type: "CREATE_SAVE_POINT", workspaceId: args.workspaceId };
    broadcastChannel.postMessage(workspaceEvent);
  },
  async kieSandboxWorkspacesGit_init(args: {
    useInMemoryFs: boolean;
    localFiles: LocalFile[];
    preferredName?: string;
    gitConfig?: { email: string; name: string };
  }): Promise<{ workspace: WorkspaceDescriptor; suggestedFirstFile?: WorkspaceWorkerFileDescriptor }> {
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
                getFileContents: async () => localFile.fileContents,
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
    const workspace = await descriptorService.get(args.workspaceId);
    await gitService.pull({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      ref: workspace.origin.branch,
      author: {
        name: args.gitConfig?.name ?? GIT_USER_DEFAULT.name,
        email: args.gitConfig?.email ?? GIT_USER_DEFAULT.email,
      },
      authInfo: args.authInfo,
    });

    const broadcastChannel2 = new BroadcastChannel(args.workspaceId);
    const workspaceEvent: WorkspaceEvents = { type: "PULL", workspaceId: args.workspaceId };
    broadcastChannel2.postMessage(workspaceEvent);
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
    return gitService.push({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      ...args,
    });
  },
  async kieSandboxWorkspacesGit_resolveRef(args: { workspaceId: string; ref: string }): Promise<string> {
    return gitService.resolveRef({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
      ref: args.ref,
    });
  },
  async kieSandboxWorkspacesGit_hasLocalChanges(args: { workspaceId: string }) {
    return gitService.hasLocalChanges({
      fs: await fsService.getWorkspaceFs(args.workspaceId),
      dir: service.getAbsolutePath({ workspaceId: args.workspaceId }),
    });
  },
};

onmessage = async (m) => bus.server.receive(m.data, impl);
