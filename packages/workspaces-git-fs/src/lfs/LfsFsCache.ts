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
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";

export class LfsFsCache {
  private fsCache = new Map<string, KieSandboxFs>();

  public getOrCreateFs(fsMountPoint: string) {
    const fs = this.fsCache.get(fsMountPoint);
    if (fs) {
      return fs;
    }

    const newFs = new KieSandboxFs(fsMountPoint, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => new DexieBackend(fileDbName, fileStoreName),
      }) as any,
    });

    this.fsCache.set(fsMountPoint, newFs);
    return newFs;
  }
}
