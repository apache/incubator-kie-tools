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

import { GwtAppFormerApi, GwtEditor } from "@kogito-tooling/kie-bc-editors/dist/common/GwtAppFormerApi";
import { DummyEditor } from "./DummyEditor";
import { Editor } from "@kie-tooling-core/editor/dist/api";

class DummyGwtEditor {
  private wrappedEditor: Editor;

  constructor(wrappedEditor: Editor) {
    this.wrappedEditor = wrappedEditor;
  }
  public get(): Editor {
    return this.wrappedEditor;
  }
}

const dummyEditor = new DummyEditor();
const dummyGwtEditor = new DummyGwtEditor(dummyEditor);
const editorId = "dummy editor";
const appFormerGwtApi = new GwtAppFormerApi();

window.gwtEditorBeans = new Map<string, { get(): GwtEditor }>();
window.gwtEditorBeans.set(editorId, dummyGwtEditor);

describe("AppFormerGwtApi", () => {
  test("get existing editor", () => {
    const possibleEditor = appFormerGwtApi.getEditor(editorId);
    expect(possibleEditor).toBeTruthy();
  });

  test("get non existing editor", () => {
    expect(() => appFormerGwtApi.getEditor("X")).toThrowError();
  });
});
