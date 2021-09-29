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

import { WorkspaceFile } from "../WorkspacesContext";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { StorageService } from "../services/StorageService";
import { WorkspaceService } from "../services/WorkspaceService";

export interface FileHandlerCommonArgs {
  workspaceService: WorkspaceService;
  storageService: StorageService;
}

export abstract class FileHandler {
  protected constructor(
    protected readonly workspaceService: WorkspaceService,
    protected readonly storageService: StorageService
  ) {}

  public abstract store(descriptor: WorkspaceDescriptor): Promise<WorkspaceFile[]>;

  public abstract sync(descriptor: WorkspaceDescriptor): Promise<void>;
}
