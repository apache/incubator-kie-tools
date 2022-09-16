/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

export interface KieSandboxWorkspacesFs {
  promises: {
    rename(path: string, newPath: string): Promise<void>;
    readFile(path: string, options?: any): Promise<Uint8Array>;
    writeFile(path: any, data: any, options?: any): Promise<void>;
    unlink(path: any): Promise<void>;
    readdir(path: any, options?: any): Promise<string[]>;
    mkdir(path: any, mode?: any): Promise<void>;
    rmdir(path: any): Promise<void>;
    stat(path: any, options?: any): Promise<LfsStat>;
    lstat(path: any, options?: any): Promise<LfsStat>;
    readlink(path: any, options?: any): Promise<string>;
    symlink(target: any, path: any, type: any): Promise<void>;
    chmod(path: any, mode: any): Promise<void>;
  };
}
