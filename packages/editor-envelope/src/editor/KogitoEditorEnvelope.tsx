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
  EditorContext,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi
} from "@kogito-tooling/editor-envelope-protocol";
import {
  EditorFactory,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType
} from "@kogito-tooling/editor-api";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { Envelope } from "@kogito-tooling/envelope";
import { KogitoEditorEnvelopeApiFactory } from "./KogitoEditorEnvelopeApiImpl";
import { EnvelopeBus } from "@kogito-tooling/envelope-bus/dist/api";

export class KogitoEditorEnvelope {
  constructor(
    private readonly args: {
      container: HTMLElement;
      bus: EnvelopeBus;
      editorFactory: EditorFactory;
      editorContext: EditorContext;
    },
    private readonly kogitoEditorEnvelopeApiFactory = new KogitoEditorEnvelopeApiFactory(args.editorFactory),
    private readonly keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      os: args.editorContext.operatingSystem
    }),
    private readonly envelope: Envelope<
      KogitoEditorEnvelopeApi,
      KogitoEditorChannelApi,
      EditorEnvelopeView,
      KogitoEditorEnvelopeContextType
    > = new Envelope(args.bus),
    private readonly context: KogitoEditorEnvelopeContextType = {
      channelApi: envelope.busClient,
      context: args.editorContext,
      services: {
        keyboardShortcuts: keyboardShortcutsService,
        guidedTour: { isEnabled: () => KogitoGuidedTour.getInstance().isEnabled() }
      }
    }
  ) {}

  public start() {
    return this.envelope.start(() => this.renderView(), this.context, this.kogitoEditorEnvelopeApiFactory);
  }

  private renderView() {
    let view: EditorEnvelopeView;

    const app = (
      <KogitoEditorEnvelopeContext.Provider value={this.context}>
        <EditorEnvelopeView exposing={self => (view = self)} />
      </KogitoEditorEnvelopeContext.Provider>
    );

    return new Promise<EditorEnvelopeView>(res => {
      setTimeout(() => {
        ReactDOM.render(app, this.args.container, () => res(view!));
      }, 0);
    });
  }
}
