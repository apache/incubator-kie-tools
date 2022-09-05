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

export class InMemoryBackend {
  constructor(public fs = new Map<string, any>()) {}

  async saveSuperblock(superblock: any) {
    this.fs.set("!root", superblock);
  }

  async loadSuperblock() {
    return this.fs.get("!root");
  }

  async readFile(inode: string) {
    return this.fs.get(inode);
  }

  async writeFile(inode: string, data: any) {
    this.fs.set(inode, data);
  }

  async unlink(inode: string) {
    this.fs.delete(inode);
  }

  async wipe() {
    this.fs = new Map();
  }

  async close() {}

  async readFileBulk(inodeBulk: string[]) {
    const ret = [];
    for (const ino of inodeBulk) {
      ret.push(this.fs.get(ino));
    }
    return ret;
  }

  async writeFileBulk(inodeBulk: string[], dataBulk: any[]) {
    for (let i = 0; i < inodeBulk.length; i++) {
      this.fs.set(inodeBulk[i], dataBulk[i]);
    }
  }

  async unlinkBulk(inodeBulk: string[]) {
    for (const ino of inodeBulk) {
      this.fs.delete(ino);
    }
  }
}
