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

import { EnvelopeBusController } from "../../envelope";
import { EnvelopeBusMessage, EnvelopeBusMessagePurpose } from "../../api";

interface ApiToConsume {
  setText(text: string): void;
  someRequest(text: string): Promise<string>;
}

interface ApiToProvide {
  init(): Promise<void>;
  someNotification(): void;
}

let api: ApiToProvide;
let envelopeBus: EnvelopeBusController<ApiToProvide, ApiToConsume>;
let sentMessages: Array<[EnvelopeBusMessage<any, any>, string]>;

beforeEach(() => {
  sentMessages = [];
  api = {
    init: async () => envelopeBus.associate("my-origin", "my-server-id"),
    someNotification: jest.fn(),
  };

  envelopeBus = new EnvelopeBusController<ApiToProvide, ApiToConsume>({
    postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void {
      sentMessages.push([message as any, targetOrigin!]);
    }
  });
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
    envelopeBus.startListening(api);

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivates when requested", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening(api);
    envelopeBus.stopListening();

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(0);
  });

  test("activation is idempotent", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening(api);
    envelopeBus.startListening(api);

    await incomingMessage("a-message");
    expect(envelopeBus.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivation is idempotent", async () => {
    spyOn(envelopeBus, "receive");
    envelopeBus.startListening(api);
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
    envelopeBus.startListening(api);
    await incomingMessage({
      requestId: "any",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "init",
      data: []
    });
    sentMessages = [];
  });

  afterEach(() => {
    envelopeBus.stopListening();
  });

  test("direct notification", async () => {

    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification"
    });

    expect(api.someNotification).toHaveBeenCalled();
  });

  test("subscription notification", async () => {
    await incomingMessage({
      data: [],
      envelopeServerId: "not-my-associated-envelope-server-id",
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification"
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });
});

describe("send without being associated", () => {
  test("throws error", () => {
    expect(() =>
      envelopeBus.send({
        data: "anything",
        requestId: "some-id",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "init"
      })
    ).toThrow();
  });
});

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}
