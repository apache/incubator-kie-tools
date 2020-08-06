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

import { useEffect } from "react";
import {
  ChannelKeyboardEvent,
  KogitoEditorEnvelopeApi,
  MessageBusClient
} from "@kogito-tooling/microeditor-envelope-protocol";

function getChannelKeyboardEvent(keyboardEvent: KeyboardEvent): ChannelKeyboardEvent {
  return {
    altKey: keyboardEvent.altKey,
    ctrlKey: keyboardEvent.ctrlKey,
    shiftKey: keyboardEvent.shiftKey,
    metaKey: keyboardEvent.metaKey,
    code: keyboardEvent.code,
    type: keyboardEvent.type,
    channelOriginalTargetTagName: (keyboardEvent.target as HTMLElement)?.tagName
  };
}

export function useSyncedKeyboardEvents(
  messageBusClient: MessageBusClient<KogitoEditorEnvelopeApi>,
  element: HTMLElement | Window = window
) {
  useEffect(() => {
    const listener = (keyboardEvent: KeyboardEvent) => {
      const channelKeyboardEvent = getChannelKeyboardEvent(keyboardEvent);
      console.debug(`New keyboard event (${JSON.stringify(channelKeyboardEvent)})!`);
      messageBusClient.notify("receive_channelKeyboardEvent", channelKeyboardEvent);
    };

    element.addEventListener("keydown", listener);
    element.addEventListener("keyup", listener);
    element.addEventListener("keypress", listener);
    return () => {
      element.removeEventListener("keydown", listener);
      element.removeEventListener("keyup", listener);
      element.removeEventListener("keypress", listener);
    };
  }, [messageBusClient]);
}
