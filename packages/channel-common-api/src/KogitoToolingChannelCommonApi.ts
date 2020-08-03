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

import { ResourceContentRequest } from "./ResourceContentRequest";
import { ResourceContent } from "./ResourceContent";
import { ResourceListRequest } from "./ResourceListRequest";
import { ResourcesList } from "./ResourcesList";
import { KogitoEdit } from "./KogitoEdit";

export interface WorkspaceApi {
  receive_openFile(path: string): void;
  receive_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined>;
  receive_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList>;
}

export interface KogitoToolingChannelCommonApi extends WorkspaceApi {
  receive_ready(): void;
  receive_newEdit(edit: KogitoEdit): void;
}
