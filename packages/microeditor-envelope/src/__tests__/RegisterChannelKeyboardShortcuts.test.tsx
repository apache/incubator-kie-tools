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
import { fireEvent, render } from "@testing-library/react";
import { RegisterChannelKeyboardShortcuts } from "../RegisterChannelKeyboardShortcuts";
import { DefaultKeyboardShortcutsService, KeyboardShortcutsApi } from "@kogito-tooling/keyboard-shortcuts";
import { StateControl } from "../api/stateControl";
import {
  ChannelType,
  EditorContent,
  LanguageData,
  OperatingSystem,
  ResourceContent,
  ResourcesList
} from "@kogito-tooling/core-api";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";

describe("RegisterChannelKeyboardShortcuts::stateControl", () => {
  let keyboardShortcuts: KeyboardShortcutsApi;
  let stateControl: StateControl;
  let messageBus;

  beforeEach(() => {
    keyboardShortcuts = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });
    stateControl = new StateControl();

    const receivedMessages: any[] = [];
    const sentMessages: any[] = [];

    messageBus = new EnvelopeBusInnerMessageHandler(
      {
        postMessage: (message, targetOrigin) => sentMessages.push([message, targetOrigin])
      },
      self => ({
        receive_contentResponse: (content: EditorContent) => {
          receivedMessages.push(["contentResponse", content]);
        },
        receive_languageResponse: (languageData: LanguageData) => {
          receivedMessages.push(["languageResponse", languageData]);
        },
        receive_contentRequest: () => {
          receivedMessages.push(["contentRequest", undefined]);
        },
        receive_resourceContentResponse: (content: ResourceContent) => {
          receivedMessages.push(["resourceContent", content]);
        },
        receive_resourceContentList: (resourcesList: ResourcesList) => {
          receivedMessages.push(["resourceContentList", resourcesList]);
        },
        receive_editorRedo(): void {
          receivedMessages.push(["notify_editorRedo", undefined]);
        },
        receive_editorUndo(): void {
          receivedMessages.push(["notify_editorUndo", undefined]);
        },
        receive_previewRequest: () => {
          receivedMessages.push(["receive_previewRequest"]);
        }
      })
    );
  });

  test.skip("undo", () => {
    stateControl.undo = jest.fn(() => null);

    const container = render(
      <div data-testid="register-keyboard-shortcuts">
        <RegisterChannelKeyboardShortcuts
          keyboardShortcuts={keyboardShortcuts}
          stateControl={stateControl}
          messageBus={messageBus}
        />
      </div>
    );

    fireEvent.keyDown(container.getByTestId("register-keyboard-shortcuts"), {
      keyCode: "KeyZ",
      ctrlKey: true
    });

    expect(stateControl.undo).toBeCalled();
  });
});
