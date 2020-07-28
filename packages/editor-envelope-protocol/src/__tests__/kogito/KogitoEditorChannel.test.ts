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

import { EnvelopeBusMessage, EnvelopeBusMessagePurpose, FunctionPropertyNames } from "@kogito-tooling/envelope-bus";
import { KogitoEditorChannel, KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "../..";
import { ContentType, ResourceContent, StateControlCommand } from "../../kogito/api";

let sentMessages: Array<EnvelopeBusMessage<unknown, any>>;
let channelBus: KogitoEditorChannel;
let api: KogitoEditorChannelApi;

beforeEach(() => {
  sentMessages = [];
  api = {
    receive_setContentError: jest.fn(),
    receive_ready: jest.fn(),
    receive_newEdit: jest.fn(),
    receive_openFile: jest.fn(),
    receive_stateControlCommandUpdate: jest.fn(),
    receive_guidedTourUserInteraction: jest.fn(),
    receive_guidedTourRegisterTutorial: jest.fn(),
    receive_contentRequest: jest.fn(),
    receive_resourceContentRequest: jest.fn(),
    receive_resourceListRequest: jest.fn()
  };

  channelBus = new KogitoEditorChannel({ postMessage: (msg: any) => sentMessages.push(msg) });
});

const delay = (ms: number) => {
  return new Promise(res => Promise.resolve().then(() => setTimeout(res, ms)));
};

describe("new instance", () => {
  test("does nothing", () => {
    expect(channelBus.initPolling).toBeFalsy();
    expect(channelBus.initPollingTimeout).toBeFalsy();
    expect(sentMessages.length).toEqual(0);
  });
});

describe("startInitPolling", () => {
  test("polls for init response", async () => {
    jest.spyOn(channelBus, "stopInitPolling");

    channelBus.startInitPolling("tests", { fileExtension: "txt", resourcesPathPrefix: "" });
    expect(channelBus.initPolling).toBeTruthy();
    expect(channelBus.initPollingTimeout).toBeTruthy();

    await delay(100); //waits for setInterval to kick in

    await incomingMessage({
      busId: channelBus.busId,
      requestId: "KogitoEditorChannel_0",
      type: "receive_initRequest",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined
    });

    expect(channelBus.stopInitPolling).toHaveBeenCalled();
    expect(channelBus.initPolling).toBeFalsy();
    expect(channelBus.initPollingTimeout).toBeFalsy();
  });

  test("stops polling after timeout", async () => {
    jest.spyOn(channelBus, "stopInitPolling");
    KogitoEditorChannel.INIT_POLLING_TIMEOUT_IN_MS = 200;

    channelBus.startInitPolling("tests", { fileExtension: "txt", resourcesPathPrefix: "" });
    expect(channelBus.initPolling).toBeTruthy();
    expect(channelBus.initPollingTimeout).toBeTruthy();

    //more than the timeout
    await delay(300);

    expect(channelBus.stopInitPolling).toHaveBeenCalled();
    expect(channelBus.initPolling).toBeFalsy();
    expect(channelBus.initPollingTimeout).toBeFalsy();
  });
});

describe("receive", () => {
  test("any message with different busId", () => {
    channelBus.receive(
      {
        busId: "unknown-id",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "receive_resourceListRequest",
        data: []
      },
      api
    );
    channelBus.receive(
      {
        busId: "unknown-id",
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "any",
        type: "receive_contentRequest",
        data: []
      },
      api
    );
  });

  test("setContentError notification", async () => {
    jest.spyOn(api, "receive_setContentError");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_setContentError",
        data: ["this is the error"]
      },
      api
    );
    expect(api.receive_setContentError).toHaveBeenCalledWith("this is the error");
  });

  test("ready notification", async () => {
    jest.spyOn(api, "receive_ready");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_ready",
        data: []
      },
      api
    );
    expect(api.receive_ready).toHaveBeenCalledWith();
  });

  test("newEdit notification", async () => {
    jest.spyOn(api, "receive_newEdit");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_newEdit",
        data: [{ id: "edit-id" }]
      },
      api
    );
    expect(api.receive_newEdit).toHaveBeenCalledWith({ id: "edit-id" });
  });
  test("openFile notification", async () => {
    jest.spyOn(api, "receive_openFile");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_openFile",
        data: ["a/path"]
      },
      api
    );
    expect(api.receive_openFile).toHaveBeenCalledWith("a/path");
  });
  test("stateControlCommandUpdate notification", async () => {
    jest.spyOn(api, "receive_stateControlCommandUpdate");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_stateControlCommandUpdate",
        data: [StateControlCommand.REDO]
      },
      api
    );
    expect(api.receive_stateControlCommandUpdate).toHaveBeenCalledWith(StateControlCommand.REDO);
  });

  test("guidedTourRegisterTutorial notification", async () => {
    jest.spyOn(api, "receive_guidedTourRegisterTutorial");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_guidedTourRegisterTutorial",
        data: []
      },
      api
    );
    expect(api.receive_guidedTourRegisterTutorial).toHaveBeenCalledWith();
  });

  test("guidedTourUserInteraction notification", async () => {
    jest.spyOn(api, "receive_guidedTourUserInteraction");
    channelBus.receive(
      {
        busId: channelBus.busId,
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        type: "receive_guidedTourUserInteraction",
        data: []
      },
      api
    );
    expect(api.receive_guidedTourUserInteraction).toHaveBeenCalledWith();
  });

  test("content request", async () => {
    const content = { content: "the language", path: "the path" };

    jest.spyOn(api, "receive_contentRequest").mockReturnValueOnce(Promise.resolve(content));

    await incomingMessage({
      busId: channelBus.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_contentRequest",
      data: []
    });

    expect(api.receive_contentRequest).toHaveBeenCalledWith();
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "receive_contentRequest",
        data: content
      }
    ]);
  });

  test("resourceContent request", async () => {
    const resourceContent = new ResourceContent("a/path", "the content", ContentType.TEXT);
    const resourceContentRequest = { path: "a/path", opts: { type: ContentType.TEXT } };

    jest.spyOn(api, "receive_resourceContentRequest").mockReturnValueOnce(Promise.resolve(resourceContent));

    await incomingMessage({
      busId: channelBus.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceContentRequest",
      data: [resourceContentRequest]
    });

    expect(api.receive_resourceContentRequest).toHaveBeenCalledWith(resourceContentRequest);
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "receive_resourceContentRequest",
        data: resourceContent
      }
    ]);
  });

  test("resourceList request", async () => {
    const resourceList = { pattern: "*", paths: ["a/resource/file.txt"] };
    const resourceListRequest = { path: "a/path", opts: { type: ContentType.TEXT } };

    jest.spyOn(api, "receive_resourceListRequest").mockReturnValueOnce(Promise.resolve(resourceList));

    await incomingMessage({
      busId: channelBus.busId,
      requestId: "requestId",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceListRequest",
      data: [resourceListRequest]
    });

    expect(api.receive_resourceListRequest).toHaveBeenCalledWith(resourceListRequest);
    expect(sentMessages).toEqual([
      {
        requestId: "requestId",
        purpose: EnvelopeBusMessagePurpose.RESPONSE,
        type: "receive_resourceListRequest",
        data: resourceList
      }
    ]);
  });
});

describe("send", () => {
  test("request init", async () => {
    const init = channelBus.request_initResponse("test-origin", { fileExtension: "txt", resourcesPathPrefix: "" });
    expect(sentMessages).toEqual([
      {
        purpose: EnvelopeBusMessagePurpose.REQUEST,
        requestId: "KogitoEditorChannel_0",
        type: "receive_initRequest",
        data: [
          { busId: channelBus.busId, origin: "test-origin" },
          { fileExtension: "txt", resourcesPathPrefix: "" }
        ]
      }
    ]);

    await incomingMessage({
      busId: channelBus.busId,
      requestId: "KogitoEditorChannel_0",
      type: "receive_initRequest",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: undefined
    });

    expect(await init).toStrictEqual(undefined);
  });

  test("request contentResponse", async () => {
    const content = channelBus.request_contentResponse();
    await incomingMessage({
      busId: channelBus.busId,
      requestId: "KogitoEditorChannel_0",
      type: "receive_contentRequest",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: { content: "the content", path: "the/path/" }
    });

    expect(await content).toStrictEqual({ content: "the content", path: "the/path/" });
  });

  test("request preview", async () => {
    const preview = channelBus.request_previewResponse();
    await incomingMessage({
      busId: channelBus.busId,
      requestId: "KogitoEditorChannel_0",
      type: "receive_previewRequest",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: "the-svg-string"
    });

    expect(await preview).toStrictEqual("the-svg-string");
  });

  test("request guidedTourElementPositionResponse", async () => {
    const position = channelBus.request_guidedTourElementPositionResponse("my-selector");
    await incomingMessage({
      busId: channelBus.busId,
      requestId: "KogitoEditorChannel_0",
      type: "receive_guidedTourElementPositionRequest",
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      data: {}
    });

    expect(await position).toStrictEqual({});
  });

  test("notify contentChanged", () => {
    channelBus.notify_contentChanged({ content: "new-content" });
    expect(sentMessages).toEqual([
      {
        type: "receive_contentChanged",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: [{ content: "new-content" }]
      }
    ]);
  });

  test("notify editorUndo", () => {
    channelBus.notify_editorUndo();
    expect(sentMessages).toEqual([
      {
        type: "receive_editorUndo",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: []
      }
    ]);
  });

  test("notify editorRedo", () => {
    channelBus.notify_editorRedo();
    expect(sentMessages).toEqual([
      {
        type: "receive_editorRedo",
        purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
        data: []
      }
    ]);
  });
});

async function incomingMessage(
  message: EnvelopeBusMessage<
    unknown,
    FunctionPropertyNames<KogitoEditorChannelApi> | FunctionPropertyNames<KogitoEditorEnvelopeApi>
  >
) {
  channelBus.receive(message, api);
  await delay(0); // waits for next event loop iteration
}
