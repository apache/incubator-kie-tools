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
import { cleanup, fireEvent, getByTestId, render, act } from "@testing-library/react";
import { EditorEnvelopeView, EditorEnvelopeViewApi } from "@kie-tooling-core/editor/dist/envelope/EditorEnvelopeView";
import { DummyEditor } from "./DummyEditor";
import { usingEditorEnvelopeI18nContext, usingEnvelopeContext } from "./utils";
import { Editor } from "@kie-tooling-core/editor/dist/api";

function renderEditorEnvelopeView(): EditorEnvelopeViewApi<Editor> {
  const editorEnvelopeRef = React.createRef<EditorEnvelopeViewApi<Editor>>();
  const setLocale = jest.fn();
  render(
    usingEditorEnvelopeI18nContext(
      usingEnvelopeContext(<EditorEnvelopeView ref={editorEnvelopeRef} setLocale={setLocale} />).wrapper
    ).wrapper
  );
  return editorEnvelopeRef.current!;
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

    act(() => {
      view.setLoadingFinished();
    });

    fireEvent.animationEnd(getByTestId(document.body, "loading-screen-div"));

    expect(document.body).toMatchSnapshot();
  });

  test("after loading stops and editor is set", () => {
    const view = renderEditorEnvelopeView();

    act(() => {
      view.setLoadingFinished();
    });

    fireEvent.animationEnd(getByTestId(document.body, "loading-screen-div"));

    act(() => {
      view.setEditor(new DummyEditor());
    });

    expect(document.body).toMatchSnapshot();
  });

  test("after set content", () => {
    const view = renderEditorEnvelopeView();

    act(() => {
      view.setLoadingFinished();
    });

    fireEvent.animationEnd(getByTestId(document.body, "loading-screen-div"));

    act(() => {
      view.setEditor(new DummyEditor());
    });
    setTimeout(() => view.getEditor()!.setContent("/some/path.txt", "some-test-content"), 100);

    expect(document.body).toMatchSnapshot();
  });
});
