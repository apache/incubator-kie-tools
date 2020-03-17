/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { Provider } from "../api/Provider";
import { FileMetadata } from "../api/FileMetadata";
import { FileType } from "../api/FileType";
import { StorageTypes } from "../api/StorageTypes";

import * as fs from "fs";
import * as path from "path";
import * as util from "util";

export class FS implements Provider {
  public readonly type = StorageTypes.FS;

  public exists(file: FileMetadata): boolean {
    if (file.storage.valueOf() === StorageTypes.FS.valueOf()) {
      return fs.existsSync(file.full_name);
    }
    return false;
  }

  public read(file: FileMetadata): Promise<string> {
    if (file.storage.valueOf() === StorageTypes.FS.valueOf()) {
      const readFile = util.promisify(fs.readFile);
      return readFile(file.full_name, "utf-8");
    }
    return Promise.resolve("");
  }

  public write(file: FileMetadata, content: string): Promise<void> {
    if (file.storage.valueOf() === StorageTypes.FS.valueOf()) {
      const writeFile = util.promisify(fs.writeFile);
      return writeFile(file.full_name, content, "utf-8");
    }
    return Promise.resolve();
  }

  public static newFile(fullPath: string) {
    return FS._newFile(fullPath, fs.existsSync(fullPath) && fs.statSync(fullPath) ? FileType.FOLDER : FileType.FILE);
  }

  public static _newFile(fullPath: string, fileType: FileType) {
    return new FileMetadata(
      path.basename(fullPath),
      fullPath,
      fullPath,
      fileType,
      `file://${fullPath}`,
      StorageTypes.FS,
      "",
      fullPath
    );
  }
}
