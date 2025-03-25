/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { render } from "@testing-library/react";
import { ReactElement } from "react";
import { PMMLEditor, PMMLEditorInterface } from "@kie-tools/pmml-editor";
import { DefaultKeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { messageBusClientApiMock } from "@kie-tools-core/envelope-bus/dist-tests/messageBusClientApiMock";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";

const channelApi = messageBusClientApiMock<KogitoEditorChannelApi>();

const envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi> = {
  shared: {} as any,
  channelApi: channelApi,
  operatingSystem: OperatingSystem.LINUX,
  services: {
    keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: OperatingSystem.LINUX }),
    i18n: new I18nService(),
  },
  supportedThemes: [],
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
    expect(channelApi.notifications.kogitoEditor_ready.send).toHaveBeenCalled();
  });

  test("getContent", async () => {
    jest.spyOn(editor, "getContent");

    await editorInterface.getContent();

    expect(editor.getContent).toHaveBeenCalledTimes(1);
  });

  test("setContent", async () => {
    jest.spyOn(editor, "setContent").mockReturnValue(Promise.resolve());

    await editorInterface.setContent("path", "content");

    expect(editor.setContent).toHaveBeenCalledTimes(1);
  });

  test("getPreview", () => {
    expect(editorInterface.getPreview()).resolves.toBeUndefined();
  });
});
