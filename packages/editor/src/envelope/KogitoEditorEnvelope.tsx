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
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType,
} from "../api";
import { DefaultKeyboardShortcutsService } from "@kie-tooling-core/keyboard-shortcuts/dist/envelope";
import { KogitoGuidedTour } from "@kie-tooling-core/guided-tour/dist/envelope";
import { EditorEnvelopeView, EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { Envelope, EnvelopeApiFactory } from "@kie-tooling-core/envelope";
import { I18nService } from "@kie-tooling-core/i18n/dist/envelope";
import { EditorEnvelopeI18nContext, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { getOperatingSystem } from "@kie-tooling-core/operating-system";
import { ApiDefinition } from "@kie-tooling-core/envelope-bus/dist/api";

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
    private readonly keyboardShortcutsService: DefaultKeyboardShortcutsService,
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
        guidedTour: { isEnabled: () => KogitoGuidedTour.getInstance().isEnabled() },
        i18n: i18nService,
      },
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
            {({ setLocale }) => <EditorEnvelopeView ref={editorEnvelopeViewRef} setLocale={setLocale} />}
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
