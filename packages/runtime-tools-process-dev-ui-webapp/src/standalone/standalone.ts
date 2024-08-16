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

export type StandaloneDevUIArgs = {
  container: Element;
  isDataIndexAvailable: boolean;
  users: User[];
  dataIndexUrl?: string;
  quarkusAppOrigin: string;
  quarkusAppRootPath?: string;
  shouldReplaceQuarkusAppOriginWithWebappOrigin?: boolean;
  page: string;
  devUIOrigin: string;
  devUIUrl: string;
  origin?: string;
  availablePages?: string[];
  customLabels?: CustomLabels;
  omittedProcessTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
};

export type StandAloneDevUIEnvelopeServerArgs = Omit<StandaloneDevUIArgs, "container"> & {
  dataIndexUrl: string;
  customLabels: CustomLabels;
};

const createEnvelopeServer = (iframe: HTMLIFrameElement, args: StandAloneDevUIEnvelopeServerArgs) => {
  const defaultOrigin = window.location.protocol === "file:" ? "*" : window.location.origin;

  return new EnvelopeServer<RuntimeToolsDevUIChannelApi, RuntimeToolsDevUIEnvelopeApi>(
    {
      postMessage: (message) => iframe.contentWindow?.postMessage(message, args.origin ?? defaultOrigin),
    },
    args.origin ?? defaultOrigin,
    (self) => {
      return self.envelopeApi.requests.runtimeToolsDevUI_initRequest(
        {
          origin: self.origin,
          envelopeServerId: self.id,
        },
        args
      );
    }
  );
};

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

export function open(args: StandaloneDevUIArgs): StandaloneDevUIApi {
  const iframe = document.createElement("iframe");
  iframe.srcdoc = devUIEnvelopeIndex; // index coming from webapp
  iframe.id = "iframe";
  iframe.style.width = "100%";
  iframe.style.height = "100%";
  iframe.style.border = "none";

  const envelopeArgs = {
    ...args,
    container: undefined,
  };

  const envelopeServer = createEnvelopeServer(iframe, {
    ...envelopeArgs,
    dataIndexUrl: args.dataIndexUrl ?? process.env.KOGITO_DATAINDEX_HTTP_URL,
    customLabels: args.customLabels ?? {
      singularProcessLabel: "Process",
      pluralProcessLabel: "Processes",
    },
    omittedProcessTimelineEvents: args.omittedProcessTimelineEvents ?? [],
  });

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

declare global {
  interface Window {
    RuntimeToolsDevUI: {
      open: typeof open;
    };
  }
}
