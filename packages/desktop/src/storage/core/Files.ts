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

import { FileMetadata } from "../api/FileMetadata";
import { Provider } from "../api/Provider";
import { StorageTypes } from "../api/StorageTypes";

export class Files {
  private static providers = new Map<StorageTypes, Provider>();

  public static register(provider: Provider) {
    this.providers.set(provider.type, provider);
  }

  public static write(file: FileMetadata, content: string): Promise<void> {
    const provider = this.providers.get(file.storage);
    if (provider) {
      return provider.write(file, content);
    }
    return Promise.resolve();
  }

  public static read(file: FileMetadata): Promise<string> {
    const provider = this.providers.get(file.storage);
    if (provider) {
      return provider.read(file);
    }
    return Promise.resolve("");
  }
}
