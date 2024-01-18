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

import * as vscode from "vscode";

export class ReadonlyIsomorphicGitFsForVsCodeWorkspaceFolders {
  // This is a hack. The isomorphic-git check for this property, if it's undefined, it tries to bind the methods.
  public readonly _original_unwrapped_fs = true;

  async mkdir(path: string) {
    throw new Error("This pseudo-FS impl is readonly.");
  }

  async rmdir(path: string) {
    throw new Error("This pseudo-FS impl is readonly.");
  }

  async unlink(path: string) {
    throw new Error("This pseudo-FS impl is readonly.");
  }

  async stat(path: string) {
    throw new Error("This pseudo-FS impl is readonly.");
  }

  async writeFile(path: string, _: any) {
    throw new Error("This pseudo-FS impl is readonly.");
  }

  async readdir(path: string) {
    const contentPath = vscode.Uri.file(path);
    try {
      return vscode.workspace.fs.readDirectory(contentPath);
    } catch (error) {
      console.debug("ERROR on vscode.workspace.fs.readDirectory", "error:", error, "path:", contentPath);
    }
  }

  async read(path: string) {
    const contentPath = vscode.Uri.file(path);
    try {
      const uint8Array = await vscode.workspace.fs.readFile(contentPath);
      return Buffer.from(uint8Array);
    } catch (error) {
      console.debug("ERROR on vscode.workspace.fs.readFile", "error:", error, "path:", contentPath);
    }
  }

  async readFile(path: string) {
    return this.read(path);
  }

  async write(path: string, buffer: Buffer) {
    const contentPath = vscode.Uri.file(path);
    try {
      return vscode.workspace.fs.writeFile(contentPath, buffer);
    } catch (error) {
      console.debug("ERROR on vscode.workspace.fs.write", "error:", error, "path:", contentPath);
    }
  }

  async lstat(path: string) {
    const contentPath = vscode.Uri.file(path);
    try {
      return vscode.workspace.fs.stat(contentPath);
    } catch (error) {
      console.debug("ERROR on vscode.workspace.fs.stat", "error:", error, "path:", contentPath);
    }
  }
}
