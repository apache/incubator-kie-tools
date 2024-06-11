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

import {
  EnvelopeBusMessage,
  EnvelopeBusMessageDirectSender,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames,
} from "@kie-tools-core/envelope-bus/dist/api";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";

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
let apiImpl: ApiToProvide;

beforeEach(() => {
  sentMessages = [];
  apiImpl = {
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
    expect(envelopeServer.initialPollingSetting).toBeFalsy();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();
    expect(sentMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    const spy = jest.spyOn(envelopeServer, "stopInitPolling");

    envelopeServer.startInitPolling(apiImpl);
    expect(envelopeServer.initialPollingSetting).toBeTruthy();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();

    await delay(100); // waits for polling setInterval to be set

    expect(envelopeServer.initPolling).toBeTruthy();
    expect(envelopeServer.initPollingTimeout).toBeTruthy();

    await delay(100); // waits for polling setInterval to kick in

    await incomingMessage({
      targetEnvelopeServerId: envelopeServer.id,
      requestId: "EnvelopeServer_0",
      type: "init",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined,
    });

    expect(envelopeServer.stopInitPolling).toHaveBeenCalled();
    expect(envelopeServer.initialPollingSetting).toBeFalsy();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();

    spy.mockReset();
    spy.mockRestore();
  });

  test("stops polling after timeout", async () => {
    const spy = jest.spyOn(envelopeServer, "stopInitPolling");
    EnvelopeServer.INIT_POLLING_TIMEOUT_IN_MS = 200;

    envelopeServer.startInitPolling(apiImpl);
    expect(envelopeServer.initialPollingSetting).toBeTruthy();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();

    //more than the timeout
    await delay(250);

    expect(envelopeServer.initPolling).toBeTruthy();
    expect(envelopeServer.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(250);

    expect(envelopeServer.stopInitPolling).toHaveBeenCalled();
    expect(envelopeServer.initialPollingSetting).toBeFalsy();
    expect(envelopeServer.initPolling).toBeFalsy();
    expect(envelopeServer.initPollingTimeout).toBeFalsy();

    spy.mockReset();
    spy.mockRestore();
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
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      apiImpl
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
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      apiImpl
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
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      apiImpl
    );

    await delay(0);

    expect(sentMessages.length).toStrictEqual(1);
    expect(apiImpl.someRequest).toBeCalledWith("param1");
  });

  test("any notification with different targetEnvelopeServerId", () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: "not-mine",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "setText",
        data: ["some text"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      apiImpl
    );

    expect(apiImpl.setText).not.toBeCalled();
  });

  test("any notification with the same targetEnvelopeServerId", () => {
    envelopeServer.receive(
      {
        targetEnvelopeServerId: envelopeServer.id,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "setText",
        data: ["some text"],
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      apiImpl
    );

    expect(apiImpl.setText).toBeCalledWith("some text");
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
      apiImpl
    );

    await delay(0);

    expect(sentMessages.length).toStrictEqual(0);
    expect(apiImpl.someRequest).not.toHaveBeenCalled();
  });
});

async function incomingMessage(
  message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>
) {
  envelopeServer.receive(message, apiImpl);
  await delay(0); // waits for next event loop iteration
}
