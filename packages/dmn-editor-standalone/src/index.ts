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

import dmnEnvelopeIndex from "!!raw-loader!../dist/envelope.html";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import {
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  EditorApi,
} from "@kie-tools-core/editor/dist/api";
import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { ContentType } from "@kie-tools-core/workspace/dist/api";
import { StandaloneDmnEditorChannelApiImpl } from "./StandaloneDmnEditorChannelApiImpl";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export interface StandaloneEditorApi extends EditorApi {
  subscribeToContentChanges: StateControl["subscribe"];
  unsubscribeToContentChanges: StateControl["unsubscribe"];
  markAsSaved: StateControl["setSavedCommand"];
  envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>;
  close: () => void;
}

export const createEditor = (
  envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>,
  stateControl: StateControl,
  listener: (message: MessageEvent) => void,
  iframe: HTMLIFrameElement
): StandaloneEditorApi => {
  return {
    undo: () => {
      stateControl.undo();
      return Promise.resolve(envelopeApi.notifications.kogitoEditor_editorUndo.send());
    },
    redo: () => {
      stateControl.redo();
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

const createEnvelopeServer = (iframe: HTMLIFrameElement, readOnly?: boolean, origin?: string) => {
  const defaultOrigin = window.location.protocol === "file:" ? "*" : window.location.origin;

  return new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
    { postMessage: (message) => iframe.contentWindow?.postMessage(message, "*") },
    origin ?? defaultOrigin,
    (self) => {
      return self.envelopeApi.requests.kogitoEditor_initRequest(
        {
          origin: self.origin,
          envelopeServerId: self.id,
        },
        {
          resourcesPathPrefix: "",
          fileExtension: "dmn",
          initialLocale: "en-US",
          isReadOnly: readOnly ?? true,
          channel: ChannelType.EMBEDDED,
          workspaceRootAbsolutePosixPath: DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
        }
      );
    }
  );
};

export function open(args: {
  container: Element;
  initialContent: Promise<string>;
  readOnly?: boolean;
  origin?: string;
  onError?: () => any;
  resources?: Map<string, { contentType: ContentType; content: Promise<string> }>;
}): StandaloneEditorApi {
  const iframe = document.createElement("iframe");
  iframe.srcdoc = dmnEnvelopeIndex;
  iframe.style.width = "100%";
  iframe.style.height = "100%";
  iframe.style.border = "none";

  const envelopeServer = createEnvelopeServer(iframe, args.readOnly, args.origin);

  const stateControl = new StateControl();

  let receivedSetContentError = false;

  const channelApiImpl = new StandaloneDmnEditorChannelApiImpl(
    stateControl,
    {
      normalizedPosixPathRelativeToTheWorkspaceRoot: "", // FIXME: https://github.com/apache/incubator-kie-issues/issues/811
      fileName: "file.dmn", // FIXME: https://github.com/apache/incubator-kie-issues/issues/811
      fileExtension: "dmn",
      getFileContents: () => Promise.resolve(args.initialContent),
      isReadOnly: args.readOnly ?? false,
    },
    "en-US",
    {
      kogitoEditor_setContentError() {
        if (!receivedSetContentError) {
          args.onError?.();
          receivedSetContentError = true;
        }
      },
    },
    args.resources
  );

  const listener = (message: MessageEvent) => {
    envelopeServer.receive(message.data, channelApiImpl);
  };

  window.addEventListener("message", listener);

  args.container.appendChild(iframe);
  envelopeServer.startInitPolling(channelApiImpl);

  const editor = createEditor(envelopeServer.envelopeApi, stateControl, listener, iframe);

  return editor;
}

declare global {
  interface Window {
    DmnEditor: { open: typeof open };
  }
}

window.DmnEditor = { open };
