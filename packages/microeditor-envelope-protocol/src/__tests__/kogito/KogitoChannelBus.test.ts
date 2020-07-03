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
  EnvelopeBusMessagePurpose,
  KogitoChannelBus,
  KogitoChannelApi,
  MessageTypesYouCanSendToTheChannel,
  MessageTypesYouCanSendToTheEnvelope
} from "../..";
import { ContentType, ResourceContent, StateControlCommand } from "@kogito-tooling/core-api";

let sentMessages: Array<EnvelopeBusMessage<unknown, any>>;
let channelBusApi: KogitoChannelBus;
let apiImpl: KogitoChannelApi;

beforeEach(() => {
  sentMessages = [];
  apiImpl = {
    receive_setContentError: jest.fn(),
    receive_ready: jest.fn(),
    receive_newEdit: jest.fn(),
    receive_openFile: jest.fn(),
    receive_stateControlCommandUpdate: jest.fn(),
    receive_guidedTourUserInteraction: jest.fn(),
    receive_guidedTourRegisterTutorial: jest.fn(),
    receive_languageRequest: jest.fn(),
    receive_contentRequest: jest.fn(),
    receive_resourceContentRequest: jest.fn(),
    receive_resourceListRequest: jest.fn()
  };

  channelBusApi = new KogitoChannelBus({ postMessage: msg => sentMessages.push(msg) }, apiImpl);
});

const delay = (ms: number) => {
  return new Promise(res => Promise.resolve().then(() => setTimeout(res, ms)));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(channelBusApi.initPolling).toBeFalsy();
    expect(channelBusApi.initPollingTimeout).toBeFalsy();
    expect(sentMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    jest.spyOn(channelBusApi, "stopInitPolling");
    jest.spyOn(channelBusApi.manager, "generateRandomId").mockReturnValueOnce("reqId");

    channelBusApi.startInitPolling("tests");
    expect(channelBusApi.initPolling).toBeTruthy();
    expect(channelBusApi.initPollingTimeout).toBeTruthy();

    await delay(100); //waits for setInterval to kick in

    await receive({
      busId: channelBusApi.busId,
      requestId: "reqId",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined
    });

    expect(channelBusApi.stopInitPolling).toHaveBeenCalled();
    expect(channelBusApi.initPolling).toBeFalsy();
    expect(channelBusApi.initPollingTimeout).toBeFalsy();
  });

  test("stops polling after timeout", async () => {
    jest.spyOn(channelBusApi, "stopInitPolling");
    KogitoChannelBus.INIT_POLLING_TIMEOUT_IN_MS = 200;

    channelBusApi.startInitPolling("tests");
    expect(channelBusApi.initPolling).toBeTruthy();
    expect(channelBusApi.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(300);

    expect(channelBusApi.stopInitPolling).toHaveBeenCalled();
    expect(channelBusApi.initPolling).toBeFalsy();
    expect(channelBusApi.initPollingTimeout).toBeFalsy();
  });
});

describe("receive", () => {
  test("any message with different busId", () => {
    channelBusApi.receive({
      busId: "unknown-id",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "any",
      type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE,
      data: undefined
    });
    channelBusApi.receive({
      busId: "unknown-id",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      requestId: "any",
      type: MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT,
      data: undefined
    });
  });

  test("setContentError", async () => {
    jest.spyOn(apiImpl, "receive_setContentError");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR,
      data: "this is the error"
    });
    expect(apiImpl.receive_setContentError).toHaveBeenCalledWith("this is the error");
  });

  test("setContentError notification", async () => {
    jest.spyOn(apiImpl, "receive_setContentError");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR,
      data: "this is the error"
    });
    expect(apiImpl.receive_setContentError).toHaveBeenCalledWith("this is the error");
  });

  test("ready notification", async () => {
    jest.spyOn(apiImpl, "receive_ready");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_READY,
      data: undefined
    });
    expect(apiImpl.receive_ready).toHaveBeenCalledWith();
  });

  test("newEdit notification", async () => {
    jest.spyOn(apiImpl, "receive_newEdit");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_NEW_EDIT,
      data: { id: "edit-id" }
    });
    expect(apiImpl.receive_newEdit).toHaveBeenCalledWith({ id: "edit-id" });
  });
  test("openFile notification", async () => {
    jest.spyOn(apiImpl, "receive_openFile");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_OPEN_FILE,
      data: "a/path"
    });
    expect(apiImpl.receive_openFile).toHaveBeenCalledWith("a/path");
  });
  test("stateControlCommandUpdate notification", async () => {
    jest.spyOn(apiImpl, "receive_stateControlCommandUpdate");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_STATE_CONTROL_COMMAND_UPDATE,
      data: StateControlCommand.REDO
    });
    expect(apiImpl.receive_stateControlCommandUpdate).toHaveBeenCalledWith(StateControlCommand.REDO);
  });

  test("guidedTourRegisterTutorial notification", async () => {
    jest.spyOn(apiImpl, "receive_guidedTourRegisterTutorial");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_REGISTER_TUTORIAL,
      data: {}
    });
    expect(apiImpl.receive_guidedTourRegisterTutorial).toHaveBeenCalledWith({});
  });

  test("guidedTourUserInteraction notification", async () => {
    jest.spyOn(apiImpl, "receive_guidedTourUserInteraction");
    channelBusApi.receive({
      busId: channelBusApi.busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_GUIDED_TOUR_USER_INTERACTION,
      data: {}
    });
    expect(apiImpl.receive_guidedTourUserInteraction).toHaveBeenCalledWith({});
  });

  test("language request", async () => {
    const languageData = { type: "a-language" };

    jest.spyOn(apiImpl, "receive_languageRequest").mockReturnValueOnce(Promise.resolve(languageData));

    await receive({
      busId: channelBusApi.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE,
      data: undefined
    });

    expect(apiImpl.receive_languageRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheChannel.REQUEST_LANGUAGE,
        data: languageData
      }
    ]);
  });

  test("content request", async () => {
    const content = { content: "the language", path: "the path" };

    jest.spyOn(apiImpl, "receive_contentRequest").mockReturnValueOnce(Promise.resolve(content));

    await receive({
      busId: channelBusApi.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT,
      data: undefined
    });

    expect(apiImpl.receive_contentRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheChannel.REQUEST_CONTENT,
        data: content
      }
    ]);
  });

  test("resourceContent request", async () => {
    const resourceContent = new ResourceContent("a/path", "the content", ContentType.TEXT);
    const resourceContentRequest = { path: "a/path", opts: { type: ContentType.TEXT } };

    jest.spyOn(apiImpl, "receive_resourceContentRequest").mockReturnValueOnce(Promise.resolve(resourceContent));

    await receive({
      busId: channelBusApi.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_CONTENT,
      data: resourceContentRequest
    });

    expect(apiImpl.receive_resourceContentRequest).toHaveBeenCalledWith(resourceContentRequest);
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_CONTENT,
        data: resourceContent
      }
    ]);
  });

  test("resourceList request", async () => {
    const resourceList = { pattern: "*", paths: ["a/resource/file.txt"] };
    const resourceListRequest = { path: "a/path", opts: { type: ContentType.TEXT } };

    jest.spyOn(apiImpl, "receive_resourceListRequest").mockReturnValueOnce(Promise.resolve(resourceList));

    await receive({
      busId: channelBusApi.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_LIST,
      data: resourceListRequest
    });

    expect(apiImpl.receive_resourceListRequest).toHaveBeenCalledWith(resourceListRequest);
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_LIST,
        data: resourceList
      }
    ]);
  });
});

describe("send", () => {
  test("request init", async () => {
    jest.spyOn(channelBusApi.manager, "generateRandomId").mockReturnValueOnce("1");
    const init = channelBusApi.request_initResponse("test-origin");
    expect(sentMessages).toEqual([
      {
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "1",
        type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
        data: { busId: channelBusApi.busId, origin: "test-origin" }
      }
    ]);

    await receive({
      busId: channelBusApi.busId,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_INIT,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined
    });

    expect(await init).toStrictEqual(undefined);
  });

  test("request contentResponse", async () => {
    jest.spyOn(channelBusApi.manager, "generateRandomId").mockReturnValueOnce("1");
    const content = channelBusApi.request_contentResponse();
    await receive({
      busId: channelBusApi.busId,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_CONTENT,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: { content: "the content", path: "the/path/" }
    });

    expect(await content).toStrictEqual({ content: "the content", path: "the/path/" });
  });

  test("request preview", async () => {
    jest.spyOn(channelBusApi.manager, "generateRandomId").mockReturnValueOnce("1");
    const preview = channelBusApi.request_previewResponse();
    await receive({
      busId: channelBusApi.busId,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_PREVIEW,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: "the-svg-string"
    });

    expect(await preview).toStrictEqual("the-svg-string");
  });

  test("request guidedTourElementPositionResponse", async () => {
    jest.spyOn(channelBusApi.manager, "generateRandomId").mockReturnValueOnce("1");
    const position = channelBusApi.request_guidedTourElementPositionResponse("my-selector");
    await receive({
      busId: channelBusApi.busId,
      requestId: "1",
      type: MessageTypesYouCanSendToTheEnvelope.REQUEST_GUIDED_TOUR_ELEMENT_POSITION,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: {}
    });

    expect(await position).toStrictEqual({});
  });

  test("notify contentChanged", () => {
    channelBusApi.notify_contentChanged({ content: "new-content" });
    expect(sentMessages).toEqual([
      {
        type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_CONTENT_CHANGED,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: { content: "new-content" }
      }
    ]);
  });

  test("notify editorUndo", () => {
    channelBusApi.notify_editorUndo();
    expect(sentMessages).toEqual([
      {
        type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_UNDO,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: undefined
      }
    ]);
  });

  test("notify editorRedo", () => {
    channelBusApi.notify_editorRedo();
    expect(sentMessages).toEqual([
      {
        type: MessageTypesYouCanSendToTheEnvelope.NOTIFY_EDITOR_REDO,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: undefined
      }
    ]);
  });
});

async function receive(
  message: EnvelopeBusMessage<unknown, MessageTypesYouCanSendToTheChannel | MessageTypesYouCanSendToTheEnvelope>
) {
  channelBusApi.receive(message);
  await delay(0); // waits for next event loop iteration
}
