/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  ContentType,
  ResourceContentOptions,
  ResourceListOptions,
  ResourcesList,
  ResourceContent,
  ResourceContentService
} from "@kogito-tooling/core-api";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { VsCodeInnerResourceContentServiceFactory } from "./VsCodeInnerResourceContentService";

export class VsCodeResourceContentService implements ResourceContentService {
  private readonly searchFactory: VsCodeInnerResourceContentServiceFactory;

  constructor(editorStore: KogitoEditorStore) {
    this.searchFactory = new VsCodeInnerResourceContentServiceFactory(editorStore);
  }

  public async get(path: string, opts?: ResourceContentOptions): Promise<ResourceContent | undefined> {
    return this.searchFactory.lookupContentService().get(path, opts);
  }

  public async list(pattern: string, opts?: ResourceListOptions): Promise<ResourcesList> {
    return this.searchFactory.lookupContentService().list(pattern, opts);
  }
}
