/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import { RuntimeToolsDevUIChannelApi, RuntimeToolsDevUIEnvelopeApi, RuntimeToolsDevUIInitArgs, User } from "../api";
import { RuntimeToolsDevUIChannelApiImpl } from "../standalone/RuntimeToolsDevUIChannelApiImpl";
import { CustomLabels } from "../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";

export interface StandaloneDevUIApi {
  close: () => void;
}

export interface Consoles {
  open: (args: {
    container: Element;
    users: User[];
    dataIndexUrl?: string;
    page: string;
    devUIUrl: string;
    remoteKogitoAppUrl?: string;
    openApiPath?: string;
    origin?: string;
    availablePages?: string[];
    customLabels?: CustomLabels;
    omittedProcessTimelineEvents?: string[];
    diagramPreviewSize?: DiagramPreviewSize;
  }) => StandaloneDevUIApi;
}

const createEnvelopeServer = (
  iframe: HTMLIFrameElement,
  isDataIndexAvailable: boolean,
  users: User[],
  dataIndexUrl: string,
  page: string,
  devUIUrl: string,
  openApiPath: string,
  remoteKogitoAppUrl: string,
  customLabels: CustomLabels,
  diagramPreviewSize?: DiagramPreviewSize,
  origin?: string,
  availablePages?: string[],
  omittedProcessTimelineEvents?: string[]
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
          users,
          dataIndexUrl,
          page,
          devUIUrl,
          openApiPath,
          customLabels,
          availablePages,
          omittedProcessTimelineEvents,
          diagramPreviewSize,
          remoteKogitoAppUrl,
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
  users: User[];
  dataIndexUrl?: string;
  remoteKogitoAppUrl?: string;
  page: string;
  devUIUrl: string;
  openApiPath?: string;
  origin?: string;
  availablePages?: string[];
  customLabels?: CustomLabels;
  omittedProcessTimelineEvents?: string[];
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
    args.users,
    args.dataIndexUrl ?? process.env.KOGITO_DATAINDEX_HTTP_URL,
    args.page,
    args.devUIUrl,
    args.openApiPath ?? process.env.KOGITO_OPENAPI_PATH,
    args.remoteKogitoAppUrl ?? process.env.KOGITO_REMOTE_KOGITO_APP_URL,
    args.customLabels ?? {
      singularProcessLabel: "Process",
      pluralProcessLabel: "Processes",
    },
    args.diagramPreviewSize,
    args.origin,
    args.availablePages,
    args.omittedProcessTimelineEvents ?? []
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
