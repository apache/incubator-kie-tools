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
import { fireEvent } from "@testing-library/react";
import { renderHook } from "@testing-library/react-hooks";
import { useSyncedKeyboardEvents } from "../Hooks";
import { MessageBusClient } from "@kogito-tooling/envelope-bus/dist/api";
import { KeyboardShortcutsEnvelopeApi } from "../../api";

let messageBusClient: MessageBusClient<KeyboardShortcutsEnvelopeApi>;

beforeEach(() => {
  messageBusClient = {
    notify: jest.fn(),
    request: jest.fn(),
    subscribe: jest.fn(),
    unsubscribe: jest.fn()
  };
});

describe("useSyncedKeyboardEvents", () => {
  test("EmbeddedEditor::notify keyboardEvent::keydown", async () => {
    renderHook(() => useSyncedKeyboardEvents(messageBusClient));
    window.dispatchEvent(new KeyboardEvent("keydown", { ctrlKey: true }));

    expect(messageBusClient.notify).toBeCalledWith("receive_channelKeyboardEvent", {
      altKey: false,
      ctrlKey: true,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keydown",
      channelOriginalTargetTagName: undefined
    });
  });

  test("EmbeddedEditor::notify keyboardEvent::keyup", async () => {
    renderHook(() => useSyncedKeyboardEvents(messageBusClient));
    window.dispatchEvent(new KeyboardEvent("keyup", { altKey: true }));

    expect(messageBusClient.notify).toBeCalledWith("receive_channelKeyboardEvent", {
      altKey: true,
      ctrlKey: false,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keyup",
      channelOriginalTargetTagName: undefined
    });
  });

  test("EmbeddedEditor::notify keyboardEvent::keypress", async () => {
    renderHook(() => useSyncedKeyboardEvents(messageBusClient));
    window.dispatchEvent(new KeyboardEvent("keypress", { shiftKey: true }));

    expect(messageBusClient.notify).toBeCalledWith("receive_channelKeyboardEvent", {
      altKey: false,
      ctrlKey: false,
      shiftKey: true,
      metaKey: false,
      code: "",
      type: "keypress",
      channelOriginalTargetTagName: undefined
    });
  });

  test("EmbeddedEditor::notify_keyboardEvent::keydown::metakey", async () => {
    renderHook(() => useSyncedKeyboardEvents(messageBusClient, document.body));
    fireEvent(document.body, new KeyboardEvent("keydown", { metaKey: true, code: "KeyA" }));

    expect(messageBusClient.notify).toBeCalledWith("receive_channelKeyboardEvent", {
      altKey: false,
      ctrlKey: false,
      shiftKey: false,
      metaKey: true,
      code: "KeyA",
      type: "keydown",
      channelOriginalTargetTagName: "BODY"
    });
  });
});
