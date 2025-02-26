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

import * as React from "react";
import { useContext } from "react";
import { ApiDefinition, ApiSharedValueConsumers, MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { KogitoEditorChannelApi } from "./KogitoEditorChannelApi";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import { EditorTheme } from "./EditorTheme";
import { KogitoEditorEnvelopeApi } from "./KogitoEditorEnvelopeApi";

export interface KogitoEditorEnvelopeContextType<
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>,
> {
  shared: ApiSharedValueConsumers<EnvelopeApi>;
  channelApi: MessageBusClientApi<ChannelApi>;
  operatingSystem?: OperatingSystem;
  services: {
    keyboardShortcuts: KeyboardShortcutsService;
    i18n: I18nService;
  };
  supportedThemes: EditorTheme[];
}

export const KogitoEditorEnvelopeContext = React.createContext<KogitoEditorEnvelopeContextType<any, any>>({} as any);

export function useKogitoEditorEnvelopeContext<
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi> = KogitoEditorEnvelopeApi,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi> = KogitoEditorChannelApi,
>() {
  return useContext(KogitoEditorEnvelopeContext) as KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi>;
}
