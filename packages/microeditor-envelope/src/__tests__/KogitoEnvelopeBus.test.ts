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

import { KogitoEnvelopeBus, KogitoEnvelopeApi } from "../KogitoEnvelopeBus";
import {
  EnvelopeBusMessagePurpose,
  MessageTypesYouCanSendToTheChannel,
  MessageTypesYouCanSendToTheEnvelope
} from "@kogito-tooling/microeditor-envelope-protocol";
import { StateControlCommand } from "@kogito-tooling/core-api";

let kogitoEnvelopeBus: KogitoEnvelopeBus;
let apiImpl: KogitoEnvelopeApi;
let receivedMessages: any[];
let sentMessages: any[];

beforeEach(() => {
  receivedMessages = [];
  sentMessages = [];
  apiImpl = {
    receive_initRequest: jest.fn(),
    receive_contentRequest: jest.fn(),
    receive_previewRequest: jest.fn(),
    receive_guidedTourElementPositionRequest: jest.fn(),
    receive_editorUndo: jest.fn(),
    receive_editorRedo: jest.fn(),
    receive_contentChangedNotification: jest.fn()
  };

  kogitoEnvelopeBus = new KogitoEnvelopeBus(
    {
      postMessage: (message, targetOrigin) => sentMessages.push([message, targetOrigin])
    },
    apiImpl
  );
});

afterEach(() => {
  kogitoEnvelopeBus.stopListening();
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("new instance", () => {
  test.skip("does nothing", () => {
    expect(sentMessages.length).toEqual(0);
    expect(receivedMessages.length).toEqual(0);
    expect(kogitoEnvelopeBus.targetOrigin).toBe(undefined);
  });
});

describe("event listening", () => {
  test.skip("activates when requested", async () => {
    spyOn(kogitoEnvelopeBus, "receive");
    kogitoEnvelopeBus.startListening();

    await incomingMessage("a-message");
    expect(kogitoEnvelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test.skip("deactivates when requested", async () => {
    spyOn(kogitoEnvelopeBus, "receive");
    kogitoEnvelopeBus.startListening();
    kogitoEnvelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(kogitoEnvelopeBus.receive).toHaveBeenCalledTimes(0);
  });

  test.skip("activation is idempotent", async () => {
    spyOn(kogitoEnvelopeBus, "receive");
    kogitoEnvelopeBus.startListening();
    kogitoEnvelopeBus.startListening();

    await incomingMessage("a-message");
    expect(kogitoEnvelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test.skip("deactivation is idempotent", async () => {
    spyOn(kogitoEnvelopeBus, "receive");
    kogitoEnvelopeBus.startListening();
    kogitoEnvelopeBus.stopListening();
    kogitoEnvelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(kogitoEnvelopeBus.receive).toHaveBeenCalledTimes(0);
  });

  test.skip("deactivation does not fail when not started", async () => {
    spyOn(kogitoEnvelopeBus, "receive");
    kogitoEnvelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(kogitoEnvelopeBus.receive).toHaveBeenCalledTimes(0);
  });
});

describe("receive", () => {
  //TODO: Tiago: Implement
});

describe("send without being initialized", () => {
  test.skip("throws error", () => {
    expect(() =>
      kogitoEnvelopeBus.send({
        data: "anything",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT
      })
    ).toThrow();
  });
});

describe("send", () => {
  beforeEach(async () => {
    kogitoEnvelopeBus.startListening();
    await incomingMessage({ type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT, data: "tgt-orgn" });
    sentMessages = [];
    receivedMessages = [];
  });

  test.skip("receive state control api - redo event", () => {
    kogitoEnvelopeBus.notify_stateControlCommandUpdate(StateControlCommand.REDO);
    expect(sentMessages).toEqual([
      [
        {
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE,
          data: StateControlCommand.REDO
        },
        "tgt-orgn"
      ]
    ]);
  });

  test.skip("receive state control api - redo event", () => {
    kogitoEnvelopeBus.notify_stateControlCommandUpdate(StateControlCommand.UNDO);
    expect(sentMessages).toEqual([
      [
        {
          type: MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE,
          data: StateControlCommand.UNDO
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
