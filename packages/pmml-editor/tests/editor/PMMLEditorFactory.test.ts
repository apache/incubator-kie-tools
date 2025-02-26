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
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  Editor,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { PMMLEditorFactory, PMMLEditorInterface } from "@kie-tools/pmml-editor";
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

describe("PMMLEditorFactory", () => {
  test("Supported type::CreateEditor", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();

    jest.spyOn(factory, "createEditor");

    const created: Promise<Editor> = factory.createEditor(envelopeContext, {
      fileExtension: "pmml",
      resourcesPathPrefix: "",
      initialLocale: "en",
      isReadOnly: false,
      channel: ChannelType.EMBEDDED,
      workspaceRootAbsolutePosixPath: DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
    });
    expect(created).resolves.toBeInstanceOf(PMMLEditorInterface);
  });
});
