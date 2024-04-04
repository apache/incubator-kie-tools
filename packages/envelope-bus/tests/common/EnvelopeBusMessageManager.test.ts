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

import { EnvelopeBusMessageManager } from "@kie-tools-core/envelope-bus/dist/common";
import { EnvelopeBusMessagePurpose, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

interface ApiToProvide {
  setText(text: string): void;
  getEnvelopeText(): Promise<string>;
  getFooBar(foo: string, bar: string): Promise<string>;
  fooValue(): SharedValueProvider<string>;
}

interface ApiToConsume {
  getText(): Promise<string>;
  setSomething(something: string): void;
  setSomethings(something1: string, something2: string): void;
  getSomething(param1: string, param2: number): Promise<number>;
  barValue(): SharedValueProvider<string>;
}

let sentMessages: any[] = [];
let manager: EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>;
let apiImpl: ApiToProvide;

beforeEach(() => {
  sentMessages = [];
  manager = new EnvelopeBusMessageManager((msg) => sentMessages.push(msg), "my-manager");
  apiImpl = {
    setText: jest.fn((text: string) => {
      console.info(`Setting text: ${text}`);
    }),
    getEnvelopeText: jest.fn(async () => {
      return "a text";
    }),
    getFooBar: jest.fn(async (foo: string, bar: string) => {
      return foo + bar;
    }),
    fooValue: () => {
      return { defaultValue: "default-foo" };
    },
  };
  manager.currentApiImpl = apiImpl;
});

describe("cached", () => {
  test("returns the same instance", async () => {
    expect(manager.clientApi.notifications.setSomething).toStrictEqual(manager.clientApi.notifications.setSomething);
    expect(manager.shared.fooValue).toStrictEqual(manager.shared.fooValue);
    expect(manager.clientApi.shared.barValue).toStrictEqual(manager.clientApi.shared.barValue);
    expect(manager.clientApi.requests.getText).toStrictEqual(manager.clientApi.requests.getText);
  });
});

describe("requests", () => {
  test("method with no parameters", async () => {
    const retPromise = manager.clientApi.requests.getText();
    expect(sentMessages).toStrictEqual([
      {
        type: "getText",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_0",
        data: [],
      },
    ]);

    await manager.server.receive(
      {
        type: "getText",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-manager_0",
        data: "foo",
      },
      apiImpl
    );

    expect(await retPromise).toBe("foo");
  });

  test("method with parameters", async () => {
    const retPromise = manager.clientApi.requests.getSomething("a", 1);
    expect(sentMessages).toStrictEqual([
      {
        type: "getSomething",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_0",
        data: ["a", 1],
      },
    ]);

    await manager.server.receive(
      {
        type: "getSomething",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-manager_0",
        data: 8,
      },
      apiImpl
    );

    expect(await retPromise).toBe(8);
  });

  test("two in a row", async () => {
    manager.clientApi.requests.getText();
    manager.clientApi.requests.getSomething("b", 2);
    expect(sentMessages).toStrictEqual([
      {
        type: "getText",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_0",
        data: [],
      },
      {
        type: "getSomething",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_1",
        data: ["b", 2],
      },
    ]);
  });
});

describe("notifications", () => {
  test("simple notification", async () => {
    manager.clientApi.notifications.setSomething.send("something");
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something"],
      },
    ]);
  });

  test("simple subscription", async () => {
    const callback = jest.fn((a, b) => {
      expect(a).toStrictEqual("foo");
      expect(b).toStrictEqual("bar");
    });

    const subscription = manager.clientApi.notifications.setSomethings.subscribe(callback);
    expect(subscription).toStrictEqual(callback);
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomethings",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
        data: [],
      },
    ]);

    await manager.server.receive(
      {
        type: "setSomethings",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["foo", "bar"],
      },
      apiImpl
    );
    expect(callback).toHaveBeenCalledWith("foo", "bar");
  });

  test("simple unsubscription", async () => {
    const subscription = manager.clientApi.notifications.setSomething.subscribe(jest.fn());
    await manager.server.receive(
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something"],
      },
      apiImpl
    );

    expect(subscription).toHaveBeenCalledWith("something");
    expect(subscription).toHaveBeenCalledTimes(1);

    manager.clientApi.notifications.setSomething.unsubscribe(subscription);
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
        data: [],
      },
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_UNSUBSCRIPTION,
        data: [],
      },
    ]);

    await manager.server.receive(
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something2"],
      },
      apiImpl
    );

    expect(subscription).toHaveBeenCalledTimes(1);
  });
});

describe("shared", () => {
  test("set consumed", () => {
    manager.clientApi.shared.barValue.set("bar1");
    manager.clientApi.shared.barValue.set("bar2");

    expect(sentMessages).toStrictEqual([
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "bar1",
      },
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "bar2",
      },
    ]);
  });

  test("set owned", () => {
    manager.shared.fooValue.set("foo1");
    manager.shared.fooValue.set("foo2");

    expect(sentMessages).toStrictEqual([
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "foo1",
      },
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "foo2",
      },
    ]);
  });

  test("set consumed with subscriptions", () => {
    const subscription1 = manager.clientApi.shared.barValue.subscribe(jest.fn());

    manager.clientApi.shared.barValue.set("bar1");
    expect(subscription1).toHaveBeenCalledWith("bar1");

    manager.clientApi.shared.barValue.set("bar2");
    expect(subscription1).toHaveBeenCalledWith("bar2");

    const subscription2 = manager.clientApi.shared.barValue.subscribe(jest.fn());
    expect(subscription2).toHaveBeenCalledWith("bar2");

    expect(sentMessages).toStrictEqual([
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_GET_DEFAULT,
        data: [],
      },
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "bar1",
      },
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "bar2",
      },
    ]);
  });

  test("set owned with subscriptions", () => {
    const subscription1 = manager.shared.fooValue.subscribe(jest.fn());

    manager.shared.fooValue.set("foo1");
    expect(subscription1).toHaveBeenCalledWith("foo1");

    manager.shared.fooValue.set("foo2");
    expect(subscription1).toHaveBeenCalledWith("foo2");

    const subscription2 = manager.shared.fooValue.subscribe(jest.fn());
    expect(subscription2).toHaveBeenCalledWith("foo2");

    expect(sentMessages).toStrictEqual([
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "foo1",
      },
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "foo2",
      },
    ]);
  });

  test("unsubscribe consumed", () => {
    const subscription = manager.clientApi.shared.barValue.subscribe(jest.fn());
    manager.clientApi.shared.barValue.unsubscribe(subscription);
    manager.clientApi.shared.barValue.set("new-bar");
  });

  test("unsubscribe owned", () => {
    const subscription = manager.shared.fooValue.subscribe(jest.fn()); //this calls the callback
    manager.shared.fooValue.unsubscribe(subscription);
    manager.shared.fooValue.set("new-foo"); //this shouldn't call the callback
    expect(subscription).toHaveBeenCalledTimes(1);
  });
});

describe("receive", () => {
  test("simple request", async () => {
    manager.server.receive(
      {
        type: "getEnvelopeText",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-req-1",
        data: [],
      },
      apiImpl
    );

    await delay(0);

    expect(sentMessages).toStrictEqual([
      {
        error: undefined,
        type: "getEnvelopeText",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-req-1",
        data: "a text",
      },
    ]);
  });

  test("request with parameters", async () => {
    manager.server.receive(
      {
        type: "getFooBar",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-req-1",
        data: ["foo", "bar"],
      },
      apiImpl
    );

    expect(apiImpl.getFooBar).toHaveBeenCalled();

    await delay(0);

    expect(sentMessages).toStrictEqual([
      {
        error: undefined,
        type: "getFooBar",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-req-1",
        data: "foobar",
      },
    ]);
  });

  test("response with no requestId", () => {
    expect(() => {
      manager.server.receive(
        {
          type: "getEnvelopeText",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          data: [],
        },
        apiImpl
      );
    }).toThrowError();
  });

  test("response with no corresponding request", () => {
    expect(() => {
      manager.server.receive(
        {
          type: "getEnvelopeText",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          requestId: "my-req-1",
          data: [],
        },
        apiImpl
      );
    }).toThrowError();
  });

  test("simple notification", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
      apiImpl
    );

    expect(apiImpl.setText).toHaveBeenCalledWith("the text");
  });

  test("normal notification subscription", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
        data: [],
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
      apiImpl
    );

    expect(apiImpl.setText).toHaveBeenCalledWith("the text");
    expect(sentMessages).toStrictEqual([
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
    ]);
  });

  test("notification subscription with no api handler", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
        data: [],
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
      {} as ApiToProvide
    );

    expect(apiImpl.setText).not.toHaveBeenCalledWith();

    expect(sentMessages).toStrictEqual([
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
    ]);
  });

  test("notification unsubscription", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
        data: [],
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION_UNSUBSCRIPTION,
        data: [],
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"],
      },
      {} as ApiToProvide
    );

    expect(apiImpl.setText).not.toHaveBeenCalled();
    expect(sentMessages).toStrictEqual([]);
  });

  test("consumed shared value update", () => {
    const subscription1 = manager.clientApi.shared.barValue.subscribe(jest.fn());

    manager.server.receive(
      {
        type: "barValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "default-bar",
      },
      apiImpl
    );
    expect(subscription1).toHaveBeenCalledWith("default-bar");
    expect(subscription1).toHaveBeenCalledTimes(1);

    const subscription2 = manager.clientApi.shared.barValue.subscribe(jest.fn());
    expect(subscription2).toHaveBeenCalledWith("default-bar");
    expect(subscription2).toHaveBeenCalledTimes(1);
  });

  test("owned shared value update", () => {
    const subscription1 = manager.shared.fooValue.subscribe(jest.fn());

    manager.server.receive(
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "foo-from-someone",
      },
      apiImpl
    );
    expect(subscription1).toHaveBeenCalledWith("foo-from-someone");
    expect(subscription1).toHaveBeenCalledTimes(2);

    const subscription2 = manager.shared.fooValue.subscribe(jest.fn());
    expect(subscription2).toHaveBeenCalledWith("foo-from-someone");
    expect(subscription2).toHaveBeenCalledTimes(1);
  });

  test("shared value subscription", () => {
    manager.server.receive(
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_GET_DEFAULT,
        data: [],
      },
      apiImpl
    );
    expect(sentMessages).toStrictEqual([
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "default-foo",
      },
    ]);

    manager.shared.fooValue.set("new-foo");
    manager.server.receive(
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_GET_DEFAULT,
        data: [],
      },
      apiImpl
    );
    expect(sentMessages).toStrictEqual([
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "default-foo",
      },
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "new-foo",
      },
      {
        type: "fooValue",
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: "new-foo",
      },
    ]);
  });
});

const delay = (ms: number) => {
  return new Promise((res) => setTimeout(res, ms));
};
