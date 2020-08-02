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

import { EditorContext } from "@kogito-tooling/editor-envelope-protocol";
import { EditorFactory } from "@kogito-tooling/editor-api";
import { KogitoEditorEnvelope } from "./editor/KogitoEditorEnvelope";
import { EnvelopeBus } from "@kogito-tooling/envelope-bus/dist/api";

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
  return new KogitoEditorEnvelope(args).start();
}

export * from "./CompositeEditorFactory";
