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
  ChannelType,
  ContentType,
  LanguageData,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  ResourceContent
} from "@kogito-tooling/core-api";
import {
  ResourceContentApi,
  EditorEnvelopeController,
  ResourceContentEditorCoordinator,
  SpecialDomElements,
  EnvelopeBusInnerMessageHandler
} from "@kogito-tooling/microeditor-envelope";
import { EnvelopeBusMessage, EnvelopeBusMessageType } from "@kogito-tooling/microeditor-envelope-protocol";
import { render } from "@testing-library/react";
import * as React from "react";
import { EditorType } from "../../common/EditorTypes";
import { File } from "../../common/File";
import { EmbeddedEditorRouter } from "../../embedded/EmbeddedEditorRouter";
import { EmbeddedViewer } from "../../embedded/EmbeddedViewer";
import { DummyEditor } from "./DummyEditor";

const StateControlMock = jest.fn(() => ({
  undo: jest.fn(),
  redo: jest.fn(),
  registry: jest.fn()
}));

let stateControl: any;
let resourceContentEditorCoordinator: ResourceContentEditorCoordinator;
let loadingScreenContainer: HTMLElement;
let envelopeContainer: HTMLElement;
let controller: EditorEnvelopeController;
let sentMessages: Array<EnvelopeBusMessage<any>>;

beforeEach(() => {
  loadingScreenContainer = document.body.appendChild(document.createElement("div"));
  loadingScreenContainer.setAttribute("id", "loading-screen");

  envelopeContainer = document.body.appendChild(document.createElement("div"));
  envelopeContainer.setAttribute("id", "envelopeContainer");
});

beforeEach(() => {
  sentMessages = [];
  stateControl = new StateControlMock();
  resourceContentEditorCoordinator = new ResourceContentEditorCoordinator();
  controller = new EditorEnvelopeController(
    {
      postMessage: message => {
        sentMessages.push(message);
      }
    },
    {
      createEditor(_: LanguageData) {
        return Promise.resolve(new DummyEditor());
      }
    },
    new SpecialDomElements(),
    stateControl,
    {
      render: (element, container, callback) => {
        callback();
      }
    },
    resourceContentEditorCoordinator
  );

});

afterEach(() => {
  controller.stop();
  loadingScreenContainer.remove();
});

const delay = (ms: number) => {
  return Promise.resolve().then(() => new Promise(res => setTimeout(res, ms)));
};

async function startController(): Promise<EnvelopeBusInnerMessageHandler> {
  return controller.start(envelopeContainer);
}

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}

describe("EmbeddedViewer", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
  const channelType: ChannelType = ChannelType.ONLINE;

  afterEach(() => {
    jest.clearAllMocks();
  });

  test("EmbeddedViewer::defaults", async () => {
    render(<EmbeddedViewer file={file} router={router} channelType={channelType} />);

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedViewer::init", async () => {
    render(<EmbeddedViewer file={file} router={router} channelType={channelType} />);

    await startController();

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });

    expect(sentMessages).toEqual([
      { type: EnvelopeBusMessageType.RETURN_INIT, data: undefined },
      { type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined }
    ]);

  });

  test("EmbeddedViewer::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => {
      console.log("smsu");
      return Promise.resolve({ path: "", type: ContentType.TEXT });
    });

    render(<EmbeddedViewer
      file={file}
      router={router}
      channelType={channelType}
      onResourceContentRequest={onResourceContentRequest}
    />);

    //Start editor and wait for it to initialise
    const messageBus: EnvelopeBusInnerMessageHandler = await startController();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });

    //Make request for content and wait for it to complete
    resourceContentEditorCoordinator.exposeApi(messageBus).get("");
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_RESOURCE_CONTENT, data: { path: "" } });

    expect(sentMessages).toEqual([
      { type: EnvelopeBusMessageType.RETURN_INIT, data: undefined },
      { type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined },
      { type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: "" } }
    ]);

    expect(onResourceContentRequest).toBeCalled();
  });

  // test("EmbeddedViewer::onResourceListRequest", async () => {
  //   const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
  //     Promise.resolve({ pattern: "", paths: [] })
  //   );

  //   const r = await startController(
  //     <EmbeddedViewer
  //       file={file}
  //       router={router}
  //       channelType={channelType}
  //       onResourceListRequest={onResourceListRequest}
  //     />
  //   );

  //   await incomingMessage({ type: EnvelopeBusMessageType.RETURN_RESOURCE_LIST, data: { pattern: "", paths: [] } });

  //   expect(sentMessages).toEqual([
  //     { type: EnvelopeBusMessageType.RETURN_INIT, data: undefined }
  //   ]);
  // });
});
