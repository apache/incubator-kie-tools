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
  Association,
  Editor,
  EditorContent,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
  StateControlCommand
} from "../api";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { EnvelopeApiFactory, EnvelopeApiFactoryArgs } from "@kogito-tooling/envelope";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { ChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts/dist/api";
import { DEFAULT_RECT } from "@kogito-tooling/guided-tour/dist/api";

export class KogitoEditorEnvelopeApiFactory
  implements
    EnvelopeApiFactory<
      KogitoEditorEnvelopeApi,
      KogitoEditorChannelApi,
      EditorEnvelopeView,
      KogitoEditorEnvelopeContextType
    > {
  constructor(private readonly editorFactory: EditorFactory) {}

  public create(
    args: EnvelopeApiFactoryArgs<
      KogitoEditorEnvelopeApi,
      KogitoEditorChannelApi,
      EditorEnvelopeView,
      KogitoEditorEnvelopeContextType
    >
  ) {
    return new KogitoEditorEnvelopeApiImpl(args, this.editorFactory);
  }
}

export class KogitoEditorEnvelopeApiImpl implements KogitoEditorEnvelopeApi {
  private capturedInitRequestYet = false;
  private editor: Editor;

  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      KogitoEditorEnvelopeApi,
      KogitoEditorChannelApi,
      EditorEnvelopeView,
      KogitoEditorEnvelopeContextType
    >,
    private readonly editorFactory: EditorFactory
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  public receive_initRequest = async (association: Association, initArgs: EditorInitArgs) => {
    this.args.envelopeBusController.associate(association.origin, association.envelopeServerId);

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();

    this.editor = await this.editorFactory.createEditor(this.args.envelopeContext, initArgs);

    await this.args.view.setEditor(this.editor);

    this.editor.af_onStartup?.();
    this.editor.af_onOpen?.();

    if (this.args.envelopeContext.context.channel !== ChannelType.VSCODE) {
      this.registerDefaultShortcuts();
    }

    this.args.view.setLoading();

    const content = await this.args.envelopeContext.channelApi.request("receive_contentRequest");

    await this.editor
      .setContent(content.path ?? "", content.content)
      .finally(() => this.args.view.setLoadingFinished());

    this.args.envelopeContext.channelApi.notify("receive_ready");
  };

  public receive_contentChanged = (editorContent: EditorContent) => {
    this.args.view.setLoading();
    this.editor
      .setContent(editorContent.path ?? "", editorContent.content)
      .finally(() => this.args.view.setLoadingFinished());
  };

  public receive_editorUndo() {
    this.editor.undo();
  }

  public receive_editorRedo() {
    this.editor.redo();
  }

  public receive_contentRequest() {
    return this.editor.getContent().then(content => ({ content: content }));
  }

  public receive_previewRequest() {
    return this.editor.getPreview().then(previewSvg => previewSvg ?? "");
  }

  public receive_guidedTourElementPositionRequest = async (selector: string) => {
    return this.editor.getElementPosition(selector).then(rect => rect ?? DEFAULT_RECT);
  };

  public receive_channelKeyboardEvent = (channelKeyboardEvent: ChannelKeyboardEvent) => {
    window.dispatchEvent(new CustomEvent(channelKeyboardEvent.type, { detail: channelKeyboardEvent }));
  };

  private registerDefaultShortcuts() {
    this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "shift+ctrl+z",
      "Edit | Redo last edit",
      async () => {
        this.editor.redo();
        this.args.envelopeContext.channelApi.notify("receive_stateControlCommandUpdate", StateControlCommand.REDO);
      }
    );
    this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "ctrl+z",
      "Edit | Undo last edit",
      async () => {
        this.editor.undo();
        this.args.envelopeContext.channelApi.notify("receive_stateControlCommandUpdate", StateControlCommand.UNDO);
      }
    );
  }
}
