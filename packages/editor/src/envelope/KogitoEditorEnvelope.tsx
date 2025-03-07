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

import {
  Editor,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType,
} from "../api";
import { EditorEnvelopeView, EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { Envelope, EnvelopeApiFactory } from "@kie-tools-core/envelope";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { EditorEnvelopeI18nContext, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { getOperatingSystem } from "@kie-tools-core/operating-system";
import { ApiDefinition } from "@kie-tools-core/envelope-bus/dist/api";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";

export class KogitoEditorEnvelope<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>,
> {
  constructor(
    private readonly kogitoEditorEnvelopeApiFactory: EnvelopeApiFactory<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi>
    >,
    private readonly keyboardShortcutsService: KeyboardShortcutsService,
    private readonly i18nService: I18nService,
    private readonly envelope: Envelope<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi>
    >,
    private readonly context: KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi> = {
      shared: envelope.shared,
      channelApi: envelope.channelApi,
      operatingSystem: getOperatingSystem(),
      services: {
        keyboardShortcuts: keyboardShortcutsService,
        i18n: i18nService,
      },
      supportedThemes: [],
    }
  ) {}

  public start(container: HTMLElement) {
    return this.envelope.start(() => this.renderView(container), this.context, this.kogitoEditorEnvelopeApiFactory);
  }

  private renderView(container: HTMLElement) {
    const editorEnvelopeViewRef = React.createRef<EditorEnvelopeViewApi<E>>();

    const app = (
      <KogitoEditorEnvelopeContext.Provider value={this.context}>
        <I18nDictionariesProvider
          defaults={editorEnvelopeI18nDefaults}
          dictionaries={editorEnvelopeI18nDictionaries}
          ctx={EditorEnvelopeI18nContext}
          initialLocale={navigator.language}
        >
          <EditorEnvelopeI18nContext.Consumer>
            {({ setLocale }) => (
              <EditorEnvelopeView
                ref={editorEnvelopeViewRef}
                setLocale={setLocale}
                showKeyBindingsOverlay={this.keyboardShortcutsService.isEnabled()}
              />
            )}
          </EditorEnvelopeI18nContext.Consumer>
        </I18nDictionariesProvider>
      </KogitoEditorEnvelopeContext.Provider>
    );

    return new Promise<() => EditorEnvelopeViewApi<E>>((res) => {
      setTimeout(() => {
        ReactDOM.render(app, container, () => {
          res(() => editorEnvelopeViewRef.current!);
        });
      }, 0);
    });
  }
}
