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

import { ChannelType, KogitoEdit, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { KogitoChannelBus, MessageTypesYouCanSendToTheChannel } from "@kogito-tooling/microeditor-envelope-protocol";
import { mount } from "enzyme";
import * as React from "react";
import { RefObject } from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditor, EmbeddedEditorRef, EmbeddedEditorRouter } from "../../embedded";
import { StateControl } from "../../stateControl";
import { incomingMessage } from "./EmbeddedEditorTestUtils";

describe("EmbeddedEditor::ONLINE", () => {
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
      getStateControl: () => new StateControl(),
      requestContent: jest.fn(),
      requestPreview: jest.fn(),
      setContent: jest.fn(),
      notifyRedo: jest.fn(),
      notifyUndo: jest.fn()
    }
  };

  beforeAll(() => spyOn<any>(KogitoChannelBus, "generateRandomBusId")); //FIXME: Tiago

  test.skip("EmbeddedEditor::defaults", () => {
    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />, {
      attachTo: holder
    });

    expect(holder.firstElementChild?.getAttribute("id")).toBe("kogito-iframe");
    expect(holder.firstElementChild?.getAttribute("data-envelope-channel")).toBe(ChannelType.ONLINE);
    expect(holder.firstElementChild?.getAttribute("src")).toBe("envelope/envelope.html");

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedEditor::setContent", () => {
    // const spyRespond_contentRequest = jest.spyOn(KogitoChannelBus.prototype, "respond_contentRequest");

    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />, {
      attachTo: holder
    });

    editorRef.current?.setContent("content");

    // expect(spyRespond_contentRequest).toBeCalledWith({ content: "content" });
  });

  test.skip("EmbeddedEditor::requestContent", () => {
    const spyRequest_contentResponse = jest.spyOn(KogitoChannelBus.prototype, "request_contentResponse");

    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />, {
      attachTo: holder
    });

    editorRef.current?.requestContent();

    expect(spyRequest_contentResponse).toBeCalled();
  });

  test.skip("EmbeddedEditor::requestPreview", () => {
    const spyRequest_previewResponse = jest.spyOn(KogitoChannelBus.prototype, "request_previewResponse");

    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} />, {
      attachTo: holder
    });

    editorRef.current?.requestPreview();

    expect(spyRequest_previewResponse).toBeCalled();
  });

  test.skip("EmbeddedEditor::onSetContentError", async () => {
    const onSetContentError = jest.fn((errorMessage: string) => null);

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onSetContentError={onSetContentError}
      />,
      { attachTo: holder }
    );

    await incomingMessage({ type: MessageTypesYouCanSendToTheChannel.NOTIFY_SET_CONTENT_ERROR });

    expect(onSetContentError).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedEditor::onReady", async () => {
    const onReady = jest.fn(() => null);

    mount(<EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onReady={onReady} />, {
      attachTo: holder
    });

    await incomingMessage({ type: MessageTypesYouCanSendToTheChannel.NOTIFY_READY });

    expect(onReady).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedEditor::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => Promise.resolve(undefined));

    mount(
      <EmbeddedEditor
        ref={editorRef}
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
      />,
      { attachTo: holder }
    );

    await incomingMessage({ type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_CONTENT, data: { path: "" } });

    expect(onResourceContentRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedEditor::onResourceListRequest", async () => {
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
      />,
      { attachTo: holder }
    );

    await incomingMessage({
      type: MessageTypesYouCanSendToTheChannel.REQUEST_RESOURCE_LIST,
      data: { pattern: "", paths: [] }
    });

    expect(onResourceListRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedEditor::onNewEdit", async () => {
    const onNewEdit = jest.fn((edit: KogitoEdit) => null);

    mount(
      <EmbeddedEditor ref={editorRef} file={file} router={router} channelType={channelType} onNewEdit={onNewEdit} />,
      { attachTo: holder }
    );

    await incomingMessage({
      type: MessageTypesYouCanSendToTheChannel.NOTIFY_EDITOR_NEW_EDIT,
      data: new KogitoEdit("1")
    });
    expect(editorRef.current?.getStateControl().getCommandStack()).toEqual(["1"]);
    expect(onNewEdit).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });
});
