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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { LfsFsCache } from "./LfsFsCache";
import { LfsWorkspaceDescriptorService } from "./LfsWorkspaceDescriptorService";

export class LfsWorkspaceFsService {
  private readonly fsCache = new LfsFsCache();

  constructor(
    private readonly descriptorService: LfsWorkspaceDescriptorService,
    private readonly fsMountPointConverterFn: (workspaceId: string) => string
  ) {}

  public async getFs(workspaceId: string): Promise<KieSandboxFs> {
    if (!(await this.descriptorService.get(workspaceId))) {
      throw new Error(`Can't get FS for non-existent descriptor '${workspaceId}'`);
    }
    return this.fsCache.getOrCreateFs(this.fsMountPointConverterFn(workspaceId));
  }
}
