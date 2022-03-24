/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { SwfServiceCatalogFunction, SwfServiceCatalogService, SwfServiceCatalogUser } from "./types";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

export interface SwfServiceCatalogChannelApi {
  kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]>;
  kogitoSwfServiceCatalog_user(): SharedValueProvider<SwfServiceCatalogUser | undefined>;
  kogitoSwfServiceCatalog_serviceRegistryUrl(): SharedValueProvider<string | undefined>;
  kogitoSwfServiceCatalog_refresh(): void;
  kogitoSwfServiceCatalog_logInToRhhcc(): void;
  kogitoSwfServiceCatalog_importFunctionFromCompletionItem(containingService: SwfServiceCatalogService): void;
  kogitoSwfServiceCatalog_setupServiceRegistryUrl(): void;
}
