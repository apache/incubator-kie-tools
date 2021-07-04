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

import { GwtEditorWrapper } from "@kogito-tooling/kie-bc-editors/dist/common/GwtEditorWrapper";
import { GwtStateControlService } from "@kogito-tooling/kie-bc-editors/dist/common/gwtStateControl";
import { KogitoEditorChannelApi } from "@kie-tooling-core/editor/dist/api";
import { messageBusClientApiMock } from "@kie-tooling-core/envelope-bus/dist-tests/common";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import {
  kieBcEditorsI18nDefaults,
  kieBcEditorsI18nDictionaries,
} from "@kogito-tooling/kie-bc-editors/dist/common/i18n";

const MockEditor = jest.fn(() => ({
  undo: jest.fn(),
  redo: jest.fn(),
  getContent: jest.fn(),
  setContent: jest.fn(() => Promise.resolve()),
  isDirty: jest.fn(),
  getPreview: jest.fn(),
  validate: jest.fn(),
}));

const mockEditor = new MockEditor();
const mockChannelApi = messageBusClientApiMock<KogitoEditorChannelApi>();
const mockXmlFormatter = { format: (c: string) => c };
const i18n = new I18n(kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries);

const wrapper = new GwtEditorWrapper(
  "MockEditorId",
  mockEditor,
  mockChannelApi,
  mockXmlFormatter,
  new GwtStateControlService(),
  i18n
);

describe("GwtEditorWrapper", () => {
  test("set content", async () => {
    await wrapper.setContent("path", " a content ");
    expect(mockEditor.setContent).toHaveBeenCalledWith("path", "a content");
  });

  test("set content error", async () => {
    mockEditor.setContent = jest.fn(() => Promise.reject());
    await expect(wrapper.setContent("path", " a content ")).rejects.toEqual(undefined);
    expect(mockEditor.setContent).toHaveBeenCalledWith("path", "a content");
  });

  test("af_onOpen removes header", () => {
    const parent = document.createElement("div");
    const workbenchHeaderPanel = document.createElement("div");
    const listBarHeading = document.createElement("div");
    listBarHeading.className = ".panel-heading.uf-listbar-panel-header";
    workbenchHeaderPanel.id = "workbenchHeaderPanel";
    parent.appendChild(workbenchHeaderPanel);
    document.body.appendChild(parent);

    wrapper.af_onOpen();

    const removedHeaderPanel = document.getElementById("workbenchHeaderPanel");
    const removedListBarHeader = document.querySelector(".panel-heading.uf-listbar-panel-header");
    expect(removedHeaderPanel).toBeFalsy();
    expect(removedListBarHeader).toBeFalsy();
  });

  test("af_onOpen no element to remove", () => {
    let removedHeaderPanel = document.getElementById("workbenchHeaderPanel");
    expect(removedHeaderPanel).toBeFalsy();

    wrapper.af_onOpen();

    removedHeaderPanel = document.getElementById("workbenchHeaderPanel");
    expect(removedHeaderPanel).toBeFalsy();
  });
});
