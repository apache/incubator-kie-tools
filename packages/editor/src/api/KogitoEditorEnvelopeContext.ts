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

import * as React from "react";
import { useContext } from "react";
import { ApiDefinition, MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { KogitoEditorChannelApi } from "./KogitoEditorChannelApi";
import { DefaultKeyboardShortcutsService } from "@kie-tooling-core/keyboard-shortcuts/dist/envelope";
import { I18nService } from "@kie-tooling-core/i18n/dist/envelope";
import { OperatingSystem } from "@kie-tooling-core/operating-system";

export interface KogitoEditorEnvelopeContextType<
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>
> {
  channelApi: MessageBusClientApi<ChannelApi>;
  operatingSystem?: OperatingSystem;
  services: {
    keyboardShortcuts: DefaultKeyboardShortcutsService;
    guidedTour: { isEnabled: () => boolean };
    i18n: I18nService;
  };
}

export const KogitoEditorEnvelopeContext = React.createContext<KogitoEditorEnvelopeContextType<any>>({} as any);

export function useKogitoEditorEnvelopeContext<
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi> = KogitoEditorChannelApi
>() {
  return useContext(KogitoEditorEnvelopeContext) as KogitoEditorEnvelopeContextType<ChannelApi>;
}
