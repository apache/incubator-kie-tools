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

import { KogitoEditorChannel } from "./KogitoEditorChannel";
import { useEffect } from "react";
import { EditorInitArgs } from "./KogitoEditorEnvelopeApi";
import { KogitoEditorChannelApi } from "./KogitoEditorChannelApi";

export function useConnectedKogitoEditorChannel(
    channel: KogitoEditorChannel,
    api: KogitoEditorChannelApi,
    targetOrigin: string,
    editorInitArgs: EditorInitArgs
) {
  useEffect(() => {
    const listener = (msg: MessageEvent) => channel.receive(msg.data, api);
    window.addEventListener("message", listener, false);
    channel.startInitPolling(targetOrigin, editorInitArgs);

    return () => {
      channel.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, [channel, targetOrigin, editorInitArgs, api]);
}
