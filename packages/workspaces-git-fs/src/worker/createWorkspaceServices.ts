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

import { FsFlushManager } from "../services/FsFlushManager";
import { GitService } from "../services/GitService";
import { StorageService } from "../services/StorageService";
import { WorkspaceDescriptorFsService } from "../services/WorkspaceDescriptorFsService";
import { WorkspaceDescriptorService } from "../services/WorkspaceDescriptorService";
import { WorkspaceFsService } from "../services/WorkspaceFsService";
import { WorkspaceService } from "../services/WorkspaceService";

export interface WorkspaceServices {
  fsFlushManager: FsFlushManager;
  storageService: StorageService;
  workspaceService: WorkspaceService;
  workspaceFsService: WorkspaceFsService;
  descriptorService: WorkspaceDescriptorService;
  descriptorsFsService: WorkspaceDescriptorFsService;
  gitService: GitService;
}

export interface CreateServicesArgs {
  gitCorsProxyUrl: Promise<string>;
}

export function createWorkspaceServices(args: CreateServicesArgs): WorkspaceServices {
  const fsFlushManager = new FsFlushManager();
  const storageService = new StorageService();
  const workspaceFsService = new WorkspaceFsService(fsFlushManager);
  const descriptorsFsService = new WorkspaceDescriptorFsService(fsFlushManager);
  const descriptorService = new WorkspaceDescriptorService(descriptorsFsService, storageService);
  const gitService = new GitService(args.gitCorsProxyUrl);
  const workspaceService = new WorkspaceService(
    storageService,
    descriptorsFsService,
    descriptorService,
    workspaceFsService
  );

  return {
    fsFlushManager,
    storageService,
    workspaceFsService,
    descriptorsFsService,
    descriptorService,
    workspaceService,
    gitService,
  };
}
