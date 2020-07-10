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
import { EnvelopeBusMessage } from "../EnvelopeBusMessage";
import { EnvelopeBusOuterMessageHandler } from "../EnvelopeBusOuterMessageHandler";
import {
  EditorContent,
  KogitoEdit,
  ResourceContentRequest,
  ResourceListRequest,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Rect, Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";

let sentMessages: Array<EnvelopeBusMessage<any>>;
let receivedMessages: string[];
let handler: EnvelopeBusOuterMessageHandler;
let initPollCount: number;

beforeEach(() => {
  sentMessages = [];
  receivedMessages = [];
  initPollCount = 0;

  handler = new EnvelopeBusOuterMessageHandler(
    {
      postMessage: msg => sentMessages.push(msg)
    },
    self => ({
      pollInit: () => {
        initPollCount++;
      },
      receive_languageRequest() {
        receivedMessages.push("languageRequest");
      },
      receive_contentRequest() {
        receivedMessages.push("contentRequest");
      },
      receive_contentResponse(content: EditorContent) {
        receivedMessages.push("contentResponse_" + content.content);
      },
      receive_setContentError: (errorMessage: string) => {
        receivedMessages.push("setContentError_" + errorMessage);
      },
      receive_dirtyIndicatorChange(isDirty: boolean): void {
        receivedMessages.push("dirtyIndicatorChange_" + isDirty);
      },
      receive_resourceContentRequest(resourceContentRequest: ResourceContentRequest): void {
        receivedMessages.push("resourceContentRequest_" + resourceContentRequest.path);
      },
      receive_readResourceContentError(errorMessage: string): void {
        receivedMessages.push("readResourceContentError_" + errorMessage);
      },
      receive_resourceListRequest(resourceListRequest: ResourceListRequest): void {
        receivedMessages.push("resourceListRequest_" + resourceListRequest);
      },
      receive_ready() {
        receivedMessages.push("ready");
      },
      notify_editorUndo() {
        receivedMessages.push("undo");
      },
      notify_editorRedo() {
        receivedMessages.push("redo");
      },
      receive_newEdit(edit: KogitoEdit) {
        receivedMessages.push("receiveNewEdit_" + edit.id);
      },
      receive_openFile(path: string): void {
        receivedMessages.push("receiveOpenFile_" + path);
      },
      receive_previewRequest(previewSvg: string) {
        receivedMessages.push("preview");
      },
      receive_stateControlCommandUpdate(command: StateControlCommand) {
        receivedMessages.push("receiveStateControlEvent_" + command);
      },
      receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
        receivedMessages.push("guidedTour_UserInteraction");
      },
      receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
        receivedMessages.push("guidedTour_RegisterTutorial");
      },
      receive_guidedTourElementPositionResponse(position: Rect) {
        receivedMessages.push("guidedTour_ElementPositionRequest");
      }
    })
  );
});

describe("useSyncedKeyboardEvents", () => {
  test("EmbeddedEditor::notify_keyboardEvent::keydown", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(handler, "notify_channelKeyboardEvent");

    renderHook(() => useSyncedKeyboardEvents(handler));
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
    const spyNotify_notifyKeyboardEvent = jest.spyOn(handler, "notify_channelKeyboardEvent");

    renderHook(() => useSyncedKeyboardEvents(handler));
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

  test("EmbeddedEditor::notify_keyboardEvent::keypress", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(handler, "notify_channelKeyboardEvent");

    renderHook(() => useSyncedKeyboardEvents(handler));
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

  test("EmbeddedEditor::notify_keyboardEvent::keypress::metakey", async () => {
    const spyNotify_notifyKeyboardEvent = jest.spyOn(handler, "notify_channelKeyboardEvent");

    renderHook(() => useSyncedKeyboardEvents(handler));
    window.dispatchEvent(new KeyboardEvent("keypress", { metaKey: true }));

    expect(spyNotify_notifyKeyboardEvent).toBeCalledWith({
      altKey: false,
      ctrlKey: false,
      shiftKey: false,
      metaKey: true,
      code: "",
      type: "keypress",
      channelOriginalTargetTagName: undefined
    });
  });
});
