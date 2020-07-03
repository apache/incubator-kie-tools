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

import { KogitoEnvelopeBus } from "../KogitoEnvelopeBus";
import {
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  MessageTypesYouCanSendToTheChannel,
  MessageTypesYouCanSendToTheEnvelope
} from "@kogito-tooling/microeditor-envelope-protocol";
import { StateControlCommand } from "@kogito-tooling/core-api";
import { KogitoEnvelopeApi } from "@kogito-tooling/microeditor-envelope-protocol";

let api: KogitoEnvelopeApi;
let envelopeBus: KogitoEnvelopeBus;
let sentMessages: Array<[EnvelopeBusMessage<any, any>, string]>;

beforeEach(() => {
  sentMessages = [];
  api = {
    receive_initRequest: async init => {
      envelopeBus.targetOrigin = init.origin;
      envelopeBus.associatedBusId = init.busId;
    },
    receive_contentRequest: jest.fn(),
    receive_previewRequest: jest.fn(),
    receive_guidedTourElementPositionRequest: jest.fn(),
    receive_editorUndo: jest.fn(),
    receive_editorRedo: jest.fn(),
    receive_contentChanged: jest.fn()
  };

  envelopeBus = new KogitoEnvelopeBus(
    {
      postMessage: (message, targetOrigin) => sentMessages.push([message, targetOrigin!])
    },
    api
  );
});

afterEach(() => {
  envelopeBus.stopListening();
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(sentMessages.length).toEqual(0);
    expect(envelopeBus.targetOrigin).toBe(undefined);
  });
});

describe("event listening", () => {
  test("activates when requested", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivates when requested", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening();
    envelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(0);
  });

  test("activation is idempotent", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening();
    envelopeBus.startListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivation is idempotent", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening();
    envelopeBus.stopListening();
    envelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(0);
  });

  test("deactivation does not fail when not started", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(0);
  });
});

describe("receive", () => {
  beforeEach(async () => {
    envelopeBus.startListening();
    await incomingMessage({
      requestId: "any",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: { origin: "org", busId: "the-bus-id" }
    });
    sentMessages = [];
  });

  afterEach(() => {
    envelopeBus.stopListening();
  });

  test("contentChangedNotification", async () => {
    jest.spyOn(api, "receive_contentChanged");
    const newContent = { content: "this is the new content", path: "a/path" };
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_CONTENT_CHANGED,
      data: newContent
    });
    expect(api.receive_contentChanged).toHaveBeenCalledWith(newContent);
  });

  test("editorUndo notification", async () => {
    jest.spyOn(api, "receive_editorUndo");
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO,
      data: undefined
    });
    expect(api.receive_editorUndo).toHaveBeenCalledWith();
  });

  test("editorRedo notification", async () => {
    jest.spyOn(api, "receive_editorRedo");
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO,
      data: undefined
    });
    expect(api.receive_editorRedo).toHaveBeenCalledWith();
  });

  test("init request", async () => {
    jest.spyOn(api, "receive_initRequest");

    await incomingMessage({
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: { origin: "org", busId: "the-bus-id" }
    });

    expect(api.receive_initRequest).toHaveBeenCalledWith({ origin: "org", busId: "the-bus-id" });
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "requestId",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
          data: undefined
        },
        "org"
      ]
    ]);
  });

  test("preview request", async () => {
    const previewSvgString = "the-preview-svg-string";
    jest.spyOn(api, "receive_previewRequest").mockReturnValueOnce(Promise.resolve(previewSvgString));

    await incomingMessage({
      requestId: "3",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW,
      data: undefined
    });

    expect(api.receive_previewRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW,
          data: previewSvgString
        },
        "org"
      ]
    ]);
  });

  test("content request", async () => {
    const content = { content: "the content", path: "the/path" };
    jest.spyOn(api, "receive_contentRequest").mockReturnValueOnce(Promise.resolve(content));

    await incomingMessage({
      requestId: "3",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT,
      data: undefined
    });

    expect(api.receive_contentRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT,
          data: content
        },
        "org"
      ]
    ]);
  });

  test("guidedTourElementPositionRequest request", async () => {
    const rect = {} as any;
    jest.spyOn(api, "receive_guidedTourElementPositionRequest").mockReturnValueOnce(Promise.resolve(rect));

    await incomingMessage({
      requestId: "3",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
      data: undefined
    });

    expect(api.receive_guidedTourElementPositionRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
          data: rect
        },
        "org"
      ]
    ]);
  });
});

describe("send without being initialized", () => {
  test("throws error", () => {
    expect(() =>
      envelopeBus.send({
        data: "anything",
        requestId: "some-id",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT
      })
    ).toThrow();
  });
});

describe("send", () => {
  beforeEach(async () => {
    envelopeBus.startListening();
    await incomingMessage({
      requestId: "2",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      data: { origin: "tgt-orgn", busId: "the-bus-id" }
    });
    sentMessages = [];
  });

  test("notify stateControlCommandUpdate", () => {
    envelopeBus.notify_stateControlCommandUpdate(StateControlCommand.REDO);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE,
          data: StateControlCommand.REDO
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify ready", () => {
    envelopeBus.notify_ready();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_READY,
          data: undefined
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify openFile", () => {
    const path = "path";
    envelopeBus.notify_openFile(path);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_OPEN_FILE,
          data: path
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify newEdit", () => {
    const kogitoEdit = { id: "the-edit-id" };
    envelopeBus.notify_newEdit(kogitoEdit);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_NEW_EDIT,
          data: kogitoEdit
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify setContentError", () => {
    const error = "the error";
    envelopeBus.notify_setContentError(error);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR,
          data: error
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify guidedTourRegisterTutorial", () => {
    const tutorial = {} as any;
    envelopeBus.notify_guidedTourRegisterTutorial(tutorial);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL,
          data: tutorial
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify guidedTourRefresh", () => {
    const userInteraction = {} as any;
    envelopeBus.notify_guidedTourRefresh(userInteraction);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_USER_INTERACTION,
          data: userInteraction
        },
        "tgt-orgn"
      ]
    ]);
  });
});

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}
