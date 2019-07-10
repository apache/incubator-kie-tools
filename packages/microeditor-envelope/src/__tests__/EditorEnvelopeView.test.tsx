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
