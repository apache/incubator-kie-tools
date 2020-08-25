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
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType
} from "../api";
import { EditorFactory } from "../api";
import { KogitoEditorEnvelope } from "./KogitoEditorEnvelope";
import { EnvelopeBus } from "@kogito-tooling/envelope-bus/dist/api";
import { KogitoEditorEnvelopeApiFactory } from "./KogitoEditorEnvelopeApiImpl";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { I18nService } from "@kogito-tooling/i18n/dist/envelope";
import { Envelope } from "@kogito-tooling/envelope";
import { EditorEnvelopeView } from "./EditorEnvelopeView";
import { EditorEnvelopeI18n, editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries } from "./i18n";
import { I18n } from "@kogito-tooling/i18n/dist/core";

/**
 * Starts the Editor envelope at a given container. Uses `bus` to send messages out of the Envelope and creates Editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.bus The implementation of EnvelopeBus to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors provided by this EditorEnvelope.
 * @param args.editorContext The context for Editors with information about the running channel.
 */
export function init(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  editorFactory: EditorFactory;
  editorContext: EditorContext;
}) {
  const i18n = new I18n<EditorEnvelopeI18n>(editorEnvelopeI18nDefaults, editorEnvelopeI18nDictionaries);
  const kogitoEditorFactory = new KogitoEditorEnvelopeApiFactory(args.editorFactory, i18n);
  const defaultKeyboardShortcuts = new DefaultKeyboardShortcutsService({ os: args.editorContext.operatingSystem });
  const i18nService = new I18nService();
  const envelope = new Envelope<
    KogitoEditorEnvelopeApi,
    KogitoEditorChannelApi,
    EditorEnvelopeView,
    KogitoEditorEnvelopeContextType
  >(args.bus);

  return new KogitoEditorEnvelope(args, kogitoEditorFactory, defaultKeyboardShortcuts, i18nService, envelope).start();
}

export * from "./CompositeEditorFactory";
