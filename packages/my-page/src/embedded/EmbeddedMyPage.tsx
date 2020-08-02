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

import * as React from "react";
import { useImperativeHandle, useMemo, useRef } from "react";
import { MyPageChannelEnvelopeServer, MyPageMapping } from "../channel";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { EnvelopeBusMessage } from "@kogito-tooling/envelope-bus/dist/api";
import { MyPageApi, MyPageChannelApi } from "../api";
import { useConnectedEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/hooks";

interface Props {
  mapping: MyPageMapping;
  targetOrigin: string;
  channelType: ChannelType;
  api: MyPageChannelApi;
}

const EmbeddedMyPage: React.RefForwardingComponent<MyPageApi, Props> = (props, forwardedRef) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);

  const envelopeServer = new MyPageChannelEnvelopeServer(
    {
      postMessage<D, T>(message: EnvelopeBusMessage<D, T>) {
        iframeRef.current?.contentWindow?.postMessage(message, "*");
      }
    },
    props.targetOrigin,
    {
      backendUrl: "https://localhost:8000",
      filePath: undefined
    }
  );

  const myPageApi: MyPageApi = useMemo(
    () => ({
      setText: (text: string) => envelopeServer.client.notify("myPage__setText", text)
    }),
    []
  );

  useImperativeHandle(forwardedRef, () => myPageApi, [myPageApi]);

  useConnectedEnvelopeServer(envelopeServer, props.api);

  return (
    <iframe ref={iframeRef} src={props.mapping.envelopePath} title="MyPage" data-envelope-channel={props.channelType} />
  );
};
