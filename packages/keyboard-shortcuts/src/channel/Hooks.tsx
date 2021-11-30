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
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { ChannelKeyboardEvent, KeyboardShortcutsEnvelopeApi } from "../api";

function getChannelKeyboardEvent(keyboardEvent: KeyboardEvent): ChannelKeyboardEvent {
  return {
    altKey: keyboardEvent.altKey,
    ctrlKey: keyboardEvent.ctrlKey,
    shiftKey: keyboardEvent.shiftKey,
    metaKey: keyboardEvent.metaKey,
    code: keyboardEvent.code,
    type: keyboardEvent.type,
    channelOriginalTargetTagName: (keyboardEvent.target as HTMLElement)?.tagName,
  };
}

export function useElementsThatStopKeyboardEventsPropagation(
  element: HTMLElement | Window = window,
  selectors: string[]
) {
  const stopPropagation = (ev: KeyboardEvent) => {
    ev.stopPropagation();
  };

  const target = element === window ? document : (element as HTMLElement);
  const elementsStoppingPropagation = selectors.flatMap((selector) => {
    const es = Array.from(target.querySelectorAll(selector));
    for (const e of es) {
      e.addEventListener("keydown", stopPropagation);
      e.addEventListener("keyup", stopPropagation);
      e.addEventListener("keypress", stopPropagation);
    }
    return es;
  });

  return () => {
    elementsStoppingPropagation?.forEach((e) => {
      e.removeEventListener("keydown", stopPropagation);
      e.removeEventListener("keyup", stopPropagation);
      e.removeEventListener("keypress", stopPropagation);
    });
  };
}

export function useSyncedKeyboardEvents(
  envelopeApi: MessageBusClientApi<KeyboardShortcutsEnvelopeApi>,
  element: HTMLElement | Window = window
) {
  useEffect(() => {
    const listener = (keyboardEvent: KeyboardEvent) => {
      const channelKeyboardEvent = getChannelKeyboardEvent(keyboardEvent);
      console.debug(`New keyboard event (${JSON.stringify(channelKeyboardEvent)})!`);
      envelopeApi.notifications.kogitoKeyboardShortcuts_channelKeyboardEvent.send(channelKeyboardEvent);
    };

    element.addEventListener("keydown", listener);
    element.addEventListener("keyup", listener);
    element.addEventListener("keypress", listener);
    return () => {
      element.removeEventListener("keydown", listener);
      element.removeEventListener("keyup", listener);
      element.removeEventListener("keypress", listener);
    };
  }, [envelopeApi, element]);
}
