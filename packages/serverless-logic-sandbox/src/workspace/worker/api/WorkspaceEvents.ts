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

export type WorkspaceEvents =
  | { type: "ADD"; workspaceId: string }
  | { type: "CREATE_SAVE_POINT"; workspaceId: string }
  | { type: "PULL"; workspaceId: string }
  | { type: "RENAME"; workspaceId: string }
  | { type: "DELETE"; workspaceId: string }
  | { type: "ADD_FILE"; relativePath: string }
  | { type: "MOVE_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE_FILE"; relativePath: string }
  | { type: "DELETE_FILE"; relativePath: string }
  | { type: "ADD_BATCH"; workspaceId: string; relativePaths: string[] }
  | { type: "MOVE_BATCH"; workspaceId: string; relativePaths: Map<string, string> }
  | { type: "DELETE_BATCH"; workspaceId: string; relativePaths: string[] };
