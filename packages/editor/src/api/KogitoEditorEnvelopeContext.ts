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
import { MessageBusClientApi } from "@kogito-tooling/envelope-bus/dist/api";
import { KogitoEditorChannelApi } from "./KogitoEditorChannelApi";
import { EditorContext } from "./EditorContext";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { I18nService } from "@kogito-tooling/i18n/dist/envelope";

export interface KogitoEditorEnvelopeContextType {
  channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
  context: EditorContext;
  services: {
    keyboardShortcuts: DefaultKeyboardShortcutsService;
    guidedTour: { isEnabled: () => boolean };
    i18n: I18nService;
  };
}

export const KogitoEditorEnvelopeContext = React.createContext<KogitoEditorEnvelopeContextType>({} as any);

export function useKogitoEditorEnvelopeContext() {
  return useContext(KogitoEditorEnvelopeContext);
}
