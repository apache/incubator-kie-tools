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

export const WORKSPACES_BROADCAST_CHANNEL = "workspaces" as const;
export const WORKSPACES_FILES_BROADCAST_CHANNEL = "workspaces_files" as const;

export type WorkspacesBroadcastEvents =
  | { type: "WSS_DELETE_ALL" }
  | { type: "WSS_ADD_WORKSPACE"; workspaceId: string }
  | { type: "WSS_RENAME_WORKSPACE"; workspaceId: string }
  | { type: "WSS_DELETE_WORKSPACE"; workspaceId: string }
  | { type: "WSS_UPDATE"; workspaceId: string };

export type WorkspacesFilesBroadcastEvents =
  | { type: "WSSFS_ADD"; workspaceId: string; relativePath: string }
  | { type: "WSSFS_MOVE"; workspaceId: string; newRelativePath: string; oldRelativePath: string }
  | { type: "WSSFS_RENAME"; workspaceId: string; newRelativePath: string; oldRelativePath: string }
  | { type: "WSSFS_UPDATE"; workspaceId: string; relativePath: string }
  | { type: "WSSFS_DELETE"; workspaceId: string; relativePath: string };
