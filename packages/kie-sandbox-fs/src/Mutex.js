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

const idb = require("@isomorphic-git/idb-keyval");

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

module.exports = class Mutex {
  constructor(dbname, storename) {
    this._id = Math.random();
    this._database = dbname;
    this._storename = storename;
    this._store = new idb.Store(this._database, this._storename);
    this._lock = null;
  }
  async has({ margin = 2000 } = {}) {
    if (this._lock && this._lock.holder === this._id) {
      const now = Date.now();
      if (this._lock.expires > now + margin) {
        return true;
      } else {
        return await this.renew();
      }
    } else {
      return false;
    }
  }
  // Returns true if successful
  async renew({ ttl = 5000 } = {}) {
    let success;
    await idb.update(
      "lock",
      (current) => {
        const now = Date.now();
        const expires = now + ttl;
        success = current && current.holder === this._id;
        this._lock = success ? { holder: this._id, expires } : current;
        return this._lock;
      },
      this._store
    );
    return success;
  }
  // Returns true if successful
  async acquire({ ttl = 5000 } = {}) {
    let success;
    let expired;
    let doubleLock;
    await idb.update(
      "lock",
      (current) => {
        const now = Date.now();
        const expires = now + ttl;
        expired = current && current.expires < now;
        success = current === undefined || expired;
        doubleLock = current && current.holder === this._id;
        this._lock = success ? { holder: this._id, expires } : current;
        return this._lock;
      },
      this._store
    );
    if (doubleLock) {
      throw new Error("Mutex double-locked");
    }
    return success;
  }
  // check at 10Hz, give up after 10 minutes
  async wait({ interval = 100, limit = 6000, ttl } = {}) {
    while (limit--) {
      if (await this.acquire({ ttl })) return true;
      await sleep(interval);
    }
    throw new Error("Mutex timeout");
  }
  // Returns true if successful
  async release({ force = false } = {}) {
    let success;
    let doubleFree;
    let someoneElseHasIt;
    await idb.update(
      "lock",
      (current) => {
        success = force || (current && current.holder === this._id);
        doubleFree = current === void 0;
        someoneElseHasIt = current && current.holder !== this._id;
        this._lock = success ? void 0 : current;
        return this._lock;
      },
      this._store
    );
    await idb.close(this._store);
    if (!success && !force) {
      if (doubleFree) throw new Error("Mutex double-freed");
      if (someoneElseHasIt) throw new Error("Mutex lost ownership");
    }
    return success;
  }
};
