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

import { KogitoEditorChannelApi, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor/dist/api";
import { render } from "@testing-library/react";
import { ReactElement } from "react";
import { PMMLEditor, PMMLEditorInterface } from "../../editor";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { OperatingSystem } from "@kogito-tooling/channel-common-api";
import { messageBusClientApiMock } from "@kogito-tooling/envelope-bus/dist/common/__tests__";
import { I18nService } from "@kogito-tooling/i18n/dist/envelope";

const channelApi = messageBusClientApiMock<KogitoEditorChannelApi>();

const envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi> = {
  channelApi: channelApi,
  operatingSystem: OperatingSystem.LINUX,
  services: {
    guidedTour: { isEnabled: () => false },
    keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: OperatingSystem.LINUX }),
    i18n: new I18nService()
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
    expect(channelApi.notifications.receive_ready).toBeCalled();
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
