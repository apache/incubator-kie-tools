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

import * as React from "react";
import { cleanup, fireEvent, getByTestId, render } from "@testing-library/react";
import { EditorEnvelopeView } from "../../editor/EditorEnvelopeView";
import { DummyEditor } from "./DummyEditor";
import { usingEnvelopeContext } from "../utils";

function renderEditorEnvelopeView(): EditorEnvelopeView {
  let view: EditorEnvelopeView;
  render(usingEnvelopeContext(<EditorEnvelopeView exposing={self => (view = self)} />).wrapper);
  return view!;
}

describe("EditorEnvelopeView", () => {
  afterEach(() => {
    cleanup();
  });

  test("first open", async () => {
    const _ = renderEditorEnvelopeView();
    expect(document.body).toMatchSnapshot();
  });

  test("after loading stops", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    expect(document.body).toMatchSnapshot();
  });

  test("after loading stops and editor is set", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    await view.setEditor(new DummyEditor());

    expect(document.body).toMatchSnapshot();
  });

  test("after set content", async () => {
    const view = renderEditorEnvelopeView();

    await view.setLoadingFinished();
    fireEvent.transitionEnd(getByTestId(document.body, "loading-screen-div"));

    await view.setEditor(new DummyEditor());
    await view.getEditor()!.setContent("/some/path.txt", "some-test-content");

    expect(document.body).toMatchSnapshot();
  });
});
