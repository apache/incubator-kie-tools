/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { BroadcasterDispatch, FsService } from "./FsService";
import { FsFlushManager } from "./FsFlushManager";
import { FsSchema } from "./FsCache";

export class WorkspaceDescriptorFsService {
  constructor(
    private readonly fsFlushManager: FsFlushManager,
    private readonly fsService = new FsService({ name: "Descriptors" }, fsFlushManager)
  ) {}

  public async withReadWriteInMemoryFs<T>(
    callback: (args: { fs: KieSandboxWorkspacesFs; schema: FsSchema; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    return this.fsService.withReadWriteInMemoryFs(this.getMountPoint(), callback);
  }

  public async withReadonlyInMemoryFs<T>(
    callback: (args: { fs: KieSandboxWorkspacesFs; schema: FsSchema }) => Promise<T>
  ) {
    return this.fsService.withReadonlyInMemoryFs(this.getMountPoint(), callback);
  }

  public getMountPoint() {
    return `fs_v1__workspaces`;
  }
}
