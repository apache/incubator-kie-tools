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

import { LfsStorageFile, LfsStorageService } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsStorageService";
import { LfsFsCache } from "@kie-tools-core/workspaces-git-fs/dist/lfs/LfsFsCache";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { join } from "path";

export class CompanionFsService {
  private readonly fsCache = new LfsFsCache();
  private readonly storageService = new LfsStorageService();

  constructor(private readonly args: { storeNameSuffix: string; emptyFileContent: string }) {}

  public getFs(workspaceId: string) {
    return this.fsCache.getOrCreateFs(this.getFsStoreName(workspaceId));
  }

  public async get(args: { workspaceId: string; workspaceFileRelativePath: string }) {
    return this.storageService.getFile(this.getFs(args.workspaceId), this.getAbsolutePath(args));
  }

  public async delete(args: { workspaceId: string; workspaceFileRelativePath: string }) {
    const file = await this.get(args);
    if (!file) {
      console.debug(
        `Can't delete companion file because it doesn't exist for file '${args.workspaceFileRelativePath}' on Workspace '${args.workspaceId}'`
      );
      return;
    }

    await this.storageService.deleteFile(this.getFs(args.workspaceId), this.getAbsolutePath(args));

    const broadcastChannel = new BroadcastChannel(this.getUniqueFileIdentifier(args));

    const broadcastMessage: CompanionFsServiceBroadcastEvents = {
      type: "CFSF_DELETE",
      content: this.args.emptyFileContent,
    };

    broadcastChannel.postMessage(broadcastMessage);
  }

  public async createOrOverwrite(args: { workspaceId: string; workspaceFileRelativePath: string }, content: string) {
    await this.storageService.createOrOverwriteFile(
      this.getFs(args.workspaceId),
      new LfsStorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(content)),
        path: this.getAbsolutePath(args),
      })
    );

    const broadcastChannel = new BroadcastChannel(this.getUniqueFileIdentifier(args));

    const broadcastMessage: CompanionFsServiceBroadcastEvents = {
      type: "CFSF_ADD",
      workspaceFileRelativePath: args.workspaceFileRelativePath,
      content: content,
    };

    broadcastChannel.postMessage(broadcastMessage);
  }

  public async update(
    args: { workspaceId: string; workspaceFileRelativePath: string },
    newContent: string
  ): Promise<void> {
    await this.storageService.updateFile(
      this.getFs(args.workspaceId),
      new LfsStorageFile({
        getFileContents: () => Promise.resolve(encoder.encode(newContent)),
        path: this.getAbsolutePath(args),
      })
    );

    const broadcastChannel = new BroadcastChannel(this.getUniqueFileIdentifier(args));

    const broadcastMessage: CompanionFsServiceBroadcastEvents = {
      type: "CFSF_UPDATE",
      content: newContent,
    };

    broadcastChannel.postMessage(broadcastMessage);
  }

  public async rename(
    args: { workspaceId: string; workspaceFileRelativePath: string },
    newFileNameWithoutExtension: string
  ) {
    const file = await this.get(args);
    if (!file) {
      console.debug(
        `Can't rename companion file because it doesn't exist for file '${args.workspaceFileRelativePath}' on Workspace '${args.workspaceId}'`
      );
      return;
    }

    return this.storageService.renameFile(this.getFs(args.workspaceId), file, newFileNameWithoutExtension);
  }

  public deleteAll(workspaceId: string) {
    indexedDB.deleteDatabase(this.getFsStoreName(workspaceId));
  }

  public getSuffix() {
    return this.args.storeNameSuffix;
  }

  public getFsStoreName(workspaceId: string) {
    return `${workspaceId}__${this.getSuffix()}`;
  }

  public getAbsolutePath(args: { workspaceId: string; workspaceFileRelativePath?: string }) {
    return join("/", args.workspaceFileRelativePath ?? "");
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; workspaceFileRelativePath: string }) {
    return args.workspaceId + this.getSuffix() + this.getAbsolutePath(args);
  }
}

export type CompanionFsServiceBroadcastEvents =
  | { type: "CFSF_ADD"; content: string; workspaceFileRelativePath: string }
  | { type: "CFSF_MOVE"; newRelativePath: string; oldRelativePath: string }
  | { type: "CFSF_RENAME"; newRelativePath: string; oldRelativePath: string }
  | { type: "CFSF_UPDATE"; content: string }
  | { type: "CFSF_DELETE"; content: string };
