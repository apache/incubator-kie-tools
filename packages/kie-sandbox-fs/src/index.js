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

const once = require("just-once");

const PromisifiedKieSandboxFs = require("./PromisifiedFS");

function wrapCallback(opts, cb) {
  if (typeof opts === "function") {
    cb = opts;
  }
  cb = once(cb);
  const resolve = (...args) => cb(null, ...args);
  return [resolve, cb];
}

module.exports = class KieSandboxFs {
  constructor(...args) {
    this.promises = new PromisifiedKieSandboxFs(...args);
    // Needed so things don't break if you destructure fs and pass individual functions around
    this.init = this.init.bind(this);
    this.deactivate = this.deactivate.bind(this);
    this.readFile = this.readFile.bind(this);
    this.readFileBulk = this.readFileBulk.bind(this);
    this.writeFile = this.writeFile.bind(this);
    this.writeFileBulk = this.writeFileBulk.bind(this);
    this.unlink = this.unlink.bind(this);
    this.unlinkBulk = this.unlinkBulk.bind(this);
    this.readdir = this.readdir.bind(this);
    this.mkdir = this.mkdir.bind(this);
    this.rmdir = this.rmdir.bind(this);
    this.rename = this.rename.bind(this);
    this.stat = this.stat.bind(this);
    this.lstat = this.lstat.bind(this);
    this.readlink = this.readlink.bind(this);
    this.symlink = this.symlink.bind(this);
    this.backFile = this.backFile.bind(this);
    this.du = this.du.bind(this);
  }
  init(name, options) {
    return this.promises.init(name, options);
  }
  deactivate() {
    return this.promises.deactivate();
  }
  readFile(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.readFile(filepath, opts).then(resolve).catch(reject);
  }
  readFileBulk(filepaths, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.readFileBulk(filepaths, opts).then(resolve).catch(reject);
  }
  writeFile(filepath, data, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.writeFile(filepath, data, opts).then(resolve).catch(reject);
  }
  writeFileBulk(filepaths, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.writeFileBulk(filepaths, opts).then(resolve).catch(reject);
  }
  unlink(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.unlink(filepath, opts).then(resolve).catch(reject);
  }
  unlinkBulk(filepaths, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.unlinkBulk(filepaths, opts).then(resolve).catch(reject);
  }
  readdir(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.readdir(filepath, opts).then(resolve).catch(reject);
  }
  mkdir(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.mkdir(filepath, opts).then(resolve).catch(reject);
  }
  rmdir(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.rmdir(filepath, opts).then(resolve).catch(reject);
  }
  rename(oldFilepath, newFilepath, cb) {
    const [resolve, reject] = wrapCallback(cb);
    this.promises.rename(oldFilepath, newFilepath).then(resolve).catch(reject);
  }
  stat(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.stat(filepath).then(resolve).catch(reject);
  }
  lstat(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.lstat(filepath).then(resolve).catch(reject);
  }
  readlink(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.readlink(filepath).then(resolve).catch(reject);
  }
  symlink(target, filepath, cb) {
    const [resolve, reject] = wrapCallback(cb);
    this.promises.symlink(target, filepath).then(resolve).catch(reject);
  }
  backFile(filepath, opts, cb) {
    const [resolve, reject] = wrapCallback(opts, cb);
    this.promises.backFile(filepath, opts).then(resolve).catch(reject);
  }
  du(filepath, cb) {
    const [resolve, reject] = wrapCallback(cb);
    this.promises.du(filepath).then(resolve).catch(reject);
  }
};
