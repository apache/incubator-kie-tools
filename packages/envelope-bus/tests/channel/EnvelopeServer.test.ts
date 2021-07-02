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

import {
  EnvelopeBusMessage,
  EnvelopeBusMessageDirectSender,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames,
} from "@kie-tooling-core/envelope-bus/dist/api";
import { EnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/channel";

interface ApiToProvide {
  setText(text: string): void;
  someRequest(text: string): Promise<string>;
}

interface ApiToConsume {
  init(): Promise<void>;
  someNotification(arg1: string): void;
  someRequest(arg1: string): Promise<void>;
}

let sentMessages: Array<EnvelopeBusMessage<unknown, any>>;
let envelopeServer: EnvelopeServer<ApiToProvide, ApiToConsume>;
let api: ApiToProvide;

beforeEach(() => {
  sentMessages = [];
  api = {
    setText: jest.fn(),
    someRequest: jest.fn(() => Promise.resolve("a string")),
  };

  envelopeServer = new EnvelopeServer({ postMessage: (msg: any) => sentMessages.push(msg) }, "tests", (self) =>
    self.envelopeApi.requests.init()
  );
});

const delay = (ms: number) => {
  return new Promise((res) => Promise.resolve().then(() => setTimeout(res, ms)));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();
    expect(sentMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    jest.spyOn(envelopeServer, "stopInitPolling");

    envelopeServer.startInitPolling();
    expect(envelopeServer.initPolling).toBeTruthy();
    expect(envelopeServer.initPollingTimeout).toBeTruthy();

    await delay(100); //waits for setInterval to kick in

    await incomingMessage({
      targetEnvelopeServerId: envelopeServer.id,
      requestId: "EnvelopeServer_0",
      type: "init",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined,
    });

    expect(envelopeServer.stopInitPolling).toHaveBeenCalled();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();
  });

  test("stops polling after timeout", async () => {
    jest.spyOn(envelopeServer, "stopInitPolling");
    EnvelopeServer.INIT_POLLING_TIMEOUT_IN_MS = 200;

    envelopeServer.startInitPolling();
    expect(envelopeServer.initPolling).toBeTruthy();
    expect(envelopeServer.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(300);

    expect(envelopeServer.stopInitPolling).toHaveBeenCalled();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();
  });
});

describe("receive", () => {
  test("any request with different targetEnvelopeServerId", () => {
    const receive = jest.spyOn(envelopeServer.manager.server, "receive");

    envelopeServer.receive(
      {
        targetEnvelopeServerId: "unknown-id",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "someRequest",
        data: [],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
      },
      api
    );

    expect(receive).not.toBeCalled();
  });

  test("any request with targetEnvelopeId", () => {
    const receive = jest.spyOn(envelopeServer.manager.server, "receive");

    envelopeServer.receive(
      {
        targetEnvelopeId: "unknown-id",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "someRequest",
        data: [],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
      },
      api
    );

    expect(receive).not.toBeCalled();
  });

  test("any request with the same targetEnvelopeServerId", async () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: envelopeServer.id,
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "someRequest",
        data: ["param1"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
      },
      api
    );

    await delay(0);

    expect(sentMessages.length).toStrictEqual(1);
    expect(api.someRequest).toBeCalledWith("param1");
  });

  test("any notification with different targetEnvelopeServerId", () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: "not-mine",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "setText",
        data: ["some text"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
      },
      api
    );

    expect(api.setText).not.toBeCalled();
  });

  test("any notification with the same targetEnvelopeServerId", () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: envelopeServer.id,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "setText",
        data: ["some text"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_BUS_CONTROLLER,
      },
      api
    );

    expect(api.setText).toBeCalledWith("some text");
  });

  test("any request from another EnvelopeServer", async () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: envelopeServer.id,
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "someRequest",
        data: ["param1"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
      },
      api
    );

    await delay(0);

    expect(sentMessages.length).toStrictEqual(0);
    expect(api.someRequest).not.toHaveBeenCalled();
  });
});

async function incomingMessage(
  message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>
) {
  envelopeServer.receive(message, api);
  await delay(0); // waits for next event loop iteration
}
