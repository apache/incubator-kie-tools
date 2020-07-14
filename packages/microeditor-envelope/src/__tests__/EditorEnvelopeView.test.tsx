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
import { cleanup, fireEvent, getByTestId, render } from "@testing-library/react";
import { EditorEnvelopeView } from "../EditorEnvelopeView";
import { DummyEditor } from "./DummyEditor";
import { KogitoEnvelopeBus } from "../KogitoEnvelopeBus";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import { ChannelType, OperatingSystem } from "@kogito-tooling/microeditor-envelope-protocol";

function renderEditorEnvelopeView(): EditorEnvelopeView {
  let view: EditorEnvelopeView;
  const context = { channel: ChannelType.VSCODE, operatingSystem: OperatingSystem.WINDOWS };
  const kogitoEnvelopeBus = new KogitoEnvelopeBus(
    {
      postMessage: jest.fn()
    },
    {
      receive_initRequest: jest.fn(),
      receive_contentChanged: jest.fn(),
      receive_contentRequest: jest.fn(),
      receive_editorUndo: jest.fn(),
      receive_editorRedo: jest.fn(),
      receive_previewRequest: jest.fn(),
      receive_guidedTourElementPositionRequest: jest.fn(),
      receive_channelKeyboardEvent: jest.fn()
    }
  );

  render(
    <EditorEnvelopeView
      keyboardShortcutsService={new DefaultKeyboardShortcutsService({ editorContext: context })}
      context={context}
      exposing={self => (view = self)}
      loadingScreenContainer={loadingScreenContainer}
      messageBus={kogitoEnvelopeBus}
    />
  );

  return view!;
}
let loadingScreenContainer: HTMLElement;

describe("EditorEnvelopeView", () => {
  beforeEach(() => {
    loadingScreenContainer = document.body.appendChild(document.createElement("div"));
  });

  afterEach(() => {
    cleanup();
    loadingScreenContainer.remove();
  });

  test("first open", async () => {
    const _ = renderEditorEnvelopeView();
    expect(document.body).toMatchSnapshot();
  });

  test("after loading stops", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    expect(document.body).toMatchSnapshot();
  });

  test("after loading stops and editor is set", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    await view.setEditor(new DummyEditor());

    expect(document.body).toMatchSnapshot();
  });

  test("after set content", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    await view.setEditor(new DummyEditor());
    await view.getEditor()!.setContent("/some/path.txt", "some-test-content");

    expect(document.body).toMatchSnapshot();
  });
});
