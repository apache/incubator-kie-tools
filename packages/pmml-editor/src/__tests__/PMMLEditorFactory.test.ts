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
import { FACTORY_TYPE, PMMLEditorFactory } from "../editor/PMMLEditorFactory";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";
import { Editor, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor-api";
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

describe("PMMLEditorFactory", () => {
  test("Unsupported LanguageData type", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();
    expect(factory.supports("unsupported")).toBeFalsy();
  });

  test("Supported LanguageData type", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();
    expect(factory.supports(FACTORY_TYPE)).toBeTruthy();
  });

  test("Supported type::CreateEditor", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();

    jest.spyOn(factory, "createEditor");

    const created: Promise<Editor> = factory.createEditor(envelopeContext, {
      fileExtension: FACTORY_TYPE,
      resourcesPathPrefix: ""
    });
    expect(created).resolves.toBeInstanceOf(PMMLEditorInterface);
  });
});
