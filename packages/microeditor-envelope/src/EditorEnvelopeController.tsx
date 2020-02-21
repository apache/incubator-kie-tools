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
import { EditorContent, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { EnvelopeBusInnerMessageHandler } from "./EnvelopeBusInnerMessageHandler";
import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { EditorFactory } from "./EditorFactory";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";
import { ResourceContentEditorCoordinator } from "./api/resourceContent";
import { StateControl } from "./api/stateControl";

export class EditorEnvelopeController {
  public static readonly ESTIMATED_TIME_TO_WAIT_AFTER_EMPTY_SET_CONTENT = 10;

  private readonly editorFactory: EditorFactory<any>;
  private readonly specialDomElements: SpecialDomElements;
  private readonly resourceContentEditorCoordinator: ResourceContentEditorCoordinator;
  private readonly envelopeBusInnerMessageHandler: EnvelopeBusInnerMessageHandler;
  private readonly stateControl: StateControl;

  private editorEnvelopeView?: EditorEnvelopeView;
  private renderer: Renderer;

  constructor(
    busApi: EnvelopeBusApi,
    editorFactory: EditorFactory<any>,
    specialDomElements: SpecialDomElements,
    stateControl: StateControl,
    renderer: Renderer,
    resourceContentEditorCoordinator: ResourceContentEditorCoordinator
  ) {
    this.renderer = renderer;
    this.editorFactory = editorFactory;
    this.specialDomElements = specialDomElements;
    this.resourceContentEditorCoordinator = resourceContentEditorCoordinator;
    this.stateControl = stateControl;
    this.envelopeBusInnerMessageHandler = new EnvelopeBusInnerMessageHandler(busApi, self => ({
      receive_contentResponse: (editorContent: EditorContent) => {
        const contentPath = editorContent.path || "";
        const editor = this.getEditor();
        if (editor) {
          this.editorEnvelopeView!.setLoading();
          editor
            .setContent("", "")
            .finally(() => this.waitForEmptySetContentThenSetLoadingFinished())
            .then(() => editor.setContent(contentPath, editorContent.content));
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
        this.stateControl.undo();
      },
      receive_editorRedo: () => {
        this.stateControl.redo();
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

  public start(container: HTMLElement): Promise<EnvelopeBusInnerMessageHandler> {
    return this.render(container).then(() => {
      this.envelopeBusInnerMessageHandler.startListening();
      return this.envelopeBusInnerMessageHandler;
    });
  }

  public stop() {
    this.envelopeBusInnerMessageHandler.stopListening();
  }
}
