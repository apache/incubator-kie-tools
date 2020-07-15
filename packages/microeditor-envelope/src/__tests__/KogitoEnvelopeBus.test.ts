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
  KogitoEnvelopeApi,
  StateControlCommand
} from "@kogito-tooling/microeditor-envelope-protocol";

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
    receive_contentChanged: jest.fn(),
    receive_channelKeyboardEvent: jest.fn()
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
      type: "receive_initRequest",
      data: [{ origin: "org", busId: "the-bus-id" }]
    });
    sentMessages = [];
  });

  afterEach(() => {
    envelopeBus.stopListening();
  });

  test("contentChanged notifcation", async () => {
    jest.spyOn(api, "receive_contentChanged");
    const newContent = { content: "this is the new content", path: "a/path" };
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_contentChanged",
      data: [newContent]
    });
    expect(api.receive_contentChanged).toHaveBeenCalledWith(newContent);
  });

  test("editorUndo notification", async () => {
    jest.spyOn(api, "receive_editorUndo");
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_editorUndo",
      data: []
    });
    expect(api.receive_editorUndo).toHaveBeenCalledWith();
  });

  test("editorRedo notification", async () => {
    jest.spyOn(api, "receive_editorRedo");
    await incomingMessage({
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_editorRedo",
      data: []
    });
    expect(api.receive_editorRedo).toHaveBeenCalledWith();
  });

  test("init request", async () => {
    jest.spyOn(api, "receive_initRequest");
    const association = { origin: "org", busId: "the-bus-id" };

    await incomingMessage({
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_initRequest",
      data: [association]
    });

    expect(api.receive_initRequest).toHaveBeenCalledWith(association);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "requestId",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: "receive_initRequest",
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
      type: "receive_previewRequest",
      data: []
    });

    expect(api.receive_previewRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: "receive_previewRequest",
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
      type: "receive_contentRequest",
      data: []
    });

    expect(api.receive_contentRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: "receive_contentRequest",
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
      type: "receive_guidedTourElementPositionRequest",
      data: []
    });

    expect(api.receive_guidedTourElementPositionRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          requestId: "3",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          type: "receive_guidedTourElementPositionRequest",
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
        type: "receive_initRequest"
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
      type: "receive_initRequest",
      data: [{ origin: "tgt-orgn", busId: "the-bus-id" }]
    });
    sentMessages = [];
  });

  test("notify stateControlCommandUpdate", () => {
    const command = StateControlCommand.REDO;
    envelopeBus.client.notify("receive_stateControlCommandUpdate", command);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_stateControlCommandUpdate",
          data: [command]
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify ready", () => {
    envelopeBus.client.notify("receive_ready");
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_ready",
          data: []
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify openFile", () => {
    const path = "some/path";
    envelopeBus.client.notify("receive_openFile", path);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_openFile",
          data: [path]
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify newEdit", () => {
    const kogitoEdit = { id: "the-edit-id" };
    envelopeBus.client.notify("receive_newEdit", kogitoEdit);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_newEdit",
          data: [kogitoEdit]
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify setContentError", () => {
    const error = "the error";
    envelopeBus.client.notify("receive_setContentError", error);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_setContentError",
          data: [error]
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify guidedTourRegisterTutorial", () => {
    const tutorial = {} as any;
    envelopeBus.client.notify("receive_guidedTourRegisterTutorial", tutorial);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_guidedTourRegisterTutorial",
          data: [tutorial]
        },
        "tgt-orgn"
      ]
    ]);
  });

  test("notify guidedTourRefresh", () => {
    const userInteraction = {} as any;
    envelopeBus.client.notify("receive_guidedTourUserInteraction", userInteraction);
    expect(sentMessages).toEqual([
      [
        {
          busId: "the-bus-id",
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          type: "receive_guidedTourUserInteraction",
          data: [userInteraction]
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
