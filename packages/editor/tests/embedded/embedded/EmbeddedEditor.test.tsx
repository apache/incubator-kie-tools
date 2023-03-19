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
  EditorEnvelopeLocator,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import * as React from "react";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";
import { EnvelopeBusMessagePurpose } from "@kie-tools-core/envelope-bus/dist/api";

describe("EmbeddedEditor::ONLINE", () => {
  const file: EmbeddedEditorFile = {
    fileName: "test.dmn",
    fileExtension: "dmn",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
  };

  const editorEnvelopeLocator = new EditorEnvelopeLocator("localhost:8888", [
    new EnvelopeMapping({
      type: "dmn",
      filePathGlob: "**/*.dmn",
      resourcesPathPrefix: "envelope",
      envelopeContent: { type: EnvelopeContentType.PATH, path: "envelope/envelope.html" },
    }),
  ]);

  const channelType = ChannelType.ONLINE;
  const editorRef = React.createRef<EmbeddedEditorRef>();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("EmbeddedEditor::defaults", () => {
    const { getByTestId, container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        locale={"en"}
      />
    );

    expect(getByTestId("kogito-iframe")).toBeVisible();
    expect(getByTestId("kogito-iframe")).toHaveAttribute("data-envelope-channel", ChannelType.ONLINE);
    expect(getByTestId("kogito-iframe")).toHaveAttribute("src", "envelope/envelope.html");

    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::setContent", () => {
    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        locale={"en"}
      />
    );

    const spyOnContentChangedNotification = jest.spyOn(
      editorRef.current!.getEnvelopeServer().envelopeApi.requests,
      "kogitoEditor_contentChanged"
    );

    editorRef.current?.setContent("path", "content");

    expect(spyOnContentChangedNotification).toBeCalledWith(
      { content: "content", path: "path" },
      { showLoadingOverlay: false }
    );
  });

  test("EmbeddedEditor::requestContent", () => {
    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        locale={"en"}
      />
    );

    const spyRequest_contentResponse = jest.spyOn(
      editorRef.current!.getEnvelopeServer().envelopeApi.requests,
      "kogitoEditor_contentRequest"
    );
    editorRef.current?.getContent();

    expect(spyRequest_contentResponse).toBeCalled();
  });

  test("EmbeddedEditor::requestPreview", () => {
    render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        locale={"en"}
      />
    );

    const spyRequest_previewResponse = jest.spyOn(
      editorRef.current!.getEnvelopeServer().envelopeApi.requests,
      "kogitoEditor_previewRequest"
    );
    editorRef.current?.getPreview();

    expect(spyRequest_previewResponse).toBeCalled();
  });

  test("EmbeddedEditor::onSetContentError", async () => {
    const onSetContentError = jest.fn();

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        kogitoEditor_setContentError={onSetContentError}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: editorRef.current!.getEnvelopeServer().id,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "kogitoEditor_setContentError",
      data: [],
    });

    expect(onSetContentError).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onReady", async () => {
    const onReady = jest.fn();

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        kogitoEditor_ready={onReady}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: editorRef.current!.getEnvelopeServer().id,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "kogitoEditor_ready",
      data: [],
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
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        kogitoWorkspace_resourceContentRequest={onResourceContentRequest}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: editorRef.current!.getEnvelopeServer().id,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "kogitoWorkspace_resourceContentRequest",
      data: [{ path: "" }],
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
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        kogitoWorkspace_resourceListRequest={onResourceListRequest}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: editorRef.current!.getEnvelopeServer().id,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "kogitoWorkspace_resourceListRequest",
      data: [{ pattern: "", paths: [] }],
    });

    expect(onResourceListRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedEditor::onNewEdit", async () => {
    const onNewEdit = jest.fn();

    const { container } = render(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        kogitoWorkspace_newEdit={onNewEdit}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: editorRef.current!.getEnvelopeServer().id,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
      type: "kogitoWorkspace_newEdit",
      data: [new WorkspaceEdit("1")],
    });

    expect(editorRef.current?.getStateControl().getCommandStack()).toEqual([{ id: "1" }]);
    expect(onNewEdit).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });
});
