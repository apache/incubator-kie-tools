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
import { EnvelopeBusMessageType } from "../EnvelopeBusMessageType";
import { getTestEnvelopeBusOuterMessageHandler, TestEnvelopeBusOuterMessageHandler } from "./utils";

let testEnvelopeBusOuterMessageHandler: TestEnvelopeBusOuterMessageHandler;

beforeEach(() => {
  testEnvelopeBusOuterMessageHandler = getTestEnvelopeBusOuterMessageHandler();
});

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(testEnvelopeBusOuterMessageHandler.handler.initPolling).toBeFalsy();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPollingTimeout).toBeFalsy();
    expect(testEnvelopeBusOuterMessageHandler.sentMessages.length).toEqual(0);
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    testEnvelopeBusOuterMessageHandler.handler.startInitPolling();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPolling).toBeTruthy();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPollingTimeout).toBeTruthy();

    //less than the timeout
    await delay(100);

    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.RETURN_INIT,
      data: undefined
    });

    expect(testEnvelopeBusOuterMessageHandler.initPollCount).toBeGreaterThan(0);
    expect(testEnvelopeBusOuterMessageHandler.handler.initPolling).toBeFalsy();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPollingTimeout).toBeFalsy();
  });

  test("stops polling after timeout", async () => {
    EnvelopeBusOuterMessageHandler.INIT_POLLING_TIMEOUT_IN_MS = 200;

    testEnvelopeBusOuterMessageHandler.handler.startInitPolling();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPolling).toBeTruthy();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(300);

    expect(testEnvelopeBusOuterMessageHandler.initPollCount).toBeGreaterThan(0);
    expect(testEnvelopeBusOuterMessageHandler.handler.initPolling).toBeFalsy();
    expect(testEnvelopeBusOuterMessageHandler.handler.initPollingTimeout).toBeFalsy();
  });
});

describe("receive", () => {
  test("any message with different id", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: "unknown-id",
      type: EnvelopeBusMessageType.REQUEST_LANGUAGE,
      data: undefined
    });
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: "unknown-id",
      type: EnvelopeBusMessageType.REQUEST_CONTENT,
      data: undefined
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual([]);
  });

  test("language request", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.REQUEST_LANGUAGE,
      data: undefined
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["languageRequest"]);
  });

  test("content request", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.REQUEST_CONTENT,
      data: undefined
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["contentRequest"]);
  });

  test("content response", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.RETURN_CONTENT,
      data: { content: "foo" }
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["contentResponse_foo"]);
  });

  test("set content error notification", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR,
      data: "errorMsg"
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["setContentError_errorMsg"]);
  });

  test("dirty indicator change notification", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE,
      data: true
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["dirtyIndicatorChange_true"]);
  });

  test("ready notification", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.NOTIFY_READY,
      data: undefined
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["ready"]);
  });

  test("open file notification", () => {
    testEnvelopeBusOuterMessageHandler.handler.receive({
      busId: testEnvelopeBusOuterMessageHandler.handler.busId,
      type: EnvelopeBusMessageType.NOTIFY_EDITOR_OPEN_FILE,
      data: "file/path/to/open"
    });
    expect(testEnvelopeBusOuterMessageHandler.receivedMessages).toEqual(["receiveOpenFile_file/path/to/open"]);
  });
});

describe("send", () => {
  test("request contentResponse", () => {
    testEnvelopeBusOuterMessageHandler.handler.request_contentResponse();
    expect(testEnvelopeBusOuterMessageHandler.sentMessages).toEqual([
      { type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined }
    ]);
  });

  test("request init", () => {
    testEnvelopeBusOuterMessageHandler.handler.request_initResponse("test-origin");
    expect(testEnvelopeBusOuterMessageHandler.sentMessages).toEqual([
      {
        busId: testEnvelopeBusOuterMessageHandler.handler.busId,
        type: EnvelopeBusMessageType.REQUEST_INIT,
        data: "test-origin"
      }
    ]);
  });

  test("respond languageRequest", () => {
    const languageData = { type: "dummy", editorId: "id", gwtModuleName: "name", resources: [] };
    testEnvelopeBusOuterMessageHandler.handler.respond_languageRequest(languageData);
    expect(testEnvelopeBusOuterMessageHandler.sentMessages).toEqual([
      { type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData }
    ]);
  });

  test("respond contentRequest", () => {
    testEnvelopeBusOuterMessageHandler.handler.respond_contentRequest({ content: "bar" });
    expect(testEnvelopeBusOuterMessageHandler.sentMessages).toEqual([
      { type: EnvelopeBusMessageType.RETURN_CONTENT, data: { content: "bar" } }
    ]);
  });
});
