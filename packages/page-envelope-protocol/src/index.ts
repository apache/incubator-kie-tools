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

import { ApiDefinition } from "@kogito-tooling/envelope-bus";

export interface Association {
  origin: string;
  busId: string;
}

export interface PageInitArgs {
  filePath?: string;
  backendUrl: string;
}

export interface KogitoPageEnvelopeApi extends ApiDefinition<KogitoPageEnvelopeApi> {
  init(association: Association, pageInitArgs: PageInitArgs): Promise<void>;
}

//

export interface SvgDiagram {
  path: string;
  img: string;
}

export interface KogitoPageChannelApi extends ApiDefinition<KogitoPageChannelApi> {
  getOpenDiagrams(): Promise<SvgDiagram[]>;
}

//

export interface PageEnvelopeLocator {
  targetOrigin: string;
  mapping: Map<string, PageMapping>;
}

export interface PageMapping {
  title: string;
  envelopePath: string;
  backendUrl: string;
}

export * from "./KogitoPageChannel";
