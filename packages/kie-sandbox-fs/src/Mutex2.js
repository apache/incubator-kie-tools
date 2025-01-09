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

module.exports = class Mutex {
  constructor(name) {
    this._id = Math.random();
    this._database = name;
    this._has = false;
    this._release = null;
  }
  async has() {
    return this._has;
  }
  // Returns true if successful
  async acquire() {
    return new Promise((resolve) => {
      navigator.locks.request(this._database + "_lock", { ifAvailable: true }, (lock) => {
        this._has = !!lock;
        resolve(!!lock);
        return new Promise((resolve) => {
          this._release = resolve;
        });
      });
    });
  }
  // Returns true if successful, gives up after 10 minutes
  async wait({ timeout = 600000 } = {}) {
    return new Promise((resolve, reject) => {
      const controller = new AbortController();
      setTimeout(() => {
        controller.abort();
        reject(new Error("Mutex timeout"));
      }, timeout);
      navigator.locks.request(this._database + "_lock", { signal: controller.signal }, (lock) => {
        this._has = !!lock;
        resolve(!!lock);
        return new Promise((resolve) => {
          this._release = resolve;
        });
      });
    });
  }
  // Returns true if successful
  async release({ force = false } = {}) {
    this._has = false;
    if (this._release) {
      this._release();
    } else if (force) {
      navigator.locks.request(this._database + "_lock", { steal: true }, (lock) => true);
    }
  }
};
