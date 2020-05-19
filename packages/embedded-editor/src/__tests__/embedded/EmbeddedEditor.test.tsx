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

import { ChannelType, EditorContent, KogitoEdit, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { EnvelopeBusMessageType, EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { mount, render } from "enzyme";
import * as React from "react";
import { RefObject } from "react";
import { EditorType } from "../../common/EditorTypes";
import { File } from "../../common/File";
import { EmbeddedEditor, EmbeddedEditorRef } from "../../embedded/EmbeddedEditor";
import { EmbeddedEditorRouter } from "../../embedded/EmbeddedEditorRouter";

const delay = (ms: number) => {
  return Promise.resolve().then(() => new Promise(res => setTimeout(res, ms)));
};

async function incomingMessage(message: any) {
  window.postMessage(message, window.location.origin);
  await delay(0); //waits til next event loop iteration
}

describe("EmbeddedEditor", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
  const channelType: ChannelType = ChannelType.ONLINE;
  const holder: HTMLElement = document.body.appendChild(document.createElement("div"));

  const editorRef: RefObject<EmbeddedEditorRef> = {
    current: {
      requestContent: () => "",
      requestPreview: () => "",
      setContent: (content: string) => null
    }
  };

  beforeEach(() => {
    spyOn<any>(EnvelopeBusOuterMessageHandler, "generateRandomBusId");
  });

  test("EmbeddedEditor::defaults", () => {
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::setContent", () => {
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.setContent("");
  });

  test("EmbeddedEditor::requestContent", () => {
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.requestContent();
  });

  test("EmbeddedEditor::requestPreview", () => {
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.requestPreview();
  });

  test("EmbeddedEditor::onContentResponse", async () => {
    const onContentResponse = jest.fn((c: EditorContent) => null);

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onContentResponse={onContentResponse}
      />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT });

    expect(onContentResponse).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onSetContentError", async () => {
    const onSetContentError = jest.fn((errorMessage: string) => null);

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onSetContentError={onSetContentError}
      />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR });

    expect(onSetContentError).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onDirtyIndicatorChange", async () => {
    const onDirtyIndicatorChange = jest.fn((isDirty: boolean) => null);

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onDirtyIndicatorChange={onDirtyIndicatorChange}
      />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE });

    expect(onDirtyIndicatorChange).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onReady", async () => {
    const onReady = jest.fn(() => null);

    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onReady={onReady} />, { attachTo: holder });

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_READY });

    expect(onReady).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => Promise.resolve(undefined));

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
      />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: "" } });

    expect(onResourceContentRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
      Promise.resolve({ pattern: "", paths: [] })
    );

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceListRequest={onResourceListRequest}
      />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_LIST, data: { pattern: "", paths: [] } });

    expect(onResourceListRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  // test("EmbeddedEditor::onEditorUndo", async () => {
  //   const onEditorUndo = jest.fn(() => null);

  //   mount(
  //     <EmbeddedEditor
  //       ref={editorRef}
  //       file={file}
  //       router={router}
  //       channelType={channelType}
  //       onEditorUndo={onEditorUndo}
  //     />, { attachTo: holder }
  //   );

  //   await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_UNDO });

  //   expect(onEditorUndo).toBeCalled();

  //   expect(document.body).toMatchSnapshot();
  // });

  // test("EmbeddedEditor::onEditorRedo", async () => {
  //   const onEditorRedo = jest.fn(() => null);

  //   mount(
  //     <EmbeddedEditor
  //       ref={editorRef}
  //       file={file}
  //       router={router}
  //       channelType={channelType}
  //       onEditorRedo={onEditorRedo}
  //     />, { attachTo: holder }
  //   );

  //   await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_REDO });

  //   expect(onEditorRedo).toBeCalled();

  //   expect(document.body).toMatchSnapshot();
  // });

  test("EmbeddedEditor::onNewEdit", async () => {
    const onNewEdit = jest.fn((edit: KogitoEdit) => null);

    mount(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onNewEdit={onNewEdit} />, { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_NEW_EDIT });

    expect(onNewEdit).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  // test("EmbeddedEditor::onPreviewResponse", async () => {
  //   const onPreviewResponse = jest.fn((previewSvg: string) => null);

  //   mount(
  //     <EmbeddedEditor
  //       ref={editorRef}
  //       file={file}
  //       router={router}
  //       channelType={channelType}
  //       onPreviewResponse={onPreviewResponse}
  //     />, { attachTo: holder }
  //   );

  //   await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_PREVIEW });

  //   expect(onPreviewResponse).toBeCalled();

  //   expect(document.body).toMatchSnapshot();
  // });
});
