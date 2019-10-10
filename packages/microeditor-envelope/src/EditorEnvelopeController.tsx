/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { LanguageData } from "@kogito-tooling/core-api";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { EnvelopeBusInnerMessageHandler } from "./EnvelopeBusInnerMessageHandler";
import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { EditorFactory } from "./EditorFactory";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";

export class EditorEnvelopeController {
  public static readonly ESTIMATED_TIME_TO_WAIT_AFTER_EMPTY_SET_CONTENT = 10;

  private readonly editorFactory: EditorFactory<any>;
  private readonly specialDomElements: SpecialDomElements;
  private readonly envelopeBusInnerMessageHandler: EnvelopeBusInnerMessageHandler;

  private editorEnvelopeView?: EditorEnvelopeView;
  private renderer: Renderer;

  constructor(
    busApi: EnvelopeBusApi,
    editorFactory: EditorFactory<any>,
    specialDomElements: SpecialDomElements,
    renderer: Renderer
  ) {
    this.renderer = renderer;
    this.editorFactory = editorFactory;
    this.specialDomElements = specialDomElements;
    this.envelopeBusInnerMessageHandler = new EnvelopeBusInnerMessageHandler(busApi, self => ({
      receive_contentResponse: (content: string) => {
        const editor = this.getEditor();
        if (editor) {
          editor
            .setContent("")
            .finally(() => this.waitForEmptySetContentThenSetLoadingFinished())
            .then(() => editor.setContent(content));
        }
      },
      receive_contentRequest: () => {
        const editor = this.getEditor();
        if (editor) {
          editor.getContent().then(content => self.respond_contentRequest(content));
        }
      },
      receive_languageResponse: (languageData: LanguageData) => {
        this.editorFactory
          .createEditor(languageData, this.envelopeBusInnerMessageHandler)
          .then(editor => this.open(editor))
          .then(() => self.request_contentResponse());
      }
    }));
  }

  private waitForEmptySetContentThenSetLoadingFinished() {
    return new Promise(res => {
      setTimeout(
        () => this.editorEnvelopeView!.setLoadingFinished().then(res),
        EditorEnvelopeController.ESTIMATED_TIME_TO_WAIT_AFTER_EMPTY_SET_CONTENT
      );
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

  private render(container: HTMLElement) {
    return new Promise<void>(res =>
      this.renderer.render(
        <EditorEnvelopeView
          exposing={self => (this.editorEnvelopeView = self)}
          loadingScreenContainer={this.specialDomElements.loadingScreenContainer}
        />,
        container,
        res
      )
    );
  }

  public start(container: HTMLElement): Promise<void> {
    return this.render(container).then(() => {
      this.envelopeBusInnerMessageHandler.startListening();
    });
  }

  public stop() {
    this.envelopeBusInnerMessageHandler.stopListening();
  }
}
