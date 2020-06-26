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

import { EditorContent, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";
import { ChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { mount } from "enzyme";
import { ReactElement } from "react";
import { PMMLEditor } from "../editor/PMMLEditor";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";

const handler: EnvelopeBusInnerMessageHandler = new EnvelopeBusInnerMessageHandler(
  {
    postMessage: (_1, _2) => {
      /*NOP*/
    }
  },
  self => ({
    receive_contentResponse: (_: EditorContent) => {
      /*NOP*/
    },
    receive_languageResponse: (_: LanguageData) => {
      /*NOP*/
    },
    receive_contentRequest: () => {
      /*NOP*/
    },
    receive_resourceContentResponse: (_: ResourceContent) => {
      /*NOP*/
    },
    receive_resourceContentList: (_: ResourcesList) => {
      /*NOP*/
    },
    receive_editorRedo(): void {
      /*NOP*/
    },
    receive_editorUndo(): void {
      /*NOP*/
    },
    receive_previewRequest: () => {
      /*NOP*/
    },
    receive_guidedTourElementPositionRequest: (_: string) => {
      /*NOP*/
    },
    receive_channelKeyboardEvent: (_: ChannelKeyboardEvent) => {
      /*NOP*/
    }
  })
);

//Mock that the bus has been setup correctly
handler.targetOrigin = "origin";

const editorInterface: PMMLEditorInterface = new PMMLEditorInterface(handler);
let editor: PMMLEditor;

beforeEach(() => {
  spyOn(handler, "notify_ready");

  const component: ReactElement = editorInterface.af_componentRoot() as ReactElement;
  mount(component);

  editor = (editorInterface as any).self as PMMLEditor;
});

describe("PMMLEditorInterface", () => {
  test("Mount", () => {
    expect(handler.notify_ready).toBeCalledTimes(1);
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
