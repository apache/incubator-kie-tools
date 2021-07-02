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

import { EnvelopeBusController } from "@kie-tooling-core/envelope-bus/dist/envelope";
import {
  EnvelopeBusMessage,
  EnvelopeBusMessageDirectSender,
  EnvelopeBusMessagePurpose,
} from "@kie-tooling-core/envelope-bus/dist/api";

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
    init: async () => envelopeBus.associate("my-origin", "my-envelope-id"),
    someNotification: jest.fn(),
  };
});

const createEnvelopeBus = (envelopeId?: string) => {
  return new EnvelopeBusController<ApiToProvide, ApiToConsume>(
    {
      postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void {
        sentMessages.push([message as any, targetOrigin!]);
      },
    },
    envelopeId
  );
};

afterEach(() => {
  envelopeBus.stopListening();
});

const delay = (ms: number) => {
  return new Promise((res) => setTimeout(res, ms));
};

describe("new instance", () => {
  beforeEach(async () => {
    envelopeBus = createEnvelopeBus();
  });

  test("does nothing", () => {
    expect(sentMessages.length).toEqual(0);
    expect(envelopeBus.targetOrigin).toBe(undefined);
  });
});

describe("event listening", () => {
  beforeEach(async () => {
    envelopeBus = createEnvelopeBus();
  });

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

describe("receive without envelopeId", () => {
  beforeEach(async () => {
    envelopeBus = createEnvelopeBus();
  });

  beforeEach(async () => {
    envelopeBus.startListening(api);
    await incomingMessage({
      requestId: "any",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "init",
      data: [],
      targetEnvelopeId: "any-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
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
      type: "someNotification",
      targetEnvelopeId: "any-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });

  test("subscription notification", async () => {
    await incomingMessage({
      data: [],
      targetEnvelopeServerId: "not-my-associated-envelope-server-id",
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "any-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });

  test("without targetEnvelopeId", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).toHaveBeenCalled();
  });

  test("from another EnvelopeBusController", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });
});

describe("receive with envelopeId", () => {
  beforeEach(async () => {
    envelopeBus = createEnvelopeBus("my-envelope-id");
  });

  beforeEach(async () => {
    envelopeBus.startListening(api);
    await incomingMessage({
      requestId: "any",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "init",
      data: [],
      targetEnvelopeId: "my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
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
      type: "someNotification",
      targetEnvelopeId: "my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).toHaveBeenCalled();
  });

  test("subscription notification", async () => {
    await incomingMessage({
      data: [],
      targetEnvelopeServerId: "not-my-associated-envelope-server-id",
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });

  test("notification to another envelope", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "not-my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });

  test("without targetEnvelopeId", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });

  test("from another EnvelopeBusController", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
    });

    expect(api.someNotification).not.toHaveBeenCalled();
  });
});

describe("send without being associated", () => {
  beforeEach(async () => {
    envelopeBus = createEnvelopeBus();
  });

  test("throws error", () => {
    expect(() =>
      envelopeBus.send({
        data: "anything",
        requestId: "some-id",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "init",
      })
    ).toThrow();
  });
});

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}
