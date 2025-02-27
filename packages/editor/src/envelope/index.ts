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

import "./styles.scss";
import {
  Editor,
  EditorFactory,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "../api";
import { KogitoEditorEnvelope } from "./KogitoEditorEnvelope";
import { ApiDefinition, EnvelopeBus } from "@kie-tools-core/envelope-bus/dist/api";
import { KogitoEditorEnvelopeApiImpl } from "./KogitoEditorEnvelopeApiImpl";
import { DefaultKeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { Envelope, EnvelopeApiFactory } from "@kie-tools-core/envelope";
import { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import { getOperatingSystem } from "@kie-tools-core/operating-system";
import { KeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/KeyboardShortcutsService";

/**
 * Starts the Editor envelope at a given container. Uses `bus` to send messages out of the Envelope and creates Editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.bus The implementation of EnvelopeBus to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors provided by this EditorEnvelope.
 * @param args.keyboardShortcutsService Custom KeyboardShortcutsService to use, otherwise DefaultKeyboardShortcutsService will be used.
 */
export function init(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  editorFactory: EditorFactory<Editor, KogitoEditorEnvelopeApi, KogitoEditorChannelApi>;
  keyboardShortcutsService?: KeyboardShortcutsService;
}) {
  initCustom({
    container: args.container,
    bus: args.bus,
    apiImplFactory: {
      create: (createArgs) => new KogitoEditorEnvelopeApiImpl(createArgs, args.editorFactory),
    },
    keyboardShortcutsService: args.keyboardShortcutsService,
  });
}

export function initCustom<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>,
>(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  apiImplFactory: EnvelopeApiFactory<
    EnvelopeApi,
    ChannelApi,
    EditorEnvelopeViewApi<E>,
    KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi>
  >;
  keyboardShortcutsService?: KeyboardShortcutsService;
}) {
  const keyboardShortcutsService =
    args.keyboardShortcutsService ?? new DefaultKeyboardShortcutsService({ os: getOperatingSystem() });
  const i18nService = new I18nService();
  const envelope = new Envelope<
    EnvelopeApi,
    ChannelApi,
    EditorEnvelopeViewApi<E>,
    KogitoEditorEnvelopeContextType<EnvelopeApi, ChannelApi>
  >(args.bus);

  return new KogitoEditorEnvelope(args.apiImplFactory, keyboardShortcutsService, i18nService, envelope).start(
    args.container
  );
}
export { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
export * from "./KogitoEditorEnvelopeApiImpl";
export * from "./LoadingScreen";
