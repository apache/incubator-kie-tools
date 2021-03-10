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
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType
} from "../api";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour/dist/envelope";
import { EditorEnvelopeView, EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { Envelope } from "@kogito-tooling/envelope";
import { KogitoEditorEnvelopeApiFactory } from "./KogitoEditorEnvelopeApiImpl";
import { I18nService } from "@kogito-tooling/i18n/dist/envelope";
import { EditorEnvelopeI18nContext, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import { getOperatingSystem } from "@kogito-tooling/channel-common-api";

export class KogitoEditorEnvelope {
  constructor(
    private readonly kogitoEditorEnvelopeApiFactory: KogitoEditorEnvelopeApiFactory,
    private readonly keyboardShortcutsService: DefaultKeyboardShortcutsService,
    private readonly i18nService: I18nService,
    private readonly envelope: Envelope<
      KogitoEditorEnvelopeApi,
      KogitoEditorChannelApi,
      EditorEnvelopeViewApi,
      KogitoEditorEnvelopeContextType
    >,
    private readonly context: KogitoEditorEnvelopeContextType = {
      channelApi: envelope.channelApi,
      operatingSystem: getOperatingSystem(),
      services: {
        keyboardShortcuts: keyboardShortcutsService,
        guidedTour: { isEnabled: () => KogitoGuidedTour.getInstance().isEnabled() },
        i18n: i18nService
      }
    }
  ) {}

  public start(container: HTMLElement) {
    return this.envelope.start(() => this.renderView(container), this.context, this.kogitoEditorEnvelopeApiFactory);
  }

  private renderView(container: HTMLElement) {
    const editorEnvelopeViewRef = React.createRef<EditorEnvelopeViewApi>();

    const app = (
      <KogitoEditorEnvelopeContext.Provider value={this.context}>
        <I18nDictionariesProvider
          defaults={editorEnvelopeI18nDefaults}
          dictionaries={editorEnvelopeI18nDictionaries}
          ctx={EditorEnvelopeI18nContext}
          initialLocale={navigator.language}
        >
          <EditorEnvelopeI18nContext.Consumer>
            {({ setLocale }) => <EditorEnvelopeView ref={editorEnvelopeViewRef} setLocale={setLocale} />}
          </EditorEnvelopeI18nContext.Consumer>
        </I18nDictionariesProvider>
      </KogitoEditorEnvelopeContext.Provider>
    );

    return new Promise<() => EditorEnvelopeViewApi>(res => {
      setTimeout(() => {
        ReactDOM.render(app, container, () => {
          res(() => editorEnvelopeViewRef.current!);
        });
      }, 0);
    });
  }
}
