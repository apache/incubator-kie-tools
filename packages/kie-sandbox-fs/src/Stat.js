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

module.exports = class Stat {
  constructor(stats) {
    this.type = stats.type;
    this.mode = stats.mode;
    this.size = stats.size;
    this.ino = stats.ino;
    this.mtimeMs = stats.mtimeMs;
    this.ctimeMs = stats.ctimeMs || stats.mtimeMs;
    this.uid = 1;
    this.gid = 1;
    this.dev = 1;
  }
  isFile() {
    return this.type === "file";
  }
  isDirectory() {
    return this.type === "dir";
  }
  isSymbolicLink() {
    return this.type === "symlink";
  }
};
