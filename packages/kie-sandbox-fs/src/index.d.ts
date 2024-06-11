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

import { Stats } from "fs";

export default class KieSandboxFs {
  /** Collection of FS Operations that returns Promises */
  promises: KieSandboxFs.PromisifiedFS;

  /** Initialise File System Backend */
  init(name: string, opt?: KieSandboxFs.FSConstructorOptions): void;
  /** Activates Cache */
  activate(): Promise<void>;
  /** Deactivates Cache */
  deactivate(): Promise<void>;
  /** Reads File Content from Disk */
  readFile(filePath: string, opts?: KieSandboxFs.ReadFileOpts): Promise<Uint8Array>;
  /** Reads File Bulk from Disk */
  readFileBulk?: (filePaths: string[], opts?: ReadFileOpts) => Promise<[filePath: string, data: Uint8Array][]>;
  /** Writes File Content to Disk */
  writeFile(filePath: string, data: Uint8Array, opts?: KieSandboxFs.WriteFileOpts): Promise<void>;
  /** (Optional) Writes File Bulk to Disk */
  writeFileBulk?: (files: [filePath: string, data: Uint8Array][], opts?: WriteFileOpts) => Promise<void>;
  /** Remove File from Disk */
  unlink(filePath: string): Promise<void>;
  /** Remove File Bulk from Disk */
  unlinkBulk?: (filePaths: string[]) => Promise<void>;
  /** Lists all files and sub-directory in given directory Path */
  readdir(filePath: string): Promise<string[]>;
  /** Creates Directory in Disk for given Path */
  mkdir(filePath: string, opts?: KieSandboxFs.DirOpts): Promise<void>;
  /** Remove Directory from Disk */
  rmdir(filePath: string): Promise<void>;
  /** Rename File Name in Disk */
  rename(oldFilepath: string, newFilepath: string): Promise<void>;
  /** Unix File Stat from Disk */
  stat(filePath: string): Promise<Stats>;
  /** Unix File Stat from Disk */
  lstat(filePath: string): Promise<Stats>;
  /** Read Content of file targeted by a Symbolic Link */
  readlink(filePath: string): Promise<string>;
  /** Create Symbolic Link to a target file */
  symlink(target: string, filePath: string): Promise<void>;

  constructor(id: string | number | symbol, opt?: KieSandboxFs.FSConstructorOptions);
}

declare namespace KieSandboxFs {
  interface FSConstructorOptions {
    /** Delete the database and start with an empty filesystem. Default: false */
    wipe?: boolean;
    /** Customize the database name */
    fileDbName?: string;
    /** Customize the store name */
    fileStoreName?: string;
    /** Customize the database name for the lock mutex */
    lockDbName?: string;
    /** Customize the store name for the lock mutex */
    lockStoreName?: string;
    /** If true, avoids mutex contention during initialization. Default: false */
    defer?: boolean;
    /**
     * If present, none of the other arguments(except `defer`) have any effect,
     * and instead of using the normal KieSandboxFs stuff, KieSandboxFs acts as a wrapper around the provided custom backend.
     */
    backend?: FSBackend;
  }

  interface ReadFileOpts {
    /** Encoding of Data */
    encoding: "utf8";
  }

  interface WriteFileOpts {
    /** Encoding of Data */
    encoding?: "utf8" | undefined;
    /** Unix Octet Represenation of File Mode */
    mode: number;
  }

  interface DirOpts {
    /** Unix Octet Represenation of File Mode */
    mode?: number | undefined;
  }

  interface FSBackend {
    /** Initialise File System Backend */
    init(name: string, opt?: FSConstructorOptions): void;
    /** Activates Cache */
    activate(): Promise<void>;
    /** Deactivates Cache */
    deactivate(): Promise<void>;
    /** Reads File Content from Disk */
    readFile(filePath: string, opts: ReadFileOpts): Promise<Uint8Array>;
    /** Reads File Bulk from Disk */
    readFileBulk?: (filePaths: string[], opts?: ReadFileOpts) => Promise<[filePath: string, data: Uint8Array][]>;
    /** Writes File Content to Disk */
    writeFile(filePath: string, data: Uint8Array, opts: WriteFileOpts): Promise<void>;
    /** Writes File Bulk to Disk */
    writeFileBulk?: (files: [filePath: string, data: Uint8Array][], opts?: WriteFileOpts) => Promise<void>;
    /** Remove File from Disk */
    unlink(filePath: string): Promise<void>;
    /** Remove File Bulk from Disk */
    unlinkBulk?: (filePaths: string[]) => Promise<void>;
    /** Lists all files and sub-directory in given directory Path */
    readdir(filePath: string): string[];
    /** Creates Directory in Disk for given Path */
    mkdir(filePath: string, opts: DirOpts): void;
    /** Remove Directory from Disk */
    rmdir(filePath: string): void;
    /** Rename File Name in Disk */
    rename(oldFilepath: string, newFilepath: string): void;
    /** Unix File Stat from Disk */
    stat(filePath: string): Stats;
    /** Unix File Stat from Disk */
    lstat(filePath: string): Stats;
    /** Read Content of file targeted by a Symbolic Link */
    readlink(filePath: string): string;
    /** Create Symbolic Link to a target file */
    symlink(target: string, filePath: string): void;
  }

  interface PromisifiedFS {
    /** Initialise File System Backend */
    init(name: string, opt?: FSConstructorOptions): void;
    /** Activates Cache */
    activate(): Promise<void>;
    /** Deactivates Cache */
    deactivate(): Promise<void>;
    /** Reads File Content from Disk */
    readFile(filePath: string, opts?: ReadFileOpts): Promise<Uint8Array>;
    /** Reads File Bulk from Disk */
    readFileBulk?: (filePaths: string[], opts?: ReadFileOpts) => Promise<[filePath: string, data: Uint8Array][]>;
    /** Writes File Content to Disk */
    writeFile(filePath: string, data: Uint8Array, opts?: WriteFileOpts): Promise<void>;
    /** Writes File Bulk to Disk */
    writeFileBulk?: (files: [filePath: string, data: Uint8Array][], opts?: WriteFileOpts) => Promise<void>;
    /** Remove File from Disk */
    unlink(filePath: string): Promise<void>;
    /** Remove File Bulk from Disk */
    unlinkBulk?: (filePaths: string[]) => Promise<void>;
    /** Lists all files and sub-directory in given directory Path */
    readdir(filePath: string): Promise<string[]>;
    /** Creates Directory in Disk for given Path */
    mkdir(filePath: string, opts?: DirOpts): Promise<void>;
    /** Remove Directory from Disk */
    rmdir(filePath: string): Promise<void>;
    /** Rename File Name in Disk */
    rename(oldFilepath: string, newFilepath: string): Promise<void>;
    /** Unix File Stat from Disk */
    stat(filePath: string): Promise<Stats>;
    /** Unix File Stat from Disk */
    lstat(filePath: string): Promise<Stats>;
    /** Read Content of file targeted by a Symbolic Link */
    readlink(filePath: string): Promise<string>;
    /** Create Symbolic Link to a target file */
    symlink(target: string, filePath: string): Promise<void>;
  }
}
