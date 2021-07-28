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
import { useSyncedKeyboardEvents } from "@kie-tooling-core/keyboard-shortcuts/dist/channel";
import { MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";
import { KeyboardShortcutsEnvelopeApi } from "@kie-tooling-core/keyboard-shortcuts/dist/api";
import { messageBusClientApiMock } from "@kie-tooling-core/envelope-bus/dist-tests/common";

let envelopeApi: MessageBusClientApi<KeyboardShortcutsEnvelopeApi>;

beforeEach(() => {
  envelopeApi = messageBusClientApiMock();
});

describe("useSyncedKeyboardEvents", () => {
  test("EmbeddedEditor::notify keyboardEvent::keydown", async () => {
    renderHook(() => useSyncedKeyboardEvents(envelopeApi));
    window.dispatchEvent(new KeyboardEvent("keydown", { ctrlKey: true }));

    expect(envelopeApi.notifications.kogitoKeyboardShortcuts_channelKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: true,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keydown",
      channelOriginalTargetTagName: undefined,
    });
  });

  test("EmbeddedEditor::notify keyboardEvent::keyup", async () => {
    renderHook(() => useSyncedKeyboardEvents(envelopeApi));
    window.dispatchEvent(new KeyboardEvent("keyup", { altKey: true }));

    expect(envelopeApi.notifications.kogitoKeyboardShortcuts_channelKeyboardEvent).toBeCalledWith({
      altKey: true,
      ctrlKey: false,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keyup",
      channelOriginalTargetTagName: undefined,
    });
  });

  test("EmbeddedEditor::notify keyboardEvent::keypress", async () => {
    renderHook(() => useSyncedKeyboardEvents(envelopeApi));
    window.dispatchEvent(new KeyboardEvent("keypress", { shiftKey: true }));

    expect(envelopeApi.notifications.kogitoKeyboardShortcuts_channelKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: false,
      shiftKey: true,
      metaKey: false,
      code: "",
      type: "keypress",
      channelOriginalTargetTagName: undefined,
    });
  });

  test("EmbeddedEditor::notify_keyboardEvent::keydown::metakey", async () => {
    renderHook(() => useSyncedKeyboardEvents(envelopeApi, document.body));
    fireEvent(document.body, new KeyboardEvent("keydown", { metaKey: true, code: "KeyA" }));

    expect(envelopeApi.notifications.kogitoKeyboardShortcuts_channelKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: false,
      shiftKey: false,
      metaKey: true,
      code: "KeyA",
      type: "keydown",
      channelOriginalTargetTagName: "BODY",
    });
  });
});
