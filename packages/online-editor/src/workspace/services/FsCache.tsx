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

import type KieSandboxFs from "@kie-tools/kie-sandbox-fs";

const inos: any = {};
declare let FS: any;
declare let IDBFS: any;

export class FsCache {
  private fsCache = new Map<string, any>();
  public async getOrCreateFs(workspaceId: string) {
    const fs = this.fsCache.get(workspaceId);
    if (fs) {
      return fs;
    }

    const newFs = {
      promises: {
        readFile: async (path: string, options: any) => {
          try {
            // console.log("readFile",path, options)
            return FS.readFile(path, toReadWriteFileOptions(options));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        writeFile: async (path: any, data: any, options: any) => {
          try {
            // console.log("writeFile",path, data, options)
            const ret = await FS.writeFile(path, data, toReadWriteFileOptions(options));
            await newFs.promises.lstat(path, {});
            return ret;
          } catch (e) {
            throwWasiErrorToNodeError(e, path, data, options);
          }
        },
        unlink: async (path: any) => {
          try {
            // console.log("unlink",path)
            // FIXME: Update inos[dir]
            return FS.unlink(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        readdir: async (path: any, options: any) => {
          try {
            // console.log("readdir",path, options)
            return removeDotPaths(FS.readdir(path, options));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        mkdir: async (path: any, mode: any) => {
          try {
            // console.log("mkdir",path, mode)
            // FIXME: Update inos[dir]
            return FS.mkdir(path, mode);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, mode);
          }
        },
        rmdir: async (path: any) => {
          try {
            // console.log("rmdir",path)
            // FIXME: Update inos[dir]
            return FS.rmdir(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        stat: async (path: any, options: any) => {
          try {
            // console.log("stat",path, options)
            return toLfsStat(workspaceId, path, FS.stat(path, options));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        lstat: async (path: any, options: any) => {
          try {
            // console.log("lstat",path, options)
            return toLfsStat(workspaceId, path, FS.stat(path, true));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        readlink: async (path: any, options: any) => {
          try {
            // console.log("readlink",path, options)
            return FS.readlink(path, options);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        symlink: async (target: any, path: any, type: any) => {
          try {
            // console.log("symlink",target, path, type)
            // FIXME: Update inos[dir]
            return FS.symlink(target, path, type);
          } catch (e) {
            throwWasiErrorToNodeError(e, target, path, type);
          }
        },
        chmod: async (path: any, mode: any) => {
          try {
            // console.log("chmod",path, mode)
            // FIXME: Update inos[dir]
            return FS.chmod(path, mode);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, mode);
          }
        },
      },
    };

    await initFs(workspaceId);
    await restoreFs(newFs as any, workspaceId);

    this.fsCache.set(workspaceId, newFs as any);
    return newFs;
  }
}
async function syncfs(isRestore: any, workspaceId: string) {
  await new Promise((res) => {
    IDBFS.syncfs({ mountpoint: workspaceId }, isRestore, res);
  });
  await new Promise((res) => {
    IDBFS.syncfs({ mountpoint: inosDir(workspaceId) }, isRestore, res);
  });
}

export async function flushFs(fs: KieSandboxFs, workspaceId: string) {
  const all = new TextEncoder().encode(JSON.stringify(Array.from(inos[workspaceId].entries())));
  fs.promises.writeFile(inosIndexJsonPath(workspaceId), all, { encoding: "utf-8" } as any);
  return syncfs(false, workspaceId);
}

export async function initFs(workspaceId: string) {
  FS.mkdir(workspaceId);
  FS.mount(IDBFS, {}, workspaceId);
  FS.mkdir(inosDir(workspaceId));
  FS.mount(IDBFS, {}, inosDir(workspaceId));
  inos[workspaceId] = new Map();
}

export async function deinitFs(workspaceId: string) {
  delete inos[workspaceId];
  FS.unmount(inosDir(workspaceId));
  FS.rmdir(inosDir(workspaceId));
  FS.unmount(workspaceId);
  FS.rmdir(workspaceId);
}

export async function restoreFs(fs: KieSandboxFs, workspaceId: string) {
  await syncfs(true, workspaceId);

  let inosIndexJson;
  try {
    inosIndexJson = await fs.promises.readFile(inosIndexJsonPath(workspaceId), { encoding: "utf8" });
  } catch (e) {
    // ENOENT
    inosIndexJson = "[]";
  }

  inos[workspaceId] = new Map(JSON.parse(inosIndexJson as string));
}

export function inosDir(workspaceId: string) {
  return workspaceId + "_inos";
}

export function inosIndexJsonPath(workspaceId: string) {
  return inosDir(workspaceId) + "/index.json";
}

// Reference: https://github.com/isomorphic-git/lightning-fs#fswritefilefilepath-data-opts-cb
function toReadWriteFileOptions(options: any) {
  return typeof options === "string" ? { encoding: options } : options;
}

// Not doing that causes a loop during `clone`s.
function removeDotPaths(a: any) {
  return a.slice(2); // Remove "." and ".." entries
}

// Reference: https://github.com/isomorphic-git/lightning-fs#fsstatfilepath-opts-cb
function toLfsStat(workspaceId: string, path: any, stat: any) {
  // isomorphic-git expects that `ino` and `mode` never change once they are created,
  // however, IDBFS does not keep `ino`s consistent between syncfs calls.
  //
  // We need to persist an index containig the `ino`s and `mode`s for all files.
  // Luckily this is very cheap to do, as long as we kepe the `inos[dir]` map up-to-date.
  const perpetualStat = inos[workspaceId]
    .set(path, inos[workspaceId].get(path) ?? { ino: stat.ino, mode: stat.mode })
    .get(path);

  const isDir = FS.isDir(perpetualStat.mode);
  const isFile = FS.isFile(perpetualStat.mode);
  const isLink = FS.isLink(perpetualStat.mode);

  return {
    mode: perpetualStat.mode,
    size: -1,
    ino: perpetualStat.ino,
    mtimeMs: 0,
    ctimeMs: 0,
    uid: 1,
    gid: 1,
    dev: 1,
    isDirectory: () => isDir,
    isFile: () => isFile,
    isSymbolicLink: () => isLink,
  };
}

// Reference: https://github.com/emscripten-core/emscripten/blob/main/system/include/wasi/api.h
function throwWasiErrorToNodeError(e: any, ...args: any[]) {
  console.info(e);
  switch (e.errno) {
    case 20:
      throw { code: "EEXIST", message: "EEXIST", args };
    case 44:
      throw { code: "ENOENT", message: "ENOENT", args };
    case 54:
      throw { code: "ENOTDIR", message: "ENOTDIR", args };
    case 55:
      throw { code: "ENOTEMPTY", message: "ENOTEMPTY", args };
    case 73:
      throw { code: "ETIMEDOUT", message: "ETIMEDOUT", args };
    default:
      throw { e, code: "UNKNOWN", args };
  }
}
