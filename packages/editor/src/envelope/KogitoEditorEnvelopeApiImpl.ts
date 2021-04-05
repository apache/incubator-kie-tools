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
  ChannelType,
  Editor,
  EditorContent,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
  StateControlCommand
} from "../api";
import { EnvelopeApiFactoryArgs } from "@kogito-tooling/envelope";
import { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import { ChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts/dist/api";
import { DEFAULT_RECT } from "@kogito-tooling/guided-tour/dist/api";
import { I18n } from "@kogito-tooling/i18n/dist/core";
import { EditorEnvelopeI18n, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { ApiDefinition } from "@kogito-tooling/envelope-bus/dist/api";

export class KogitoEditorEnvelopeApiImpl<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi> = KogitoEditorEnvelopeApi,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi> = KogitoEditorChannelApi
> implements KogitoEditorEnvelopeApi {
  private capturedInitRequestYet = false;
  private editor: E;

  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>
    >,
    private readonly editorFactory: EditorFactory<E, KogitoEditorChannelApi>,
    private readonly i18n: I18n<EditorEnvelopeI18n> = new I18n<EditorEnvelopeI18n>(
      editorEnvelopeI18nDefaults,
      editorEnvelopeI18nDictionaries
    )
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

    this.setupI18n(initArgs);

    this.editor = await this.editorFactory.createEditor(this.args.envelopeContext, initArgs);

    await this.args.view().setEditor(this.editor);

    this.editor.af_onStartup?.();
    this.editor.af_onOpen?.();

    this.args.view().setLoading();

    const editorContent = await this.args.envelopeContext.channelApi.requests.receive_contentRequest();

    await this.editor
      .setContent(editorContent.path ?? "", editorContent.content)
      .catch(e => this.args.envelopeContext.channelApi.notifications.receive_setContentError(editorContent))
      .finally(() => this.args.view().setLoadingFinished());

    this.registerDefaultShortcuts(initArgs);

    this.args.envelopeContext.channelApi.notifications.receive_ready();
  };

  public receive_contentChanged = (editorContent: EditorContent) => {
    this.args.view().setLoading();
    return this.editor
      .setContent(editorContent.path ?? "", editorContent.content)
      .catch(e => {
        this.args.envelopeContext.channelApi.notifications.receive_setContentError(editorContent);
        throw e;
      })
      .finally(() => this.args.view().setLoadingFinished());
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

  public receive_localeChange(locale: string) {
    return this.args.envelopeContext.services.i18n.executeOnLocaleChangeSubscriptions(locale);
  }

  public validate() {
    return this.editor.validate();
  }

  private setupI18n(initArgs: EditorInitArgs) {
    this.i18n.setLocale(initArgs.initialLocale);
    this.args.envelopeContext.services.i18n.subscribeToLocaleChange(locale => {
      this.i18n.setLocale(locale);
      this.args.view().setLocale(locale);
    });
  }

  private registerDefaultShortcuts(initArgs: EditorInitArgs) {
    if (initArgs.channel === ChannelType.VSCODE || initArgs.isReadOnly) {
      return;
    }

    const i18n = this.i18n.getCurrent();
    const redoId = this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "shift+ctrl+z",
      `${i18n.keyBindingsHelpOverlay.categories.edit} | ${i18n.keyBindingsHelpOverlay.commands.redo}`,
      async () => {
        this.editor.redo();
        this.args.envelopeContext.channelApi.notifications.receive_stateControlCommandUpdate(StateControlCommand.REDO);
      }
    );
    const undoId = this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "ctrl+z",
      `${i18n.keyBindingsHelpOverlay.categories.edit} | ${i18n.keyBindingsHelpOverlay.commands.undo}`,
      async () => {
        this.editor.undo();
        this.args.envelopeContext.channelApi.notifications.receive_stateControlCommandUpdate(StateControlCommand.UNDO);
      }
    );

    const subscription = this.args.envelopeContext.services.i18n.subscribeToLocaleChange(locale => {
      this.args.envelopeContext.services.keyboardShortcuts.deregister(redoId);
      this.args.envelopeContext.services.keyboardShortcuts.deregister(undoId);
      this.args.envelopeContext.services.i18n.unsubscribeToLocaleChange(subscription);
      this.registerDefaultShortcuts(initArgs);
    });
  }
}
