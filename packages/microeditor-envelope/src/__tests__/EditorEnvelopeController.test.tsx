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

import { EditorEnvelopeController } from "../EditorEnvelopeController";
import { SpecialDomElements } from "../SpecialDomElements";
import { mount } from "enzyme";
import { EnvelopeBusMessage, EnvelopeBusMessageType } from "@kogito-tooling/microeditor-envelope-protocol";
import { LanguageData } from "@kogito-tooling/core-api";
import { DummyEditor } from "./DummyEditor";

let loadingScreenContainer: HTMLElement;
let envelopeContainer: HTMLElement;
beforeEach(() => {
  loadingScreenContainer = document.body.appendChild(document.createElement("div"));
  loadingScreenContainer.setAttribute("id", "loading-screen");

  envelopeContainer = document.body.appendChild(document.createElement("div"));
  envelopeContainer.setAttribute("id", "envelopeContainer");
});

afterEach(() => loadingScreenContainer.remove());

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

const languageData = {
  editorId: "test-editor-id",
  gwtModuleName: "none",
  resources: []
};

let sentMessages: Array<EnvelopeBusMessage<any>>;
let controller: EditorEnvelopeController;
let mockComponent: ReturnType<typeof mount>;

beforeEach(() => {
  sentMessages = [];

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
    {
      render: (element, container, callback) => {
        mockComponent = mount(element);
        callback();
      }
    }
  );
});

afterEach(() => {
  controller.stop();
});

async function startController() {
  await controller.start(envelopeContainer);
  return mockComponent!;
}

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}

describe("EditorEnvelopeController", () => {
  test("opens", async () => {
    const render = await startController();
    expect(render).toMatchSnapshot();
  });

  test("receives init request", async () => {
    const render = await startController();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });
    expect(sentMessages).toEqual([
      { type: EnvelopeBusMessageType.RETURN_INIT, data: undefined },
      { type: EnvelopeBusMessageType.REQUEST_LANGUAGE, data: undefined }
    ]);
    expect(render.update()).toMatchSnapshot();
  });

  test("receives language response", async () => {
    await startController();
    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });
    sentMessages = [];
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData });

    expect(sentMessages).toEqual([{ type: EnvelopeBusMessageType.REQUEST_CONTENT, data: undefined }]);
  });

  test("right after received content", async () => {
    const render = await startController();

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData });
    sentMessages = [];
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: "test content" });

    expect(sentMessages).toEqual([]);
    expect(render.update()).toMatchSnapshot();
  });

  test("after received content and set empty first", async () => {
    const render = await startController();

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_INIT, data: "test-target-origin" });
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_LANGUAGE, data: languageData });
    sentMessages = [];
    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT, data: "test content" });
    await delay(EditorEnvelopeController.ESTIMATED_TIME_TO_WAIT_AFTER_EMPTY_SET_CONTENT);

    expect(sentMessages).toEqual([]);
    expect(render.update()).toMatchSnapshot();
  });
});
