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

import { ContentType } from "@kie-tooling-core/workspace/dist/api";
import { EditorApi, KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "@kie-tooling-core/editor/dist/api";
import { StateControl } from "@kie-tooling-core/editor/dist/channel";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { EnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/channel";
import { Rect } from "@kie-tooling-core/guided-tour/dist/api";

export interface StandaloneEditorApi extends EditorApi {
  subscribeToContentChanges: StateControl["subscribe"];
  unsubscribeToContentChanges: StateControl["unsubscribe"];
  markAsSaved: StateControl["setSavedCommand"];
  envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>;
  close: () => void;
}

export interface Editor {
  open: (args: {
    container: Element;
    initialContent: Promise<string>;
    readOnly: boolean;
    origin?: string;
    resources?: Map<string, { contentType: ContentType; content: Promise<string> }>;
  }) => StandaloneEditorApi;
}

export const createEditor = (
  envelopeServer: EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>,
  stateControl: StateControl,
  listener: (message: MessageEvent) => void,
  iframe: HTMLIFrameElement
) => {
  return {
    getElementPosition: (selector: string): Promise<Rect> =>
      envelopeServer.envelopeApi.requests.kogitoGuidedTour_guidedTourElementPositionRequest(selector),
    undo: () => {
      stateControl.undo();
      return Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorUndo());
    },
    redo: () => {
      stateControl.redo();
      return Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorRedo());
    },
    getContent: () => envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content),
    getPreview: () => envelopeServer.envelopeApi.requests.kogitoEditor_previewRequest(),
    setContent: (path: string, content: string) =>
      envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged({
        path: path,
        content: content,
      }),
    subscribeToContentChanges: (callback: (isDirty: boolean) => void) => stateControl.subscribe(callback),
    unsubscribeToContentChanges: (callback: (isDirty: boolean) => void) => stateControl.unsubscribe(callback),
    markAsSaved: () => stateControl.setSavedCommand(),
    envelopeApi: envelopeServer.envelopeApi,
    close: () => {
      window.removeEventListener("message", listener);
      iframe.remove();
    },
    validate: () => {
      return envelopeServer.envelopeApi.requests.kogitoEditor_validate();
    },
  };
};
