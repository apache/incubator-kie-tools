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

declare module "@isomorphic-git/lightning-fs/src/DefaultBackend" {
  import LightningFS from "@isomorphic-git/lightning-fs";

  export default class DefaultBackend implements LightningFS.FSBackend {
    constructor(args: { idbBackendDelegate: (fileDbName: string, fileStoreName: string) => any }) {}
  }
}

declare module "@isomorphic-git/lightning-fs/src/DexieBackend" {
  export default class DexieBackend {
    _dexie: any;
    _storename: any;
    constructor(dbName: string, storeName: string) {}
    async saveSuperblock(superblock: any);
    async loadSuperblock();
    async readFile(inode: string);
    async readFileBulk(inodeBulk: string[]);
    async writeFile(inode: string, data: any);
    async writeFileBulk(inodeBulk: string[], dataBulk: any[]);
    async unlink(inode: string);
    async unlinkBulk(inodeBulk: string[]);
    async wipe();
    async close();
  }
}
