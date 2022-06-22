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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";

export class FsCache {
  private fsCache = new Map<string, KieSandboxFs>();
  public getOrCreateFs(id: string) {
    const fs = this.fsCache.get(id);
    if (fs) {
      return fs;
    }

    const newFs = new KieSandboxFs(id, {
      backend: new DefaultBackend({
        idbBackendDelegate: (dbName, storeName) => new DexieBackend(dbName, storeName),
      }) as any,
    });

    this.fsCache.set(id, newFs);
    return newFs;
  }
}
