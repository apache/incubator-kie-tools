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
import { FileType } from "./FileType";

export class FileMetadata {
  public readonly name: string;
  public readonly full_name: string;
  public readonly relative_name: string;
  public readonly type: FileType;
  public readonly uri: string;
  public readonly storage: StorageTypes;
  public readonly origin: string;
  public readonly id: string;

  constructor(
    name: string,
    full_name: string,
    relative_name: string,
    type: FileType,
    uri: string,
    storage: StorageTypes,
    origin: string,
    id: string
  ) {
    this.name = name;
    this.full_name = full_name;
    this.relative_name = relative_name;
    this.type = type;
    this.uri = uri;
    this.storage = storage;
    this.origin = origin;
    this.id = id;
  }
}
