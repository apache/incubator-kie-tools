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

import {
  Editor,
  EditorApi,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType,
} from "../api";
import { EditorEnvelopeView, EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import * as React from "react";
import { Envelope, EnvelopeApiFactory } from "@kie-tools-core/envelope";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { EditorEnvelopeI18nContext, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { getOperatingSystem } from "@kie-tools-core/operating-system";
import { ApiDefinition } from "@kie-tools-core/envelope-bus/dist/api";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";
import { createRoot } from "react-dom/client";
import { useEffect } from "react";

export class KogitoEditorEnvelope<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>
> {
  constructor(
    private readonly kogitoEditorEnvelopeApiFactory: EnvelopeApiFactory<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<ChannelApi>
    >,
    private readonly keyboardShortcutsService: KeyboardShortcutsService,
    private readonly i18nService: I18nService,
    private readonly envelope: Envelope<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<ChannelApi>
    >,
    private readonly context: KogitoEditorEnvelopeContextType<ChannelApi> = {
      channelApi: envelope.channelApi,
      operatingSystem: getOperatingSystem(),
      services: {
        keyboardShortcuts: keyboardShortcutsService,
        i18n: i18nService,
      },
    }
  ) {}

  public start(container: HTMLElement) {
    return this.envelope.start(() => this.renderView(container), this.context, this.kogitoEditorEnvelopeApiFactory);
  }

  private renderView(container: HTMLElement) {
    return new Promise<() => EditorEnvelopeViewApi<E>>((res) => {
      setTimeout(() => {
        createRoot(container).render(
          <KogitoEditorEnvelopeViewWrapper<E, EnvelopeApi, ChannelApi>
            setEditorEnvelopeViewApi={(api) => res(() => api)}
            isKeyboardServiceEnabled={this.keyboardShortcutsService.isEnabled()}
            context={this.context}
          />
        );
      }, 0);
    });
  }
}

type Props<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>
> = {
  setEditorEnvelopeViewApi: (api: EditorEnvelopeViewApi<E>) => void;
  context: KogitoEditorEnvelopeContextType<ChannelApi>;
  isKeyboardServiceEnabled: boolean;
};

function KogitoEditorEnvelopeViewWrapper<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>
>(props: Props<E, EnvelopeApi, ChannelApi>) {
  const ref = React.createRef<EditorEnvelopeViewApi<E>>();
  useEffect(() => {
    props.setEditorEnvelopeViewApi(ref.current!);
  }, []);

  return (
    <KogitoEditorEnvelopeContext.Provider value={props.context}>
      <I18nDictionariesProvider
        defaults={editorEnvelopeI18nDefaults}
        dictionaries={editorEnvelopeI18nDictionaries}
        ctx={EditorEnvelopeI18nContext}
        initialLocale={navigator.language}
      >
        <EditorEnvelopeI18nContext.Consumer>
          {({ setLocale }) => (
            <EditorEnvelopeView
              ref={ref}
              setLocale={setLocale}
              showKeyBindingsOverlay={props.isKeyboardServiceEnabled}
            />
          )}
        </EditorEnvelopeI18nContext.Consumer>
      </I18nDictionariesProvider>
    </KogitoEditorEnvelopeContext.Provider>
  );
}
