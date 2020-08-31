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

import { EnvelopeBusMessageManager } from "../EnvelopeBusMessageManager";
import { EnvelopeBusMessagePurpose } from "../../api";

interface ApiToProvide {
  setText(text: string): void;
  getEnvelopeText(): Promise<string>;
  getFooBar(foo: string, bar: string): Promise<string>;
}

interface ApiToConsume {
  getText(): Promise<string>;
  setSomething(something: string): void;
  setSomethings(something1: string, something2: string): void;
  getSomething(param1: string, param2: number): Promise<number>;
}

let sentMessages: any[] = [];
let manager: EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>;
let apiImpl: ApiToProvide;

beforeEach(() => {
  sentMessages = [];
  manager = new EnvelopeBusMessageManager(msg => sentMessages.push(msg), "my-manager");
  apiImpl = {
    setText: jest.fn((text: string) => {
      console.info(`Setting text: ${text}`);
    }),
    getEnvelopeText: jest.fn(async () => {
      return "a text";
    }),
    getFooBar: jest.fn(async (foo: string, bar: string) => {
      return foo + bar;
    })
  };
});

describe("request", () => {
  test("method with no parameters", async () => {
    const retPromise = manager.clientApi.requests.getText();
    expect(sentMessages).toStrictEqual([
      {
        type: "getText",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_0",
        data: []
      }
    ]);

    await manager.server.receive(
      {
        type: "getText",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-manager_0",
        data: "foo"
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
        data: ["a", 1]
      }
    ]);

    await manager.server.receive(
      {
        type: "getSomething",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        requestId: "my-manager_0",
        data: 8
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
        data: []
      },
      {
        type: "getSomething",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-manager_1",
        data: ["b", 2]
      }
    ]);
  });
});

describe("notify", () => {
  test("simple notification", async () => {
    manager.clientApi.notifications.setSomething("something");
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something"]
      }
    ]);
  });
});

describe("subscribe", () => {
  test("simple subscription", async () => {
    const callback = jest.fn((a, b) => {
      expect(a).toStrictEqual("foo");
      expect(b).toStrictEqual("bar");
    });

    const subscription = manager.clientApi.subscribe("setSomethings", callback);
    expect(subscription).toStrictEqual(callback);
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomethings",
        purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
        data: []
      }
    ]);

    await manager.server.receive(
      {
        type: "setSomethings",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["foo", "bar"]
      },
      apiImpl
    );
    expect(callback).toHaveBeenCalledWith("foo", "bar");
  });
});

describe("unsubscribe", () => {
  test("simple unsubscription", async () => {
    const subscription = manager.clientApi.subscribe("setSomething", jest.fn());
    await manager.server.receive(
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something"]
      },
      apiImpl
    );

    expect(subscription).toHaveBeenCalledWith("something");
    expect(subscription).toHaveBeenCalledTimes(1);

    manager.clientApi.unsubscribe("setSomething", subscription);
    expect(sentMessages).toStrictEqual([
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
        data: []
      },
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.UNSUBSCRIPTION,
        data: []
      }
    ]);

    await manager.server.receive(
      {
        type: "setSomething",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["something2"]
      },
      apiImpl
    );

    expect(subscription).toHaveBeenCalledTimes(1);
  });
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("receive", () => {
  test("simple request", async () => {
    manager.server.receive(
      {
        type: "getEnvelopeText",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-req-1",
        data: []
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
        data: "a text"
      }
    ]);
  });
  test("request with parameters", async () => {
    manager.server.receive(
      {
        type: "getFooBar",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "my-req-1",
        data: ["foo", "bar"]
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
        data: "foobar"
      }
    ]);
  });

  test("response with no requestId", () => {
    expect(() => {
      manager.server.receive(
        {
          type: "getEnvelopeText",
          purpose: EnvelopeBusMessagePurpose.RESPONSE,
          data: []
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
          data: []
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
        data: ["the text"]
      },
      apiImpl
    );

    expect(apiImpl.setText).toHaveBeenCalledWith("the text");
  });

  test("normal subscription", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
        data: []
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"]
      },
      apiImpl
    );

    expect(apiImpl.setText).toHaveBeenCalledWith("the text");
    expect(sentMessages).toStrictEqual([
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"]
      }
    ]);
  });

  test("subscription with no api handler", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
        data: []
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"]
      },
      {} as ApiToProvide
    );

    expect(apiImpl.setText).not.toHaveBeenCalledWith();

    expect(sentMessages).toStrictEqual([
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"]
      }
    ]);
  });

  test("unsubscription", () => {
    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
        data: []
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.UNSUBSCRIPTION,
        data: []
      },
      apiImpl
    );

    manager.server.receive(
      {
        type: "setText",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: ["the text"]
      },
      {} as ApiToProvide
    );

    expect(apiImpl.setText).not.toHaveBeenCalled();
    expect(sentMessages).toStrictEqual([]);
  });
});
