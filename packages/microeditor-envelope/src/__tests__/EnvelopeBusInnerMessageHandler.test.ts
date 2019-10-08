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

import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";
import { EnvelopeBusMessageType } from "@kogito-tooling/microeditor-envelope-protocol";
import { LanguageData, ResourcesList, ResourceContent } from "@kogito-tooling/core-api";

let handler: EnvelopeBusInnerMessageHandler;
let receivedMessages: any[];
let sentMessages: any[];

beforeEach(() => {
  receivedMessages = [];
  sentMessages = [];

  handler = new EnvelopeBusInnerMessageHandler(
    {
      postMessage: (message, targetOrigin) => sentMessages.push([message, targetOrigin])
    },
    self => ({
      receive_contentResponse: (content: string) => {
        receivedMessages.push(["contentResponse", content]);
      },
      receive_languageResponse: (languageData: LanguageData) => {
        receivedMessages.push(["languageResponse", languageData]);
      },
      receive_contentRequest: () => {
        receivedMessages.push(["contentRequest", undefined]);
      },
      receive_resourceContentResponse: (content: ResourceContent) => {
        receivedMessages.push(["resourceContent", content]);
      },
      receive_resourceContentList: (resourcesList: ResourcesList) => {
        receivedMessages.push(["resourceContentList", resourcesList]);
      }
    })
  );
});

afterEach(() => {
  handler.stopListening();
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(sentMessages.length).toEqual(0);
    expect(receivedMessages.length).toEqual(0);

    expect(handler.capturedInitRequestYet).toBe(false);
    expect(handler.targetOrigin).toBe(undefined);
  });
});

describe("event listening", () => {
  test("activates when requested", async () => {
    spyOn(handler, "receive");
    handler.startListening();

    await incomingMessage("a-message");
    expect(handler.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivates when requested", async () => {
    spyOn(handler, "receive");
    handler.startListening();
    handler.stopListening();

    await incomingMessage("a-message");
    expect(handler.receive).toHaveBeenCalledTimes(0);
  });

  test("activation is idempotent", async () => {
    spyOn(handler, "receive");
    handler.startListening();
    handler.startListening();

    await incomingMessage("a-message");
    expect(handler.receive).toHaveBeenCalledTimes(1);
  });

  test("deactivation is idempotent", async () => {
    spyOn(handler, "receive");
    handler.startListening();
    handler.stopListening();
    handler.stopListening();

    await incomingMessage("a-message");
    expect(handler.receive).toHaveBeenCalledTimes(0);
  });

  test("deactivation does not fail when not started", async () => {
    spyOn(handler, "receive");
    handler.stopListening();

    await incomingMessage("a-message");
    expect(handler.receive).toHaveBeenCalledTimes(0);
  });
});

describe("receive", () => {
  test("initRequest", async () => {
    handler.startListening();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "tgt-orgn" });

    expect(handler.capturedInitRequestYet).toBe(true);
    expect(handler.targetOrigin).toBe("tgt-orgn");
    expect(sentMessages).toEqual([
      [{ type: EnvelopeBusMessageType.RETURN_INIT, data: undefined }, "tgt-orgn"],
      [{ type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined }, "tgt-orgn"]
    ]);
  });

  test("languageResponse", async () => {
    handler.startListening();
    const languageData = { editorId: "", gwtModuleName: "", resources: [] };
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData });

    expect(receivedMessages).toEqual([["languageResponse", languageData]]);
  });

  test("contentResponse", async () => {
    handler.startListening();
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: "foo" });

    expect(receivedMessages).toEqual([["contentResponse", "foo"]]);
  });

  test("contentRequest", async () => {
    handler.startListening();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });

    expect(receivedMessages).toEqual([["contentRequest", undefined]]);
  });
});

describe("send without being initialized", () => {
  test("throws error", () => {
    expect(() => handler.send({ data: "anything", type: EnvelopeBusMessageType.RETURN_INIT })).toThrow();
  });
});

describe("send", () => {
  beforeEach(async () => {
    handler.startListening();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "tgt-orgn" });
    sentMessages = [];
    receivedMessages = [];
  });

  test("request languageResponse", () => {
    handler.request_languageResponse();
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined }, "tgt-orgn"]]);
  });

  test("request contentResponse", () => {
    handler.request_contentResponse();
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined }, "tgt-orgn"]]);
  });

  test("respond initRequest", () => {
    handler.respond_initRequest();
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.RETURN_INIT, data: undefined }, "tgt-orgn"]]);
  });

  test("respond contentRequest", () => {
    handler.respond_contentRequest("some");
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.RETURN_CONTENT, data: "some" }, "tgt-orgn"]]);
  });

  test("notify setContentError", () => {
    handler.notify_setContentError("error msg");
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR, data: "error msg" }, "tgt-orgn"]]);
  });

  test("notify dirtyIndicatorChange", () => {
    handler.notify_dirtyIndicatorChange(true);
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE, data: true }, "tgt-orgn"]]);
  });

  test("notify ready", () => {
    handler.notify_ready();
    expect(sentMessages).toEqual([[{ type: EnvelopeBusMessageType.NOTIFY_READY, data: undefined }, "tgt-orgn"]]);
  });
});

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}
