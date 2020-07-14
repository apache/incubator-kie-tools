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

import { ChannelType, KogitoEdit } from "@kogito-tooling/microeditor-envelope-protocol";
import { EnvelopeBusMessagePurpose, KogitoChannelBus } from "@kogito-tooling/microeditor-envelope-protocol";
import * as React from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditor, EmbeddedEditorRef, EmbeddedEditorRouter } from "../../embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";

describe("EmbeddedEditor::ONLINE", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router = new EmbeddedEditorRouter();
  const channelType = ChannelType.ONLINE;
  const editorRef = React.createRef<EmbeddedEditorRef>();
  const busId = "test-bus-id";

  beforeEach(() => {
    jest.spyOn(KogitoChannelBus.prototype, "generateRandomId").mockReturnValue(busId);
    jest.clearAllMocks();
  });

  test("EmbeddedEditor::defaults", () => {
    const { getByTestId, container } = render(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />
    );

    expect(getByTestId("kogito-iframe")).toBeVisible();
    expect(getByTestId("kogito-iframe")).toHaveAttribute("data-envelope-channel", ChannelType.ONLINE);
    expect(getByTestId("kogito-iframe")).toHaveAttribute("src", "envelope/envelope.html");

    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::setContent", () => {
    const spyRespond_contentRequest = jest.spyOn(KogitoChannelBus.prototype, "notify_contentChanged");
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);
    editorRef.current?.setContent("content");

    expect(spyRespond_contentRequest).toBeCalledWith({ content: "content" });
  });

  test("EmbeddedEditor::requestContent", () => {
    const spyRequest_contentResponse = jest.spyOn(KogitoChannelBus.prototype, "request_contentResponse");
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);
    editorRef.current?.requestContent();

    expect(spyRequest_contentResponse).toBeCalled();
  });

  test("EmbeddedEditor::requestPreview", () => {
    const spyRequest_previewResponse = jest.spyOn(KogitoChannelBus.prototype, "request_previewResponse");
    render(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />);
    editorRef.current?.requestPreview();

    expect(spyRequest_previewResponse).toBeCalled();
  });

  test("EmbeddedEditor::onSetContentError", async () => {
    const onSetContentError = jest.fn();

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onSetContentError={onSetContentError}
      />
    );

    await incomingMessage({
      busId: busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_setContentError",
      data: []
    });

    expect(onSetContentError).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onReady", async () => {
    const onReady = jest.fn();

    const { container } = render(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onReady={onReady} />
    );

    await incomingMessage({
      busId: busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_ready",
      data: []
    });

    expect(onReady).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn(() => Promise.resolve({} as any));

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
      />
    );

    await incomingMessage({
      busId: busId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceContentRequest",
      data: [{ path: "" }]
    });

    expect(onResourceContentRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn(() => Promise.resolve({} as any));

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceListRequest={onResourceListRequest}
      />
    );

    await incomingMessage({
      busId: busId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceListRequest",
      data: [{ pattern: "", paths: [] }]
    });

    expect(onResourceListRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onNewEdit", async () => {
    const onNewEdit = jest.fn();

    const { container } = render(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onNewEdit={onNewEdit} />
    );

    await incomingMessage({
      busId: busId,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "receive_newEdit",
      data: [new KogitoEdit("1")]
    });

    expect(editorRef.current?.getStateControl().getCommandStack()).toEqual(["1"]);
    expect(onNewEdit).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });
});
