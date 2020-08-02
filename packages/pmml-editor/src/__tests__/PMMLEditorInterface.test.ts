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

import { ChannelType, EditorContext, OperatingSystem } from "@kogito-tooling/editor-envelope-protocol";
import { render } from "@testing-library/react";
import { ReactElement } from "react";
import { PMMLEditor } from "../editor/PMMLEditor";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";
import { KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor-api";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";

const messageBusClient = {
  notify: jest.fn(),
  request: jest.fn(),
  subscribe: jest.fn(),
  unsubscribe: jest.fn()
};

const editorContext: EditorContext = { channel: ChannelType.EMBEDDED, operatingSystem: OperatingSystem.LINUX };
const envelopeContext: KogitoEditorEnvelopeContextType = {
  channelApi: messageBusClient,
  context: editorContext,
  services: {
    guidedTour: { isEnabled: () => false },
    keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: editorContext.operatingSystem })
  }
};

const editorInterface: PMMLEditorInterface = new PMMLEditorInterface(envelopeContext);
let editor: PMMLEditor;

beforeEach(() => {
  const component: ReactElement = editorInterface.af_componentRoot() as ReactElement;
  render(component);

  editor = (editorInterface as any).self as PMMLEditor;
});

describe("PMMLEditorInterface", () => {
  test("Mount", () => {
    expect(messageBusClient.notify).toBeCalledWith("receive_ready");
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
