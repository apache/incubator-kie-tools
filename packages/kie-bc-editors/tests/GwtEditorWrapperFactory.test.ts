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

import { GwtEditorWrapperFactory } from "@kie-tools/kie-bc-editors/dist/common/GwtEditorWrapperFactory";
import { GwtLanguageData, Resource } from "@kie-tools/kie-bc-editors/dist/common/GwtLanguageData";
import { GwtStateControlService } from "@kie-tools/kie-bc-editors/dist/common/gwtStateControl";
import { messageBusClientApiMock } from "@kie-tools-core/envelope-bus/dist-tests/messageBusClientApiMock";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import {
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorChannelApi,
} from "@kie-tools-core/editor/dist/api";
import { GwtEditorWrapper } from "@kie-tools/kie-bc-editors/dist/common/GwtEditorWrapper";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { XmlFormatter } from "@kie-tools/kie-bc-editors/dist/common/XmlFormatter";

const cssResource: Resource = {
  type: "css",
  paths: ["resource1.css", "resource2.css"],
};

const jsResource: Resource = {
  type: "js",
  paths: [
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js",
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js",
    "resource1.js",
    "resource2.js",
    "resource3.js",
    "resource4.js",
    "resource5.js",
  ],
};

const xmlFormatter = { format: (c: string) => c };

function waitForNScriptsToLoad(remaining: number) {
  if (remaining <= 0) {
    return Promise.resolve();
  }

  const script = Array.from(document.getElementsByTagName("script")).pop()!;
  return new Promise<void>((res) => {
    script.addEventListener("load", () => {
      waitForNScriptsToLoad(remaining - 1).then(res);
    });
    script.dispatchEvent(new Event("load"));
  });
}

describe("GwtEditorWrapperFactory", () => {
  test("create editor", async () => {
    const testLanguageData: GwtLanguageData = {
      type: "gwt",
      editorId: "editorID",
      gwtModuleName: "moduleName",
      resources: [cssResource, jsResource],
    };

    const channelApiMock: MessageBusClientApi<KogitoEditorChannelApi> = messageBusClientApiMock();

    const gwtEditorWrapperFactory: GwtEditorWrapperFactory<GwtEditorWrapper> = new GwtEditorWrapperFactory(
      testLanguageData,
      (self) => {
        return new GwtEditorWrapper(
          testLanguageData.editorId,
          self.gwtAppFormerConsumedInteropApi.getEditor(testLanguageData.editorId),
          channelApiMock,
          new XmlFormatter(),
          self.gwtStateControlService,
          self.kieBcEditorsI18n
        );
      },
      { shouldLoadResourcesDynamically: true },
      xmlFormatter,
      {
        onFinishedLoading: (callback: () => Promise<any>) => (window.appFormerGwtFinishedLoading = callback),
        getEditor: jest.fn(),
      },
      new GwtStateControlService()
    );

    const editorCreation = gwtEditorWrapperFactory.createEditor(
      {
        shared: {} as any,
        channelApi: channelApiMock,
        services: {
          keyboardShortcuts: {} as any,
          i18n: new I18nService(),
        },
        supportedThemes: [],
      },
      {
        resourcesPathPrefix: "",
        fileExtension: "txt",
        initialLocale: "en",
        isReadOnly: false,
        channel: ChannelType.ONLINE,
        workspaceRootAbsolutePosixPath: DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
      }
    );

    await waitForNScriptsToLoad(jsResource.paths.length);
    await window.appFormerGwtFinishedLoading();
    await editorCreation;

    const links = document.getElementsByTagName("link");
    expect(links.length).toBe(cssResource.paths.length);
    Array.from(links).forEach((l, i) => {
      expect(l.href).toContain(cssResource.paths[i]);
    });

    const scripts = document.getElementsByTagName("script");
    expect(scripts.length).toBe(jsResource.paths.length);
    Array.from(scripts).forEach((s, i) => {
      expect(s.src).toContain(jsResource.paths[i]);
    });
  });
});
