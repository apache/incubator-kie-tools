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

import { EditorApi, KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { SwfPreviewOptions } from "@kie-tools/serverless-workflow-combined-editor/dist/api";

export interface StandaloneEditorApi extends EditorApi {
  subscribeToContentChanges: StateControl["subscribe"];
  unsubscribeToContentChanges: StateControl["unsubscribe"];
  markAsSaved: StateControl["setSavedCommand"];
  envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>;
  close: () => void;
}

export type ServerlessWorkflowType = "json" | "yaml" | "yml";
export interface Editor {
  open: (args: {
    container: Element;
    initialContent: Promise<string>;
    readOnly: boolean;
    languageType: ServerlessWorkflowType;
    swfPreviewOptions: SwfPreviewOptions;
    origin?: string;
  }) => StandaloneEditorApi;
}

export const createEditor = (
  envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>,
  stateControl: StateControl,
  listener: (message: MessageEvent) => void,
  iframe: HTMLIFrameElement
): StandaloneEditorApi => {
  return {
    undo: () => {
      return Promise.resolve(envelopeApi.notifications.kogitoEditor_editorUndo.send());
    },
    redo: () => {
      return Promise.resolve(envelopeApi.notifications.kogitoEditor_editorRedo.send());
    },
    close: () => {
      window.removeEventListener("message", listener);
      iframe.remove();
    },
    getContent: () => envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content),
    getPreview: () => envelopeApi.requests.kogitoEditor_previewRequest(),
    setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot, content) =>
      envelopeApi.requests.kogitoEditor_contentChanged(
        { normalizedPosixPathRelativeToTheWorkspaceRoot, content },
        { showLoadingOverlay: true }
      ),
    subscribeToContentChanges: (callback) => stateControl.subscribe(callback),
    unsubscribeToContentChanges: (callback) => stateControl.unsubscribe(callback),
    markAsSaved: () => stateControl.setSavedCommand(),
    validate: () => envelopeApi.requests.kogitoEditor_validate(),
    setTheme: (theme) => Promise.resolve(),
    envelopeApi,
  };
};
