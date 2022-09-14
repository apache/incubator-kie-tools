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

import type KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { flushFs, FsCache } from "./FsCache";

export class WorkspaceFsService {
  constructor(
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsCache = new FsCache()
  ) {}

  public async getWorkspaceFs(workspaceId: string) {
    if (!(await this.workspaceDescriptorService.get(workspaceId))) {
      throw new Error(`Can't get FS for non-existent workspace '${workspaceId}'`);
    }
    return this.fsCache.getOrCreateFs(workspaceId);
  }

  public async withInMemoryFs<T>(workspaceId: string, callback: (fs: KieSandboxFs) => Promise<T>) {
    const { fs, flush } = await this.createInMemoryWorkspaceFs(workspaceId);
    const ret = await callback(fs);
    await flush();
    return ret;
  }

  private async createInMemoryWorkspaceFs(workspaceId: string) {
    const fs = await this.fsCache.getOrCreateFs(workspaceId);
    const flush = () => flushFs(fs, workspaceId);
    return { fs, flush };
  }
}
