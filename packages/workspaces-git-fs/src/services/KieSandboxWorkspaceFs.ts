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

export interface LfsStat {
  mode: number;
  size: number;
  ino: number;
  mtimeMs: number;
  ctimeMs: number;
  uid: 1;
  gid: 1;
  dev: 1;
  isDirectory: () => boolean;
  isFile: () => boolean;
  isSymbolicLink: () => boolean;
}

export interface EmscriptenFsStat {
  dev: number;
  ino: number;
  mode: number;
  nlink: number;
  uid: number;
  gid: number;
  rdev: number;
  size: number;
  atime: number;
  mtime: number;
  ctime: number;
  blksize: number;
  blocks: number;
}

export interface KieSandboxWorkspacesFs {
  promises: {
    rename(path: string, newPath: string): Promise<void>;
    readFile(path: string, options?: any): Promise<Uint8Array | string>;
    writeFile(path: string, data: Uint8Array | string, options?: any): Promise<void>;
    unlink(path: string): Promise<void>;
    readdir(path: string, options?: any): Promise<string[]>;
    mkdir(path: string, mode?: number): Promise<void>;
    rmdir(path: string): Promise<void>;
    stat(path: string, options?: any): Promise<LfsStat>;
    lstat(path: string): Promise<LfsStat>;
    readlink(path: string, options?: any): Promise<string>;
    symlink(target: string, path: string, type: any): Promise<void>;
    chmod(path: string, mode: number): Promise<void>;
  };
}

export interface EmscriptenFs {
  rename(path: string, newPath: string): void;
  readFile(path: string, options?: any): Uint8Array | string;
  writeFile(path: string, data: Uint8Array | string, options?: any): void;
  unlink(path: string): void;
  readdir(path: string, options?: any): string[];
  mkdir(path: string, mode?: number): void;
  rmdir(path: string): void;
  stat(path: string): EmscriptenFsStat;
  lstat(path: string): EmscriptenFsStat;
  readlink(path: string): string;
  symlink(target: string, path: string): void;
  chmod(path: string, mode: number): void;
  mount(fs: any, opts: any, mountpoint: string): void;
  unmount(mountpoint: string): void;
  isDir(mode: number): boolean;
  isFile(mode: number): boolean;
  isLink(mode: number): boolean;
}
