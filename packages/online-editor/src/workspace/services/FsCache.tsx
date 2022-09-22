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

import { join } from "path";
import { EmscriptenFs, KieSandboxWorkspacesFs, LfsStat } from "./KieSandboxWorkspaceFs";

// comes from fsMain.fs
declare let FS: EmscriptenFs;

// comes from fsMain.fs
declare let IDBFS: EmscriptenFs & {
  syncfs(mount: { mountpoint: string }, mode: boolean, callback: (...args: any[]) => void): void;
};

export type FsSchema = Map<
  string,
  {
    ino: number;
    mode: number;
  }
>;

const encoder = new TextEncoder();

export class FsCache {
  private readonly schemasCache: Record<string, Promise<FsSchema>> = {};
  public readonly cache = new Map<string, Promise<KieSandboxWorkspacesFs>>();

  // get or create

  public getOrCreateFsSchema(fsMountPoint: string) {
    const fsSchema = this.schemasCache[fsMountPoint];
    if (fsSchema) {
      return fsSchema;
    }

    const newFsSchemaPromise = this.createFsSchema(fsMountPoint);
    this.schemasCache[fsMountPoint] = newFsSchemaPromise;
    return newFsSchemaPromise;
  }

  public getOrCreateFs(fsMountPoint: string) {
    const fs = this.cache.get(fsMountPoint);
    if (fs) {
      return fs;
    }

    const newFsPromise = this.createFs(fsMountPoint);
    this.cache.set(fsMountPoint, newFsPromise);
    return newFsPromise;
  }

  // fs schema

  private async createFsSchema(fsMountPoint: string): Promise<FsSchema> {
    console.debug(`Getting FS Schema for ${fsMountPoint}`);
    console.time(`Get FS Schema for ${fsMountPoint}`);

    this.initFsSchema(fsMountPoint);
    await this.syncFsSchema(true, fsMountPoint);

    try {
      const fsSchemaIndexJson = FS.readFile(
        fsSchemaJsonPath(fsMountPoint),
        toReadWriteFileOptions({ encoding: "utf8" })
      );
      return new Map(JSON.parse(fsSchemaIndexJson as string));
    } catch (e) {
      try {
        throwWasiErrorToNodeError("Reading FS Schema JSON", e);
      } catch (err) {
        if (err.code === "ENOENT") {
          // If there's no JSON to read, it means that this is a new FS.
          return new Map();
        } else {
          throw err;
        }
      }
    } finally {
      console.timeEnd(`Get FS Schema for ${fsMountPoint}`);
    }
  }

  private async syncFsSchema(isRestore: boolean, fsMountPoint: string) {
    return new Promise((res) => {
      try {
        IDBFS.syncfs({ mountpoint: fsSchemaDir(fsMountPoint) }, isRestore, res);
      } catch (e) {
        try {
          throwWasiErrorToNodeError(`Sync FS Schema '${fsMountPoint}' (${isRestore})`, e);
        } catch (err) {
          console.error(err);
          throw err;
        }
      }
    });
  }

  private initFsSchema(fsMountPoint: string) {
    try {
      FS.stat(fsSchemaDir(fsMountPoint));
      console.debug(`FS Schema already initiated for ${fsMountPoint}`);
    } catch (e) {
      FS.mkdir(fsSchemaDir(fsMountPoint));
      FS.mount(IDBFS, {}, fsSchemaDir(fsMountPoint));
    }
  }

  private async flushFsSchema(fsMountPoint: string) {
    const fsSchemaToFlush = encoder.encode(
      JSON.stringify(Array.from((await this.getOrCreateFsSchema(fsMountPoint)).entries()))
    );

    try {
      FS.writeFile(fsSchemaJsonPath(fsMountPoint), fsSchemaToFlush, toReadWriteFileOptions({ encoding: "utf8" }));
    } catch (e) {
      throwWasiErrorToNodeError("Writing FS Schema JSON", e);
    }

    await this.syncFsSchema(false, fsMountPoint);
  }

  // fs

  private async createFs(fsMountPoint: string) {
    const newFs: KieSandboxWorkspacesFs = {
      promises: {
        rename: async (path: string, newPath: string) => {
          try {
            // console.debug("rename", path, newPath);
            FS.rename(path, newPath);
            (await this.getOrCreateFsSchema(fsMountPoint)).delete(path);
            await newFs.promises.lstat(newPath);
          } catch (e) {
            throwWasiErrorToNodeError("rename", e, path, newPath);
          }
        },
        readFile: async (path: string, options: any) => {
          try {
            // console.debug("readFile", path, options);
            return FS.readFile(path, toReadWriteFileOptions(options));
          } catch (e) {
            throwWasiErrorToNodeError("readFile", e, path, options);
          }
        },
        writeFile: async (path: string, data: Uint8Array | string, options: any) => {
          try {
            // console.debug("writeFile", path, data, options);
            FS.writeFile(path, data, toReadWriteFileOptions(options));
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError("writeFile", e, path, data, options);
          }
        },
        unlink: async (path: string) => {
          try {
            // console.debug("unlink", path);
            FS.unlink(path);
            (await this.getOrCreateFsSchema(fsMountPoint)).delete(path);
          } catch (e) {
            throwWasiErrorToNodeError("unlink", e, path);
          }
        },
        readdir: async (path: string, options: any) => {
          try {
            // console.debug("readdir", path, options);
            return removeDotPaths(FS.readdir(path, options));
          } catch (e) {
            throwWasiErrorToNodeError("readdir", e, path, options);
          }
        },
        mkdir: async (path: string, mode?: number) => {
          try {
            // console.debug("mkdir", path, mode);
            FS.mkdir(path, mode);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError("mkdir", e, path, mode);
          }
        },
        rmdir: async (path: string) => {
          try {
            // console.debug("rmdir", path);
            FS.rmdir(path);
            (await this.getOrCreateFsSchema(fsMountPoint)).delete(path);
          } catch (e) {
            throwWasiErrorToNodeError("rmdir", e, path);
          }
        },
        stat: async (path: string) => {
          try {
            // console.debug("stat", path);
            return await toLfsStat(this, fsMountPoint, path, FS.stat(path));
          } catch (e) {
            throwWasiErrorToNodeError("stat", e, path);
          }
        },
        lstat: async (path: string) => {
          try {
            // console.debug("lstat", path);
            return await toLfsStat(this, fsMountPoint, path, FS.stat(path));
          } catch (e) {
            throwWasiErrorToNodeError("lstat", e, path);
          }
        },
        readlink: async (path: string, options: any) => {
          try {
            // console.debug("readlink", path, options);
            return FS.readlink(path);
          } catch (e) {
            throwWasiErrorToNodeError("readlink", e, path, options);
          }
        },
        symlink: async (target: string, path: string, type: any) => {
          try {
            // console.debug("symlink", target, path, type);
            FS.symlink(target, path);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError("symlink", e, target, path, type);
          }
        },
        chmod: async (path: string, mode: number) => {
          try {
            // console.debug("chmod", path, mode);
            FS.chmod(path, mode);
            await newFs.promises.lstat(path);
          } catch (e) {
            throwWasiErrorToNodeError("chmod", e, path, mode);
          }
        },
      },
    };

    console.time(`Bring FS to memory - ${fsMountPoint}`);
    console.debug(`Bringing FS to memory - ${fsMountPoint}`);
    await this.initFs(fsMountPoint);
    await this.syncFs(true, fsMountPoint);
    await this.getOrCreateFsSchema(fsMountPoint);
    console.timeEnd(`Bring FS to memory - ${fsMountPoint}`);

    return newFs;
  }

  private async syncFs(isRestore: boolean, fsMountPoint: string) {
    await new Promise((res) => {
      try {
        IDBFS.syncfs({ mountpoint: fsMountPoint }, isRestore, res);
      } catch (e) {
        try {
          throwWasiErrorToNodeError(`Sync FS '${fsMountPoint}' (${isRestore})`, e);
        } catch (err) {
          console.error(err);
          throw err;
        }
      }
    });
  }

  private initFs(fsMountPoint: string) {
    console.time(`Init FS - ${fsMountPoint}`);
    console.debug(`Initiating FS - ${fsMountPoint}`);
    try {
      FS.mkdir(fsMountPoint);
      FS.mount(IDBFS, {}, fsMountPoint);
      this.initFsSchema(fsMountPoint);
    } catch (e) {
      try {
        throwWasiErrorToNodeError(`Init FS ${fsMountPoint}`, e, fsMountPoint);
      } catch (err) {
        console.error(`Error initiating FS - ${fsMountPoint}`);
        console.error(err);
      }
    } finally {
      console.timeEnd(`Init FS - ${fsMountPoint}`);
    }
  }

  public deinitFs(fsMountPoint: string) {
    console.debug(`Deinitiating FS - ${fsMountPoint}`);
    console.time(`Deinit FS - ${fsMountPoint}`);
    this.cache.delete(fsMountPoint);
    try {
      FS.unmount(fsMountPoint);
      FS.rmdir(fsMountPoint);
    } catch (e) {
      try {
        throwWasiErrorToNodeError(`Deinit FS ${fsMountPoint}`, e, fsMountPoint);
      } catch (err) {
        console.error(`Error deinitiating FS - ${fsMountPoint}`);
        console.error(err);
      }
    } finally {
      console.timeEnd(`Deinit FS - ${fsMountPoint}`);
    }
  }

  public async flushFs(fsMountPoint: string) {
    console.time(`Flush FS - ${fsMountPoint}`);
    console.debug(`Flushing FS - ${fsMountPoint}`);
    await this.syncFs(false, fsMountPoint);
    await this.flushFsSchema(fsMountPoint);
    console.timeEnd(`Flush FS - ${fsMountPoint}`);
  }
}

// schema structure

export function fsSchemaDir(fsMountPoint: string) {
  return `${fsMountPoint}_inos`; // FIXME: Rename to _schema
}

function fsSchemaJsonPath(fsMountPoint: string) {
  return join(fsSchemaDir(fsMountPoint), "index.json"); // FIXME: Rename to fsSchema.json
}

// emscripten-fs adaptation to lightning-fs

// Reference: https://github.com/isomorphic-git/lightning-fs#fswritefilefilepath-data-opts-cb
function toReadWriteFileOptions(options: any) {
  return typeof options === "string" ? { encoding: options } : options;
}

// Not doing that causes a loop during `clone`s.
function removeDotPaths(paths: string[]) {
  return paths.slice(2); // Remove "." and ".." entries
}

// Reference: https://github.com/isomorphic-git/lightning-fs#fsstatfilepath-opts-cb
async function toLfsStat(fsCache: FsCache, fsMountPoint: string, path: string, stat: any): Promise<LfsStat> {
  // isomorphic-git expects that `ino` and `mode` never change once they are created,
  // however, IDBFS does not keep `ino`s consistent between syncfs calls.
  // We need to persist an index containing the `ino`s and `mode`s for all files.
  // Luckily this is very cheap to do, as long as we keep the `fsSchema[fsMountPoint]` map up-to-date.
  const fsSchema = await fsCache.getOrCreateFsSchema(fsMountPoint);
  const perpetualStat = fsSchema.set(path, fsSchema.get(path) ?? { ino: stat.ino, mode: stat.mode }).get(path)!;

  const isDir = FS.isDir(perpetualStat.mode);
  const isFile = FS.isFile(perpetualStat.mode);
  const isLink = FS.isLink(perpetualStat.mode);

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
function throwWasiErrorToNodeError(id: string, e: any, ...args: any[]): never {
  switch (e.errno) {
    case 20:
      throw { id, code: "EEXIST", message: "EEXIST", args };
    case 44:
      throw { id, code: "ENOENT", message: "ENOENT", args };
    case 54:
      throw { id, code: "ENOTDIR", message: "ENOTDIR", args };
    case 55:
      throw { id, code: "ENOTEMPTY", message: "ENOTEMPTY", args };
    case 73:
      throw { id, code: "ETIMEDOUT", message: "ETIMEDOUT", args };
    default:
      throw { id, e, code: "UNKNOWN", args };
  }
}
