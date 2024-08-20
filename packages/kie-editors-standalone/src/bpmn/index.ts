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

// eslint-disable-next-line @typescript-eslint/triple-slash-reference
/// <reference path="../global.d.ts" /> // Required for bundling types

import bpmnEnvelopeIndex from "../../dist/resources/bpmn/bpmnEnvelopeIndex.html";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import {
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorChannelApi,
} from "@kie-tools-core/editor/dist/api";
import { StandaloneEditorsEditorChannelApiImpl } from "../envelope/StandaloneEditorsEditorChannelApiImpl";
import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { ContentType } from "@kie-tools-core/workspace/dist/api";
import { createEditor, Editor, StandaloneEditorApi } from "../common/Editor";
import { BpmnEditorDiagramApi } from "../jsdiagram/BpmnEditorDiagramApi";
import { BpmnEditorEnvelopeApi } from "@kie-tools/kie-bc-editors/dist/bpmn/api";

declare global {
  interface Window {
    BpmnEditor: Editor;
  }
}

const createEnvelopeServer = (iframe: HTMLIFrameElement, readOnly?: boolean, origin?: string) => {
  const defaultOrigin = window.location.protocol === "file:" ? "*" : window.location.origin;

  return new EnvelopeServer<KogitoEditorChannelApi, BpmnEditorEnvelopeApi>(
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
          fileExtension: "bpmn",
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
}): StandaloneEditorApi & BpmnEditorDiagramApi {
  const iframe = document.createElement("iframe");
  iframe.srcdoc = bpmnEnvelopeIndex;
  iframe.style.width = "100%";
  iframe.style.height = "100%";
  iframe.style.border = "none";

  const envelopeServer = createEnvelopeServer(iframe, args.readOnly, args.origin);

  const stateControl = new StateControl();

  let receivedSetContentError = false;

  const channelApiImpl = new StandaloneEditorsEditorChannelApiImpl(
    stateControl,
    {
      normalizedPosixPathRelativeToTheWorkspaceRoot: "", // FIXME: https://github.com/apache/incubator-kie-issues/issues/811
      fileName: "", // FIXME: https://github.com/apache/incubator-kie-issues/issues/811
      fileExtension: "bpmn",
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

  return {
    ...editor,
    canvas: {
      getNodeIds: () => {
        return envelopeServer.envelopeApi.requests.canvas_getNodeIds();
      },
      getBackgroundColor: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_getBackgroundColor(uuid);
      },
      setBackgroundColor: (uuid: string, backgroundColor: string) => {
        return envelopeServer.envelopeApi.requests.canvas_setBackgroundColor(uuid, backgroundColor);
      },
      getBorderColor: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_getBorderColor(uuid);
      },
      setBorderColor: (uuid: string, backgroundColor: string) => {
        return envelopeServer.envelopeApi.requests.canvas_setBorderColor(uuid, backgroundColor);
      },
      getLocation: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_getLocation(uuid);
      },
      getAbsoluteLocation: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_getAbsoluteLocation(uuid);
      },
      getDimensions: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_getDimensions(uuid);
      },
      applyState: (uuid: string, state: string) => {
        return envelopeServer.envelopeApi.requests.canvas_applyState(uuid, state);
      },
      centerNode: (uuid: string) => {
        return envelopeServer.envelopeApi.requests.canvas_centerNode(uuid);
      },
    },
  };
}

window.BpmnEditor = { open };
