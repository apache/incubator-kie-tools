/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { EnvelopeClient } from "@kie-tools-core/envelope-bus/dist/envelope";
import {
  EnvelopeBusMessage,
  EnvelopeBusMessageDirectSender,
  EnvelopeBusMessagePurpose,
} from "@kie-tools-core/envelope-bus/dist/api";

interface ApiToConsume {
  setText(text: string): void;
  someRequest(text: string): Promise<string>;
}

interface ApiToProvide {
  init(): Promise<void>;
  someNotification(): void;
}

let apiImpl: ApiToProvide;
let envelopeClient: EnvelopeClient<ApiToProvide, ApiToConsume>;
let sentMessages: Array<[EnvelopeBusMessage<any, any>, string]>;

beforeEach(() => {
  sentMessages = [];
  apiImpl = {
    init: async () => envelopeClient.associate("my-origin", "my-envelope-id"),
    someNotification: jest.fn(),
  };
});

const createEnvelopeBus = (envelopeId?: string) => {
  return new EnvelopeClient<ApiToProvide, ApiToConsume>(
    {
      postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void {
        sentMessages.push([message as any, targetOrigin!]);
      },
    },
    envelopeId
  );
};

afterEach(() => {
  envelopeClient.stopListening();
});

const delay = (ms: number) => {
  return new Promise((res) => setTimeout(res, ms));
};

describe("new instance", () => {
  beforeEach(async () => {
    envelopeClient = createEnvelopeBus();
  });

  test("does nothing", () => {
    expect(sentMessages.length).toEqual(0);
    expect(envelopeClient.targetOrigin).toBe(undefined);
  });
});

describe("event listening", () => {
  beforeEach(async () => {
    envelopeClient = createEnvelopeBus();
  });

  test("activates when requested", async () => {
    spyOn(envelopeClient, "receive");
    envelopeClient.startListening(apiImpl);

    await incomingMessage("a-message");
    expect(envelopeClient.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivates when requested", async () => {
    spyOn(envelopeClient, "receive");
    envelopeClient.startListening(apiImpl);
    envelopeClient.stopListening();

    await incomingMessage("a-message");
    expect(envelopeClient.receive).toHaveBeenCalledTimes(0);
  });

  test("activation is idempotent", async () => {
    spyOn(envelopeClient, "receive");
    envelopeClient.startListening(apiImpl);
    envelopeClient.startListening(apiImpl);

    await incomingMessage("a-message");
    expect(envelopeClient.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivation is idempotent", async () => {
    spyOn(envelopeClient, "receive");
    envelopeClient.startListening(apiImpl);
    envelopeClient.stopListening();
    envelopeClient.stopListening();

    await incomingMessage("a-message");
    expect(envelopeClient.receive).toHaveBeenCalledTimes(0);
  });

  test("deactivation does not fail when not started", async () => {
    spyOn(envelopeClient, "receive");
    envelopeClient.stopListening();

    await incomingMessage("a-message");
    expect(envelopeClient.receive).toHaveBeenCalledTimes(0);
  });
});

describe("receive without envelopeId", () => {
  beforeEach(async () => {
    envelopeClient = createEnvelopeBus();
  });

  beforeEach(async () => {
    envelopeClient.startListening(apiImpl);
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
    envelopeClient.stopListening();
  });

  test("direct notification", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "any-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
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

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });

  test("without targetEnvelopeId", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(apiImpl.someNotification).toHaveBeenCalled();
  });

  test("from another EnvelopeClient", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
    });

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });
});

describe("receive with envelopeId", () => {
  beforeEach(async () => {
    envelopeClient = createEnvelopeBus("my-envelope-id");
  });

  beforeEach(async () => {
    envelopeClient.startListening(apiImpl);
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
    envelopeClient.stopListening();
  });

  test("direct notification", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(apiImpl.someNotification).toHaveBeenCalled();
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

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });

  test("notification to another envelope", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      targetEnvelopeId: "not-my-envelope-id",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });

  test("without targetEnvelopeId", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
    });

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });

  test("from another EnvelopeClient", async () => {
    await incomingMessage({
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "someNotification",
      directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
    });

    expect(apiImpl.someNotification).not.toHaveBeenCalled();
  });
});

describe("send without being associated", () => {
  beforeEach(async () => {
    envelopeClient = createEnvelopeBus();
  });

  test("throws error", () => {
    expect(() =>
      envelopeClient.send({
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
