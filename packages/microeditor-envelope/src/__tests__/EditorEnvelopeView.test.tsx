/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { shallow } from "enzyme";
import { EditorEnvelopeView } from "../EditorEnvelopeView";
import { DummyEditor } from "./DummyEditor";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import {
  ChannelType,
  EditorContent,
  LanguageData,
  OperatingSystem,
  ResourceContent,
  ResourcesList
} from "@kogito-tooling/core-api";
import { StateControlService } from "../api/stateControl";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";

let loadingScreenContainer: HTMLElement;
beforeEach(() => (loadingScreenContainer = document.body.appendChild(document.createElement("div"))));
afterEach(() => loadingScreenContainer.remove());

function renderEditorEnvelopeView(): [EditorEnvelopeView, ReturnType<typeof shallow>] {
  let view: EditorEnvelopeView;
  const context = { channel: ChannelType.VSCODE, operatingSystem: OperatingSystem.WINDOWS };
  const receivedMessages: any[] = [];
  const sentMessages: any[] = [];
  const messageBus = new EnvelopeBusInnerMessageHandler(
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

  const render = shallow(
    <EditorEnvelopeView
      keyboardShortcuts={new DefaultKeyboardShortcutsService(context)}
      context={context}
      exposing={self => (view = self)}
      loadingScreenContainer={loadingScreenContainer}
      stateControlService={new StateControlService()}
      messageBus={messageBus}
    />
  );
  return [view!, render];
}

describe("EditorEnvelopeView", () => {
  test("first open", () => {
    const [_, render] = renderEditorEnvelopeView();
    expect(render).toMatchSnapshot();
  });

  test("after loading stops", async () => {
    const [view, render] = renderEditorEnvelopeView();
    await view.setLoadingFinished();
    expect(render).toMatchSnapshot();
  });

  test("after loading stops and editor is set", async () => {
    const [view, render] = renderEditorEnvelopeView();
    await view.setLoadingFinished();
    await view.setEditor(new DummyEditor());
    expect(render).toMatchSnapshot();
  });
});
