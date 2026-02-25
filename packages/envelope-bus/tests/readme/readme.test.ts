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

import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EnvelopeClient } from "@kie-tools-core/envelope-bus/dist/envelope";
import { EnvelopeBusMessage } from "@kie-tools-core/envelope-bus/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

interface ChannelApi {
  setFoo(foo: string): void;
  requestFoo(id: string): Promise<string>;
  foo(): SharedValueProvider<string>;
}

interface EnvelopeApi {
  init(origin: string): Promise<void>;
  setBar(bar: string): void;
  requestBar(id: string): Promise<string>;
  bar(): SharedValueProvider<string>;
}

let channelApiImpl: ChannelApi;
let envelopeApiImpl: EnvelopeApi;

let envelopeServer: EnvelopeServer<ChannelApi, EnvelopeApi>;
let envelopeClient: EnvelopeClient<EnvelopeApi, ChannelApi>;

// Due to the direct-plug implementation of EnvelopeServer and EnvelopeClient, all calls are synchronous.
describe("readme", () => {
  beforeEach(() => {
    channelApiImpl = {
      setFoo: jest.fn(),
      requestFoo: async (id) => `fooId: ${id}`,
      foo: () => ({ defaultValue: "default-foo" }),
    };

    envelopeApiImpl = {
      init: async (origin) => envelopeClient.associate(origin, envelopeServer.id),
      setBar: jest.fn(),
      requestBar: async (id) => `barId: ${id}`,
      bar: () => ({ defaultValue: "default-bar" }),
    };

    envelopeClient = new EnvelopeClient<EnvelopeApi, ChannelApi>({
      postMessage: (msg: EnvelopeBusMessage<any, any>, targetOrigin) => {
        // Direct-plug. Normally this would've been something like `window.parent.postMessage(msg, targetOrigin)`
        return envelopeServer.receive(msg, channelApiImpl);
      },
    });

    envelopeServer = new EnvelopeServer<ChannelApi, EnvelopeApi>(
      {
        postMessage: (msg: EnvelopeBusMessage<any, any>, targetOrigin) => {
          // Direct-plug. Normally this would've been something like `iframe.contentWindow?.postMessage(msg, targetOrigin)`
          return envelopeClient.receive(msg, envelopeApiImpl);
        },
      },
      "my-origin",
      (self) => self.envelopeApi.requests.init(self.origin)
    );

    envelopeServer.startInitPolling(channelApiImpl);
    expect(envelopeServer.initPolling).toStrictEqual(undefined);
  });

  //

  test("channel -> envelope", async () => {
    // notification
    envelopeServer.envelopeApi.notifications.setBar.send("bar");
    expect(envelopeApiImpl.setBar).toHaveBeenCalledTimes(1);
    expect(envelopeApiImpl.setBar).toHaveBeenCalledWith("bar");

    // request
    const barId = await envelopeServer.envelopeApi.requests.requestBar("1");
    expect(barId).toStrictEqual("barId: 1");

    // default shared value
    let barValue;
    let barValueSubs = envelopeServer.envelopeApi.shared.bar.subscribe((bar) => (barValue = bar));
    expect(barValue).toStrictEqual("default-bar");

    // modified shared value
    envelopeServer.envelopeApi.shared.bar.set("bar");
    expect(barValue).toStrictEqual("bar");

    // unsubscribed shared value
    envelopeServer.envelopeApi.shared.bar.unsubscribe(barValueSubs);
    envelopeServer.envelopeApi.shared.bar.set("bar-2");
    expect(barValue).toStrictEqual("bar");
  });

  test("envelope -> channel", async () => {
    // notification
    envelopeClient.channelApi.notifications.setFoo.send("foo");
    expect(channelApiImpl.setFoo).toHaveBeenCalledTimes(1);
    expect(channelApiImpl.setFoo).toHaveBeenCalledWith("foo");

    // request
    const fooId = await envelopeClient.channelApi.requests.requestFoo("1");
    expect(fooId).toStrictEqual("fooId: 1");

    // default shared value
    let fooValue;
    let fooValueSubs = envelopeClient.channelApi.shared.foo.subscribe((foo) => (fooValue = foo));
    expect(fooValue).toStrictEqual("default-foo");

    // modified shared value
    envelopeClient.channelApi.shared.foo.set("foo");
    expect(fooValue).toStrictEqual("foo");

    // unsubscribed shared value
    envelopeClient.channelApi.shared.foo.unsubscribe(fooValueSubs);
    envelopeClient.channelApi.shared.foo.set("foo-2");
    expect(fooValue).toStrictEqual("foo");
  });
});
