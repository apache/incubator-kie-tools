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

const { Dexie } = require("dexie");

module.exports = class DexieBackend {
  constructor(dbname, storename) {
    const stores = {};
    stores[storename] = "";
    this._dexie = new Dexie(dbname);
    this._dexie.version(1).stores(stores);
    this._storename = storename;
  }
  async saveSuperblock(superblock) {
    await this._dexie.open();
    return this._dexie[this._storename].put(superblock, "!root");
  }
  async loadSuperblock() {
    await this._dexie.open();
    return this._dexie[this._storename].get("!root");
  }
  async readFile(inode) {
    await this._dexie.open();
    return this._dexie[this._storename].get(inode);
  }
  async readFileBulk(inodeBulk) {
    await this._dexie.open();
    return this._dexie[this._storename].bulkGet(inodeBulk);
  }
  async writeFile(inode, data) {
    await this._dexie.open();
    return this._dexie[this._storename].put(data, inode);
  }
  async writeFileBulk(inodeBulk, dataBulk) {
    await this._dexie.open();
    return this._dexie[this._storename].bulkPut(dataBulk, inodeBulk);
  }
  async unlink(inode) {
    await this._dexie.open();
    return this._dexie[this._storename].delete(inode);
  }
  async unlinkBulk(inodeBulk) {
    await this._dexie.open();
    return this._dexie[this._storename].bulkDelete(inodeBulk);
  }
  async wipe() {
    await this._dexie.open();
    return this._dexie[this._storename].clear();
  }
  async close() {
    return this._dexie.close();
  }
};
