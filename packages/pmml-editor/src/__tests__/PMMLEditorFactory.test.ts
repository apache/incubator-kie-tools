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
  Editor,
  EditorContext,
  I18nService,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType
} from "@kogito-tooling/editor/dist/api";
import { FACTORY_TYPE, PMMLEditorFactory } from "../editor/PMMLEditorFactory";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts/dist/envelope";
import { ChannelType, OperatingSystem } from "@kogito-tooling/channel-common-api";
import { messageBusClientApiMock } from "@kogito-tooling/envelope-bus/dist/common/__tests__";

const channelApi = messageBusClientApiMock<KogitoEditorChannelApi>();

const editorContext: EditorContext = { channel: ChannelType.EMBEDDED, operatingSystem: OperatingSystem.LINUX };
const envelopeContext: KogitoEditorEnvelopeContextType = {
  channelApi: channelApi,
  context: editorContext,
  services: {
    guidedTour: { isEnabled: () => false },
    keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: editorContext.operatingSystem }),
    i18n: new I18nService()
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
