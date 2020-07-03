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

import { ChannelType, ContentType, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { mount } from "enzyme";
import * as React from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditorRouter, EmbeddedViewer } from "../../embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { MessageTypesYouCanSendToTheChannel } from "@kogito-tooling/microeditor-envelope-protocol";

describe("EmbeddedViewer::ONLINE", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
  const channelType: ChannelType = ChannelType.ONLINE;
  const holder: HTMLElement = document.body.appendChild(document.createElement("div"));

  test.skip("EmbeddedViewer::defaults", () => {
    mount(<EmbeddedViewer file={file} router={router} channelType={channelType} />, {
      attachTo: holder
    });

    expect(holder.firstElementChild?.getAttribute("id")).toBe("kogito-iframe");
    expect(holder.firstElementChild?.getAttribute("data-envelope-channel")).toBe(ChannelType.ONLINE);
    expect(holder.firstElementChild?.getAttribute("src")).toBe("envelope/envelope.html");

    expect(document.body).toMatchSnapshot();
  });

  test.skip("EmbeddedViewer::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) =>
      Promise.resolve({ path: "", type: ContentType.TEXT })
    );

    mount(
      <EmbeddedViewer
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

  test.skip("EmbeddedViewer::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
      Promise.resolve({ pattern: "", paths: [] })
    );

    mount(
      <EmbeddedViewer
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
});
