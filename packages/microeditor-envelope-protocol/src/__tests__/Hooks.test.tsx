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
import { renderHook } from "@testing-library/react-hooks";
import { useSyncedKeyboardEvents } from "../Hooks";
import { getTestEnvelopeBusOuterMessageHandler, TestEnvelopeBusOuterMessageHandler } from "./utils";

describe("useSyncedKeyboardEvents", () => {
  let testEnvelopeBusOuterMessageHandler: TestEnvelopeBusOuterMessageHandler;

  beforeEach(() => {
    testEnvelopeBusOuterMessageHandler = getTestEnvelopeBusOuterMessageHandler();
  });

  test("EmbeddedEditor::notify_keyboardEvent::keydown", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(
      testEnvelopeBusOuterMessageHandler.handler,
      "notify_channelKeyboardEvent"
    );

    renderHook(() => useSyncedKeyboardEvents(testEnvelopeBusOuterMessageHandler.handler));
    window.dispatchEvent(new KeyboardEvent("keydown", { ctrlKey: true }));

    expect(spyNotify_notifyKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: true,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keydown",
      channelOriginalTargetTagName: undefined
    });
  });

  test("EmbeddedEditor::notify_keyboardEvent::keyup", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(
      testEnvelopeBusOuterMessageHandler.handler,
      "notify_channelKeyboardEvent"
    );

    renderHook(() => useSyncedKeyboardEvents(testEnvelopeBusOuterMessageHandler.handler));
    window.dispatchEvent(new KeyboardEvent("keyup", { altKey: true }));

    expect(spyNotify_notifyKeyboardEvent).toBeCalledWith({
      altKey: true,
      ctrlKey: false,
      shiftKey: false,
      metaKey: false,
      code: "",
      type: "keyup",
      channelOriginalTargetTagName: undefined
    });
  });

  test("EmbeddedEditor::notify_keyboardEvent::keyup", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(
      testEnvelopeBusOuterMessageHandler.handler,
      "notify_channelKeyboardEvent"
    );

    renderHook(() => useSyncedKeyboardEvents(testEnvelopeBusOuterMessageHandler.handler));
    window.dispatchEvent(new KeyboardEvent("keypress", { shiftKey: true }));

    expect(spyNotify_notifyKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: false,
      shiftKey: true,
      metaKey: false,
      code: "",
      type: "keypress",
      channelOriginalTargetTagName: undefined
    });
  });
});
