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

export type WorkspaceBroadcastEvents =
  | { type: "WS_ADD"; workspaceId: string }
  | { type: "WS_CREATE_SAVE_POINT"; workspaceId: string }
  | { type: "WS_PULL"; workspaceId: string }
  | { type: "WS_CHECKOUT_FILES_FROM_LOCAL_HEAD"; workspaceId: string; relativePaths: string[] }
  | { type: "WS_RENAME"; workspaceId: string }
  | { type: "WS_DELETE"; workspaceId: string }
  | { type: "WS_ADD_FILE"; relativePath: string }
  | { type: "WS_MOVE_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "WS_RENAME_FILE"; newRelativePath: string; oldRelativePath: string }
  | { type: "WS_UPDATE_FILE"; relativePath: string }
  | { type: "WS_DELETE_FILE"; relativePath: string }
  | { type: "WS_UPDATE_DESCRIPTOR" }
  | { type: "WS_CHECKOUT" };
