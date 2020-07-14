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
import {
  Association,
  ChannelKeyboardEvent,
  DEFAULT_RECT,
  EditorContent,
  EditorContext,
  EnvelopeBus
} from "@kogito-tooling/microeditor-envelope-protocol";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { KogitoEnvelopeBus } from "./KogitoEnvelopeBus";
import { Editor, EditorFactory, EnvelopeContext, EnvelopeContextType } from "@kogito-tooling/editor-api";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour";

export class EditorEnvelopeController {
  public readonly kogitoEnvelopeBus: KogitoEnvelopeBus;
  public readonly envelopeContext: EnvelopeContextType;
  public capturedInitRequestYet = false;

  private editorEnvelopeView?: EditorEnvelopeView;

  constructor(
    bus: EnvelopeBus,
    private readonly editorFactory: EditorFactory<any>,
    private readonly specialDomElements: SpecialDomElements,
    private readonly renderer: Renderer,
    private readonly editorContext: EditorContext,
    private readonly keyboardShortcutsService: DefaultKeyboardShortcutsService
  ) {
    this.kogitoEnvelopeBus = new KogitoEnvelopeBus(bus, {
      receive_initRequest: async (association: Association) => {
        this.kogitoEnvelopeBus.associate(association);

        if (this.capturedInitRequestYet) {
          return;
        }

        this.capturedInitRequestYet = true;

        const language = await this.kogitoEnvelopeBus.request_languageResponse();
        const editor = await editorFactory.createEditor(language, this.envelopeContext);

        await this.open(editor);
        this.editorEnvelopeView!.setLoading();

        const editorContent = await this.kogitoEnvelopeBus.request_contentResponse();

        await editor
          .setContent(editorContent.path ?? "", editorContent.content)
          .finally(() => this.editorEnvelopeView!.setLoadingFinished());

        this.kogitoEnvelopeBus.notify_ready();
      },
      receive_contentChanged: (editorContent: EditorContent) => {
        this.editorEnvelopeView!.setLoading();
        this.getEditor()!
          .setContent(editorContent.path ?? "", editorContent.content)
          .finally(() => this.editorEnvelopeView!.setLoadingFinished());
      },
      receive_editorUndo: () => {
        this.getEditor()!.undo();
      },
      receive_editorRedo: () => {
        this.getEditor()!.redo();
      },
      receive_contentRequest: async () => {
        return this.getEditor()!
          .getContent()
          .then(content => ({ content: content }));
      },
      receive_previewRequest: () => {
        return this.getEditor()!
          .getPreview()
          .then(previewSvg => previewSvg ?? "");
      },
      receive_guidedTourElementPositionRequest: async (selector: string) => {
        return this.getEditor()!
          .getElementPosition(selector)
          .then(rect => rect ?? DEFAULT_RECT); //FIXME: tiago -> check with guilherme
      },
      receive_channelKeyboardEvent(channelKeyboardEvent: ChannelKeyboardEvent) {
        window.dispatchEvent(new CustomEvent(channelKeyboardEvent.type, { detail: channelKeyboardEvent }));
      }
    });

    this.envelopeContext = {
      channelApi: this.kogitoEnvelopeBus.client,
      context: editorContext,
      services: {
        keyboardShortcuts: keyboardShortcutsService,
        guidedTour: {
          isEnabled: () => KogitoGuidedTour.getInstance().isEnabled()
        }
      }
    };
  }

  //TODO: Create messages to control the lifecycle of enveloped components?
  //TODO: No-op when same Editor class?
  //TODO: Can I open an editor if there's already an open one?
  //TODO: What about close and shutdown methods?
  private open(editor: Editor) {
    return this.editorEnvelopeView!.setEditor(editor).then(() => {
      editor.af_onStartup();
      editor.af_onOpen();
    });
  }

  private getEditor() {
    return this.editorEnvelopeView!.getEditor();
  }

  private render(args: { container: HTMLElement }) {
    return new Promise<void>(res =>
      this.renderer.render(
        <EnvelopeContext.Provider value={this.envelopeContext}>
          <EditorEnvelopeView
            exposing={self => (this.editorEnvelopeView = self)}
            loadingScreenContainer={this.specialDomElements.loadingScreenContainer}
            keyboardShortcutsService={this.keyboardShortcutsService}
            context={this.editorContext}
            messageBus={this.kogitoEnvelopeBus}
          />
        </EnvelopeContext.Provider>,
        args.container,
        res
      )
    );
  }

  public start(args: { container: HTMLElement }) {
    return this.render(args).then(() => {
      this.kogitoEnvelopeBus.startListening();
      return this.kogitoEnvelopeBus;
    });
  }

  public stop() {
    this.kogitoEnvelopeBus.stopListening();
  }
}
