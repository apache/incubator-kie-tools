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

import { WorkspaceFile } from "../WorkspacesContext";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { WorkspaceOrigin } from "../model/WorkspaceOrigin";
import { BaseService, BaseServiceCreateProps, BaseServiceEvents } from "../commonServices/BaseService";

export const WORKSPACES_BROADCAST_CHANNEL = "workspaces";

export interface WorkspaceServiceCreateProps extends BaseServiceCreateProps<WorkspaceDescriptor, WorkspaceFile> {
  origin: WorkspaceOrigin;
  preferredName?: string;
}

export class WorkspaceService extends BaseService<WorkspaceDescriptor, WorkspaceFile, WorkspaceServiceCreateProps> {
  protected broadcastMessage(args: BaseServiceEvents): void {
    if (args.type === "ADD") {
      const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({
        type: "ADD_WORKSPACE",
        workspaceId: args.descriptorId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: args.descriptorId } as WorkspaceEvents);
    } else if (args.type === "DELETE") {
      const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({ type: "DELETE_WORKSPACE", workspaceId: args.descriptorId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId: args.descriptorId } as WorkspaceEvents);
    } else if (args.type === "RENAME") {
      const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({ type: "RENAME_WORKSPACE", workspaceId: args.descriptorId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId: args.descriptorId } as WorkspaceEvents);
    } else if (args.type === "ADD_FILE") {
      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(args));
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({
        type: "ADD",
        relativePath: args.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD_FILE",
        relativePath: args.relativePath,
      } as WorkspaceEvents);
    } else if (args.type === "UPDATE_FILE") {
      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(args));
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({
        type: "UPDATE",
        relativePath: args.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "UPDATE_FILE",
        relativePath: args.relativePath,
      } as WorkspaceEvents);
    } else if (args.type === "DELETE_FILE") {
      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(args));
      const broadcastChannel2 = new BroadcastChannel(args.descriptorId);
      broadcastChannel1.postMessage({
        type: "DELETE",
        relativePath: args.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "DELETE_FILE",
        relativePath: args.relativePath,
      } as WorkspaceEvents);
    } else if (args.type === "RENAME_FILE") {
      const { descriptorId, oldRelativePath, newRelativePath } = args;
      const broadcastChannel1 = new BroadcastChannel(
        this.getUniqueFileIdentifier({ descriptorId, relativePath: oldRelativePath })
      );
      const broadcastChannel2 = new BroadcastChannel(
        this.getUniqueFileIdentifier({ descriptorId, relativePath: newRelativePath })
      );
      const broadcastChannel3 = new BroadcastChannel(descriptorId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldRelativePath,
        newRelativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD",
        relativePath: newRelativePath,
      } as WorkspaceFileEvents);
      broadcastChannel3.postMessage({
        type: "RENAME_FILE",
        oldRelativePath,
        newRelativePath,
      } as WorkspaceEvents);
    } else if (args.type === "DELETE_BATCH") {
      const broadcastChannel = new BroadcastChannel(args.descriptorId);
      broadcastChannel.postMessage({
        type: "DELETE_BATCH",
        workspaceId: args.descriptorId,
        relativePaths: args.relativePaths,
      } as WorkspaceEvents);
    } else if (args.type === "MOVE_FILE") {
      const broadcastChannel = new BroadcastChannel(args.descriptorId);
      broadcastChannel.postMessage({
        type: "MOVE_FILE",
        workspaceId: args.descriptorId,
        oldRelativePath: args.oldRelativePath,
        newRelativePath: args.newRelativePath,
      } as WorkspaceEvents);
    } else if (args.type === "MOVE_BATCH") {
      const broadcastChannel = new BroadcastChannel(args.descriptorId);
      broadcastChannel.postMessage({
        type: "MOVE_BATCH",
        workspaceId: args.descriptorId,
        relativePaths: args.relativePaths,
      } as WorkspaceEvents);
    }
  }

  protected newFile(id: string, relativePath: string, getFileContents: () => Promise<Uint8Array>): WorkspaceFile {
    return new WorkspaceFile({
      workspaceId: id,
      relativePath,
      getFileContents,
    });
  }
}
