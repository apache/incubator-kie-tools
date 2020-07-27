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
  KogitoEdit,
  LanguageData,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";
import {
  EnvelopeBus,
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  KogitoChannelApi,
  KogitoChannelBus
} from "@kogito-tooling/microeditor-envelope-protocol";
import { render } from "@testing-library/react";
import { ReactElement } from "react";
import { PMMLEditor } from "../editor/PMMLEditor";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";

const languageData: LanguageData = { type: "unused" };

const bus: EnvelopeBus = {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any) {
    /*NOP*/
  }
};
const api: KogitoChannelApi = {
  receive_setContentError(_: string) {
    /*NOP*/
  },
  receive_ready() {
    /*NOP*/
  },
  receive_openFile(_: string) {
    /*NOP*/
  },
  receive_guidedTourUserInteraction(_: UserInteraction) {
    /*NOP*/
  },
  receive_guidedTourRegisterTutorial(_: Tutorial) {
    /*NOP*/
  },
  receive_newEdit(_: KogitoEdit) {
    /*NOP*/
  },
  receive_stateControlCommandUpdate(_: StateControlCommand) {
    /*NOP*/
  },
  receive_languageRequest() {
    return Promise.resolve(languageData);
  },
  receive_contentRequest() {
    return Promise.resolve({ content: "" });
  },
  receive_resourceContentRequest(_: ResourceContentRequest) {
    return Promise.resolve(undefined);
  },
  receive_resourceListRequest(_: ResourceListRequest) {
    return Promise.resolve(new ResourcesList("", []));
  }
};
const messageBus: KogitoChannelBus = new KogitoChannelBus(bus, api);

const editorInterface: PMMLEditorInterface = new PMMLEditorInterface(messageBus.client);
let editor: PMMLEditor;

beforeEach(() => {
  spyOn(bus, "postMessage");

  const component: ReactElement = editorInterface.af_componentRoot() as ReactElement;
  render(component);

  editor = (editorInterface as any).self as PMMLEditor;
});

describe("PMMLEditorInterface", () => {
  test("Mount", () => {
    expect(bus.postMessage).toBeCalledWith({
      type: "receive_ready",
      data: [],
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION
    });
  });

  test("getContent", async () => {
    spyOn(editor, "getContent");

    await editorInterface.getContent();

    expect(editor.getContent).toBeCalledTimes(1);
  });

  test("setContent", async () => {
    spyOn(editor, "setContent");

    await editorInterface.setContent("path", "content");

    expect(editor.setContent).toBeCalledTimes(1);
  });

  test("getPreview", () => {
    expect(editorInterface.getPreview()).resolves.toBeUndefined();
  });
});
