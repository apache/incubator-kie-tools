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

import { StorageTypes } from "./StorageTypes";
import { FileMetadata } from "./FileMetadata";

export interface Provider {
  readonly type: StorageTypes;

  read(file: FileMetadata): Promise<string>;

  write(file: FileMetadata, content: string): Promise<void>;

  exists(file: FileMetadata): boolean;

  remove(file: FileMetadata): void;

  list(file: FileMetadata): FileMetadata[];

  isDirectory(file: FileMetadata): boolean;
}
