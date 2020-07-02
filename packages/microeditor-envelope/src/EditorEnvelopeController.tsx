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
import { EditorContent, EditorContext, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";
import {
  ChannelKeyboardEvent,
  DefaultKeyboardShortcutsService,
} from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { EnvelopeBusInnerMessageHandler } from "./EnvelopeBusInnerMessageHandler";
import { EditorFactory } from "./EditorFactory";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";
import { ResourceContentEditorCoordinator } from "./api/resourceContent";
import { StateControlService } from "./api/stateControl";
import { getGuidedTourElementPosition } from "./handlers/GuidedTourRequestHandler";

export class EditorEnvelopeController {
  private readonly envelopeBusInnerMessageHandler: EnvelopeBusInnerMessageHandler;

  private editorEnvelopeView?: EditorEnvelopeView;

  constructor(
    busApi: EnvelopeBusApi,
    private readonly editorFactory: EditorFactory<any>,
    private readonly specialDomElements: SpecialDomElements,
    private readonly stateControlService: StateControlService,
    private readonly renderer: Renderer,
    private readonly resourceContentEditorCoordinator: ResourceContentEditorCoordinator,
    private readonly keyboardShortcutsService: DefaultKeyboardShortcutsService
  ) {
    this.envelopeBusInnerMessageHandler = new EnvelopeBusInnerMessageHandler(busApi, self => ({
      receive_contentResponse: (editorContent: EditorContent) => {
        const contentPath = editorContent.path || "";
        const editor = this.getEditor();
        if (editor) {
          this.editorEnvelopeView!.setLoading();
          editor
            .setContent(contentPath, editorContent.content)
            .finally(() => this.editorEnvelopeView!.setLoadingFinished())
            .then(() => self.notify_ready());
        }
      },
      receive_contentRequest: () => {
        const editor = this.getEditor();
        if (editor) {
          editor.getContent().then(content => self.respond_contentRequest({ content: content }));
        }
      },
      receive_languageResponse: (languageData: LanguageData) => {
        this.editorFactory
          .createEditor(languageData, this.envelopeBusInnerMessageHandler)
          .then(editor => this.open(editor))
          .then(() => self.request_contentResponse());
      },
      receive_resourceContentResponse: (resourceContent: ResourceContent) => {
        this.resourceContentEditorCoordinator.resolvePending(resourceContent);
      },
      receive_resourceContentList: (resourcesList: ResourcesList) => {
        this.resourceContentEditorCoordinator.resolvePendingList(resourcesList);
      },
      receive_editorUndo: () => {
        this.stateControlService.undo();
      },
      receive_editorRedo: () => {
        this.stateControlService.redo();
      },
      receive_previewRequest: () => {
        this.getEditor()
          ?.getPreview()
          .then(preview => self.respond_previewRequest(preview!))
          .catch(error => console.log(`Error retrieving preview: ${error}`));
      },
      receive_guidedTourElementPositionRequest: (selector: string) => {
        const position = getGuidedTourElementPosition(selector);
        self.respond_guidedTourElementPositionRequest(position);
      },
      receive_channelKeyboardEvent(channelKeyboardEvent: ChannelKeyboardEvent) {
        window.dispatchEvent(new CustomEvent(channelKeyboardEvent.type, { detail: channelKeyboardEvent }));
      }
    }));
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
