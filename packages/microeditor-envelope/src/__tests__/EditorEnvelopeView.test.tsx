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

import * as React from "react";
import { shallow } from "enzyme";
import { EditorEnvelopeView } from "../EditorEnvelopeView";
import { DummyEditor } from "./DummyEditor";

let loadingScreenContainer: HTMLElement;
beforeEach(() => (loadingScreenContainer = document.body.appendChild(document.createElement("div"))));
afterEach(() => loadingScreenContainer.remove());

function renderEditorEnvelopeView(): [EditorEnvelopeView, ReturnType<typeof shallow>] {
  let view: EditorEnvelopeView;
  const render = shallow(
    <EditorEnvelopeView exposing={self => (view = self)} loadingScreenContainer={loadingScreenContainer} />
  );
  return [view!, render];
}

describe("EditorEnvelopeView", () => {
  test("first open", () => {
    const [_, render] = renderEditorEnvelopeView();
    expect(render).toMatchSnapshot();
  });

  test("after loading stops", async () => {
    const [view, render] = renderEditorEnvelopeView();
    await view.setLoadingFinished();
    expect(render).toMatchSnapshot();
  });

  test("after loading stops and editor is set", async () => {
    const [view, render] = renderEditorEnvelopeView();
    await view.setLoadingFinished();
    await view.setEditor(new DummyEditor());
    expect(render).toMatchSnapshot();
  });
});
