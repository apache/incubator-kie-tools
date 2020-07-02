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

import {
  ChannelType,
  EditorContent,
  KogitoEdit,
  ResourceContentRequest,
  ResourceListRequest
} from "@kogito-tooling/core-api";
import { EnvelopeBusMessageType, EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import * as React from "react";
import { RefObject } from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditor, EmbeddedEditorRef, EmbeddedEditorRouter } from "../../embedded";
import { StateControl } from "../../stateControl";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom";

describe("EmbeddedEditor::ONLINE", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
  const channelType: ChannelType = ChannelType.ONLINE;

  const editorRef: RefObject<EmbeddedEditorRef> = {
    current: {
      getStateControl: () => new StateControl(),
      requestContent: () => "",
      requestPreview: () => "",
      setContent: (content: string) => null,
      notifyRedo: () => "",
      notifyUndo: () => ""
    }
  };

  beforeAll(() => spyOn<any>(EnvelopeBusOuterMessageHandler, "generateRandomBusId"));
  beforeEach(() => jest.clearAllMocks());

  test("EmbeddedEditor::defaults", () => {
    const { getByTestId } = render(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />
    );

    expect(getByTestId("kogito-iframe")).toBeVisible();
    expect(getByTestId("kogito-iframe")).toHaveAttribute("data-envelope-channel", ChannelType.ONLINE);
    expect(getByTestId("kogito-iframe")).toHaveAttribute("src", "envelope/envelope.html");

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::setContent", () => {
    const spyRespond_contentRequest = jest.spyOn(EnvelopeBusOuterMessageHandler.prototype, "respond_contentRequest");

    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.setContent("content");

    expect(spyRespond_contentRequest).toBeCalledWith({ content: "content" });
  });

  test("EmbeddedEditor::requestContent", () => {
    const spyRequest_contentResponse = jest.spyOn(EnvelopeBusOuterMessageHandler.prototype, "request_contentResponse");

    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.requestContent();

    expect(spyRequest_contentResponse).toBeCalled();
  });

  test("EmbeddedEditor::requestPreview", () => {
    const spyRequest_previewResponse = jest.spyOn(EnvelopeBusOuterMessageHandler.prototype, "request_previewResponse");

    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);

    editorRef.current?.requestPreview();

    expect(spyRequest_previewResponse).toBeCalled();
  });

  test("EmbeddedEditor::onContentResponse", async () => {
    const onContentResponse = jest.fn((c: EditorContent) => null);

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onContentResponse={onContentResponse}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_CONTENT });

    expect(onContentResponse).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onSetContentError", async () => {
    const onSetContentError = jest.fn((errorMessage: string) => null);

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onSetContentError={onSetContentError}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_SET_CONTENT_ERROR });

    expect(onSetContentError).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onDirtyIndicatorChange", async () => {
    const onDirtyIndicatorChange = jest.fn((isDirty: boolean) => null);

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onDirtyIndicatorChange={onDirtyIndicatorChange}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_DIRTY_INDICATOR_CHANGE });

    expect(onDirtyIndicatorChange).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onReady", async () => {
    const onReady = jest.fn(() => null);

    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onReady={onReady} />);

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_READY });

    expect(onReady).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => Promise.resolve(undefined));

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: "" } });

    expect(onResourceContentRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
      Promise.resolve({ pattern: "", paths: [] })
    );

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceListRequest={onResourceListRequest}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_LIST, data: { pattern: "", paths: [] } });

    expect(onResourceListRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onNewEdit", async () => {
    const onNewEdit = jest.fn((edit: KogitoEdit) => null);

    render(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onNewEdit={onNewEdit} />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.NOTIFY_EDITOR_NEW_EDIT, data: new KogitoEdit("1") });
    expect(editorRef.current?.getStateControl().getCommandStack()).toEqual(["1"]);
    expect(onNewEdit).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedEditor::onPreviewResponse", async () => {
    const onPreviewResponse = jest.fn((previewSvg: string) => null);

    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onPreviewResponse={onPreviewResponse}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.RETURN_PREVIEW });

    expect(onPreviewResponse).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });
});
