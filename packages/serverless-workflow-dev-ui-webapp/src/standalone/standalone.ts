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

import devUIEnvelopeIndex from "!!raw-loader!../../resources/iframe.html";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { RuntimeToolsDevUIChannelApi, RuntimeToolsDevUIEnvelopeApi } from "../api";
import { RuntimeToolsDevUIChannelApiImpl } from "../standalone/RuntimeToolsDevUIChannelApiImpl";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";

export interface StandaloneDevUIApi {
  close: () => void;
}

export interface Consoles {
  open: (args: {
    container: Element;
    dataIndexUrl: string;
    page: string;
    devUIUrl: string;
    openApiBaseUrl: string;
    openApiPath: string;
    origin?: string;
    availablePages?: string[];
    omittedWorkflowTimelineEvents?: string[];
    diagramPreviewSize?: DiagramPreviewSize;
    isStunnerEnabled: boolean;
  }) => StandaloneDevUIApi;
}

const createEnvelopeServer = (
  iframe: HTMLIFrameElement,
  isDataIndexAvailable: boolean,
  dataIndexUrl: string,
  page: string,
  devUIUrl: string,
  openApiBaseUrl: string,
  openApiPath: string,
  isStunnerEnabled: boolean,
  diagramPreviewSize?: DiagramPreviewSize,
  origin?: string,
  availablePages?: string[],
  omittedWorkflowTimelineEvents?: string[]
) => {
  const defaultOrigin = window.location.protocol === "file:" ? "*" : window.location.origin;
  return new EnvelopeServer<RuntimeToolsDevUIChannelApi, RuntimeToolsDevUIEnvelopeApi>(
    {
      postMessage: (message) => iframe.contentWindow?.postMessage(message, origin ?? defaultOrigin),
    },
    origin ?? defaultOrigin,
    (self) => {
      return self.envelopeApi.requests.runtimeToolsDevUI_initRequest(
        {
          origin: self.origin,
          envelopeServerId: self.id,
        },
        {
          isDataIndexAvailable,
          dataIndexUrl,
          page,
          devUIUrl,
          openApiBaseUrl,
          openApiPath,
          availablePages,
          omittedWorkflowTimelineEvents,
          isStunnerEnabled,
          diagramPreviewSize,
        }
      );
    }
  );
};

declare global {
  interface Window {
    RuntimeToolsDevUI: Consoles;
  }
}

export const createDevUI = (
  envelopeServer: EnvelopeServer<RuntimeToolsDevUIChannelApi, RuntimeToolsDevUIEnvelopeApi>,
  listener: (message: MessageEvent) => void,
  iframe: HTMLIFrameElement
): any => {
  return {
    envelopeApi: envelopeServer.envelopeApi,
    close: () => {
      window.removeEventListener("message", listener);
      iframe.remove();
    },
  };
};

export function open(args: {
  container: Element;
  isDataIndexAvailable: boolean;
  dataIndexUrl: string;
  page: string;
  devUIUrl: string;
  openApiBaseUrl: string;
  openApiPath: string;
  origin?: string;
  availablePages?: string[];
  omittedWorkflowTimelineEvents?: string[];
  isStunnerEnabled: boolean;
  diagramPreviewSize?: DiagramPreviewSize;
}): StandaloneDevUIApi {
  const iframe = document.createElement("iframe");
  iframe.srcdoc = devUIEnvelopeIndex; // index coming from webapp
  iframe.id = "iframe";
  iframe.style.width = "100%";
  iframe.style.height = "100%";
  iframe.style.border = "none";

  const envelopeServer = createEnvelopeServer(
    iframe,
    args.isDataIndexAvailable,
    args.dataIndexUrl,
    args.page,
    args.devUIUrl,
    args.openApiBaseUrl,
    args.openApiPath,
    args.isStunnerEnabled,
    args.diagramPreviewSize,
    args.origin,
    args.availablePages,
    args.omittedWorkflowTimelineEvents ?? []
  );
  const channelApi = new RuntimeToolsDevUIChannelApiImpl();
  const listener = (message: MessageEvent) => {
    envelopeServer.receive(message.data, channelApi);
  };
  window.addEventListener("message", listener);

  args.container.appendChild(iframe);
  envelopeServer.startInitPolling(channelApi);

  return createDevUI(envelopeServer, listener, iframe);
}

window.RuntimeToolsDevUI = { open };
