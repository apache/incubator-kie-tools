/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  Association,
  ChannelType,
  Editor,
  EditorContent,
  EditorFactory,
  EditorInitArgs,
  EditorTheme,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
  StateControlCommand,
} from "../api";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import { ChannelKeyboardEvent } from "@kie-tools-core/keyboard-shortcuts/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { EditorEnvelopeI18n, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { ApiDefinition } from "@kie-tools-core/envelope-bus/dist/api";

export class KogitoEditorEnvelopeApiImpl<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi> = KogitoEditorEnvelopeApi,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi> = KogitoEditorChannelApi,
> implements KogitoEditorEnvelopeApi
{
  protected view: () => EditorEnvelopeViewApi<E>;
  private capturedInitRequestYet = false;
  private normalizedPosixPathRelativeToTheWorkspaceRoot: string;
  private editor: E;

  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      EnvelopeApi,
      ChannelApi,
      EditorEnvelopeViewApi<E>,
      KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>
    >,
    private readonly editorFactory: EditorFactory<E, KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
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

  public kogitoEditor_initRequest = async (association: Association, initArgs: EditorInitArgs) => {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();

    this.view = await this.args.viewDelegate();

    this.setupI18n(initArgs);

    this.editor = await this.editorFactory.createEditor(this.args.envelopeContext, initArgs);

    // "Permanent" theme subscription, destroyed along editor's iFrame
    if (this.args.envelopeContext.supportedThemes.length > 1) {
      this.args.envelopeContext.channelApi.shared.kogitoEditor_theme.subscribe((theme: EditorTheme) => {
        this.editor.setTheme(theme);
      });
    }

    await this.view().setEditor(this.editor);

    this.editor.af_onStartup?.();
    this.editor.af_onOpen?.();

    this.view().setLoading();

    const editorContent = await this.args.envelopeContext.channelApi.requests.kogitoEditor_contentRequest();
    this.normalizedPosixPathRelativeToTheWorkspaceRoot = editorContent.normalizedPosixPathRelativeToTheWorkspaceRoot;

    await this.editor
      .setContent(editorContent.normalizedPosixPathRelativeToTheWorkspaceRoot, editorContent.content)
      .catch((e) => this.args.envelopeContext.channelApi.notifications.kogitoEditor_setContentError.send(editorContent))
      .finally(() => this.view().setLoadingFinished());

    this.registerDefaultShortcuts(initArgs);

    this.args.envelopeContext.channelApi.notifications.kogitoEditor_ready.send();
  };

  public kogitoEditor_contentChanged = (editorContent: EditorContent, args: { showLoadingOverlay: boolean }) => {
    if (args.showLoadingOverlay) {
      this.view().setLoading();
    }

    return this.editor
      .setContent(editorContent.normalizedPosixPathRelativeToTheWorkspaceRoot, editorContent.content)
      .catch((e) => {
        this.args.envelopeContext.channelApi.notifications.kogitoEditor_setContentError.send(editorContent);
        throw e;
      })
      .finally(() => this.view().setLoadingFinished());
  };

  public kogitoEditor_editorUndo() {
    this.editor.undo();
  }

  public kogitoEditor_editorRedo() {
    this.editor.redo();
  }

  public kogitoEditor_contentRequest() {
    return this.editor.getContent().then((content) => ({
      content: sanitize(content),
      normalizedPosixPathRelativeToTheWorkspaceRoot: this.normalizedPosixPathRelativeToTheWorkspaceRoot,
    }));
  }

  public kogitoEditor_previewRequest() {
    return this.editor.getPreview().then((previewSvg) => previewSvg ?? "");
  }

  public kogitoKeyboardShortcuts_channelKeyboardEvent = (channelKeyboardEvent: ChannelKeyboardEvent) => {
    window.dispatchEvent(new CustomEvent(channelKeyboardEvent.type, { detail: channelKeyboardEvent }));
  };

  public kogitoI18n_localeChange(locale: string) {
    return this.args.envelopeContext.services.i18n.executeOnLocaleChangeSubscriptions(locale);
  }

  public kogitoEditor_validate() {
    return this.editor.validate();
  }

  private setupI18n(initArgs: EditorInitArgs) {
    this.i18n.setLocale(initArgs.initialLocale);
    this.args.envelopeContext.services.i18n.subscribeToLocaleChange((locale) => {
      this.i18n.setLocale(locale);
      this.view().setLocale(locale);
    });
  }

  private registerDefaultShortcuts(initArgs: EditorInitArgs) {
    if (
      initArgs.channel === ChannelType.VSCODE_DESKTOP ||
      initArgs.channel === ChannelType.VSCODE_WEB ||
      initArgs.isReadOnly
    ) {
      return;
    }

    const i18n = this.i18n.getCurrent();
    const redoId = this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "shift+ctrl+z",
      `${i18n.keyBindingsHelpOverlay.categories.edit} | ${i18n.keyBindingsHelpOverlay.commands.redo}`,
      async () => {
        this.editor.redo();
        this.args.envelopeContext.channelApi.notifications.kogitoEditor_stateControlCommandUpdate.send(
          StateControlCommand.REDO
        );
      }
    );
    const undoId = this.args.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "ctrl+z",
      `${i18n.keyBindingsHelpOverlay.categories.edit} | ${i18n.keyBindingsHelpOverlay.commands.undo}`,
      async () => {
        this.editor.undo();
        this.args.envelopeContext.channelApi.notifications.kogitoEditor_stateControlCommandUpdate.send(
          StateControlCommand.UNDO
        );
      }
    );

    const subscription = this.args.envelopeContext.services.i18n.subscribeToLocaleChange((locale) => {
      this.args.envelopeContext.services.keyboardShortcuts.deregister(redoId);
      this.args.envelopeContext.services.keyboardShortcuts.deregister(undoId);
      this.args.envelopeContext.services.i18n.unsubscribeToLocaleChange(subscription);
      this.registerDefaultShortcuts(initArgs);
    });
  }

  /**
   * Gets the view's editor or throws an error.
   *
   * @returns the editor
   * @throws {"Editor not found"} if the editor is not found
   */
  protected getEditorOrThrowError(): E {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor;
  }
}

// Not using the `u` flag as it's not trying to match a unicode character outside the BMP (Basic Multilingual Plane)
// The BMP are the unicodes from U+0000 to U+FFFF. Also, the `u` flag requires target `es6`+
function sanitize(str: string): string {
  return str.replace(/[\u202a\u202b\u202c\u202d\u202e\u2066\u2067\u2068\u2069]/g, "");
}
