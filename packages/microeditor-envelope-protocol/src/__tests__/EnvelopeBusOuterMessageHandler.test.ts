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

import { EnvelopeBusOuterMessageHandler } from "../EnvelopeBusOuterMessageHandler";
import { EnvelopeBusMessage } from "../EnvelopeBusMessage";
import { EnvelopeBusMessageType } from "../EnvelopeBusMessageType";
import { EditorContent, ResourceContentRequest } from "@kogito-tooling/core-api";

let sentMessages: Array<EnvelopeBusMessage<any>>;
let receivedMessages: string[];
let handler: EnvelopeBusOuterMessageHandler;
let initPollCount: number;

beforeEach(() => {
  sentMessages = [];
  receivedMessages = [];
  initPollCount = 0;

  handler = new EnvelopeBusOuterMessageHandler(
    {
      postMessage: msg => sentMessages.push(msg)
    },
    self => ({
      pollInit: () => {
        initPollCount++;
      },
      receive_languageRequest() {
        receivedMessages.push("languageRequest");
      },
      receive_contentRequest() {
        receivedMessages.push("contentRequest");
      },
      receive_contentResponse(content: EditorContent) {
        receivedMessages.push("contentResponse_" + content.content);
      },
      receive_setContentError: (errorMessage: string) => {
        receivedMessages.push("setContentError_" + errorMessage);
      },
      receive_dirtyIndicatorChange(isDirty: boolean): void {
        receivedMessages.push("dirtyIndicatorChange_" + isDirty);
      },
      receive_resourceContentRequest(resourceContentRequest: ResourceContentRequest): void {
        receivedMessages.push("resourceContentRequest_" + resourceContentRequest.path);
      },
      receive_readResourceContentError(errorMessage: string): void {
        receivedMessages.push("readResourceContentError_" + errorMessage);
      },
      receive_resourceListRequest(pattern: string): void {
        receivedMessages.push("resourceListRequest_" + pattern);
      },
      receive_ready() {
        receivedMessages.push("ready");
      }
    })
  );
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(handler.initPolling).toBeFalsy();
    expect(handler.initPollingTimeout).toBeFalsy();
    expect(sentMessages.length).toEqual(0);
    expect(receivedMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    handler.startInitPolling();
    expect(handler.initPolling).toBeTruthy();
    expect(handler.initPollingTimeout).toBeTruthy();

    //less than the timeout
    await delay(100);

    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.RETURN_INIT, data: undefined });

    expect(initPollCount).toBeGreaterThan(0);
    expect(handler.initPolling).toBeFalsy();
    expect(handler.initPollingTimeout).toBeFalsy();
  });

  test("stops polling after timeout", async () => {
    EnvelopeBusOuterMessageHandler.INIT_POLLING_TIMEOUT_IN_MS = 200;

    handler.startInitPolling();
    expect(handler.initPolling).toBeTruthy();
    expect(handler.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(300);

    expect(initPollCount).toBeGreaterThan(0);
    expect(handler.initPolling).toBeFalsy();
    expect(handler.initPollingTimeout).toBeFalsy();
  });
});

describe("receive", () => {
  test("any message with different id", () => {
    handler.receive({ busId: "unknown-id", type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined });
    handler.receive({ busId: "unknown-id", type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
    expect(receivedMessages).toEqual([]);
  });

  test("language request", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined });
    expect(receivedMessages).toEqual(["languageRequest"]);
  });

  test("content request", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined });
    expect(receivedMessages).toEqual(["contentRequest"]);
  });

  test("content response", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.RETURN_CONTENT, data: { content: "foo" } });
    expect(receivedMessages).toEqual(["contentResponse_foo"]);
  });

  test("set content error notification", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR, data: "errorMsg" });
    expect(receivedMessages).toEqual(["setContentError_errorMsg"]);
  });

  test("dirty indicator change notification", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE, data: true });
    expect(receivedMessages).toEqual(["dirtyIndicatorChange_true"]);
  });

  test("ready notification", () => {
    handler.receive({ busId: handler.busId, type: EnvelopeBusMessageType.NOTIFY_READY, data: undefined });
    expect(receivedMessages).toEqual(["ready"]);
  });
});

describe("send", () => {
  test("request contentResponse", () => {
    handler.request_contentResponse();
    expect(sentMessages).toEqual([{ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined }]);
  });

  test("request init", () => {
    handler.request_initResponse("test-origin");
    expect(sentMessages).toEqual([
      { busId: handler.busId, type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-origin" }
    ]);
  });

  test("respond languageRequest", () => {
    const languageData = { type: "dummy", editorId: "id", gwtModuleName: "name", resources: [] };
    handler.respond_languageRequest(languageData);
    expect(sentMessages).toEqual([{ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData }]);
  });

  test("respond contentRequest", () => {
    handler.respond_contentRequest({ content: "bar" });
    expect(sentMessages).toEqual([{ type: EnvelopeBusMessageType.RETURN_CONTENT, data: { content: "bar" } }]);
  });
});
