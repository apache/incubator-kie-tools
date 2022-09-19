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

import { EmscriptenFs, KieSandboxWorkspacesFs, LfsStat } from "./KieSandboxWorkspaceFs";

const inos: Record<string, Map<string, { ino: number; mode: number }>> = {};

// comes from fsMain.fs
declare let FS: EmscriptenFs;

// comes from fsMain.fs
declare let IDBFS: EmscriptenFs & {
  syncfs(mount: { mountpoint: string }, mode: boolean, callback: (...args: any[]) => void): void;
};

export class FsCache {
  private fsCache = new Map<string, KieSandboxWorkspacesFs>();

  public async getOrCreateFs(fsMountPoint: string) {
    const fs = this.fsCache.get(fsMountPoint);
    if (fs) {
      return fs;
    }

    const newFs: KieSandboxWorkspacesFs = {
      promises: {
        rename: async (path: string, newPath: string) => {
          try {
            // console.debug("rename", path, newPath)
            return FS.rename(path, newPath);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, newPath);
          }
        },
        readFile: async (path: string, options: any) => {
          try {
            // console.debug("readFile",path, options)
            return FS.readFile(path, toReadWriteFileOptions(options));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        writeFile: async (path: string, data: Uint8Array | string, options: any) => {
          try {
            // console.debug("writeFile",path, data, options)
            FS.writeFile(path, data, toReadWriteFileOptions(options));
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, data, options);
          }
        },
        unlink: async (path: string) => {
          try {
            // console.debug("unlink",path)
            FS.unlink(path);
            inos[fsMountPoint].delete(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        readdir: async (path: string, options: any) => {
          try {
            // console.debug("readdir",path, options)
            return removeDotPaths(FS.readdir(path, options));
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        mkdir: async (path: string, mode?: number) => {
          try {
            // console.debug("mkdir",path, mode)
            FS.mkdir(path, mode);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, mode);
          }
        },
        rmdir: async (path: string) => {
          try {
            // console.debug("rmdir",path)
            FS.rmdir(path);
            inos[fsMountPoint].delete(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        stat: async (path: string) => {
          try {
            // console.debug("stat",path, options)
            return toLfsStat(fsMountPoint, path, FS.stat(path));
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        lstat: async (path: string) => {
          try {
            // console.debug("lstat",path, options)
            return toLfsStat(fsMountPoint, path, FS.stat(path));
          } catch (e) {
            throwWasiErrorToNodeError(e, path);
          }
        },
        readlink: async (path: string, options: any) => {
          try {
            // console.debug("readlink",path, options)
            return FS.readlink(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, options);
          }
        },
        symlink: async (target: string, path: string, type: any) => {
          try {
            // console.debug("symlink",target, path, type)
            FS.symlink(target, path);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, target, path, type);
          }
        },
        chmod: async (path: string, mode: number) => {
          try {
            // console.debug("chmod",path, mode)
            FS.chmod(path, mode);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError(e, path, mode);
          }
        },
      },
    };

    console.time(`Bring FS to memory - ${fsMountPoint}`);
    console.debug(`Bringing FS to memory - ${fsMountPoint}`);
    await initFs(fsMountPoint);
    await restoreFs(newFs, fsMountPoint);
    console.timeEnd(`Bring FS to memory - ${fsMountPoint}`);

    // Keep only one FS in memory at the time
    if (this.fsCache.size > 0) {
      const previouslyCachedFss = [...this.fsCache.keys()];
      this.fsCache.clear();
      for (const mountPoint of previouslyCachedFss) {
        await deinitFs(mountPoint);
      }
    }

    this.fsCache.set(fsMountPoint, this.fsCache.get(fsMountPoint) ?? newFs);
    return this.fsCache.get(fsMountPoint)!;
  }
}

async function syncfs(isRestore: boolean, fsMountPoint: string) {
  await new Promise((res) => {
    IDBFS.syncfs({ mountpoint: fsMountPoint }, isRestore, res);
  });
  await new Promise((res) => {
    IDBFS.syncfs({ mountpoint: inosDir(fsMountPoint) }, isRestore, res);
  });
}

export async function flushFs(fs: KieSandboxWorkspacesFs, fsMountPoint: string) {
  const all = new TextEncoder().encode(JSON.stringify(Array.from(inos[fsMountPoint].entries())));
  await fs.promises.writeFile(inosIndexJsonPath(fsMountPoint), all, { encoding: "utf-8" });
  return syncfs(false, fsMountPoint);
}

export async function initFs(fsMountPoint: string) {
  console.time(`Init FS - ${fsMountPoint}`);
  console.debug(`Initiating FS - ${fsMountPoint}`);
  try {
    FS.mkdir(fsMountPoint);
    FS.mount(IDBFS, {}, fsMountPoint);
    FS.mkdir(inosDir(fsMountPoint));
    FS.mount(IDBFS, {}, inosDir(fsMountPoint));
  } catch (e) {
    try {
      throwWasiErrorToNodeError(e, fsMountPoint);
    } catch (e) {
      console.error(`Error initiating FS - ${fsMountPoint}`);
      console.error(e);
    }
  } finally {
    console.timeEnd(`Init FS - ${fsMountPoint}`);
  }
  inos[fsMountPoint] = new Map();
}

export async function deinitFs(fsMountPoint: string) {
  console.debug(`Deinitiating FS - ${fsMountPoint}`);
  console.time(`Deinit FS - ${fsMountPoint}`);
  delete inos[fsMountPoint];
  try {
    FS.unmount(inosDir(fsMountPoint));
    FS.rmdir(inosDir(fsMountPoint));
    FS.unmount(fsMountPoint);
    FS.rmdir(fsMountPoint);
  } catch (e) {
    try {
      throwWasiErrorToNodeError(e, fsMountPoint);
    } catch (e) {
      console.error(`Error deinitiating FS - ${fsMountPoint}`);
      console.error(e);
    }
  } finally {
    console.timeEnd(`Deinit FS - ${fsMountPoint}`);
  }
}

export async function restoreFs(fs: KieSandboxWorkspacesFs, fsMountPoint: string) {
  await syncfs(true, fsMountPoint);

  let inosIndexJson;
  try {
    inosIndexJson = await fs.promises.readFile(inosIndexJsonPath(fsMountPoint), { encoding: "utf8" });
  } catch (e) {
    // ENOENT
    inosIndexJson = "[]";
  }

  inos[fsMountPoint] = new Map(JSON.parse(inosIndexJson as string));
}

export function inosDir(fsMountPoint: string) {
  return fsMountPoint + "_inos";
}

function inosIndexJsonPath(fsMountPoint: string) {
  return inosDir(fsMountPoint) + "/index.json";
}

// Reference: https://github.com/isomorphic-git/lightning-fs#fswritefilefilepath-data-opts-cb
function toReadWriteFileOptions(options: any) {
  return typeof options === "string" ? { encoding: options } : options;
}

// Not doing that causes a loop during `clone`s.
function removeDotPaths(paths: string[]) {
  return paths.slice(2); // Remove "." and ".." entries
}

// Reference: https://github.com/isomorphic-git/lightning-fs#fsstatfilepath-opts-cb
function toLfsStat(fsMountPoint: string, path: string, stat: any): LfsStat {
  // isomorphic-git expects that `ino` and `mode` never change once they are created,
  // however, IDBFS does not keep `ino`s consistent between syncfs calls.
  // We need to persist an index containing the `ino`s and `mode`s for all files.
  // Luckily this is very cheap to do, as long as we keep the `inos[fsMountPoint]` map up-to-date.
  const perpetualStat = inos[fsMountPoint]
    .set(path, inos[fsMountPoint].get(path) ?? { ino: stat.ino, mode: stat.mode })
    .get(path)!;

  const isDir: boolean = FS.isDir(perpetualStat.mode);
  const isFile: boolean = FS.isFile(perpetualStat.mode);
  const isLink: boolean = FS.isLink(perpetualStat.mode);

  return {
    mode: perpetualStat.mode,
    size: -1,
    ino: perpetualStat.ino,
    mtimeMs: stat.mtime,
    ctimeMs: stat.ctime,
    uid: 1,
    gid: 1,
    dev: 1,
    isDirectory: () => isDir,
    isFile: () => isFile,
    isSymbolicLink: () => isLink,
  };
}

// Reference: https://github.com/emscripten-core/emscripten/blob/main/system/include/wasi/api.h
function throwWasiErrorToNodeError(e: any, ...args: any[]): never {
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
