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

import { GitService } from "../workspace/services/GitService";
import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { WorkspacesWorkerApi } from "./WorkspacesWorkerApi";
import { StorageService } from "../workspace/services/StorageService";
import { WorkspaceDescriptorService } from "../workspace/services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "../workspace/services/WorkspaceFsService";
import { WorkspaceService } from "../workspace/services/WorkspaceService";

console.log("[workspaces-worker] Loaded.");

const bus = new EnvelopeBusMessageManager<WorkspacesWorkerApi, {}>((a) => postMessage(a));

const storageService = new StorageService();
const descriptorService = new WorkspaceDescriptorService(storageService);
const fsService = new WorkspaceFsService(descriptorService);
const workspacesService = new WorkspaceService(storageService, descriptorService, fsService);
const gitService = new GitService("");

const impl: WorkspacesWorkerApi = {
  async kieSandboxWorkspaces_resolveRef(args: { workspaceId: string; ref: string }): Promise<string> {
    console.log(`[workspaces-worker] Starting "resolveRef"`);
    console.log(`[workspaces-worker] workspaceId: ${args.workspaceId}`);
    console.log(`[workspaces-worker] ref: ${args.ref}`);
    return gitService
      .resolveRef({
        fs: await fsService.getWorkspaceFs(args.workspaceId),
        dir: workspacesService.getAbsolutePath({ workspaceId: args.workspaceId }),
        ref: args.ref,
      })
      .then((ref) => {
        console.log("[workspaces-worker] Success.");
        console.log(ref);
        console.log("[workspaces-worker] Posting message back to main script");
        return ref;
      })
      .finally(() => {
        console.log("[workspaces-worker] Done.");
      });
  },
  async kieSandboxWorkspaces_hasLocalChanges(args: { workspaceId: string }) {
    console.log(`[workspaces-worker] Starting "hasLocalChanges"`);
    console.log(`[workspaces-worker] workspaceId: ${args.workspaceId}`);

    return gitService
      .hasLocalChanges({
        fs: await fsService.getWorkspaceFs(args.workspaceId),
        dir: workspacesService.getAbsolutePath({ workspaceId: args.workspaceId }),
      })
      .then((hasLocalChanges) => {
        console.log("[workspaces-worker] Success.");
        console.log(hasLocalChanges);
        console.log("[workspaces-worker] Posting message back to main script");
        return hasLocalChanges;
      })
      .finally(() => {
        console.log("[workspaces-worker] Done.");
      });
  },
};

onmessage = async (m) => bus.server.receive(m.data, impl);
