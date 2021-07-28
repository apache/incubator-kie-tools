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

import "@patternfly/react-core/dist/styles/base.css";
import "./styles.scss";
import {
  Editor,
  EditorFactory,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "../api";
import { KogitoEditorEnvelope } from "./KogitoEditorEnvelope";
import { ApiDefinition, EnvelopeBus } from "@kie-tooling-core/envelope-bus/dist/api";
import { KogitoEditorEnvelopeApiImpl } from "./KogitoEditorEnvelopeApiImpl";
import { DefaultKeyboardShortcutsService } from "@kie-tooling-core/keyboard-shortcuts/dist/envelope";
import { I18nService } from "@kie-tooling-core/i18n/dist/envelope";
import { Envelope, EnvelopeApiFactory } from "@kie-tooling-core/envelope";
import { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
import { getOperatingSystem } from "@kie-tooling-core/operating-system";

/**
 * Starts the Editor envelope at a given container. Uses `bus` to send messages out of the Envelope and creates Editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.bus The implementation of EnvelopeBus to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors provided by this EditorEnvelope.
 */
export function init(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  editorFactory: EditorFactory<Editor, KogitoEditorChannelApi>;
}) {
  initCustom({
    container: args.container,
    bus: args.bus,
    apiImplFactory: {
      create: (createArgs) => new KogitoEditorEnvelopeApiImpl(createArgs, args.editorFactory),
    },
  });
}

export function initCustom<
  E extends Editor,
  EnvelopeApi extends KogitoEditorEnvelopeApi & ApiDefinition<EnvelopeApi>,
  ChannelApi extends KogitoEditorChannelApi & ApiDefinition<ChannelApi>
>(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  apiImplFactory: EnvelopeApiFactory<
    EnvelopeApi,
    ChannelApi,
    EditorEnvelopeViewApi<E>,
    KogitoEditorEnvelopeContextType<ChannelApi>
  >;
}) {
  const defaultKeyboardShortcuts = new DefaultKeyboardShortcutsService({ os: getOperatingSystem() });
  const i18nService = new I18nService();
  const envelope = new Envelope<
    EnvelopeApi,
    ChannelApi,
    EditorEnvelopeViewApi<E>,
    KogitoEditorEnvelopeContextType<ChannelApi>
  >(args.bus);

  return new KogitoEditorEnvelope(args.apiImplFactory, defaultKeyboardShortcuts, i18nService, envelope).start(
    args.container
  );
}
export { EditorEnvelopeViewApi } from "./EditorEnvelopeView";
export * from "./KogitoEditorEnvelopeApiImpl";
