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

import { GwtEditorWrapper } from "../GwtEditorWrapper";
import { GwtStateControlService } from "../gwtStateControl";

const MockEditor = jest.fn(() => ({
  undo: jest.fn(),
  redo: jest.fn(),
  getContent: jest.fn(),
  setContent: jest.fn(() => Promise.resolve()),
  isDirty: jest.fn(),
  getPreview: jest.fn()
}));

const mockEditor = new MockEditor();
const mockMessageBus = { notify: jest.fn(), request: jest.fn(), subscribe: jest.fn(), unsubscribe: jest.fn() };
const mockXmlFormatter = { format: (c: string) => c };

const wrapper = new GwtEditorWrapper(
  "MockEditorId",
  mockEditor,
  mockMessageBus,
  mockXmlFormatter,
  new GwtStateControlService()
);

describe("GwtEditorWrapper", () => {
  test("set content", async () => {
    await wrapper.setContent("path", " a content ");
    expect(mockEditor.setContent).toHaveBeenCalledWith("path", "a content");
  });

  test("set content error", async () => {
    mockEditor.setContent = jest.fn(() => {
      return Promise.reject();
    });

    await wrapper.setContent("path", " a content ");
    expect(mockEditor.setContent).toHaveBeenCalledWith("path", "a content");
    expect(mockMessageBus.notify).toHaveBeenCalled();
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
