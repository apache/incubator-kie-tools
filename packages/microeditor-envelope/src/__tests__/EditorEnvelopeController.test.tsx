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
import { mount } from "enzyme";
import {
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  MessageTypesYouCanSendToTheChannel,
  MessageTypesYouCanSendToTheEnvelope
} from "@kogito-tooling/microeditor-envelope-protocol";
import { ChannelType, LanguageData, OperatingSystem } from "@kogito-tooling/core-api";
import { DummyEditor } from "./DummyEditor";
import { ResourceContentServiceCoordinator } from "../api/resourceContent";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";

const StateControlMock = jest.fn(() => ({
  undo: jest.fn(),
  redo: jest.fn(),
  registry: jest.fn()
}));

let stateControl: any;

let loadingScreenContainer: HTMLElement;
let envelopeContainer: HTMLElement;

beforeEach(() => {
  loadingScreenContainer = document.body.appendChild(document.createElement("div"));
  loadingScreenContainer.setAttribute("id", "loading-screen");

  envelopeContainer = document.body.appendChild(document.createElement("div"));
  envelopeContainer.setAttribute("id", "envelopeContainer");
});

afterEach(() => loadingScreenContainer.remove());

const delay = (ms: number) => {
  return Promise.resolve().then(() => new Promise(res => setTimeout(res, ms)));
};

const languageData = {
  editorId: "test-editor-id",
  gwtModuleName: "none",
  resources: []
};

let sentMessages: Array<EnvelopeBusMessage<any, any>>;
let controller: EditorEnvelopeController;
let mockComponent: ReturnType<typeof mount>;

beforeEach(() => {
  sentMessages = [];

  stateControl = new StateControlMock();

  controller = new EditorEnvelopeController(
    {
      postMessage: message => {
        sentMessages.push(message);
      }
    },
    {
      createEditor(_: LanguageData) {
        return Promise.resolve(new DummyEditor());
      }
    },
    new SpecialDomElements(),
    stateControl,
    {
      render: (element, container, callback) => {
        mockComponent = mount(element);
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
});

async function startController() {
  const context = { channel: ChannelType.VSCODE, operatingSystem: OperatingSystem.WINDOWS };
  await controller.start({
    container: envelopeContainer,
    context: context
  });
  return mockComponent!;
}

async function incomingMessage(message: EnvelopeBusMessage<any, any>) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}

describe("EditorEnvelopeController", () => {
  test.skip("opens", async () => {
    const render = await startController();
    expect(render).toMatchSnapshot();
  });

  test.skip("receives init request", async () => {
    const render = await startController();
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: "test-target-origin"
    });

    expect(sentMessages).toEqual([
      { type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT, data: undefined },
      { type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE, data: undefined }
    ]);
    expect(render.update()).toMatchSnapshot();
  });

  test.skip("receives language response", async () => {
    await startController();
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: "test-target-origin"
    });

    sentMessages = [];
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE,
      data: languageData
    });

    expect(sentMessages).toEqual([{ type: MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT, data: undefined }]);
  });

  test.skip("after received content", async () => {
    const render = await startController();

    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: "test-target-origin"
    });

    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE,
      data: languageData
    });
    sentMessages = [];
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT,
      data: { content: "test content" }
    });

    expect(sentMessages).toEqual([{ type: MessageTypesYouCanSendToTheChannel.NOTIFY_READY, data: undefined }]);
    expect(render.update()).toMatchSnapshot();
  });

  test.skip("test notify undo/redo", async () => {
    const render = await startController();

    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO,
      data: "commandID"
    });
    expect(stateControl.undo).toBeCalledTimes(1);

    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO,
      data: "commandID"
    });
    expect(stateControl.redo).toBeCalledTimes(1);
  });
});
