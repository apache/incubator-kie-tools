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

import { SwfServiceCatalogChannelApi, SwfService } from "../api";
import { SwfServiceCatalogRegistry } from "./SwfServiceCatalogRegistry";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";

export class SwfServiceCatalogChannelApiImpl implements SwfServiceCatalogChannelApi {
  constructor(
    private readonly envelopeServer: EnvelopeServer<SwfServiceCatalogChannelApi, KogitoEditorEnvelopeApi>,
    private readonly registry: SwfServiceCatalogRegistry
  ) {
    this.registry.init((services) => this.envelopeServer.shared.kogitoSwfServiceCatalog_services.set(services));
    this.registry.loadServices();
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfService[]> {
    return {
      defaultValue: [],
    };
  }

  public dispose(): void {
    this.registry.dispose();
  }
}
