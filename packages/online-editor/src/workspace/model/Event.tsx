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

interface BasicFileEvent {
  path: string;
}

interface BasicFileBatchEvent {
  paths: string[];
}

interface BasicWorkspaceEvent {
  context: string;
}

export interface AddFileEvent extends BasicFileEvent {}

export interface DeleteFileEvent extends BasicFileEvent {}

export interface UpdateFileEvent extends BasicFileEvent {}

export interface MoveFileEvent extends BasicFileEvent {
  newPath: string;
}

export interface AddFileBatchEvent extends BasicFileBatchEvent {}

export interface DeleteFileBatchEvent extends BasicFileBatchEvent {}

export interface UpdateFileBatchEvent extends BasicFileBatchEvent {}

export interface MoveFileBatchEvent extends BasicFileBatchEvent {
  pathMap: Map<string, string>;
}

export interface AddWorkspaceEvent extends BasicWorkspaceEvent {}

export interface DeleteWorkspaceEvent extends BasicWorkspaceEvent {}

export type FileEvent = BasicFileEvent | AddFileEvent | DeleteFileEvent | UpdateFileEvent | MoveFileEvent;

export type FileBatchEvent =
  | BasicFileBatchEvent
  | AddFileBatchEvent
  | DeleteFileBatchEvent
  | UpdateFileBatchEvent
  | MoveFileBatchEvent;

export type WorkspaceEvent = BasicWorkspaceEvent | AddWorkspaceEvent | DeleteWorkspaceEvent;

export type Event = FileEvent | FileBatchEvent | WorkspaceEvent;

export enum ChannelKind {
  ADD_FILE = "ADD_FILE",
  DELETE_FILE = "DELETE_FILE",
  UPDATE_FILE = "UPDATE_FILE",
  MOVE_FILE = "MOVE_FILE",

  ADD_FILE_BATCH = "ADD_FILE_BATCH",
  DELETE_FILE_BATCH = "DELETE_FILE_BATCH",
  UPDATE_FILE_BATCH = "UPDATE_FILE_BATCH",
  MOVE_FILE_BATCH = "MOVE_FILE_BATCH",

  ADD_WORKSPACE = "ADD_WORKSPACE",
  DELETE_WORKSPACE = "DELETE_WORKSPACE",
}
