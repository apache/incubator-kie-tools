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

/**
 * EXPOSED INTEROP API
 *
 * This API is exposed from the Envelope to be consumed on Java code.
 *
 * GWT State Control API for command-based editors. It gives access to the {@link StateControlCommandRegistry} and allows setting
 * the editor undo & redo commands for a correct integration with the envelope.
 */
export interface StateControlExposedInteropApi {
  registry: StateControlCommandRegistry<any>;
  setUndoCommand(undoCommand: () => void): void;
  setRedoCommand(redoCommand: () => void): void;
}

export interface StateControlCommandRegistry<T> {
  register(id: string, command: T): void;
  peek(): T | null;
  pop(): T | null;
  isEmpty(): boolean;
  getCommands(): T[];
  clear(): void;
  setMaxSize(size: number): void;
}
