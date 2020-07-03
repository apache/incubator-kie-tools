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
import * as AppFormer from "@kogito-tooling/core-api";
import { EditorContent, EditorContext } from "@kogito-tooling/core-api";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { KogitoEnvelopeBus } from "./KogitoEnvelopeBus";
import { EditorFactory } from "./EditorFactory";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";
import { ResourceContentServiceCoordinator } from "./api/resourceContent";
import { StateControlService } from "./api/stateControl";
import { getGuidedTourElementPosition } from "./handlers/GuidedTourRequestHandler";

export class EditorEnvelopeController {
  private readonly envelopeBusInnerMessageHandler: KogitoEnvelopeBus;
  public capturedInitRequestYet = false;

  private editorEnvelopeView?: EditorEnvelopeView;

  constructor(
    busApi: EnvelopeBusApi,
    private readonly editorFactory: EditorFactory<any>,
    private readonly specialDomElements: SpecialDomElements,
    private readonly stateControlService: StateControlService,
    private readonly renderer: Renderer,
    private readonly resourceContentEditorCoordinator: ResourceContentServiceCoordinator,
    private readonly keyboardShortcutsService: DefaultKeyboardShortcutsService
  ) {
    this.envelopeBusInnerMessageHandler = new KogitoEnvelopeBus(busApi, {
      receive_initRequest: async (init: { origin: string; busId: string }) => {
        this.envelopeBusInnerMessageHandler.targetOrigin = init.origin;
        this.envelopeBusInnerMessageHandler.associatedBusId = init.busId;

        if (this.capturedInitRequestYet) {
          return;
        }

        this.capturedInitRequestYet = true;

        const language = await this.envelopeBusInnerMessageHandler.request_languageResponse();
        const editor = await editorFactory.createEditor(language, this.envelopeBusInnerMessageHandler);

        await this.open(editor);
        this.editorEnvelopeView!.setLoading();

        const editorContent = await this.envelopeBusInnerMessageHandler.request_contentResponse();

        await editor
          .setContent(editorContent.path ?? "", editorContent.content)
          .finally(() => this.editorEnvelopeView!.setLoadingFinished());

        this.envelopeBusInnerMessageHandler.notify_ready();
      },
      receive_contentChangedNotification: (editorContent: EditorContent) => {
        this.editorEnvelopeView!.setLoading();
        this.getEditor()!
          .setContent(editorContent.path ?? "", editorContent.content)
          .finally(() => this.editorEnvelopeView!.setLoadingFinished());
      },
      receive_editorUndo: () => {
        this.stateControlService.undo();
      },
      receive_editorRedo: () => {
        this.stateControlService.redo();
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
        return getGuidedTourElementPosition(selector);
      }
    });
  }

  //TODO: Create messages to control the lifecycle of enveloped components?
  //TODO: No-op when same Editor class?
  //TODO: Can I open an editor if there's already an open one?
  //TODO: What about close and shutdown methods?
  private open(editor: AppFormer.Editor) {
    return this.editorEnvelopeView!.setEditor(editor).then(() => {
      editor.af_onStartup();
      editor.af_onOpen();
    });
  }

  private getEditor() {
    return this.editorEnvelopeView!.getEditor();
  }

  private render(args: { container: HTMLElement; context: EditorContext }) {
    return new Promise<void>(res =>
      this.renderer.render(
        <EditorEnvelopeView
          exposing={self => (this.editorEnvelopeView = self)}
          loadingScreenContainer={this.specialDomElements.loadingScreenContainer}
          keyboardShortcutsService={this.keyboardShortcutsService}
          context={args.context}
          stateControlService={this.stateControlService}
          messageBus={this.envelopeBusInnerMessageHandler}
        />,
        args.container,
        res
      )
    );
  }

  public start(args: { container: HTMLElement; context: EditorContext }) {
    return this.render(args).then(() => {
      this.envelopeBusInnerMessageHandler.startListening();
      return this.envelopeBusInnerMessageHandler;
    });
  }

  public stop() {
    this.envelopeBusInnerMessageHandler.stopListening();
  }
}
