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

import { EditorEnvelopeController } from "../EditorEnvelopeController";
import { SpecialDomElements } from "../SpecialDomElements";
import { cleanup, fireEvent, getByTestId, render } from "@testing-library/react";
import {
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  LanguageData
} from "@kogito-tooling/microeditor-envelope-protocol";
import { ChannelType, OperatingSystem } from "@kogito-tooling/microeditor-envelope-protocol";
import { DummyEditor } from "./DummyEditor";
import { ResourceContentServiceCoordinator } from "../api/resourceContent";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";

let loadingScreenContainer: HTMLElement;
let envelopeContainer: HTMLElement;
let sentMessages: Array<EnvelopeBusMessage<any, any>>;
let controller: EditorEnvelopeController;
let mockComponent: ReturnType<typeof render>;
let dummyEditor: DummyEditor;

const languageData = { editorId: "test-editor-id", gwtModuleName: "none", resources: [] };

describe("EditorEnvelopeController", () => {
  beforeEach(() => {
    loadingScreenContainer = document.body.appendChild(document.createElement("div"));
    loadingScreenContainer.setAttribute("id", "loading-screen");

    envelopeContainer = document.body.appendChild(document.createElement("div"));
    envelopeContainer.setAttribute("id", "envelopeContainer");

    sentMessages = [];
    dummyEditor = new DummyEditor();
    controller = new EditorEnvelopeController(
      {
        postMessage: message => sentMessages.push(message)
      },
      {
        createEditor: (_: LanguageData) => Promise.resolve(dummyEditor)
      },
      new SpecialDomElements(),
      {
        render: (element, container, callback) => {
          mockComponent = render(element, { container: envelopeContainer });
          callback();
        }
      },
      new ResourceContentServiceCoordinator(),
      new DefaultKeyboardShortcutsService({
        editorContext: { channel: ChannelType.VSCODE, operatingSystem: OperatingSystem.WINDOWS }
      })
    );
  });

  afterEach(() => {
    controller.stop();
    loadingScreenContainer.remove();
    envelopeContainer.remove();
    cleanup();
  });

  test("opens", async () => {
    const { container } = await startController();
    expect(document.body).toMatchSnapshot();
  });

  test("receives init request", async () => {
    const { container } = await startController();
    await incomingMessage({
      requestId: "KogitoChannelBus_0",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_initRequest",
      data: [{ origin: "test-target-origin", busId: "someBusId" }]
    });

    expect(sentMessages).toEqual([
      {
        busId: controller.kogitoEnvelopeBus.associatedBusId,
        requestId: "KogitoEnvelopeBus_0",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        type: "receive_languageRequest",
        data: []
      }
    ]);

    expect(document.body).toMatchSnapshot();
  });

  test("receives language response", async () => {
    await startController();

    await incomingMessage({
      requestId: "KogitoChannelBus_0",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_initRequest",
      data: [{ origin: "test-target-origin", busId: "someBusId" }]
    });

    sentMessages = [];
    await incomingMessage({
      requestId: "KogitoEnvelopeBus_0",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: "receive_languageRequest",
      data: languageData
    });

    expect(sentMessages).toEqual([
      {
        busId: controller.kogitoEnvelopeBus.associatedBusId,
        requestId: "KogitoEnvelopeBus_1",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        type: "receive_contentRequest",
        data: []
      }
    ]);

    expect(document.body).toMatchSnapshot();
  });

  test("after received content", async () => {
    const { container } = await startController();

    await incomingMessage({
      requestId: "KogitoChannelBus_0",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_initRequest",
      data: [{ origin: "test-target-origin", busId: "someBusId" }]
    });

    await incomingMessage({
      requestId: "KogitoEnvelopeBus_0",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: "receive_languageRequest",
      data: languageData
    });

    sentMessages = [];
    await incomingMessage({
      requestId: "KogitoEnvelopeBus_1",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: "receive_contentRequest",
      data: { content: "test content" }
    });

    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    expect(sentMessages).toEqual([
      {
        busId: controller.kogitoEnvelopeBus.associatedBusId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_ready",
        data: []
      },
      {
        requestId: "KogitoChannelBus_0",
        busId: controller.kogitoEnvelopeBus.associatedBusId,
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "receive_initRequest",
        data: undefined
      }
    ]);

    expect(document.body).toMatchSnapshot();
  });

  test("test notify undo/redo", async () => {
    jest.spyOn(dummyEditor, "undo");
    jest.spyOn(dummyEditor, "redo");

    await startController();

    await incomingMessage({
      requestId: "KogitoChannelBus_0",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_initRequest",
      data: [{ origin: "test-target-origin", busId: "someBusId" }]
    });

    await incomingMessage({
      requestId: "KogitoEnvelopeBus_0",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: "receive_languageRequest",
      data: languageData
    });

    await incomingMessage({
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_editorUndo",
      data: ["commandID"]
    });
    expect(dummyEditor.undo).toBeCalledTimes(1);

    await incomingMessage({
      requestId: "2",
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_editorRedo",
      data: ["commandID"]
    });
    expect(dummyEditor.redo).toBeCalledTimes(1);
  });
});

function delay(ms: number) {
  return Promise.resolve().then(() => new Promise(res => setTimeout(res, ms)));
}

async function startController() {
  const context = { channel: ChannelType.VSCODE, operatingSystem: OperatingSystem.WINDOWS };
  await controller.start({ container: envelopeContainer, context: context });
  return mockComponent!;
}

async function incomingMessage(message: EnvelopeBusMessage<any, any>) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}
